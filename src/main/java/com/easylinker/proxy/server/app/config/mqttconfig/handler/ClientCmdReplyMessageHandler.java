package com.easylinker.proxy.server.app.config.mqttconfig.handler;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.mqttconfig.MqttMessageSender;
import com.easylinker.proxy.server.app.utils.HttpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * IN/CMD
 * 给客户端发送指定的指令，然后返回  在这里处理，暂时打印
 * send -> getInfo
 * reply-> name:rpi,date:2018......
 * 原理:  设备向IN方向echo进来数据,然后,转发到OUT,前端的JS mqtt订阅以后,就可以获取内容了
 */
@Component
public class ClientCmdReplyMessageHandler implements MessageHandler {
    @Autowired
    MqttMessageSender mqttMessageSender;
    @Autowired
    HttpTool httpTool;
    @Value("${emq.api.host}")
    String apiHost;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        System.out.println("收到客户端回复命令:" + message.getPayload());
        //消息转发
        try {

            JSONObject messageJson = new JSONObject();
            messageJson.put("topic", "OUT/REAL_TIME/" + message.getHeaders().get("mqtt_topic", String.class).split("/")[2]);
            messageJson.put("payload", message.getPayload().toString());
            messageJson.put("retain", false);
            messageJson.put("qos", 1);
            messageJson.put("client_id", "SERVER_PROXY");
            httpTool.postWithAuthorization(apiHost + "mqtt/publish", messageJson);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
