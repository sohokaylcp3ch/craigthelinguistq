package com.easylinker.proxy.server.app.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.upyunconfig.UpYunConfig;
import com.easylinker.proxy.server.app.interfaces.InterceptorPlugin;
import com.easylinker.proxy.server.app.model.user.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class EmailSender {
    Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailUsername;

    /**
     * spring.mail.host=smtp.163.com
     * spring.mail.username=18059150204@163.com
     * spring.mail.port=25
     * spring.mail.password=easylinkerauto1
     */
    @Value("${spring.mail.host}")
    String mailHost;
    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.password}")
    String password;


    public void sendHtmlMail(AppUser appUser) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mailUsername);
        helper.setTo(appUser.getEmail());
        helper.setSubject("激活账户");
        helper.setText(getSMSTemplate(appUser).toString(), true);
        mailSender.send(message);


    }


    /**
     * 解析配置文件
     *
     * @return
     */

    public String getSMSTemplate(AppUser appUser) throws Exception{

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream("/sms_template.json")));
            StringBuffer pluginConfigJsonStringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                pluginConfigJsonStringBuffer.append(line);
            }
            bufferedReader.close();

            JSONObject smsTemplateJson = JSONObject.parseObject(pluginConfigJsonStringBuffer.toString());
            JSONArray contentJsonArray = smsTemplateJson.getJSONArray("content");
            String title = smsTemplateJson.getString("title");
            String group = smsTemplateJson.getString("group");
            String url = smsTemplateJson.getString("url");

            BufferedReader htmlBufferedReader = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream("/html_mail_template.html")));
            StringBuffer htmlBuffer = new StringBuffer();
            String html;
            while ((html = htmlBufferedReader.readLine()) != null) {
                htmlBuffer.append(html);
            }
            return htmlBuffer.toString().replace("${title}", title).replace("${group}", group).replace("${url}", url + "/" + appUser.getId());

    }

}
