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
        LocalTime now = LocalTime.now(); // Local system time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = now.format(formatter);
        String message = "Checking ticket and sending JMS to ticketmicroservice at: " + time;
        System.out.println(message);
        messageSender.sendToTicketMicroservice(message);

        // System.out.println("Checking ticket");
    }
}