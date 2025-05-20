<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS and CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/myStyles.css">
</head>
<script src="/js/load-ticket-elements-v2.js"></script>
<script>
    let roles = JSON.parse('${roles}');
    let userId = JSON.parse('${userId}');
    $(document).ready(function() {
        function loadTicketsDiv (baseURL) {
            $.ajax({
                url: baseURL + userId, 
                method: 'GET',
                contentType: 'application/json',
                success: function (data) {
                    let htmlContent = (data.length !== 0) ? loadTicketTableHtml(data, roles) : '<h4>No Tickets Found</h4>';
                    $('#ticketsDiv').html(htmlContent);
                },
                error: function (xhr, status, error) {
                    console.log(error);
                }
            });
        };
        loadTicketsDiv($('#selectTicketsToLoad').val());
        $('#selectTicketsToLoad').on('change', function() {
            const baseURL = $(this).val();
            loadTicketsDiv(baseURL);
        });
    });
</script>
<body>
<%@ include file="navbar.jsp" %>
<div class="container mt-4">
    <h1>Admin Dashboard</h1>
    <div class="mb-3 mt-3">
        <select class="form-select" id="selectTicketsToLoad" name="selectTicketsToLoad">
            <option value="/getActiveAssignedTickets/" selected>Active Assigned Tickets</option>
            <option value="/getAssignedTickets/">All Assigned Tickets</option>
        </select>
    </div>
    <div class="row" id="ticketsDiv"></div>
  </div>
</body>