package com.ticketmicroservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.ticketmicroservice.domain.Employee;
import com.ticketmicroservice.domain.Ticket;
// import com.ticketmicroservice.domain.TicketHistory;
import com.ticketmicroservice.domain.TicketPriority;
import com.ticketmicroservice.domain.TicketRequest;
import com.ticketmicroservice.domain.TicketStatus;
import com.ticketmicroservice.repository.EmployeeRepository;
import com.ticketmicroservice.service.TicketHistoryService;
import com.ticketmicroservice.service.TicketService;

@RestController
public class TicketController {

    // Service foRESTr domain.Ticket - CRUD operations available through Service
    @Autowired
    TicketService ticketService;

    // Service for domain.TicketHistory - CRD operations available through Service
    //(Update not allowed, personal implementation as this is a history object and shouldn't rewrite history unless corrupted data)
    // Assume data has not been corrupted for the scale of this project
    @Autowired
    TicketHistoryService ticketHistoryService;

    // Repository- used only for checking data in the Employee table, no CUD operations used
    // Soft enforcement of no-CUD operations (This would be fixed by having a common-module to contain ALL domains, repositories, and services)
    // TODO/Future steps: Implement multi-modular version of this project to have a common-library type module
    @Autowired
    EmployeeRepository employeeRepository;

    @RequestMapping(value="/postTicket", method=RequestMethod.POST)
    public ResponseEntity<?> postTicket(@RequestBody TicketRequest request) {
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

        // Construct the Ticket from param: request
        Employee createdByEmployee = employeeRepository.findById(request.getCreatedBy()).orElse(null);
        Employee assigneeEmployee = employeeRepository.findById(request.getAssignee()).orElse(null);
        TicketPriority priority = TicketPriority.valueOf(request.getPriority());
        TicketStatus status = TicketStatus.valueOf(request.getStatus());
        Ticket ticket = new Ticket(
            request.getTitle(), 
            request.getDescription(), 
            createdByEmployee, 
            assigneeEmployee, 
            priority, 
            status, 
            request.getCreationDate(),
            request.getCategory(), 
            request.getFileAttachmentPath());
        
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
    public List<JsonNode> getAllTickets() {
		return ticketService.findAllTickets();
    }

    @RequestMapping(value="/getTicket/{ticketId}", method=RequestMethod.GET)
    public JsonNode getTicket(@PathVariable Long ticketId) {
		return ticketService.findById(ticketId);
    }

    // TODO create tickethistoryresponsedto
    @RequestMapping(value="/getHistory/{ticketId}", method=RequestMethod.GET)
    public List<JsonNode> getHistory(@PathVariable Long ticketId) {
		return ticketService.getHistory(ticketId);
    }

    @RequestMapping(value="/deleteTicket/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        if (!ticketService.existsById(id)) {
            return ResponseEntity.notFound().build(); // 404 error code if not ticket to delete
        }
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value="/approveTicket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<?> approveTicket(@RequestBody Long managerId, @PathVariable Long ticketId, @RequestParam(required=false) String comments) {
        if (!ticketService.existsById(ticketId)) {
            return ResponseEntity.notFound().build(); // 404 error code if not ticket to delete
        }

        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, managerId, "APPROVED");
        } else {
            ticketService.updateTicketStatus(ticketId, managerId, "APPROVED", comments);
        }
        return ResponseEntity.ok().body(String.format("APPROVED - Ticket ID: %d", ticketId));
    }

    @RequestMapping(value="/rejectTicket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<?> rejectTicket(@RequestBody Long managerId, @PathVariable Long ticketId, @RequestParam(required=false) String comments) {
        if (!ticketService.existsById(ticketId)) {
            return ResponseEntity.notFound().build(); // 404 error code if not ticket to delete
        }

        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, managerId, "REJECTED");
        } else {
            ticketService.updateTicketStatus(ticketId, managerId, "REJECTED", comments);
        }
        return ResponseEntity.ok().body(String.format("REJECTED - Ticket ID: %d", ticketId));
    }

    @RequestMapping(value="/resolveTicket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<?> resolveTicket(@RequestBody Long adminId, @PathVariable Long ticketId, @RequestParam(required=false) String comments) {
        if (!ticketService.existsById(ticketId)) {
            return ResponseEntity.notFound().build(); // 404 error code if not ticket to delete
        }

        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, adminId, "RESOLVED");
        } else {
            ticketService.updateTicketStatus(ticketId, adminId, "RESOLVED", comments);
        }
        return ResponseEntity.ok().body(String.format("RESOLVED - Ticket ID: %d", ticketId));
    }

    @RequestMapping(value="/reopenTicket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<?> reopenTicket(@RequestBody Long userId, @PathVariable Long ticketId, @RequestParam(required=false) String comments) {
        if (!ticketService.existsById(ticketId)) {
            return ResponseEntity.notFound().build(); // 404 error code if not ticket to delete
        }

        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, userId, "REOPENED");
        } else {
            ticketService.updateTicketStatus(ticketId, userId, "REOPENED", comments);
        }
        return ResponseEntity.ok().body(String.format("REOPENED - Ticket ID: %d", ticketId));
    }

    @RequestMapping(value="/closeticket/{ticketId}", method=RequestMethod.PUT)
    public ResponseEntity<?> closeTicket(@RequestBody Long userId, @PathVariable Long ticketId, @RequestParam(required=false) String comments) {
        if (!ticketService.existsById(ticketId)) {
            return ResponseEntity.notFound().build(); // 404 error code if not ticket to delete
        }

        if (comments == null) {
            ticketService.updateTicketStatus(ticketId, userId, "CLOSED");
        } else {
            ticketService.updateTicketStatus(ticketId, userId, "CLOSED", comments);
        }
        return ResponseEntity.ok().body(String.format("CLOSED - Ticket ID: %d", ticketId));
    }

}
