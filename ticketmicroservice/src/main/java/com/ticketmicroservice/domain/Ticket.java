package com.ticketmicroservice.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"title"})
) 
public class Ticket {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String description;
	
    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
	private Employee createdBy;
	
    @ManyToOne
    @JoinColumn(name = "assignee")
	private Employee assignee;

	@Column(nullable = false)
    @Enumerated(EnumType.STRING)
	private TicketPriority priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
	private TicketStatus status;

    @Column(nullable = false)
    private Date creationDate;

    private String category;

    private String fileAttachmentPath;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TicketHistory> history = new ArrayList<>();

    //Default constructor
    public Ticket() {}
    
    // Custom constructor (omits id)
    public Ticket(
        String title,
        String description,
        Employee createdBy,
        Employee assignee,
        TicketPriority priority,
        TicketStatus status,
        Date creationDate,
        String category,
        String fileAttachmentPath
    ) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.assignee = assignee;
        this.priority = priority;
        this.status = status;
        this.creationDate = creationDate;
        this.category = category;
        this.fileAttachmentPath = fileAttachmentPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public Employee getAssignee() {
        return assignee;
    }

    public void setAssignee(Employee assignee) {
        this.assignee = assignee;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFileAttachmentPath() {
        return fileAttachmentPath;
    }

    public void setFileAttachmentPath(String fileAttachmentPath) {
        this.fileAttachmentPath = fileAttachmentPath;
    }

    public List<TicketHistory> getHistory() {
        return history;
    }

    public void setHistory(List<TicketHistory> history) {
        this.history = history;
    }


}
