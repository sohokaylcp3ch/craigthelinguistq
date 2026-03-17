package com.easylinker.proxy.server.app.config.upyunconfig;

import com.easylinker.proxy.server.app.EasyLinkerApplication;
import main.java.com.UpYun;
import main.java.com.upyun.UpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * 又拍云的配置
 */
@Configuration
public class UpYunConfig {
    Logger logger = LoggerFactory.getLogger(UpYunConfig.class);

    @Value("${upyun.account.username}")
    String username;
    @Value("${upyun.account.password}")
    String password;
    @Value("${upyun.account.apiKey}")
    String apiKey;
    @Value("${upyun.account.bucketname}")
    String bucketName;

    @Bean
    public UpYun configUpYun() {
        if (username == null || password == null || apiKey == null || bucketName == null) {
            logger.warn("又拍云配置失败!请检查,否则无法使用数据导出功能!");
        }
        UpYun upyun = new UpYun(bucketName, username, password);

        upyun.setApiDomain(UpYun.ED_AUTO);
        return upyun;
    }

}
