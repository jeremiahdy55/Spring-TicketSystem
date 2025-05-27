package com.notificationmicroservice.email;

import java.io.Serializable;
import java.util.List;

// This is the representation of an email that can be sent to USERs, MANAGERs, or ADMINs
public class SimpleEmail implements Serializable {
	private static final long serialVersionUID = 1L;
    private List<String> recipients;
    private String body;
	private String subject;

	@Override
	public String toString() {
		String recipientList = "{";
		for (String recipient : recipients) {
			recipientList += recipient + ", ";
		}
		recipientList = recipientList.substring(0, recipientList.length() - 2);
		recipientList += "}";
		return "Email [recipients=" + recipientList + ", body=" + body + "]";
	}

	public SimpleEmail() {
		super();
	}

	public SimpleEmail(List<String> recipients, String body, String subject) {
		super();
		this.recipients = recipients;
		this.body = body;
		this.subject = subject;

	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

    
}