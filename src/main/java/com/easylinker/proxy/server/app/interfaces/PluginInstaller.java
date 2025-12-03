package com.easylinker.proxy.server.app.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件安装器
 */
public class PluginInstaller {
    private List<EasyPlugin> pluginList;

    public PluginInstaller() {
        pluginList = new ArrayList<>();

    }

    public void install(EasyPlugin easyPlugin) {
        pluginList.add(easyPlugin);

    }
}
