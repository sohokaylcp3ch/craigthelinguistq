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
import java.util.Base64;

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
    public void sendActiveMail(AppUser appUser) throws Exception {

        sendHtmlMail(username, appUser.getEmail(), "激活账户", getActiveHTMLMailTemplate(appUser));
    }

    /**
     * 找回密码
     * 这里发送一个邮件
     * D:\github\EasyLinker\src\main\resources\html_active_user_template.html
     *
     * @param
     * @throws Exception
     */
    public void sendForgetPasswordMail(AppUser appUser) throws Exception {
        sendHtmlMail(username, appUser.getEmail(), "找回密码", getForgetPasswordHTMLMailTemplate(appUser));
    }


    /**
     * 构造激活用户的邮件
     *
     * @return
     */

    private String getActiveHTMLMailTemplate(AppUser appUser) throws Exception {

        return readTemplate("/html_active_user_template.html").toString()
                .replace("${url}", appUser.getId().toString());

    }

    /**
     * 构造忘记密码的邮件
     *
     * @param appUser
     * @return
     * @throws Exception
     */
    private String getForgetPasswordHTMLMailTemplate(AppUser appUser) throws Exception {

        return readTemplate("/html_forget_password_template.html").toString()
                .replace("${emailBase64}", Base64.getEncoder().encodeToString(appUser.getEmail().getBytes()));

    }

    /**
     * 读取文件内容
     *
     * @param path
     * @return
     * @throws Exception
     */
    private String readTemplate(String path) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/mail_template.json")));
        StringBuffer content = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line);
        }
        bufferedReader.close();
        return content.toString();
    }

}
