package com.easylinker.proxy.server.app.config.quartz.pojo;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * job父类，包含一个抽象函方法，将实现推迟到具体的子类
 */
public abstract class BaseJob implements Job, Serializable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    public abstract void action(JobExecutionContext context);

    /**
     * 统计时间
     *
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) {
        this.action(context);
    }
}
