package com.notificationmicroservice.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {
    private final JmsTemplate jmsTemplate;

    public MessageSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendToTicketMicroservice(String message) {
        jmsTemplate.convertAndSend("queue.ticketMS", message);
    }
}