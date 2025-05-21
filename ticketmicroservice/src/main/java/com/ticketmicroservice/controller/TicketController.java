package com.ticketmicroservice.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import com.ticketmicroservice.domain.Employee;
import com.ticketmicroservice.domain.Ticket;
import com.ticketmicroservice.domain.TicketPriority;
import com.ticketmicroservice.domain.TicketRequest;
import com.ticketmicroservice.domain.TicketStatus;
import com.ticketmicroservice.repository.EmployeeRepository;
import com.ticketmicroservice.service.TicketHistoryService;
import com.ticketmicroservice.service.TicketService;

@RestController
@RequestMapping(value="/api")
public class TicketController {

    // Service for domain.Ticket - CRUD operations available through Service
    @Autowired
    TicketService ticketService;

    // Service for domain.TicketHistory - CRD operations available through Service
    // (Update not allowed, personal implementation as this is a history object and shouldn't rewrite history unless corrupted data)
    // Assume data has not been corrupted for the scale of this project
    @Autowired
    TicketHistoryService ticketHistoryService;

    // Repository- used only for checking data in the Employee table, no CUD operations used
    // Soft enforcement of no-CUD operations (This would be fixed by having a common-module to contain ALL domains, repositories, and services)
    // TODO/Future steps: Implement multi-modular version of this project to have a common-library type module
    @Autowired
    EmployeeRepository employeeRepository;

    @RequestMapping(value="/postTicket", method=RequestMethod.POST)
    public ResponseEntity<String> postTicket(@RequestBody TicketRequest request) {
        // Check for the ENUM TYPES
        try {
            TicketPriority.valueOf(request.getPriority());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Not an accepted TicketPriority VALUE");
            return ResponseEntity.badRequest().body("Not an accepted TicketPriority VALUE: "+ request.getPriority());
        }
        try {
            TicketStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Not an accepted TicketStatus VALUE");
            return ResponseEntity.badRequest().body("Not an accepted TicketStatus VALUE: " + request.getStatus());
        }

        // Check if an Employee exists in the necessary fields
        if (!employeeRepository.existsById(request.getCreatedBy())) {
            // If the createdBy Employee does not exist
            System.out.println("createdBy employee does not exist!");
            return ResponseEntity.notFound().build();
        }

        // Construct the Ticket from request body
        Employee createdByEmployee = employeeRepository.findById(request.getCreatedBy()).orElse(null);
        Employee assigneeEmployee = employeeRepository.findById(request.getAssignee()).orElse(null);
        TicketPriority priority = TicketPriority.valueOf(request.getPriority());
        TicketStatus status = TicketStatus.valueOf(request.getStatus());
        Ticket ticket = new Ticket(
            request.getTitle(), 
            request.getDescription(), 
            createdByEmployee, 
            assigneeEmployee, // usually null, as a newly created ticket would not have been assigned to anyone yet 
            priority, 
            status, 
            request.getCreationDate(),
            request.getCategory(), 
            request.getFileAttachmentPaths());
        
        Ticket savedTicket = null;
        // Comments is an optional String value to be used for the TicketHistory logging of the CREATE action
        if (request.getComments() == null || request.getComments().trim().isEmpty()) {
            savedTicket = ticketService.createTicket(ticket);
        } else {
            savedTicket = ticketService.createTicket(ticket, request.getComments());
        }
        return ResponseEntity.ok().body("Successfully saved ticket with id: "+ savedTicket.getId());
    }

    @RequestMapping(value="/getAllTickets", method=RequestMethod.GET)
    public ResponseEntity<List<JsonNode>> getAllTickets() {
		return ResponseEntity.ok(ticketService.findAllTickets());
    }

    @RequestMapping(value="/getTicket/{ticketId}", method=RequestMethod.GET)
    public ResponseEntity<JsonNode> getTicket(@PathVariable Long ticketId) {
		return ResponseEntity.ok(ticketService.findById(ticketId));
    }

    @RequestMapping(value="/getHistory/{ticketId}", method=RequestMethod.GET)
    public ResponseEntity<List<JsonNode>> getHistory(@PathVariable Long ticketId) {
		return ResponseEntity.ok(ticketService.getHistory(ticketId));
    }

    @RequestMapping(value="/getAssignedTickets/{assigneeId}", method=RequestMethod.GET)
    public ResponseEntity<List<JsonNode>> getTicketsByAssigneeId(@PathVariable Long assigneeId) {
		return ResponseEntity.ok(ticketService.getTicketsByAssigneeId(assigneeId));
    }

    @RequestMapping(value="/getUserTickets/{createdById}", method=RequestMethod.GET)
    public ResponseEntity<List<JsonNode>> getTicketsByUserId(@PathVariable Long createdById) {
		return ResponseEntity.ok(ticketService.getTicketsByCreatedById(createdById));
    }

