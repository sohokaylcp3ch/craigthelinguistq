package com.easylinker.proxy.server.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.quartz.job.ScheduleSendMessageJob;
import com.easylinker.proxy.server.app.constants.result.ReturnResult;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.DeviceGroup;
import com.easylinker.proxy.server.app.model.device.DeviceJob;
import com.easylinker.proxy.server.app.model.device.Location;
import com.easylinker.proxy.server.app.model.user.AppUser;
import com.easylinker.proxy.server.app.service.*;
import com.easylinker.proxy.server.app.utils.HttpTool;
import com.easylinker.proxy.server.app.utils.Image2Base64Tool;
import com.easylinker.proxy.server.app.utils.QRCodeGenerator;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/user")
@PreAuthorize(value = "hasRole('ROLE_USER')")
/**
 * 用户的业务逻辑层
 */
public class UserController {
    private static final String REG_1_Z = "(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}";

    Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    @Qualifier("Scheduler")
    Scheduler scheduler;
    @Autowired
    AppUserService appUserService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    DeviceGroupService deviceGroupService;
    @Autowired
    LocationService locationService;
    @Autowired
    DeviceJobService deviceJobService;

    @Autowired
    HttpTool httpTool;
    @Value("${emq.api.host}")
    String apiHost;


    /**
     * 把单个设备绑定到用户
     *
     * @param deviceId
     * @return
     */

    @RequestMapping(value = "/bind/{deviceId}/{groupId}")
    public JSONObject bind(@PathVariable Long deviceId, @PathVariable Long groupId) {
        Device device = deviceService.findADevice(deviceId);
        DeviceGroup deviceGroup = deviceGroupService.findADeviceGroupById(groupId);
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (device != null && deviceGroup != null && deviceGroup.getAppUser().getId().longValue() == appUser.getId().longValue()) {
            if (device.getAppUser() == null) {
                device.setAppUser(appUser);
                device.setTopic("IN/DEVICE/" + appUser.getId() + "/" + deviceGroup.getId() + "/" + device.getId());
                device.setDeviceGroup(deviceGroup);
                deviceService.save(device);
                return ReturnResult.returnTipMessage(1, "设备绑定成功!");
            } else {
                return ReturnResult.returnTipMessage(0, "设备已经绑定!");
            }
        } else {
            return ReturnResult.returnTipMessage(0, "设备或者分组不存在!");

        }
    }



    /**
     * 绑定默认分组
     *
     * @param deviceId
     * @return
     */

    @RequestMapping(value = "/bindToDefaultGroup/{deviceId}")
    public JSONObject bindToDefaultGroup(@PathVariable Long deviceId) {
        Device device = deviceService.findADevice(deviceId);
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long appUserId=appUser.getId();
        DeviceGroup deviceGroup=deviceGroupService.findADeviceGroupById(appUserId);
        if (device != null || deviceGroup != null || deviceGroup.getAppUser().getId().longValue() == appUser.getId().longValue()) {
            if (device.getAppUser() == null) {
                device.setAppUser(appUser);
                device.setTopic("IN/DEVICE/" + appUser.getId() + "/" + deviceGroup.getId() + "/" + device.getId());
                device.setDeviceGroup(deviceGroup);
                deviceService.save(device);
                return ReturnResult.returnTipMessage(1, "设备绑定默认成功!");
            } else {
                DeviceGroup newDeviceGroup = new DeviceGroup();
                newDeviceGroup.setId(device.getId());
                newDeviceGroup.setGroupName("DefaultGroup");
                newDeviceGroup.setComment("默认分组");
                newDeviceGroup.setAppUser(appUser);
                deviceGroupService.save(newDeviceGroup);
                device.setAppUser(appUser);
                device.setTopic("IN/DEVICE/" + appUser.getId() + "/" + newDeviceGroup.getId() + "/" + device.getId());
                device.setDeviceGroup(newDeviceGroup);
                deviceService.save(device);
                return ReturnResult.returnTipMessage(1, "设备成功创建分组并绑定默认分组!");
            }
        } else {
            return ReturnResult.returnTipMessage(0, "设备不存在!");
        }
    }


