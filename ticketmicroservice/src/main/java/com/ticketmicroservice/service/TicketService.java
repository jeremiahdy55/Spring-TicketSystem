package com.ticketmicroservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmicroservice.domain.Employee;
import com.ticketmicroservice.domain.Ticket;
import com.ticketmicroservice.domain.TicketHistory;
import com.ticketmicroservice.domain.TicketHistoryAction;
import com.ticketmicroservice.domain.TicketStatus;
import com.ticketmicroservice.repository.EmployeeRepository;
import com.ticketmicroservice.repository.TicketRepository;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository; // REPOSITORY //

    @Autowired
    TicketHistoryService ticketHistoryService; // SERVICE //
    
    @Autowired
    EmployeeRepository employeeRepository; // REPOSITORY //

    // CREATE w/o comments
    public Ticket createTicket(Ticket ticket) {
        Ticket savedTicket = ticketRepository.save(ticket);
        TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.CREATED, ticket.getCreatedBy(), ticket.getCreationDate(), "");
        ticketHistoryService.createTicketHistory(actionLog);
        return savedTicket;
    }

    // CREATE w/comments
    public Ticket createTicket(Ticket ticket, String comments) {
        Ticket savedTicket = ticketRepository.save(ticket);
        TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.CREATED, ticket.getCreatedBy(), ticket.getCreationDate(), comments);
        ticketHistoryService.createTicketHistory(actionLog);
        return savedTicket;
    }

    // DELETE
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

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

    // Check if Ticket exists
    public boolean existsById(Long id) {
        return ticketRepository.existsById(id);
    }

    // UPDATE Ticket
    public Ticket updateTicketStatus(Long ticketId, Long employeeId, String action) {
        if (!ticketRepository.existsById(ticketId)) {
            // If the Ticket does not exist
            System.out.println("updateTicketStatus: ticket does not exist!");
            return null;
        } else {
            Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
            ticket.setStatus(TicketStatus.valueOf(action));
            Ticket returnTicket = ticketRepository.save(ticket);
            Employee actionBy = employeeRepository.findById(employeeId).orElse(null);
            TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.valueOf(action), actionBy, new Date(), "");
            ticketHistoryService.createTicketHistory(actionLog);
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
            ticket.setStatus(TicketStatus.valueOf(action));
            Ticket returnTicket = ticketRepository.save(ticket);
            Employee actionBy = employeeRepository.findById(employeeId).orElse(null);
            TicketHistory actionLog = new TicketHistory(ticket, TicketHistoryAction.valueOf(action), actionBy, new Date(), comments);
            ticketHistoryService.createTicketHistory(actionLog);
            return returnTicket;
        }
    }

    // Convert List<Ticket> to List<JsonNode>
    public List<JsonNode> convertTicketsToJsonNodes(List<Ticket> tickets) {
        List<JsonNode> returnList = new ArrayList<JsonNode>();
        for (Ticket ticket : tickets) {
            returnList.add(convertToJsonNode(ticket));
        }
        return returnList;
    }

    // Convert to JsonNode object
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