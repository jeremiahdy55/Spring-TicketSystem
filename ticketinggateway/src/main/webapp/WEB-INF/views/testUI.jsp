<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ticket Dashboard</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    var username = '<sec:authentication property="name" />';
    $(document).ready(function () {
        // Get all tickets
        $('#getTicketsBtn').click(function () {
            $.ajax({
                url: '/getAllTickets',  // This is the URL to the controller method
                method: 'GET',
                contentType: 'application/json',
                success: function (data) {
                    // Dynamically update the result div with the response data
                    var htmlContent = '<table class="table table-bordered"><thead><tr><th>#</th><th>Details</th></tr></thead><tbody>';
                    
                    data.forEach(function(ticket, index) {
                        htmlContent += '<tr><td>' + (index + 1) + '</td><td><pre>' + JSON.stringify(ticket, null, 2) + '</pre></td></tr>';
                    });

                    htmlContent += '</tbody></table>';
                    $('#result').html(htmlContent);
                },
                error: function (xhr, status, error) {
                    $('#result').html("<div class='text-danger'>Error: " + error + "</div>");
                }
            });
        });
        // Get ONE ticket
        $('#getTicketByIdBtn').click(function () {
            console.log("Button called: " + '/getTicket/'+ $("#ticketIdtoGet").val())
            $.ajax({
                url: '/getTicket/'+ $("#ticketIdtoGet").val(),  // This is the URL to the controller method
                method: 'GET',
                contentType: 'application/json',
                success: function (data) {
                    $('#result').html("<pre>" + JSON.stringify(data, null, 2) + "</pre>");
                },
                error: function (xhr, status, error) {
                    $('#result').html("<div class='text-danger'>Error: " + error + "</div>");
                }
            });
        });
        // Get history
        $('#getTicketHistoryBtn').click(function () {
            console.log("Button called: " + '/getHistory/'+ $("#ticketIdToGetHistory").val())
            $.ajax({
                url: '/getHistory/'+ $("#ticketIdToGetHistory").val(),  // This is the URL to the controller method
                method: 'GET',
                contentType: 'application/json',
                success: function (data) {
                    $('#result').html("<pre>" + JSON.stringify(data, null, 2) + "</pre>");
                },
                error: function (xhr, status, error) {
                    $('#result').html("<div class='text-danger'>Error: " + error + "</div>");
                }
            });
        });
        // Approve/Reject ticket
        $('#approveOrRejectTicketBtn').click(function () {
            let comments = ($("#approveOrRejectComments").val()).trim()
            let baseURL = ""
            if ("APPROVED".localeCompare($("#approveOrRejectSelect").val()) === 0) {
                console.log("APPROVED")
                baseURL = '/approveTicket/'
            } else if ("REJECTED".localeCompare($("#approveOrRejectSelect").val()) === 0) {
                console.log("REJECTED")
                baseURL = '/rejectTicket/'
            } else {console.log("grimace") 
            return null}
            if (comments !== "") {
                comments = "?comments=" + encodeURIComponent(comments) // construct the RequestParam
            }
            let ticketId = $("#approveOrRejectTicketId").val()
            // console.log("approveOrRejectTicketBtn called: " + '/approveTicket/'+ $("#approveOrRejectTicketId").val() +comments)
            let requestURL = baseURL + ticketId + comments
            console.log("approveOrRejectTicketBtn called: " + requestURL)
            console.log("APPROVED".localeCompare($("#approveOrRejectSelect").val()))
            $.ajax({
                url : requestURL,
                method: 'PUT',
                contentType: 'application/json',
            });
        });
    });
</script>


<body>
<div class="container mt-5">
    <sec:authorize access="isAuthenticated()">
        <h2>Welcome, <sec:authentication property="name"/>!</h2>
        <h3>User ID: ${userId}</h3>
      </sec:authorize>
    <h1 class="mb-4">Ticket Viewing</h1>
    <button class="btn btn-primary" id="getTicketsBtn">Get All Tickets</button>
    <br>
    <br>
    <input type="text" id="ticketIdToGetHistory" placeholder="history: ticket ID" />
    <br>
    <button class="btn btn-primary" id="getTicketHistoryBtn">Get Ticket History</button>
    <br>
    <br>
    <input type="text" id="ticketIdtoGet" placeholder="get: ticket ID" />
    <br>
    <button class="btn btn-primary" id="getTicketByIdBtn">Get Ticket</button>
    <br>
    <br>
    <div id="result" class="mt-4"></div>
    <br>
    <br>
    <input type="text" id="approveOrRejectTicketId" placeholder="approve or reject: ticket ID" />
    <br>
    <input type="text" id="approveOrRejectComments" placeholder="approve or reject: comments" />
    <br>
    <label for="approveOrRejectSelect" class="form-label">Manager-Approve/reject:</label>
    <select class="form-select" id="approveOrRejectSelect" name="approveOrRejectSelect">
        <option value="APPROVED">APPROVED</option>
        <option value="REJECTED">REJECTED</option>

        <!-- this is the placeholder and will disappear once interacted -->
        <option value="" selected disabled hidden>Approve or Reject</option>
      </select>
    <br>
    <button class="btn btn-primary" id="approveOrRejectTicketBtn">Get Ticket</button>
    <br>
    <br>
    <h1>Create ticket</h1>
    <form>
        <div class="mb-3">
            <label for="name" class="form-label">Title</label>
            <input type="text" class="form-control" id="formTitle" placeholder="Title">
        </div>

        <div class="mb-3">
            <label for="name" class="form-label">Description</label>
            <input type="text" class="form-control" id="formDescription" placeholder="Description">
        </div>

        <div class="mb-3">
            <label for="name" class="form-label">Priority</label>
            <input type="text" class="form-control" id="formPriority" placeholder="Priority">
        </div>

        <div class="mb-3">
            <label for="name" class="form-label">Category</label>
            <input type="text" class="form-control" id="formCategory" placeholder="Category">
        </div>

        <div class="mb-3">
            <label for="name" class="form-label">Description</label>
            <input type="text" class="form-control" id="formDescription" placeholder="Description">
        </div>

        <div class="mb-3">
            <label for="formFile" class="form-label">Choose file</label>
            <input class="form-control" type="file" id="formFile" name="files" multiple>
        </div>

        <!-- <div class="mb-3">
            <label for="email" class="form-label">Email address</label>
            <input type="email" class="form-control" id="email" placeholder="john@example.com">
        </div> -->


        <div class="mb-3">
            <label for="message" class="form-label">comments</label>
            <textarea class="form-control" id="comments" rows="4" placeholder="Your message..."></textarea>
        </div>

        <button type="submit" id="createTicketBtn" class="btn btn-primary w-100">Submit</button>
    </form>
    <form action="${pageContext.request.contextPath}/login?logout" method="post">
        <button type="submit" class="btn btn-danger">Logout</button>
    </form>
</div>


</body>
</html>