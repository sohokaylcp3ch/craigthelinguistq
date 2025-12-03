package com.easylinker.proxy.server.app.interfaces;

/**
 * 插件编写的时候，需要实现这个接口
 */
public interface EasyPlugin {
    public void init();
    public void addSlot();
}
