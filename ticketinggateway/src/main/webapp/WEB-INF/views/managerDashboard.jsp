<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manager Dashboard</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS and CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/myStyles.css">
</head>
<script src="/js/load-ticket-elements-v2.js" ></script>
<script defer>
    let roles = JSON.parse('${roles}');
    let userId = JSON.parse('${userId}');
    $(document).ready(function() {
        // Create a reusable function
        function loadTicketsDivManager(baseURL) {
            $.ajax({ // In the wrapping AJAX call, get a list of all ADMINs
                url: '/getAdminEmployees',
                method: 'GET',
                dataType: 'json',
                success: function (adminIdList) {
                    console.log(adminIdList)
                    $.ajax({ // If successful in getting ADMINs, get the corresponding ticket data
                        url: baseURL,
                        method: 'GET',
                        contentType: 'application/json',
                        success: function (data) { // If successful in getting ticket data, load the table
                            let htmlContent = (data.length !== 0) ? loadTicketTableHtml(data, roles, adminIdList) : '<h4>No Tickets Found</h4>';
                            
                            // Show the resulting table HTML content
                            $('#ticketsDiv').html(htmlContent);
                            if (htmlContent === '<h4>No Tickets Found</h4>') { // If there's action to be taken, allow comments
                                $('#commentsSection').prop('hidden', true);
                            } else {
                                $('#commentsSection').prop('hidden', false);
                            };
                        },
                        error: function (xhr, status, error) {
                            console.log(error);
                        }
                    });
                },
                error: function (xhr, status, error) {
                    console.log(error);
                }
            });
        };

        // Call the reusable function with whatever HTTP request the select has at page load and on change
        loadTicketsDivManager($('#selectTicketsToLoad').val());
        $('#selectTicketsToLoad').on('change', function() {
            const baseURL = $(this).val();
            loadTicketsDivManager(baseURL);
        });
    });
</script>
<body>
<%@ include file="navbar.jsp" %>
<div class="container mt-4">
    <h1>Manager Dashboard</h1>
    <div class="mb-3 mt-3">
        <select class="form-select" id="selectTicketsToLoad" name="selectTicketsToLoad">
            <option value="/getOpenTickets" selected>Tickets Awaiting Approval</option>
            <option value="/getAllTickets">Get All Tickets</option>
        </select>
    </div>
    <div class="row" id="ticketsDiv"></div>
    <div class="mb-3" id="commentsSection" hidden>
        <strong>Comments</strong>
        <textarea class="form-control" id="comments" rows="3" placeholder="Your message..."></textarea>
    </div>
  </div>
</body>