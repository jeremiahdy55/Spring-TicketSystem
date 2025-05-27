package com.ticketmicroservice.jms;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.ticketmicroservice.service.TicketService;

@Component
public class MessageReceiver {

    @Autowired
    TicketService ticketService;

    @Autowired
    MessageSender messageSender;

    @JmsListener(destination = "queue.ticketMS")
    public void receive(String message) {
        System.out.println(message);
        if (message.contains("check tickets' statuses")) {
            // Check "PENDING/OPEN" tickets for emailing
            ticketService.checkForOldPendingTickets();
            // Check RESOLVED tickets for any to auto-close
            ticketService.autoCloseTickets();
        }
        // messageSender.sendToNotificationMicroservice("sending back:" + message);
    }
}