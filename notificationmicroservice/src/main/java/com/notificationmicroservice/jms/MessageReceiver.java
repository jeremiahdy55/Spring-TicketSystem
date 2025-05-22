package com.notificationmicroservice.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    @JmsListener(destination = "queue.notificationMS")
    public void receive(String message) {
        System.out.println("notification microservice received: " + message);
    }
}