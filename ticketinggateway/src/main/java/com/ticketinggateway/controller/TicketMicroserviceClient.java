package com.ticketinggateway.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.ticketinggateway.domain.Employee;
import com.ticketinggateway.domain.TicketRequest;
import com.ticketinggateway.service.EmployeeService;

// This Controller provides Ticket information to any webpages in webapp
// by sending an HTTP request to the equivalent methods in ticketmicroservice
@Controller
public class TicketMicroserviceClient {

    @Autowired
    EmployeeService employeeService;

    // POST request URL
    private static final String postTicketURL = "http://localhost:8282/api/postTicket";

    // GET request URLs
    private static final String getAllTicketsURL = "http://localhost:8282/api/getAllTickets";
    private static final String getTicketURL = "http://localhost:8282/api/getTicket/";
    private static final String getHistoryURL = "http://localhost:8282/api/getHistory/";
    private static final String getOpenTicketsURL = "http://localhost:8282/api/getOpenTickets";
    private static final String getTicketsByAssigneeURL = "http://localhost:8282/api/getAssignedTickets/";
    private static final String getTicketsByCreatedByURL = "http://localhost:8282/api/getUserTickets/";
    private static final String getActiveTicketsByAssigneeURL = "http://localhost:8282/api/getActiveAssignedTickets/";
    private static final String getActiveTicketsByCreatedByURL = "http://localhost:8282/api/getActiveUserTickets/";

    // PUT request URLs
    private static final String approveTicketURL = "http://localhost:8282/api/approveTicket/";
    private static final String rejectTicketURL = "http://localhost:8282/api/rejectTicket/";
    private static final String resolveTicketURL = "http://localhost:8282/api/resolveTicket/";
    private static final String reopenTicketURL = "http://localhost:8282/api/reopenTicket/";
    private static final String closeTicketURL = "http://localhost:8282/api/closeTicket/";

    // DELETE request URL
    private static final String deleteTicketURL = "http://localhost:8282/api/deleteTicket/";

    private final Path fileStorageLocation;

    public TicketMicroserviceClient() throws IOException {
        this.fileStorageLocation = Paths.get("src/main/webapp/uploads").toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    // POST Ticket, calls ticketmicroservice--TicketController.postTicket()
    // This method accepts the formData object from JS-AJAX call
    @RequestMapping(value = "/postTicket", method = RequestMethod.POST)
    public ResponseEntity<String> postTicket(
            Principal principal,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String priority,
            @RequestParam String category,
            @RequestParam String comments,
            @RequestParam List<MultipartFile> files) {
        Employee thisUser = employeeService.findByName(principal.getName());
        List<String> fileAttachmentPaths = null;

        System.out.println("Request received");
        // Handle file upload (optional save)
        try {
            if (files != null && !files.isEmpty()) { // if there are files
                List<String> tempPaths = new ArrayList<>();
                for (MultipartFile file : files) {
                    // Get filename
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    String timestamp = String.valueOf(System.currentTimeMillis());

                    // Insert identifier before the file extension (if any ext)
                    // Unique identifier made by combining system time and user id
                    int extIndex = fileName.lastIndexOf(".");
                    if (extIndex > 0) {
                        String namePart = fileName.substring(0, extIndex);
                        String extPart = fileName.substring(extIndex);
                        fileName = namePart + "_emp" + String.valueOf(thisUser.getId())
                                + "_" + timestamp + extPart;
                    } else {
                        fileName = fileName + "_emp" + String.valueOf(thisUser.getId()) + "_" + timestamp;
                    }

                    // Save the unique file name to save in DB
                    tempPaths.add(fileName);

                    // Create target file path and save using transferTo
                    Path destinationPath = fileStorageLocation.resolve(fileName);
                    file.transferTo(destinationPath.toFile());
                }
                fileAttachmentPaths = tempPaths;
            }
        } catch (IOException e) {
            System.out.println("Could not save files");
            return ResponseEntity.internalServerError().body("Could not save files");
        }
        // Create the TicketRequest object to send in request body
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setTitle(title);
        ticketRequest.setDescription(description);
        ticketRequest.setCreatedBy(thisUser.getId()); // Get current USER's id
        ticketRequest.setAssignee(0L); // This will set the assignee to null in the DB
        ticketRequest.setPriority(priority);
        ticketRequest.setStatus("OPEN"); // All newly created tickets are OPEN
        ticketRequest.setCategory(category);
        ticketRequest.setComments(comments);
        ticketRequest.setFileAttachmentPaths(fileAttachmentPaths);
        ticketRequest.setCreationDate(new Date());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Sending JSON body
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN)); // Expecting plain text back

        // requestEntity encompasses both the body and the headers of the request
        HttpEntity<TicketRequest> requestEntity = new HttpEntity<>(ticketRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                postTicketURL,
                HttpMethod.POST,
                requestEntity,
                String.class);

