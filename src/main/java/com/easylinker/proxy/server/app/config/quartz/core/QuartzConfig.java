package com.easylinker.proxy.server.app.config.quartz.core;

import com.easylinker.proxy.server.app.config.quartz.pojo.BaseJob;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;

/**
 * Spring boot 集成Quartz核心配置
 */
@Configuration
public class QuartzConfig {

    /**
     * 配置一个JOB任务工厂
     * @return
     */
    @Bean
    public JobFactory jobFactory() {
        return new SpringBeanJobFactory();
    }

    /**
     * 从配置文件加载Quartz的配置
     * @param dataSource
     * @param jobFactory
     * @return
     */

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(@Qualifier("dataSource") DataSource dataSource, JobFactory jobFactory) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);

        // 延时启动
        factory.setStartupDelay(20);
        factory.setJobFactory(jobFactory);
        factory.setApplicationContextSchedulerContextKey(BaseJob.APPLICATION_CONTEXT_KEY);

        // 加载quartz数据源配置
        factory.setConfigLocation(new ClassPathResource("quartz.properties"));
        factory.setDataSource(dataSource);
        // 自定义Job Factory，用于Spring注入
        factory.setJobFactory(jobFactory());
        factory.setSchedulerName("quartz-scheduler");
        return factory;
    }

}