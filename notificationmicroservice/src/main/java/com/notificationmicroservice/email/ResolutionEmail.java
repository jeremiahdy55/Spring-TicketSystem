package com.notificationmicroservice.email;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;

// This is an email to be sent to USERs upon ticket resolution
// Object is used for conversion to MimeMessage and has a copy in ticketmicroservice
@JsonTypeName("resolution")
public class ResolutionEmail extends BaseEmail {

	private List<JsonNode> ticketHistoryData; // upon ticket resolution, this is used to generate a PDF

	public ResolutionEmail() {}  // no-args constructor is used for deserialization in MessageReceiver

	public ResolutionEmail(List<String> recipients, String body, String subject, List<JsonNode> ticketHistoryData) {
		setRecipients(recipients);
		setBody(body);
		setSubject(subject);
		this.ticketHistoryData = ticketHistoryData;
	}

	public List<JsonNode> getTicketHistoryData() {
		return ticketHistoryData;
	}

	public void setTicketHistoryData(List<JsonNode> ticketHistoryData) {
		this.ticketHistoryData = ticketHistoryData;
	}

}