        return responseEntity;
    }

    // GET TicketHistory, calls ticketmicroservice--TicketController.getAllTickets()
    @RequestMapping(value = "/getAllTickets", method = RequestMethod.GET)
    @ResponseBody
    public List<JsonNode> getAllTicketsFromTicketMicroservice() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Set the header to specify that only JSON data is accepted as response
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<List<JsonNode>> responseEntity = restTemplate.exchange(
                getAllTicketsURL,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<JsonNode>>() {
                });

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
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getTicketURL + ticketId,
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
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getHistoryURL + ticketId,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

        return responseEntity.getBody();
    }

    // GET Tickets assigned to current ADMIN Employee, calls
    // ticketmicroservice--TicketController.getTicketsByAssigneeId()
    @RequestMapping(value = "/getAssignedTickets/{assigneeId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getTicketsByAssigneeIdFromTicketMicroservice(@PathVariable Long assigneeId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Set the header to specify that only JSON data is accepted as response
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getTicketsByAssigneeURL + assigneeId,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

        return responseEntity.getBody();
    }

    // GET Tickets assigned to current ADMIN Employee, calls
    // ticketmicroservice--TicketController.getTicketsByAssigneeId()
    @RequestMapping(value = "/getActiveAssignedTickets/{assigneeId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getActiveTicketsByAssigneeIdFromTicketMicroservice(@PathVariable Long assigneeId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Set the header to specify that only JSON data is accepted as response
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getActiveTicketsByAssigneeURL + assigneeId,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

        return responseEntity.getBody();
    }


    // GET Tickets created by current USER Employee, calls
    // ticketmicroservice--TicketController.getTicketsByUserId()
    @RequestMapping(value = "/getUserTickets/{createdById}", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getTicketsByCreatedByIdFromTicketMicroservice(@PathVariable Long createdById) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Set the header to specify that only JSON data is accepted as response
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getTicketsByCreatedByURL + createdById,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

        return responseEntity.getBody();
    }

    // GET Tickets created by current USER Employee, calls
    // ticketmicroservice--TicketController.getTicketsByUserId()
    @RequestMapping(value = "/getActiveUserTickets/{createdById}", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getActiveTicketsByCreatedByIdFromTicketMicroservice(@PathVariable Long createdById) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Set the header to specify that only JSON data is accepted as response
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getActiveTicketsByCreatedByURL + createdById,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

        return responseEntity.getBody();
    }

    // GET Tickets created by current USER Employee, calls
    // ticketmicroservice--TicketController.getTicketsByUserId()
    @RequestMapping(value = "/getOpenTickets", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getOpenTicketsFromTicketMicroservice() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Set the header to specify that only JSON data is accepted as response
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getOpenTicketsURL,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

        return responseEntity.getBody();
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.approveTicket()
    // Approves Ticket and is only accessible (frontend) by MANAGER(S)
    @RequestMapping(value = "/approveTicket/{ticketId}", method = RequestMethod.PUT)
    @ResponseBody
    public String approveTicket(Principal principal,
            @PathVariable Long ticketId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String comments) throws UnsupportedEncodingException {
        String URLtoSend = approveTicketURL + ticketId + "?managerId="
                + employeeService.findByName(principal.getName()).getId();
        if (assigneeId != null) {
            URLtoSend = URLtoSend + "&assigneeId=" + assigneeId;
        }
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        return responseEntity.getBody();
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.rejectTicket()
    // Rejects Ticket and is only accessible (frontend) by MANAGER(S)
    @RequestMapping(value = "/rejectTicket/{ticketId}", method = RequestMethod.PUT)
    @ResponseBody
    public String rejectTicket(Principal principal,
            @PathVariable Long ticketId,
            @RequestParam(required = false) String comments) throws UnsupportedEncodingException {
        System.out.println();
        System.out.println(comments);
        String URLtoSend = rejectTicketURL + ticketId + "?managerId="
                + employeeService.findByName(principal.getName()).getId();
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        return responseEntity.getBody();
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.resolveTicket()
    // Resolve Ticket and is only accessible (frontend) by ADMIN(S)
    @RequestMapping(value = "/resolveTicket/{ticketId}", method = RequestMethod.PUT)
    @ResponseBody
    public String resolveTicket(Principal principal,
            @PathVariable Long ticketId,
            @RequestParam(required = false) String comments) throws UnsupportedEncodingException {
        String URLtoSend = resolveTicketURL + ticketId + "?adminId="
                + employeeService.findByName(principal.getName()).getId();
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        return responseEntity.getBody();
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.reopenTicket()
    // Reopen Ticket and is only accessible (frontend) by USER(S)
    @RequestMapping(value = "/reopenTicket/{ticketId}", method = RequestMethod.PUT)
    @ResponseBody
    public String reopenTicket(Principal principal,
            @PathVariable Long ticketId,
            @RequestParam(required = false) String comments) throws UnsupportedEncodingException {
        String URLtoSend = reopenTicketURL + ticketId + "?userId="
                + employeeService.findByName(principal.getName()).getId();
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        return responseEntity.getBody();
    }

    // PUT Ticket, calls ticketmicroservice--TicketController.closeTicket()
    // Closes Ticket and is only accessible (frontend) by USER(S)
    @RequestMapping(value = "/closeTicket/{ticketId}", method = RequestMethod.PUT)
    @ResponseBody
    public String closeTicket(Principal principal,
            @PathVariable Long ticketId,
            @RequestParam(required = false) String comments) throws UnsupportedEncodingException {
        String URLtoSend = closeTicketURL + ticketId + "?userId="
                + employeeService.findByName(principal.getName()).getId();
        if (comments != null) {
            URLtoSend = URLtoSend + "&comments=" + URLEncoder.encode(comments, "UTF-8");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.PUT,
                requestEntity,
                String.class);

        return responseEntity.getBody();
    }

    // DELETE Ticket, calls ticketmicroservice--TicketController.deleteTicket()
    // Closes Ticket and is accessible (frontend) by USER(S), MANAGER(S), ADMIN(S)
    @RequestMapping(value = "/deleteTicket/{ticketId}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteTicket(Principal principal, @PathVariable Long ticketId) {
        String URLtoSend = deleteTicketURL + ticketId;
        System.out.println(URLtoSend);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URLtoSend,
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        return responseEntity.getBody();
    }
}