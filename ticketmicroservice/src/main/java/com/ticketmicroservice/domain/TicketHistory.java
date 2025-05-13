package com.ticketmicroservice.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TicketHistory {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    
    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketHistoryAction action;
    
    @ManyToOne
    @JoinColumn(name = "action_by", nullable = false)
	private Employee actionBy;

    @Column(nullable = false)
	private Date actionDate;

	private String comments;

    // Default constructor
    public TicketHistory() {}

    // Custom constructor
    public TicketHistory(
        Ticket ticket,
        TicketHistoryAction action,
        Employee actionBy,
        Date actionDate,
        String comments
    ) {
        this.ticket = ticket;
        this.action = action;
        this.actionBy = actionBy;
        this.actionDate = actionDate;
        this.comments = comments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public TicketHistoryAction getAction() {
        return action;
    }

    public void setAction(TicketHistoryAction action) {
        this.action = action;
    }

    public Employee getActionBy() {
        return actionBy;
    }

    public void setActionBy(Employee actionBy) {
        this.actionBy = actionBy;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
