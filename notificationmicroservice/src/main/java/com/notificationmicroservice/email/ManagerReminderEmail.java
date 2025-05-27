package com.notificationmicroservice.email;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

// This is an email to be sent to MANAGERs to check OPEN/PENDING tickets
// Object is used for conversion to MimeMessage and has a copy in ticketmicroservice
@JsonTypeName("managerReminder")
public class ManagerReminderEmail extends BaseEmail {

	public ManagerReminderEmail() {}  // no-args constructor is used for deserialization in MessageReceiver

	public ManagerReminderEmail(List<String> recipients, String body, String subject) {
		setRecipients(recipients);
		setBody(body);
		setSubject(subject);
	}

}