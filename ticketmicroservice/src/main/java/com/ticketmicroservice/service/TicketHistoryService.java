package com.ticketmicroservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.ticketmicroservice.domain.Employee;
// import com.ticketmicroservice.domain.Ticket;
import com.ticketmicroservice.domain.TicketHistory;
import com.ticketmicroservice.repository.TicketHistoryRepository;
import com.ticketmicroservice.repository.TicketRepository;

@Service
public class TicketHistoryService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TicketHistoryRepository ticketHistoryRepository;

    // CREATE
    public TicketHistory createTicketHistory(TicketHistory ticketHistory) {
        return ticketHistoryRepository.save(ticketHistory);
    }

    // DELETE
    public void deleteTicketHistory (Long id) {
        ticketHistoryRepository.deleteById(id);
    }

    // READ (ALL)
    public List<TicketHistory> findAllTicketHistory() {
        return ticketHistoryRepository.findAll();
    }

    // Check if exists
    public boolean existsById (Long id) {
        return ticketRepository.existsById(id);
    }

    // Convert to JsonNode object
    public JsonNode convertToJsonNode(TicketHistory ticketHistory) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ticketHistory.getId());
        data.put("ticket", ticketHistory.getTicket().getId());
        data.put("action", ticketHistory.getAction().name());
        data.put("actionBy", ticketHistory.getActionBy().getId());
        data.put("actionDate", ticketHistory.getActionDate().toString());
        data.put("comments", ticketHistory.getComments());
        JsonNode jsonNode = objectMapper.valueToTree(data);
        return jsonNode;
    }
    
}