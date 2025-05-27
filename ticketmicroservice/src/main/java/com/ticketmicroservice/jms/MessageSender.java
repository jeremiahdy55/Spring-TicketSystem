package com.ticketmicroservice.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmicroservice.email.ResolutionEmail;
import com.ticketmicroservice.email.SimpleEmail;

@Service
public class MessageSender {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public MessageSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendSimpleEmailToNotificationMicroservice(SimpleEmail message) throws Exception {
        String messageAsJSON = objectMapper.writeValueAsString(message);
        jmsTemplate.convertAndSend("queue.notificationMS", messageAsJSON);
    }

    public void sendResolutionEmailToNotificationMicroservice(ResolutionEmail message) throws Exception {
        String messageAsJSON = objectMapper.writeValueAsString(message);
        jmsTemplate.convertAndSend("queue.notificationMS", messageAsJSON);
    }
}