    /**
     * 增加一个分组
     * 参数
     * 分组名字：设备组必须用英文或者数字组合且不下6位!
     * 分组简介
     *
     * @return
     */
    @RequestMapping(value = "/addGroup", method = RequestMethod.POST)
    public JSONObject addAGroup(@RequestBody JSONObject body) {
        String groupName = body.getString("groupName");
        String comment = body.getString("comment");
        if (groupName == null || comment == null) {
            return ReturnResult.returnTipMessage(0, "请求参数不完整!");
        } else if (!groupName.matches(REG_1_Z)) {
            return ReturnResult.returnTipMessage(0, "设备组必须用英文或者数字组合且不下6位!");
        } else if (deviceGroupService.getADeviceGroupByName(groupName) == null) {
            AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            DeviceGroup deviceGroup = new DeviceGroup();
            deviceGroup.setGroupName(groupName);
            deviceGroup.setComment(comment);
            deviceGroup.setAppUser(appUser);
            deviceGroupService.save(deviceGroup);
            return ReturnResult.returnTipMessage(1, "分组增加成功!");
        } else {
            return ReturnResult.returnTipMessage(0, "分组名称已经存在!");

        }
    }


    /**
     * 改变设备的分组
     *
     * @return
     */
    @RequestMapping(value = "/changeDeviceGroup", method = RequestMethod.POST)
    public JSONObject changeDeviceGroup(@RequestBody JSONObject body) {
        Long deviceId = body.getLongValue("deviceId");
        String deviceName = body.getString("deviceName");
        String deviceDescribe = body.getString("deviceDescribe");
        String locationDescribe = body.getString("locationDescribe");
        String groupName = body.getString("groupName");
        System.out.println(groupName);
        System.out.println(deviceId);
        if (groupName == null || deviceId == null) {
            return ReturnResult.returnTipMessage(0, "请求参数不完整!");
        } else if (!groupName.matches(REG_1_Z)) {
            return ReturnResult.returnTipMessage(0, "设备组必须用英文或者数字组合且不下6位!");

        } else {
            Device device = deviceService.findADevice(deviceId);
            DeviceGroup deviceGroup = deviceGroupService.findADeviceGroupByName(groupName);
            if (device != null) {
                if (deviceGroup != null) {
                    AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    DeviceGroup group = new DeviceGroup();
                    Location location = new Location();
                    group.setGroupName(groupName);
                    group.setAppUser(appUser);
                    deviceGroupService.save(group);
                    device.setDeviceGroup(deviceGroup);
                    location.setLocationDescribe(locationDescribe);
                    device.setDeviceDescribe(deviceDescribe);
                    device.setDeviceName(deviceName);
                    device.setLocation(location);

                    deviceService.save(device);
                    return ReturnResult.returnTipMessage(1, "分配新分组成功!");
                } else {
                    return ReturnResult.returnTipMessage(0, "分组不存在");

                }
            } else {
                return ReturnResult.returnTipMessage(0, "设备不存在!");
            }
        }


    }

    /**
     * 获取所有分组
     *
     * @return
     */

