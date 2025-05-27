package com.notificationmicroservice.jms;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notificationmicroservice.email.EmailService;
import com.notificationmicroservice.email.SimpleEmail;

@Component
public class MessageReceiver {

    @Autowired
    EmailService emailService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageReceiver() {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    @JmsListener(destination = "queue.notificationMS")
    public void receive(Message message) throws Exception {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String messageContent = textMessage.getText();
            System.out.println(messageContent);
            try {
                // Deserialize the object
                SimpleEmail simpleEmail = objectMapper.readValue(messageContent, SimpleEmail.class);
                for (String recipient : simpleEmail.getRecipients()) {
                    emailService.sendSimpleEmail(recipient, simpleEmail.getSubject(), simpleEmail.getBody());
                }
            } catch (JsonMappingException e) {
                System.out.println("Not a SimpleEmail object");
            }
            // try {
            //     // Deserialize the object
            //     SimpleEmail simpleEmail = objectMapper.readValue(messageContent, SimpleEmail.class);
            //     for (String recipient : simpleEmail.getRecipients()) {
            //         emailService.sendSimpleEmail(recipient, simpleEmail.getSubject(), simpleEmail.getBody());
            //     }
            // } catch (JsonMappingException e) {
            //     System.out.println("Not a SimpleEmail object");
            // }

        } else {
            System.out.println("Message is not of type:TextMessage, unsure how to handle");
            System.out.println(message);
        }
    }
}