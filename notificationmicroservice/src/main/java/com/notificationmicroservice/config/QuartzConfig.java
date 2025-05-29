package com.notificationmicroservice.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.notificationmicroservice.jobs.CheckTicketsJob;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail checkTicketsJobDetail() {
        return JobBuilder.newJob(CheckTicketsJob.class)
                .withIdentity("NotifyManagerJobDetail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger checkTicketsTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                // .cronSchedule("0 * * * * ?"); //for testing purposes, refresh every minute
                .cronSchedule("0 0 8 ? * MON"); 

        return TriggerBuilder.newTrigger()
                .forJob(checkTicketsJobDetail())
                .withIdentity("NotifyManagerCronTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

}