    @RequestMapping(value = "/getALlGroups", method = RequestMethod.GET)
    public JSONObject getALlGroups() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        JSONObject returnJson = new JSONObject();
        returnJson.put("state", 1);
        returnJson.put("message", "查询成功!");
        returnJson.put("data", deviceGroupService.getAllByAppUser(appUser));
        return returnJson;

    }

    /**
     * 分页获取分组
     */
    @RequestMapping(value = "/getAllGroupByPage/{page}/{size}", method = RequestMethod.GET)
    public JSONObject getAllGroupByPage(@PathVariable int page, @PathVariable int size) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ReturnResult.returnDataMessage(1, "获取成功!",
                deviceGroupService.getAllDeviceGroupByPage(appUser, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"))));

    }


    /**
     * 用户获取所有的设备
     *
     * @return
     */
    @RequestMapping(value = "/getAllDevices/{page}/{size}", method = RequestMethod.GET)
    public JSONObject getAllDevices(@PathVariable int page, @PathVariable int size) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ReturnResult.returnDataMessage(1, "查询成功!", deviceService.getAllDevicesByAppUser(appUser, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"))));

    }

    /**
     * 当前登陆用户根据分组ID查询所有的设备
     *
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "/getAllDevicesByGroup/{groupId}/{page}/{size}", method = RequestMethod.GET)
    public JSONObject getAllDevicesByGroup(@PathVariable Long groupId, @PathVariable int page, @PathVariable int size) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DeviceGroup group = deviceGroupService.findADeviceGroupById(groupId);
        return ReturnResult.returnDataMessage(1, "查询成功!", deviceService.getAllDevicesByAppUserAndGroup(appUser, group, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"))));


    }

    /**
     * 用户查看自己的设备状况
     * 1 设备量
     * 2 在线数目
     * 3 离线数目
     */
    @RequestMapping(value = "/getCurrentState", method = RequestMethod.GET)
    public JSONObject getCurrentState() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ReturnResult.returnDataMessage(1, "查询成功!", deviceService.getCurrentState(appUser));
    }

    /**
     * 修改分组备注
     */
    @RequestMapping(value = "/updateGroup", method = RequestMethod.POST)
    public JSONObject updateGroup(@RequestBody JSONObject body) {
        Long groupId = body.getLongValue("groupId");
        String groupName = body.getString("groupName");
        String comment = body.getString("comment");
        if (groupId == null || groupName == null || comment == null) {
            return ReturnResult.returnTipMessage(0, "请求参数不完整!");
        } else if (!groupName.matches(REG_1_Z)) {
            return ReturnResult.returnTipMessage(0, "设备组必须用英文或者数字组合且不下6位!");
        } else {
            AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            DeviceGroup deviceGroup = deviceGroupService.findADeviceGroupById(groupId);
            if ((deviceGroup != null) && (deviceGroup.getAppUser().getId().longValue() == appUser.getId().longValue())) {
                deviceGroup.setGroupName(groupName);
                deviceGroup.setComment(comment);
                deviceGroupService.save(deviceGroup);
                return ReturnResult.returnTipMessage(1, "修改成功!");
            }
            {
                return ReturnResult.returnTipMessage(0, "分组不存在!");
            }

        }

    }


    /**
     * 增加一个设备
     *
     * @param deviceBody 包含设备信息的JSON
     * @return
     */

    @RequestMapping("/createADevice")
    public JSONObject createADevice(@RequestBody JSONObject deviceBody) {
        String deviceName = deviceBody.getString("deviceName");
        String deviceDescribe = deviceBody.getString("deviceDescribe");
        String latitude = deviceBody.getString("latitude");
        String longitude = deviceBody.getString("longitude");
        String locationDescribe = deviceBody.getString("locationDescribe");
        Long groupId = deviceBody.getLong("groupId");
        if (deviceDescribe == null || deviceName == null ||

                groupId == null || latitude == null || longitude == null || locationDescribe == null

                ) {
            return ReturnResult.returnTipMessage(0, "参数不全!");
        }
        DeviceGroup deviceGroup = deviceGroupService.findADeviceGroupById(groupId.longValue());
        if (deviceGroup == null) {
            return ReturnResult.returnTipMessage(0, "分组不存在!");
        } else {
            AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Device device = new Device();
            device.setDeviceGroup(deviceGroup);
            device.setAppUser(appUser);
            device.setLastActiveDate(new Date());
            device.setDeviceName(deviceName);
            device.setDeviceDescribe(deviceDescribe);
            device.setClientId(device.getId().toString());
            device.setSecretKey(appUser.getId() + "-" + deviceGroup.getId() + "-" + device.getId());
            device.setTopic("IN/DEVICE/" + appUser.getId() + "/" + deviceGroup.getId() + "/" + device.getId());
            device.setBarCode(Image2Base64Tool.imageToBase64String(QRCodeGenerator.string2BarCode(device.getId().toString())));
            device.setOpenId(device.getId().toString());

            deviceService.save(device);

            Location location = new Location();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setLocationDescribe(locationDescribe);
            location.setDevice(device);
            locationService.save(location);//先保存位置
            device.setLocation(location);

            deviceService.save(device);


            return ReturnResult.returnTipMessage(1, "设备创建成功!");
        }
    }

    /**
     * 删除设备
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public JSONObject delete(@PathVariable("id") Long id) {
        Device device = deviceService.findADevice(id);
        if (device != null) {
            deviceService.delete(device);
            return ReturnResult.returnTipMessage(1, "设备删除成功!");

        } else {
            return ReturnResult.returnTipMessage(0, "设备不存在!");
        }
    }


    /**
     * 当前用户关键字搜索
     */
    @RequestMapping(value = "/searchByAppUser/{keyWords}", method = RequestMethod.POST)
    public JSONObject searchByAppUser(@PathVariable String keyWords) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (keyWords != null) {
            return ReturnResult.returnDataMessage(1, "查询成功!", deviceService.searchByAppUser(keyWords, appUser));

        } else {
            return ReturnResult.returnTipMessage(0, "查询参数不完整!");
        }

    }

    /**
     * 分页获取当前用户的在线设备信息
     *
     * @return
     */
    @RequestMapping(value = "/getAllOnlineDevicesByAppUser/{page}/{size}", method = RequestMethod.GET)
    public JSONObject getAllOnlineDevicesByAppUser(@PathVariable int page, @PathVariable int size) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ReturnResult.returnDataMessage(1, "查询成功!", deviceService.getAllOnlineDevicesByAppUser(appUser, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"))));
    }


    /**
     * 添加一个定时任务
     */
    @RequestMapping(value = "/addJob", method = RequestMethod.POST)

    public JSONObject addJob(@RequestBody JSONObject jobBody) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String status = jobBody.getString("status");
//        String name = jobBody.getString("name");
//        String group = jobBody.getString("group");
//        String description = jobBody.getString("description");
//        String className = jobBody.getString("className");
        String cronExpression = jobBody.getString("cronExpression");

        JSONObject jobJson = jobBody.getJSONObject("jobJson");
        System.out.println(jobJson.toJSONString());
        Long deviceId = jobBody.getLongValue("deviceId");

        if (cronExpression == null || deviceId == null || jobJson == null) {
            return ReturnResult.returnTipMessage(0, "参数缺少!");
        } else {
            Device device = deviceService.findADevice(deviceId);
            //惯例,检查设备是否存在
            if (device != null) {
                //检查是否该设备已经绑定了任务
                if (deviceJobService.findAJobByDevice(device) != null) {
                    return ReturnResult.returnTipMessage(0, "设备已经绑定任务!");
                } else {
                    DeviceJob scheduleJob = new DeviceJob();
                    scheduleJob.setDevice(device);
                    scheduleJob.setCronExpression(cronExpression);
                    scheduleJob.setJobJson(jobJson.toJSONString());
                    scheduleJob.setAppUser(appUser);
                    scheduleJob.setState("1");
                    try {
                        addJob(scheduleJob);
                        deviceJobService.save(scheduleJob);
                        return ReturnResult.returnTipMessage(1, "任务添加成功!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e instanceof RuntimeException) {
                            return ReturnResult.returnTipMessage(0, "CRON表达式格式错误!");
                        }
                        return ReturnResult.returnTipMessage(0, "任务添加失败!");
                    }
                }


            } else {
                return ReturnResult.returnTipMessage(0, "设备不存在!");
            }
        }


    }


    /**
     * 停止一个定时任务
     */
    @RequestMapping(value = "/stopJob/{deviceId}", method = RequestMethod.GET)
    public JSONObject stopJob(@PathVariable("deviceId") Long deviceId) {
        Device device = deviceService.findADevice(deviceId);
        //惯例,检查设备是否存在
        if (device != null) {
            //检查是否该设备已经绑定了任务
            DeviceJob deviceJob = deviceJobService.findAJobByDevice(device);
            if (deviceJob == null) {
                return ReturnResult.returnTipMessage(0, "设备没有绑定任务!");
            } else {
                try {
                    if (deviceJob.getState().equals("1")) {
                        pauseJob(deviceJob);
                        deviceJob.setState("0");
                        deviceJobService.save(deviceJob);
                        return ReturnResult.returnTipMessage(1, "设备任务取消成功!");
                    } else {
                        pauseJob(deviceJob);
                        deviceJob.setState("1");
                        deviceJobService.save(deviceJob);
                        return ReturnResult.returnTipMessage(1, "设备任务恢复成功!");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return ReturnResult.returnTipMessage(0, "设备任务取消失败!");
                }
            }
        } else {
            return ReturnResult.returnTipMessage(0, "设备不存在!");
        }

    }

    /**
     * 删除一个设备任务
     */
    @RequestMapping(value = "/deleteJob/{deviceId}", method = RequestMethod.DELETE)
    public JSONObject deleteDeviceJob(@PathVariable("deviceId") Long id) {
        Device device = deviceService.findADevice(id);
        if (device != null) {
            //检查是否该设备已经绑定了任务
            DeviceJob deviceJob = deviceJobService.findAJobByDevice(device);
            if (deviceJob == null) {
                return ReturnResult.returnTipMessage(0, "设备没有绑定任务!");
            } else
                try {
                    deleteJob(deviceJob);
                    deviceJobService.delete(deviceJob);
                    return ReturnResult.returnTipMessage(1, "设备任务删除成功!");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return ReturnResult.returnTipMessage(0, "设备任务取消失败!");
                }
        } else {
            return ReturnResult.returnTipMessage(0, "设备不存在!");
        }
    }

    /**
     * 查询用户所有的定时任务
     * <p>
     * 分页获取设备的报警信息
     *
     * @param page
     * @param size
     * @return
     */


    @RequestMapping(value = "/getAllJobByAppUser/{page}/{size}", method = RequestMethod.GET)

    public JSONObject getAllJobByAppUser(@PathVariable int page, @PathVariable int size) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ReturnResult.returnDataMessage(1, "查询成功!", deviceJobService.getAllJobByAppUser(appUser, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"))));
    }


    /**
     * 给设备增加要给定时任务
     *
     * @param scheduleJob
     * @throws Exception
     */

    public void addJob(DeviceJob scheduleJob) throws Exception {
        if (scheduler.isShutdown()) scheduler.start();
        JobDetail jobDetail = JobBuilder.newJob(ScheduleSendMessageJob.class)
                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup()).build();
        jobDetail.getJobDataMap().put("topic", scheduleJob.getDevice().getTopic().replace("IN", "OUT"));
        //表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(scheduleJob.getId().toString(), scheduleJob.getJobGroup())
                .withSchedule(scheduleBuilder).build();
        trigger.getJobDataMap().put("cron", scheduleJob.getCronExpression());
        trigger.getJobDataMap().put("jobJson", scheduleJob.getJobJson());
        trigger.getJobDataMap().put("apiHost", apiHost);
        trigger.getJobDataMap().put("deviceId", scheduleJob.getDevice().getId());
        scheduler.scheduleJob(jobDetail, trigger);
        logger.info("添加新JOB:" + scheduleJob.getId().toString());

    }

    /**
     * 删除定时任务
     *
     * @param deviceJob
     * @throws Exception
     */
    public void deleteJob(DeviceJob deviceJob) throws Exception {
        //通过ID来删除
        scheduler.deleteJob(JobKey.jobKey(deviceJob.getId().toString(), "JOB_GROUP"));
        logger.info("删除JOB:" + deviceJob.getId().toString());
    }

    /**
     * 暂停定时任务
     *
     * @param deviceJob
     * @throws Exception
     */
    public void pauseJob(DeviceJob deviceJob) throws Exception {
        //通过ID来暂停
        if (deviceJob.getState().equals("0")) {
            resumeJob(deviceJob);
        } else {
            scheduler.pauseJob(JobKey.jobKey(deviceJob.getId().toString(), "JOB_GROUP"));
            logger.info("暂停JOB:" + deviceJob.getId().toString());
        }
    }

    /**
     * 恢复定时任务
     *
     * @param deviceJob
     * @throws Exception
     */
    public void resumeJob(DeviceJob deviceJob) throws Exception {
        //通过ID来恢复
        scheduler.resumeJob(JobKey.jobKey(deviceJob.getId().toString(), "JOB_GROUP"));
        logger.info("恢复JOB:" + deviceJob.getId().toString());
    }

    /**
     * 根据设备ID  获取直播地址
     * @param deviceId
     * @param liveType
     * @return
     */
    @RequestMapping(value = "/getLiveUrlByDevice/{deviceId}/{liveType}", method = RequestMethod.GET)
    public JSONObject getLiveUrlByDevice(@PathVariable Long deviceId, @PathVariable String liveType) {
        Device device = deviceService.findADevice(deviceId);
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (device != null && device.getAppUser().getId().longValue() == appUser.getId().longValue()) {
            return ReturnResult.returnDataMessage(1, "获取成功!", deviceService.getLiveUrl(device.getSecretKey(), liveType));
        }
        return ReturnResult.returnTipMessage(0, "获取播放地址失败!");
    }

}

