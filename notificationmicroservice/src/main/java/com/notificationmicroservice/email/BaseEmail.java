package com.notificationmicroservice.email;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

// Define JsonType(s) to allow MessageReceiver to appropriately handle each message
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleEmail.class, name = "simple"),
        @JsonSubTypes.Type(value = ResolutionEmail.class, name = "resolution")
})
public abstract class BaseEmail {
    
    private List<String> recipients;
    private String subject;
    private String body;

	@Override
	public String toString() {
		String recipientList = "{";
		for (String recipient : getRecipients()) {
			recipientList += recipient + ", ";
		}
		recipientList = recipientList.substring(0, recipientList.length() - 2);
		recipientList += "}";
		return "Email [recipients=" + recipientList + ", body=" + getBody() + "]";
	}

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
