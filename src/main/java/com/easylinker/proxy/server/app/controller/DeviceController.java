package com.easylinker.proxy.server.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.constants.result.ReturnResult;
import com.easylinker.proxy.server.app.model.user.AppUser;
import com.easylinker.proxy.server.app.service.DeviceDataService;
import com.easylinker.proxy.server.app.service.DeviceGroupService;
import com.easylinker.proxy.server.app.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备操作业务逻辑
 */
@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    DeviceService deviceService;
    @Autowired
    DeviceDataService deviceDataService;
    @Autowired
    DeviceGroupService deviceGroupService;

    /**
     * 获取单个设备的细节
     *
     * @return
     */
    @RequestMapping(value = "/getDeviceDetail/{deviceId}", method = RequestMethod.GET)
    public JSONObject getDeviceDetail(@PathVariable Long deviceId) {

        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Device device = deviceService.findADevice(deviceId);
        if (device != null && device.getAppUser().getId().longValue() == appUser.getId().longValue()) {
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("id", device.getId());
            deviceJson.put("barCode", device.getBarCode());
            deviceJson.put("isOnline", device.isOnline());
            deviceJson.put("openId", device.getOpenId());
            deviceJson.put("name", device.getDeviceName());
            deviceJson.put("describe", device.getDeviceDescribe());
            deviceJson.put("location", device.getLocation().getLocationDescribe());
            deviceJson.put("lastActiveDate", device.getLastActiveDate());
            return ReturnResult.returnDataMessage(1, "查询成功!", deviceJson);
        } else {
            return ReturnResult.returnTipMessage(0, "设备不存在!");
        }


    }

    /**
     * 获取设备产生的数据
     *
     * @return
     */
    @RequestMapping(value = "/getDeviceData/{deviceId}/{page}/{size}", method = RequestMethod.GET)
    public JSONObject getDeviceData(@PathVariable Long deviceId, @PathVariable int page, @PathVariable int size) {
        Device device = deviceService.findADevice(deviceId);
        if (device != null) {
            return ReturnResult.returnDataMessage(1, "查询成功!", deviceDataService.getAllDeviceDataByDevice(device, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))));

        } else {
            return ReturnResult.returnTipMessage(0, "设备不存在!");
        }

    }

//    /**
//     * 分页获取分组
//     */
//    @RequestMapping(value = "/getAllDeviceGroupByPage/{page}/{size}", method = RequestMethod.GET)
//    public JSONObject getAllDeviceGroupByPage(@PathVariable int page, @PathVariable int size) {
//        return ReturnResult.returnDataMessage(1, "获取成功!", deviceGroupService.getAllDeviceGroupByPage(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))));
//
//    }

}
