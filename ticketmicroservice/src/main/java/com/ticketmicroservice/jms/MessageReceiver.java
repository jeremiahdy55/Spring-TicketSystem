package com.ticketmicroservice.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    @Autowired
    MessageSender messageSender;

    @JmsListener(destination = "queue.ticketMS")
    public void receive(String message) {
        System.out.println(message);
        if (message.contains("check tickets' statuses")) {
            // Check "PENDING/OPEN" tickets for emailing
            // Check RESOLVED tickets for auto-close
            System.out.println("checking ticket statuses");
        }
        // messageSender.sendToNotificationMicroservice("sending back:" + message);
    }
}