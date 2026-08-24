package com.easylinker.proxy.server.app.config.quartz.job;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.quartz.pojo.BaseJob;
import com.easylinker.proxy.server.app.constants.result.ReturnResult;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.utils.HttpTool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * 定时任务具体业务接口
 * 要实现业务可以在这里加入逻辑

 */
@DisallowConcurrentExecution
public class ScheduleSendMessageJob extends BaseJob {


    public void action(JobExecutionContext context) {
        System.out.println("定时任务:" + context.getJobDetail().getKey().getName());
        System.out.println("定时任务Cron表达式:" + context.getTrigger().getJobDataMap().getString("cron"));
        System.out.println("准备给设备发送数据,目标设备:" + context.getJobDetail().getJobDataMap().getString("topic"));
        System.out.println("准备给设备发送数据:" + context.getTrigger().getJobDataMap().getString("jobJson"));


        JSONObject cmd = new JSONObject();
        cmd.put("topic", context.getJobDetail().getJobDataMap().getString("topic"));
        cmd.put("payload", context.getTrigger().getJobDataMap().getString("jobJson"));//这里注意：必须是String类型的
        cmd.put("qos", 1);
        cmd.put("retain", false);
        cmd.put("client_id", "Schedule_job_sender");
        try {
            //给设备发送数据

            postWithAuthorization(context.getTrigger().getJobDataMap().getString("apiHost") + "mqtt/publish", cmd);

            System.out.println("JSON消息内容:"+cmd.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("定时消息出错了!" + e.getMessage());


        }


    }
}


