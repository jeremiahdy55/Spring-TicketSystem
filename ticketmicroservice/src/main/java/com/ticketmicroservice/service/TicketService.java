package com.ticketmicroservice.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmicroservice.domain.Employee;
import com.ticketmicroservice.domain.RoleName;
import com.ticketmicroservice.domain.Ticket;
import com.ticketmicroservice.domain.TicketHistory;
import com.ticketmicroservice.domain.TicketHistoryAction;
import com.ticketmicroservice.domain.TicketStatus;
import com.ticketmicroservice.email.ManagerReminderEmail;
import com.ticketmicroservice.email.ResolutionEmail;
import com.ticketmicroservice.email.SimpleEmail;
import com.ticketmicroservice.jms.MessageSender;
import com.ticketmicroservice.repository.EmployeeRepository;
import com.ticketmicroservice.repository.TicketHistoryRepository;
import com.ticketmicroservice.repository.TicketRepository;

@Service
public class TicketService {

    private final TicketHistoryRepository ticketHistoryRepository;

    @Autowired
    TicketRepository ticketRepository; // REPOSITORY //

    @Autowired
    TicketHistoryService ticketHistoryService; // SERVICE //
    
    @Autowired
    EmployeeRepository employeeRepository; // REPOSITORY //

    // For communication between ticketmicroservice and notificationmicroservice via JMS
    @Autowired
    MessageSender messageSender;


    TicketService(TicketHistoryRepository ticketHistoryRepository) {
        this.ticketHistoryRepository = ticketHistoryRepository;
    }


