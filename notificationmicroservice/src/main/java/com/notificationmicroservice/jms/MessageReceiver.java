package com.notificationmicroservice.jms;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notificationmicroservice.email.BaseEmail;
import com.notificationmicroservice.email.ManagerReminderEmail;
import com.notificationmicroservice.email.ResolutionEmail;
import com.notificationmicroservice.email.SimpleEmail;
import com.notificationmicroservice.service.EmailService;

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
            // Cast to TextMessage then convert to a BaseEmail wrapper Object
            TextMessage textMessage = (TextMessage) message;
            String messageContent = textMessage.getText();
            BaseEmail baseEmail = objectMapper.readValue(messageContent, BaseEmail.class);

            // Send an email specific to the BaseEmail.child class
            if (baseEmail instanceof SimpleEmail) {
                SimpleEmail email = (SimpleEmail) baseEmail;
                for (String recipient : email.getRecipients()) {
                    emailService.sendSimpleEmail(recipient, email.getSubject(), email.getBody());
                }
            } else if (baseEmail instanceof ResolutionEmail) {
                ResolutionEmail email = (ResolutionEmail) baseEmail;
                for (String recipient : email.getRecipients()) {
                    emailService.sendResolutionEmail(recipient, email.getSubject(), email.getBody(),
                            email.getTicket(), email.getTicketHistoryData());
                }
            } else if (baseEmail instanceof ManagerReminderEmail) {
                ManagerReminderEmail email = (ManagerReminderEmail) baseEmail;
                for (String recipient : email.getRecipients()) {
                    emailService.sendManagerReminderEmail(recipient, email.getSubject(), email.getBody());
                }
            } else {
                System.out.println("Message is not of type:TextMessage, unsure how to handle");
                System.out.println(message);
            }
        }
    }
}