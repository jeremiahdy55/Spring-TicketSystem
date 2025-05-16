package com.ticketinggateway.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.ticketinggateway.service.EmployeeService;

@Controller
public class TicketMicroserviceClient {

    @Autowired
    EmployeeService employeeService;

    private static final String postTicketURL = "http://localhost:8282/postTicket";
    private static final String getAllTicketsURL = "http://localhost:8282/getAllTickets";
    private static final String getTicketURL = "http://localhost:8282/getTicket/";
    private static final String approveTicketURL = "http://localhost:8282/approveTicket/";
    private static final String rejectTicketURL = "http://localhost:8282/rejectTicket/";
    private static final String resolveTicketURL = "http://localhost:8282/resolveTicket/";
    private static final String reopenTicketURL = "http://localhost:8282/reopenTicket/";
    private static final String closeTicketURL = "http://localhost:8282/closeTicket/";
    private static final String getHistoryURL = "http://localhost:8282/getHistory/";
    private static final String deleteTicketURL = "http://localhost:8282/deleteTicket/";

    // GET TicketHistory, calls ticketmicroservice--TicketController.getAllTickets()
    @RequestMapping(value = "/getAllTickets", method = RequestMethod.GET)
    @ResponseBody
    public List<JsonNode> getAllTicketsFromTicketMicroservice() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Set the header to specify that only JSON data is accepted as response
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // requestEntity encompasses both the body (there is none here) and the headers of the request
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<JsonNode>> responseEntity = restTemplate.exchange(
                getAllTicketsURL,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<JsonNode>>() {});

        return responseEntity.getBody();
    }

    // GET TicketHistory, calls ticketmicroservice--TicketController.getTicket()
    @RequestMapping(value = "/getTicket/{ticketId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getTicketFromTicketMicroservice(@PathVariable Long ticketId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Set the header to specify that only JSON data is accepted as response
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // requestEntity encompasses both the body (there is none here) and the headers of the request
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getTicketURL+ticketId,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

        return responseEntity.getBody();
    }

    // GET TicketHistory, calls ticketmicroservice--TicketController.getHistory()
    @RequestMapping(value = "/getHistory/{ticketId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getTicketHistoryFromTicketMicroservice(@PathVariable Long ticketId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Set the header to specify that only JSON data is accepted as response
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // requestEntity encompasses both the body (there is none here) and the headers of the request
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getHistoryURL+ticketId,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

        return responseEntity.getBody();
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.approveTicket()
    // Approves Ticket and is only accessible (frontend) by MANAGER(S)
    @RequestMapping(value = "/approveTicket/{ticketId}", method = RequestMethod.PUT)
    public void approveTicket(Principal principal, 
                            @PathVariable Long ticketId,
                            @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        System.out.println();
        System.out.println(comments);
        String URLtoSend = approveTicketURL + ticketId + "?managerId=" + employeeService.findByName(principal.getName()).getId();
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));

        // requestEntity encompasses both the body (there is none here) and the headers of the request
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        System.out.println(responseEntity.getBody());
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.rejectTicket()
    // Rejects Ticket and is only accessible (frontend) by MANAGER(S)
    @RequestMapping(value = "/rejectTicket/{ticketId}", method = RequestMethod.PUT)
    public void rejectTicket(Principal principal, 
                            @PathVariable Long ticketId, 
                            @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        System.out.println();
        System.out.println(comments);
        String URLtoSend = rejectTicketURL + ticketId + "?managerId=" + employeeService.findByName(principal.getName()).getId();
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));

        // requestEntity encompasses both the body (there is none here) and the headers of the request
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        System.out.println(responseEntity.getBody());
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.resolveTicket()
    // Resolve Ticket and is only accessible (frontend) by ADMIN(S)
    @RequestMapping(value = "/resolveTicket/{ticketId}", method = RequestMethod.PUT)
    public void resolveTicket(Principal principal, 
                            @PathVariable Long ticketId, 
                            @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        System.out.println();
        System.out.println(comments);
        String URLtoSend = resolveTicketURL + ticketId + "?adminId=" + employeeService.findByName(principal.getName()).getId();
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));

        // requestEntity encompasses both the body (there is none here) and the headers of the request
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        System.out.println(responseEntity.getBody());
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.reopenTicket()
    // Reopen Ticket and is only accessible (frontend) by USER(S)
    @RequestMapping(value = "/reopenTicket/{ticketId}", method = RequestMethod.PUT)
    public void reopenTicket(Principal principal, 
                            @PathVariable Long ticketId, 
                            @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        String URLtoSend = reopenTicketURL + ticketId + "?userId=" + employeeService.findByName(principal.getName()).getId();
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));

        // requestEntity encompasses both the body (there is none here) and the headers of the request
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        System.out.println(responseEntity.getBody());
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.closeTicket()
    // Closes Ticket and is only accessible (frontend) by USER(S)
    @RequestMapping(value = "/closeTicket/{ticketId}", method = RequestMethod.PUT)
    public void closeTicket(Principal principal, 
                            @PathVariable Long ticketId, 
                            @RequestParam(required=false) String comments) throws UnsupportedEncodingException {
        String URLtoSend = closeTicketURL + ticketId + "?userId=" + employeeService.findByName(principal.getName()).getId();
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));

        // requestEntity encompasses both the body (there is none here) and the headers of the request
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        System.out.println(responseEntity.getBody());
    }

    // DELETE Ticket, calls ticketmicroservice--TicketController.deleteTicket()
    // Closes Ticket and is accessible (frontend) by USER(S), MANAGER(S), ADMIN(S)
    @RequestMapping(value = "/deleteTicket/{ticketId}", method = RequestMethod.DELETE)
    public void deleteTicket(Principal principal, @PathVariable Long ticketId){
        String URLtoSend = deleteTicketURL + ticketId;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));

        // requestEntity encompasses both the body (there is none here) and the headers of the request
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        System.out.println(responseEntity.getBody());
    }
}
