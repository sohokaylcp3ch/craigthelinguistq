package com.easylinker.proxy.server.app.interfaces;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 插件编写的时候，需要实现这个接口
 */
public interface InterceptorPlugin extends HandlerInterceptor{

    void init();

    void addSlot();

    void destory();

    void doFilter();
}
