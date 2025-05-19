<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ticket Details</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/table-auto-width.css">
</head>
<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="/js/load-ticket-elements.js"></script>
<script>
    let ticketId = JSON.parse('${ticketId}');
    let roles = JSON.parse('${roles}')
    $(document).ready(function() {
        $.ajax({
                url: '/getTicket/'+ ticketId, 
                method: 'GET',
                contentType: 'application/json',
                success: function (data) {
                    let htmlContent = loadTicketCardHtml(data, roles);
                    $('#ticketCard').html(htmlContent);
                },
                error: function (xhr, status, error) {
                    console.log(error);
                }
            });
        $.ajax({
            url: '/getHistory/'+ ticketId, 
            method: 'GET',
            contentType: 'application/json',
            success: function (data) {
                let htmlContent = loadTicketHistoryTableHtml(data);
                console.log(htmlContent)
                $('#historyTable').html(htmlContent);
            },
            error: function (xhr, status, error) {
                console.log(error);
            }
        });
    });
</script>
<body>
    
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