package com.easylinker.proxy.server.app.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;

import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by ruilin on 2018/9/1.
 */
@Component
public class AliLiveUtil {
    @Value("${ali.accesskey}")
    String accessKeyId;
    @Value("${ali.secret}")
    String accessSecret;
    @Value("${ali.live.basedomain}")
    String baseDomain;
    @Value("${ali.live.pushdomain}")
    String pushDomain;
    @Value("${ali.live.privatekey}")
    String privateKey;

    @Value("${ali.app.name}")
    String appName;
    @Value("${ali.app.secret.key}")
    String secretKey;

    private static IAcsClient client = null;

//    public static void main(String[] args) {
//        AliLiveUtil aliLiveUtil =new AliLiveUtil();
//        aliLiveUtil.getLiveUrl("test", "stream", "rtmp");
//
//    }
    /**
     * 初始化Client
     * SDK通过IAcsClient的instance来完成openAPI的调用，因此在您发起调用前，请先初始化IAcsClient实例。示例代码如下：
     *
     * @throws ClientException
     */
    public void init() {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
        //DefaultProfile.addEndpoint("cn-shanghai", "cn-shanghai", "live", "live.aliyuncs.com"); //添加自定义endpoint。
        client = new DefaultAcsClient(profile);
        //System.setProperty("http.proxyHost", "127.0.0.1"); //此设置用于设置代理，可用fiddler拦截查看http请求，便于调试。
        //System.setProperty("http.proxyPort", "8888");
    }


    /**
     * 获取鉴权的播流地址
     * 参考文档:https://help.aliyun.com/document_detail/85018.html
     * @param type
     * @return
     */
    public String getLiveUrl(String deviceKey,String type) {
        ///{AppName}/{StreamName}-{timestamp}-{rand}-{uid}-{privatekey}
        String rand = "0";
        String uid = "0";
        Date date = new Date();
        String timeStamp = (date.getTime() + 60 * 60 * 1000) / 1000 + "";
        String liveFormat = "";
        switch (type) {
            case "rtmp":
                liveFormat = "/%s/%s-%s-%s-%s-%s";
                break;
            case "flv":
                liveFormat = "/%s/%s.flv-%s-%s-%s-%s";
                break;
            case "m3u8":
                liveFormat = "/%s/%s.m3u8-%s-%s-%s-%s";
                break;
            default:
        }
        String liveUrl = String.format(liveFormat, appName, deviceKey, timeStamp, rand, uid, privateKey);
        String liveRealFormat = "/%s/%s?auth_key=%s-%s-%s-%s";
        String liveRealUrl = baseDomain + String.format(liveRealFormat, appName, deviceKey, timeStamp, rand, uid, MD5Generator.EncodingMD5(liveUrl));
        //System.out.println(type + "直播地址：" + liveRealUrl);
        return liveRealUrl;
    }

    /**
     * 获取鉴权的推流地址
     * 参考文档:https://help.aliyun.com/document_detail/87396.html
     * @param appName
     * @param streamName
     * @return
     */
    public String getPushLiveUrl(String appName, String streamName) {
        String rand = "0";
        String uid = "0";
        Date date = new Date();
        String timeStamp = (date.getTime() + 60 * 60 * 1000) / 1000 + "";
        String pushLiveFormat = "/%s/%s-%s-%s-%s-%s";
        String pushLiveUrl = String.format(pushLiveFormat, appName, streamName, timeStamp, rand, uid, privateKey);
        String pushLiveRealFormat = "/%s/%s?auth_key=%s-%s-%s-%s";
        String pushLiveRealUrl = pushDomain + String.format(pushLiveRealFormat, appName, streamName, timeStamp, rand, uid, MD5Generator.EncodingMD5(pushLiveUrl));
        return pushLiveRealUrl;
    }

    // TODO: 2018/9/2 视频鉴权


}
