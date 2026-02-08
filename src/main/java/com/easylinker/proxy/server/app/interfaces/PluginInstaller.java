package com.easylinker.proxy.server.app.interfaces;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.web.servlet.filter.ApplicationContextHeaderFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件安装器
 */
@Component
public class PluginInstaller extends ApplicationContextHeaderFilter implements WebMvcConfigurer {


    public PluginInstaller(org.springframework.context.ApplicationContext context) {
        super(context);


    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        for (InterceptorPlugin interceptorPlugin : LoadPlugin()) {


        }

    }

    public List<InterceptorPlugin> LoadPlugin() {
        List<InterceptorPlugin> interceptorPluginList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream("/plugin.json")));
            StringBuffer pluginConfigJsonStringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                pluginConfigJsonStringBuffer.append(line);
            }
            bufferedReader.close();
            JSONObject pluginConfigJson = JSONObject.parseObject(pluginConfigJsonStringBuffer.toString());
            JSONArray pluginsJsonArray = pluginConfigJson.getJSONArray("plugins");
            for (Object plugin : pluginsJsonArray) {
                Class<InterceptorPlugin> interceptorPlugin = (Class<InterceptorPlugin>) Class.forName(((JSONObject) plugin).getString("package"));

                System.out.println(interceptorPlugin.getName());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("插件加载数目:" + interceptorPluginList.size());


        return interceptorPluginList;
    }
}
