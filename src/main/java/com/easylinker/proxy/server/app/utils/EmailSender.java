package com.easylinker.proxy.server.app.utils;

import com.easylinker.proxy.server.app.config.upyunconfig.UpYunConfig;
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
import java.util.Base64;
import java.util.Date;

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

//    public EmailSender() {
////        if (mailHost == null || username == null || password == null) {
////            logger.warn("邮件服务器配置失败,请检查配置,否则无法使用邮件功能!");
////        }
//    }
//
//    public void sendTextMail(AppUser appUser) throws Exception {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(mailUsername);
//        message.setSentDate(new Date());
//        message.setTo(appUser.getEmail());
//        message.setSubject("[激活账户]");
//        message.setText("欢迎注册!\n请点击下方链接激活账户:http://localhost/user/active/" + Base64.getEncoder().encodeToString(appUser.getUsername().getBytes()));
//        mailSender.send(message);
//
//    }

    public void sendHtmlMail(AppUser appUser) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailUsername);
            helper.setTo(appUser.getEmail());
            helper.setSubject("激活账户");

            StringBuffer sb = new StringBuffer();
            sb.append("<h1>激活账户</h1>")
                    .append("<div>")
                    .append("<p>账户注册成功,请激活账户!</p>")
                    .append("<p>激活参数:activeCode,可以是username,或者email ,phone参数!</p>")
                    .append("<p><a href=\"#\">激活账户,默认路径:[http://host/user/activeUser/{activeCode}]可以根据自己的需求改.</a></div>")
                    .append("</div>");
            helper.setText(sb.toString(), true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("邮件发送失败:", e.getLocalizedMessage());
        }

    }

}
