package com.easylinker.proxy.server.app;

import com.easylinker.proxy.server.app.constants.result.ReturnResult;
import com.easylinker.proxy.server.app.model.user.AppUser;
import com.easylinker.proxy.server.app.model.user.UserRole;
import com.easylinker.proxy.server.app.service.AppUserService;
import com.easylinker.proxy.server.app.service.UserRoleService;
import com.easylinker.proxy.server.app.utils.MD5Generator;
import com.sun.mail.smtp.SMTPAddressFailedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    @Autowired
    AppUserService appUserService;
    @Autowired
    UserRoleService userRoleService;

    @Test
    public void addDefaultAccount() throws Exception {

        if (appUserService.getAUserById(1L) == null) {
            AppUser appUser = new AppUser();
            appUser.setId(1L);
            appUser.setUsername("administrator");
            appUser.setPassword(MD5Generator.EncodingMD5("administrator"));
            appUser.setEmail("administrator@admin.com");
            appUser.setPhone("8888888888");
            appUserService.save(appUser);
            //普通用户的角色
            UserRole userRole = new UserRole();
            userRole.setAppUser(appUser);
            userRole.setRole("ROLE_USER");
            userRoleService.save(userRole);

            // 默认用户是管理员

            UserRole adminRole = new UserRole();
            adminRole.setAppUser(appUser);
            adminRole.setRole("ROLE_ADMIN");
            userRoleService.save(adminRole);

            System.out.println("默认用户创建成功.");
        }else {
            System.out.println("默认用户已存在.");
        }
        System.out.println("初始化工作完成.");


    }


}
