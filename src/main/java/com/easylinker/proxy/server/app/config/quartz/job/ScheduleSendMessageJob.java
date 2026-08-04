package com.easylinker.proxy.server.app.config.quartz.job;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.quartz.pojo.BaseJob;
import com.easylinker.proxy.server.app.constants.result.ReturnResult;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.utils.HttpTool;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 定时任务具体业务接口
 * 要实现业务可以在这里加入逻辑
 * 比如现在实现的是:定时发送消息给设备
 * context.getJobDetail().getJobDataMap().getLongValue("deviceId"):获取设备的ID
 * context.getTrigger().getJobDataMap().getString("jobJson"):获取发送的JSON数据
 * 下面可以直发送了
 */
@DisallowConcurrentExecution
@Component
public class ScheduleSendMessageJob extends BaseJob {

    HttpTool httpTool;
    String apiHost;

    ScheduleSendMessageJob() {
        //默认构造方法

    }

    ScheduleSendMessageJob(String apiHost, HttpTool httpTool) {
        this.httpTool = httpTool;
        this.apiHost = apiHost;

    }


    public void action(JobExecutionContext context) {
        System.out.println("定时任务:" + context.getJobDetail().getKey().getName());
        System.out.println("定时任务Cron表达式:" + context.getTrigger().getJobDataMap().getString("cron"));
        System.out.println("准备给设备发送数据,目标设备:" + context.getJobDetail().getJobDataMap().getLongValue("deviceId"));
        System.out.println("准备给设备发送数据:" + context.getTrigger().getJobDataMap().getString("jobJson"));


        JSONObject cmd = new JSONObject();
        cmd.put("topic", "OUT/DEVICE/" + context.getJobDetail().getJobDataMap().getLongValue("deviceId"));
        cmd.put("payload", context.getTrigger().getJobDataMap().getString("jobJson"));//这里注意：必须是String类型的
        cmd.put("qos", 1);
        cmd.put("retain", false);
        cmd.put("client_id", "Schedule_job_sender");
        try {
            //给设备发送数据

            //httpTool.postWithAuthorization(context.getTrigger().getJobDataMap().getString("apiHost") + "mqtt/publish", cmd);

            System.out.println(cmd.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("定时消息出错了!" + e.getMessage());


        }


    }
}


