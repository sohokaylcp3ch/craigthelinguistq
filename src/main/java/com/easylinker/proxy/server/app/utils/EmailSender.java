package com.easylinker.proxy.server.app.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.model.user.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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


    private void sendHtmlMail(String from, String to, String title, String content) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(title);
        helper.setText(content, true);
        mailSender.send(message);


    }

    /**
     * 发送激活邮件
     *
     * @param
     * @throws Exception
     */
    public void sendActiveMail(String to) throws Exception {

        sendHtmlMail(username, to, "激活账户","激活账户");
    }

    /**
     * 找回密码
     *
     * @param
     * @throws Exception
     */
    public void sendForgetPasswordMail(String to) throws Exception {
        sendHtmlMail(username, to, "激活账户","找回密码");
    }


    /**
     * 解析配置文件
     *
     * @return
     */

    public String getSMSTemplate(AppUser appUser) throws Exception {

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/mail_template.json")));
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
        return htmlBuffer.toString().replace("${title}", title).replace("${group}", group).replace("${url}", appUser.getId().toString());

    }

}
