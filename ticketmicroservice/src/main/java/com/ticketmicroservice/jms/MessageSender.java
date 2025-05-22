package com.ticketmicroservice.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {
    private final JmsTemplate jmsTemplate;

    public MessageSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendToNotificationMicroservice(String message) {
        jmsTemplate.convertAndSend("queue.notificationMS", message);
    }
}