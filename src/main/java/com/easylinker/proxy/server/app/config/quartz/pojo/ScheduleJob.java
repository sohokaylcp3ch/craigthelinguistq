package com.easylinker.proxy.server.app.config.quartz.pojo;


import com.easylinker.proxy.server.app.model.base.BaseEntity;
import com.easylinker.proxy.server.app.model.device.Device;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
public class ScheduleJob extends BaseEntity implements Serializable {

    private String name;    //任务名
    private String group;    //任务组
    private String cronExpression;    //cron表达式
    private String status;    //状态
    private String description;    //描述
    private String className;    //要执行的任务类路径名


    @OneToOne(targetEntity = Device.class, fetch = FetchType.LAZY)

    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public ScheduleJob() {
        super();
    }


    public ScheduleJob(String name, String group, String cronExpression,
                       String status, String description, String className) {
        super();
        this.name = name;
        this.group = group;
        this.cronExpression = cronExpression;
        this.status = status;
        this.description = description;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}

