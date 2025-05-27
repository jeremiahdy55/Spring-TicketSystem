package com.notificationmicroservice.jobs;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.notificationmicroservice.jms.MessageSender;

public class CheckTicketsJob implements Job {

    @Autowired
    MessageSender messageSender;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String localTime = LocalTime.now().format(formatter); // Local system time
        String message = localTime + ": check tickets' statuses";
        try {
            messageSender.sendToTicketMicroservice(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}