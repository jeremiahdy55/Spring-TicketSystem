package com.notificationmicroservice.email;

import java.util.List;

// This is a simple email to be sent to USERs, ADMINs, or MANAGERs upon ticket history being logged
// Object is used for conversion to SimpleMailMessage and has a copy in ticketmicroservice
public class SimpleEmail extends BaseEmail {

	public SimpleEmail() {} // no-args constructor is used for deserialization in MessageReceiver

	public SimpleEmail(List<String> recipients, String body, String subject) {
		setRecipients(recipients);
		setBody(body);
		setSubject(subject);
	}

}