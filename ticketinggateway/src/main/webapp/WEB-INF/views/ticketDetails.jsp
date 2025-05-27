<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ticket Details</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS and CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/myStyles.css">
    <script src="/js/load-ticket-elements-v2.js"></script>
</head>
<script>
    let ticketId = JSON.parse('${ticketId}');
    let roles = JSON.parse('${roles}')
    $(document).ready(function() {
        $.ajax({ // Get a list of all ADMIN employees
                url: '/getAdminEmployees', 
                method: 'GET',
                dataType: 'json',
                success: function (adminIdList) { // If successful in getting all ADMINs
                    $.ajax({ // Get the ticket we want details for
                        url: '/getTicket/'+ ticketId, 
                        method: 'GET',
                        contentType: 'application/json',
                        success: function (data) {
                            // If successful in getting ticket data, load the ticket Card on the left-hand side
                            let htmlContent = loadTicketCardHtml(data, roles, adminIdList);
                            $('#ticketCard').html(htmlContent);
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
        
        $.ajax({ // Request the ticket history of the ticket we're viewing
            url: '/getHistory/'+ ticketId, 
            method: 'GET',
            contentType: 'application/json',
            success: function (data) {
                // If sucessful, load the ticket history action logs as a table sorted by latests on top
                let htmlContent = loadTicketHistoryTableHtml(data);
                $('#historyTable').html(htmlContent);
            },
            error: function (xhr, status, error) {
                console.log(error);
            }
        });
    });
</script>
<body>
<%@ include file="navbar.jsp" %>
<div class="container mt-4">
    <h1>Ticket Details & History</h1>
    <div class="row">
      <!-- Left column: Ticket details card -->
      <div class="col-md-4">
        <div id="ticketCard"></div>
      </div>
  
      <!-- Right column: Scrollable table -->
      <div class="col-md-8">
        <div id="historyTable" style="max-height: 400px; overflow-y: auto;"></div>
      </div>
    </div>
  </div>
</body>