package com.easylinker.proxy.server.app.config.mqttconfig.handler;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.HistoryLocation;
import com.easylinker.proxy.server.app.model.device.Location;
import com.easylinker.proxy.server.app.service.DeviceLocationService;
import com.easylinker.proxy.server.app.service.DeviceService;
import com.easylinker.proxy.server.app.service.HistoryLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * 这个类是，设备更新地理位置的时候，接受消息的
 * 比如：
 * 100号设备，send->la:120`120``,lo:90`0``
 * 然后在这里更新设备的位置
 * [payload={'la': 120.0, 'lo': 39.0},
 * headers={mqtt_retained=false, mqtt_qos=0,
 * id=b710ae92-6b1a-564b-f950-09d6abbf7eec,
 * mqtt_topic=IN/LOCATION/1527344358402,
 * mqtt_duplicate=false,
 * timestamp=1532795493886}]
 */
@Component
public class LocationMessageHandler implements MessageHandler {
    Logger logger = LoggerFactory.getLogger(LocationMessageHandler.class);
    @Autowired
    DeviceService deviceService;
    @Autowired
    DeviceLocationService deviceLocationService;
    @Autowired
    HistoryLocationService historyLocationService;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {

        String topic = message.getHeaders().get("mqtt_topic").toString();
        try {
            if (topic.startsWith("IN/LOCATION/")) {
                logger.info("设备:"+topic.split("/")[2]+" 位置发生变化");
                Long openId = Long.parseLong(topic.split("/")[2]);
                Device device = deviceService.findADevice(openId);
                if (device != null) {
                    JSONObject locationInfoJson = JSONObject.parseObject(message.getPayload().toString());
                    String latitude = locationInfoJson.getString("latitude");
                    String longitude = locationInfoJson.getString("longitude");
                    String locationDescribe = locationInfoJson.getString("locationDescribe");
                    String mode=locationInfoJson.getString("mode");
                    if (latitude!=null && longitude!=null && locationDescribe!=null){
                        //生成一个历史地理位置
                        Location location = device.getLocation();
                        //只有消息带上持久化的标记的时候才创建历史位置，否则就是仅仅更新
                        //persistent
                        //待定
                        if (mode.equalsIgnoreCase("persistent")){
                            HistoryLocation historyLocation = new HistoryLocation();
                            historyLocation.setLatitude(location.getLatitude());
                            historyLocation.setLocationDescribe(location.getLocationDescribe());
                            historyLocation.setLongitude(location.getLongitude());
                            historyLocation.setDevice(device);
                            historyLocationService.save(historyLocation);
                        }


                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                        location.setLocationDescribe(locationDescribe);
                        deviceLocationService.save(location);

                        deviceService.save(device);
                    }else {
                        logger.error("地理位置参数不全!");
                    }

                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("消息格式解析出错!", e.getMessage());
        }


    }
}
