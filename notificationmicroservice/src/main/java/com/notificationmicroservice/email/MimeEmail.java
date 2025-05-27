package com.notificationmicroservice.email;

import java.io.Serializable;
import java.util.List;

// This is the representation of a the resolution email to be sent to USERs
public class MimeEmail implements Serializable {
	private static final long serialVersionUID = 1L;
    private List<String> recipients;
    private String body;
	private String subject;
	private List<String[]> ticketHistoryData; // upon ticket resolution, this is used to generate a PDF

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

	public MimeEmail() {
		super();
	}

	public MimeEmail(List<String> recipients, String body, String subject) {
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

	public List<String[]> getTicketHistoryData() {
		return ticketHistoryData;
	}

	public void setTicketHistoryData(List<String[]> ticketHistoryData) {
		this.ticketHistoryData = ticketHistoryData;
	}

}