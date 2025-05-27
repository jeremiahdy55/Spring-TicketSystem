package com.notificationmicroservice.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.notificationmicroservice.service.EmailService;

@Service
public class MessageSender {
    private final JmsTemplate jmsTemplate;

    @Autowired
    EmailService emailService;

    public MessageSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendToTicketMicroservice(String message) throws Exception {
        jmsTemplate.convertAndSend("queue.ticketMS", message);
    }
}