    /*********************************** DELETE METHODS ***********************************/
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }


    /*********************************** CREATE METHODS ***********************************/
    // CREATE w/o comments
    public Ticket createTicket(Ticket ticket) {
        Ticket savedTicket = ticketRepository.save(ticket);
        TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.CREATED, ticket.getCreatedBy(), ticket.getCreationDate(), "");
        ticketHistoryService.createTicketHistory(actionLog);
        sendTicketLifecycleEmail(savedTicket, savedTicket.getCreatedBy(), "");
        return savedTicket;
    }

    // CREATE w/comments
    public Ticket createTicket(Ticket ticket, String comments) {
        Ticket savedTicket = ticketRepository.save(ticket);
        TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.CREATED, ticket.getCreatedBy(), ticket.getCreationDate(), comments);
        ticketHistoryService.createTicketHistory(actionLog);
        sendTicketLifecycleEmail(savedTicket, savedTicket.getCreatedBy(), comments);
        return savedTicket;
    }


    /*********************************** READ METHODS ***********************************/
    // READ (ALL)
    public List<JsonNode> findAllTickets() {
        return convertTicketsToJsonNodes(ticketRepository.findAll());
    }

    // READ (ONE)
    public JsonNode findById(Long id) {
        if (!ticketRepository.existsById(id)) {
            // If the Ticket does not exist
            System.out.println("findById: ticket does not exist!");
            return null;
        } else {
            return convertToJsonNode(ticketRepository.findById(id).orElse(null));
        } 
    }

    // READ (ALL) 
    // WHERE ASSIGNEE.ID = assigneeId
    public List<JsonNode> getTicketsByAssigneeId(Long assigneeId) {
        return convertTicketsToJsonNodes(ticketRepository.findByAssignee_Id(assigneeId));
    }

    // READ (ALL) 
    // WHERE ASSIGNEE.ID = assigneeId
    public List<JsonNode> getTicketsByCreatedById(Long assigneeId) {
        return convertTicketsToJsonNodes(ticketRepository.findByCreatedBy_Id(assigneeId));
    }

    // READ (ALL) 
    // WHERE ASSIGNEE.ID = assigneeId and STATUS IN (statuses)
    public List<JsonNode> getTicketsByStatusInAndAssigneeId(List<TicketStatus> statuses, Long assigneeId) {
        return convertTicketsToJsonNodes(ticketRepository.findByStatusInAndAssignee_Id(statuses, assigneeId));
    }

    // READ (ALL) 
    // WHERE CREATEDBY.ID = createdById and STATUS IN (statuses)
    public List<JsonNode> getTicketsByStatusInAndCreatedById(List<TicketStatus> statuses, Long createdById) {
        return convertTicketsToJsonNodes(ticketRepository.findByStatusInAndCreatedBy_Id(statuses, createdById));
    }

    // READ (ALL) 
    // WHERE STATUS IN (statuses)
    public List<JsonNode> getTicketsByStatus(List<TicketStatus> statuses) {
        return convertTicketsToJsonNodes(ticketRepository.findByStatusIn(statuses));
    }

    /*********************************** UPDATE METHODS ***********************************/
    // UPDATE Ticket
    public Ticket updateTicketStatus(Long ticketId, Long employeeId, String action) {
        if (!ticketRepository.existsById(ticketId)) {
            // If the Ticket does not exist
            System.out.println("updateTicketStatus: ticket does not exist!");
            return null;
        } else {
            Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
            Employee actionBy = employeeRepository.findById(employeeId).orElse(null);
            TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.valueOf(action), actionBy, new Date(), "");

            // Manually create the history to avoid delays (database is not synchronized with code)
            List<JsonNode> history = getHistory(ticket.getId());
            history.add(ticketHistoryService.convertToJsonNode(actionLog));

            // save the data
            ticketHistoryService.createTicketHistory(actionLog);
            ticket.setStatus(TicketStatus.valueOf(action));
            Ticket returnTicket = ticketRepository.save(ticket);
            if (returnTicket.getStatus().equals(TicketStatus.RESOLVED)) {
                sendTicketResolutionEmail(returnTicket, returnTicket.getCreatedBy(), history, "");
            } else {
                sendTicketLifecycleEmail(returnTicket, returnTicket.getCreatedBy(), "");
            }            
            return returnTicket;
        }
    }

    // UPDATE Ticket with comments
    public Ticket updateTicketStatus(Long ticketId, Long employeeId, String action, String comments) {
        if (!ticketRepository.existsById(ticketId)) {
            // If the Ticket does not exist
            System.out.println("updateTicketStatus (with comments): ticket does not exist!");
            return null;
        } else {
            Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
            Employee actionBy = employeeRepository.findById(employeeId).orElse(null);
            TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.valueOf(action), actionBy, new Date(), comments);

            // Manually create the history to avoid delays (database is not synchronized with code)
            List<JsonNode> history = getHistory(ticket.getId());
            history.add(ticketHistoryService.convertToJsonNode(actionLog));

            // save the data
            ticketHistoryService.createTicketHistory(actionLog);
            ticket.setStatus(TicketStatus.valueOf(action));
            Ticket returnTicket = ticketRepository.save(ticket);
            if (returnTicket.getStatus().equals(TicketStatus.RESOLVED)) {
                sendTicketResolutionEmail(returnTicket, returnTicket.getCreatedBy(), history, comments);
            } else {
                sendTicketLifecycleEmail(returnTicket, returnTicket.getCreatedBy(), comments);
            }            
            return returnTicket;
        }
    }

     // Assign Ticket to ADMIN
     public Ticket assignTicket(Long ticketId, Long managerId, Long assigneeId) {
        if (!ticketRepository.existsById(ticketId)) {
            // If the Ticket does not exist
            System.out.println("updateTicketStatus (with comments): ticket does not exist!");
            return null;
        } else {
            Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
            Employee assignEmployee = employeeRepository.findById(assigneeId).orElse(null);
            Employee actionBy = employeeRepository.findById(managerId).orElse(null);
            ticket.setStatus(TicketStatus.ASSIGNED);
            ticket.setAssignee(assignEmployee);
            Ticket returnTicket = ticketRepository.save(ticket);
            TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.ASSIGNED, actionBy, new Date(), "");
            ticketHistoryService.createTicketHistory(actionLog);
            sendTicketLifecycleEmail(returnTicket, returnTicket.getAssignee(), "");
            return returnTicket;
        }
    }

    // Assign Ticket to ADMIN with comments
    public Ticket assignTicket(Long ticketId, Long managerId, Long assigneeId, String comments) {
        if (!ticketRepository.existsById(ticketId)) {
            // If the Ticket does not exist
            System.out.println("updateTicketStatus (with comments): ticket does not exist!");
            return null;
        } else {
            Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
            Employee assignEmployee = employeeRepository.findById(assigneeId).orElse(null);
            Employee actionBy = employeeRepository.findById(managerId).orElse(null);
            ticket.setStatus(TicketStatus.ASSIGNED);
            ticket.setAssignee(assignEmployee);
            Ticket returnTicket = ticketRepository.save(ticket);
            TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.ASSIGNED, actionBy, new Date(), comments);
            ticketHistoryService.createTicketHistory(actionLog);
            sendTicketLifecycleEmail(returnTicket, returnTicket.getAssignee(), comments);
            return returnTicket;
        }
    }


    /*********************************** EMAIL METHODS **************************************************/
    // Create the base notification email for ticket creation, assignment, and updates and send
    // to notificationmicroservice to be emailed to the the receipient (can be USER or ADMIN)
    public void sendTicketLifecycleEmail(Ticket ticket, Employee recipient, String comments) {
        String recipientEmailAddress = recipient.getEmail();
        String ticketStatus = ticket.getStatus().name().equals("OPEN") ? "CREATED" : ticket.getStatus().name();
        String emailSubject = ticketStatus + " ticket ID: " + ticket.getId();
        String emailBody = ticketStatus + " ticket ID: " + ticket.getId() + "\n" 
            + "Title: " + ticket.getTitle() + "\n"
            + "Description: " + ticket.getDescription() + "\n"
            + "Priority: " + ticket.getPriority() + "\n"
            + "Category: " + ticket.getCategory() + "\n"
            + "comments: " + comments;
        SimpleEmail email = new SimpleEmail(List.of(recipientEmailAddress), emailBody, emailSubject);
        try {
            messageSender.sendSimpleEmailToNotificationMicroservice(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create the notification email for ticket resolution and send it via JMS 
    // to notificationmicroservice to be emailed to the USER
    public void sendTicketResolutionEmail(Ticket ticket, Employee recipient, List<JsonNode> ticketHistory, String comments) {
        String recipientEmailAddress = recipient.getEmail();
        String emailSubject = ticket.getStatus().name() + " ticket ID: " + ticket.getId();
        String emailBody = "<h2>Resolving ticket ID: " + ticket.getId() + "</h2>" 
            + "<p>Resolved by: " + ticket.getAssignee().getId() + "<br>"
            + "Title: " + ticket.getTitle() + "<br>"
            + "Description: " + ticket.getDescription() + "<br>"
            + "Priority: " + ticket.getPriority() + "<br>"
            + "Category: " + ticket.getCategory() + "<br>"
            + "comments: " + comments + "</p>";

        ResolutionEmail email = new ResolutionEmail(List.of(recipientEmailAddress), emailBody, emailSubject, convertToJsonNode(ticket), ticketHistory);
        try {
            messageSender.sendResolutionEmailToNotificationMicroservice(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create the reminder email for ticket approval and send it via JMS 
    // to notificationmicroservice to be emailed to the MANAGER
    public void sendManagerReminderEmail(List<Ticket> tickets, List<Employee> recipients) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        // Get the list of managers to remind
        List<String> recipientEmailAddresses = new ArrayList<>();
        for (Employee recipient : recipients) {
            recipientEmailAddresses.add(recipient.getEmail());
        }

        String emailSubject = "Pending tickets: " + LocalDate.now();

        String emailBody = "<h2>Pending tickets: " + LocalDate.now()+"</h2>";
        if (tickets != null && !tickets.isEmpty()) {
            String tableHeadStartTag = "<th style=\" background-color:#f8f9fa; border:1px solid rgb(0, 0, 0);\">";
            String tableDataStartTag = "<td style=\"padding:8px; border:1px solid rgb(0, 0, 0);\">";
            emailBody +=  "<table style=\"width:100%;\"><thead><tr>" +
                tableHeadStartTag + "ID:</th>" + 
                tableHeadStartTag + "Title:</th>" +
                tableHeadStartTag + "Created By:</th>" +
                tableHeadStartTag + "Priority</th>" +
                tableHeadStartTag + "Creation Date</th>" +
                tableHeadStartTag + "Category</th>" +
                "</tr></thead><tbody>";
            for (Ticket ticket : tickets) {
                emailBody += "<tr>" + tableDataStartTag + ticket.getId() + "</td>" + 
                tableDataStartTag + ticket.getTitle() + "</td>" + 
                tableDataStartTag + ticket.getCreatedBy().getId() + "</td>" + 
                tableDataStartTag + ticket.getPriority() + "</td>" + 
                tableDataStartTag + sdf.format(ticket.getCreationDate()) + "</td>" + 
                tableDataStartTag + ticket.getCategory() + "</td></tr>";
            }
            emailBody += "</tbody></table>";
        } else {
            emailBody += "<h4>No pending tickets today!</h4>";
        }
        ManagerReminderEmail email = new ManagerReminderEmail(recipientEmailAddresses, emailBody, emailSubject);
        try {
            messageSender.sendManagerReminderEmailToNotificationMicroservice(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*********************************** JMS-TRIGGERED METHODS ***********************************/
    // Check if any tickets have been OPEN/PENDING_APPROVAL for 7+ days
    // If any, send a reminder email to all MANAGERs and change any OPEN to PENDING_APPROVAL
    public void checkForOldPendingTickets() {
        List<Ticket> openTickets = ticketRepository.findByStatusIn(List.of(TicketStatus.OPEN, TicketStatus.PENDING_APPROVAL));
        LocalDate today = LocalDate.now();
        long daysBetween = 0L;
        LocalDate createDate = null;
        List<Ticket> oldOpenTickets = new ArrayList<>();

        for (Ticket ticket : openTickets) {
            daysBetween = 0L; createDate = null; // reset the values for fresh comparisons
            createDate = ticket.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            daysBetween = ChronoUnit.DAYS.between(createDate, today);
            if (Math.abs(daysBetween) >= 7) oldOpenTickets.add(ticket);
            if (!ticket.getStatus().equals(TicketStatus.PENDING_APPROVAL)){
                ticket.setStatus(TicketStatus.PENDING_APPROVAL);
                ticketRepository.save(ticket);
                sendTicketLifecycleEmail(ticket, ticket.getCreatedBy(), "Changed ticket status to PENDING_APPROVAL on daily check-up");
            }
        }
        sendManagerReminderEmail(oldOpenTickets, employeeRepository.findByRoleName(RoleName.MANAGER));
    }

    // Check if any tickets have been RESOLVE for 5+ days
    // If any, automatically CLOSE the ticket and send an email to the USER
    public void autoCloseTickets() {
        String autoCloseComments = "Automatically closed ticket on " + LocalDate.now() + " after 5 or more days of inactivity post ticket resolution";
        List<Ticket> resolvedTickets = ticketRepository.findByStatusIn(List.of(TicketStatus.RESOLVED));
        LocalDate today = LocalDate.now();
        long daysBetween = 0L;

        for (Ticket ticket : resolvedTickets) {
            daysBetween = 0L; // reset the values for fresh comparisons
            List<Date> resolvedDates = new ArrayList<>();
            for (TicketHistory event : ticket.getHistory()) {
                System.out.println(event.getActionDate());
                if (event.getAction().equals(TicketHistoryAction.RESOLVED)) {
                    resolvedDates.add(event.getActionDate());
                }
            }
            if (!resolvedDates.isEmpty()) {
                LocalDate latestResolvedDate = Collections.max(resolvedDates).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                daysBetween = ChronoUnit.DAYS.between(latestResolvedDate, today);
                if (Math.abs(daysBetween) >= 5) {
                    ticket.setStatus(TicketStatus.CLOSED);
                    ticketRepository.save(ticket);
                    sendTicketLifecycleEmail(ticket, ticket.getCreatedBy(), autoCloseComments);
                } 
            }
            
        }
    }


    /*********************************** UTILITY METHODS ***********************************/
    // Check if Ticket exists
    public boolean existsById(Long id) {
        return ticketRepository.existsById(id);
    }

    // Convert List<Ticket> to List<JsonNode>
    public List<JsonNode> convertTicketsToJsonNodes(List<Ticket> tickets) {
        List<JsonNode> returnList = new ArrayList<JsonNode>();
        for (Ticket ticket : tickets) {
            returnList.add(convertToJsonNode(ticket));
        }
        return returnList;
    }

    // Convert Ticket to JsonNode object
    public JsonNode convertToJsonNode(Ticket ticket) {
        ObjectMapper objectMapper = new ObjectMapper();
        Employee assignee = ticket.getAssignee();
        long assigneeId = 0;
        if (assignee != null) {
            assigneeId = ticket.getAssignee().getId();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", ticket.getId());
        data.put("title", ticket.getTitle());
        data.put("description", ticket.getDescription());
        data.put("createdBy", ticket.getCreatedBy().getId());
        data.put("assignee", assigneeId);
        data.put("priority", ticket.getPriority().name());
        data.put("status", ticket.getStatus().name());
        data.put("creationDate", ticket.getCreationDate().toString());
        data.put("category", ticket.getCategory());
        data.put("fileAttachmentPaths", ticket.getFileAttachmentPaths());
        JsonNode jsonNode = objectMapper.valueToTree(data);
        return jsonNode;
    }

    // Get all TicketHistory items associated with ticketId
    public List<JsonNode> getHistory(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            // If the Ticket does not exist
            System.out.println("getHistory: ticket does not exist!");
            return null;
        } else {
            Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
            List<JsonNode> returnList = new ArrayList<JsonNode>();
            List<TicketHistory> history = ticket.getHistory();
            for (TicketHistory event: history) {
                returnList.add(ticketHistoryService.convertToJsonNode(event));
            }
            return returnList;
        }
    }
}