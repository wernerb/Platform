/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.config;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.alliander.osgp.core.application.tasks.ScheduledTaskScheduler;

@Configuration
@EnableScheduling
@PropertySource("file:${osp/osgpCore/config}")
public class SchedulingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingConfig.class);

    private static final String PROPERTY_NAME_SCHEDULING_SCHEDULED_TASKS_CRON_EXPRESSION = "scheduling.scheduled.tasks.cron.expression";
    private static final String PROPERTY_NAME_SCHEDULING_TASK_SCHEDULER_POOL_SIZE = "scheduling.task.scheduler.pool.size";
    private static final String PROPERTY_NAME_SCHEDULING_TASK_SCHEDULER_THREAD_NAME_PREFIX = "scheduling.task.scheduler.thread.name.prefix";

    @Resource
    private Environment environment;

    @Autowired
    private ScheduledTaskScheduler scheduledTaskScheduler;

    @Bean
    public CronTrigger scheduledTasksCronTrigger() {
        final String cron = this.environment
                .getRequiredProperty(PROPERTY_NAME_SCHEDULING_SCHEDULED_TASKS_CRON_EXPRESSION);
        return new CronTrigger(cron);
    }

    @Bean(destroyMethod = "shutdown")
    public TaskScheduler taskScheduler() {
        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_SCHEDULING_TASK_SCHEDULER_POOL_SIZE)));
        taskScheduler.setThreadNamePrefix(this.environment
                .getRequiredProperty(PROPERTY_NAME_SCHEDULING_TASK_SCHEDULER_THREAD_NAME_PREFIX));
        taskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        taskScheduler.setAwaitTerminationSeconds(10);
        taskScheduler.initialize();
        taskScheduler.schedule(this.scheduledTaskScheduler, this.scheduledTasksCronTrigger());
        return taskScheduler;
    }
}