    @RequestMapping(value="/getActiveAssignedTickets/{assigneeId}", method=RequestMethod.GET)
    public ResponseEntity<List<JsonNode>> getTicketsByStatusInAndAssigneeId(@PathVariable Long assigneeId) {
        List<TicketStatus> statuses = List.of(TicketStatus.APPROVED, TicketStatus.REOPENED);
		return ResponseEntity.ok(ticketService.getTicketsByStatusInAndAssigneeId(statuses, assigneeId));
    }

    @RequestMapping(value="/getActiveUserTickets/{createdById}", method=RequestMethod.GET)
    public ResponseEntity<List<JsonNode>> getTicketsStatusInAndByUserId(@PathVariable Long createdById) {
        List<TicketStatus> statuses = List.of(TicketStatus.APPROVED, TicketStatus.ASSIGNED, TicketStatus.OPEN, TicketStatus.PENDING_APPROVAL, TicketStatus.REJECTED, TicketStatus.REOPENED, TicketStatus.RESOLVED);
		return ResponseEntity.ok(ticketService.getTicketsByStatusInAndCreatedById(statuses, createdById));
    }

    @RequestMapping(value="/getOpenTickets", method=RequestMethod.GET)
    public ResponseEntity<List<JsonNode>> getOpenTickets() {
        List<TicketStatus> statuses = List.of(TicketStatus.OPEN, TicketStatus.PENDING_APPROVAL, TicketStatus.REOPENED);
		return ResponseEntity.ok(ticketService.getTicketsByStatus(statuses));
    }

    @RequestMapping(value="/deleteTicket/{ticketId}", method=RequestMethod.DELETE)
    public ResponseEntity<String> deleteTicket(@PathVariable Long ticketId) {
        if (!ticketService.existsById(ticketId)) {
            return ticketNotFound(ticketId);
        }
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.ok().body(String.format("DELETED - Ticket ID: %d", ticketId));
    }

    @RequestMapping(value="/approveTicket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<String> approveTicket(@PathVariable Long ticketId, @RequestParam Long managerId, 
    @RequestParam(required=false) Long assigneeId,
    @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        String assignComments = String.format("Ticket ID: %d - ASSIGNED by: %d", ticketId, managerId);
        if (!ticketService.existsById(ticketId)) {
            return ticketNotFound(ticketId);
        }
        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, managerId, "APPROVED");
        } else {
            ticketService.updateTicketStatus(ticketId, managerId, "APPROVED", URLDecoder.decode(comments, "UTF-8"));
        }
        // Approve the ticket if the manager selected an ADMIN Employee to assign the ticket to (will always have comments)
        if (assigneeId != null) ticketService.assignTicket(ticketId, managerId, assigneeId, assignComments);
        return ResponseEntity.ok().body(String.format("APPROVED - Ticket ID: %d", ticketId));
    }

    @RequestMapping(value="/rejectTicket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<String> rejectTicket(@PathVariable Long ticketId, @RequestParam Long managerId, 
    @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        if (!ticketService.existsById(ticketId)) {
            return ticketNotFound(ticketId);
        }
        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, managerId, "REJECTED");
        } else {
            System.out.println(comments);
            ticketService.updateTicketStatus(ticketId, managerId, "REJECTED", URLDecoder.decode(comments, "UTF-8"));
        }
        return ResponseEntity.ok().body(String.format("REJECTED - Ticket ID: %d", ticketId));
    }

    @RequestMapping(value="/resolveTicket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<String> resolveTicket(@PathVariable Long ticketId, @RequestParam Long adminId, 
    @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        if (!ticketService.existsById(ticketId)) {
            return ticketNotFound(ticketId);
        }
        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, adminId, "RESOLVED");
        } else {
            ticketService.updateTicketStatus(ticketId, adminId, "RESOLVED", URLDecoder.decode(comments, "UTF-8"));
        }
        return ResponseEntity.ok().body(String.format("RESOLVED - Ticket ID: %d", ticketId));
    }

    @RequestMapping(value="/reopenTicket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<String> reopenTicket(@PathVariable Long ticketId, @RequestParam Long userId, 
    @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        if (!ticketService.existsById(ticketId)) {
            return ticketNotFound(ticketId);
        }
        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, userId, "REOPENED");
        } else {
            ticketService.updateTicketStatus(ticketId, userId, "REOPENED", URLDecoder.decode(comments, "UTF-8"));
        }
        return ResponseEntity.ok().body(String.format("REOPENED - Ticket ID: %d", ticketId));
    }

    @RequestMapping(value="/closeTicket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<String> closeTicket(@PathVariable Long ticketId, @RequestParam Long userId, 
    @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        if (!ticketService.existsById(ticketId)) {
            return ticketNotFound(ticketId);
        }
        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, userId, "CLOSED");
        } else {
            ticketService.updateTicketStatus(ticketId, userId, "CLOSED", URLDecoder.decode(comments, "UTF-8"));
        }
        return ResponseEntity.ok().body(String.format("CLOSED - Ticket ID: %d", ticketId));
    }

    private ResponseEntity<String> ticketNotFound(Long ticketId) {
        return ResponseEntity.badRequest().body(String.format("Ticket ID: %d not found!", ticketId));
    }
}
