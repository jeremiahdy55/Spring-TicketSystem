<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ page isELIgnored="false" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib prefix="sec"
uri="http://www.springframework.org/security/tags" %> <%@ taglib
uri="http://www.springframework.org/tags/form" prefix="frm" %>
<!DOCTYPE html>
<html>
  <script
    src="https://code.jquery.com/jquery-3.6.3.min.js"
    integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU="
    crossorigin="anonymous"
  ></script>
  <link
    rel="stylesheet"
    href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css"
  />
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js"></script>
  <link
    rel="stylesheet"
    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"
  />
  <head>
    <meta charset="UTF-8" />
    <title>HomePage</title>
  </head>
  <body>
    <div class="container mt-5">
      <sec:authorize access="isAuthenticated()">
        <h2>Welcome, <sec:authentication property="name"/>!</h2>
      </sec:authorize>
      <button class="btn btn-primary" id="getTicketsBtn">Get All Tickets</button>
      <br>
      <br>
      <input type="text" id="ticketIdToGetHistory" placeholder="history: ticket ID" />
      <br>
      <button class="btn btn-primary" id="getTicketHistoryBtn">Get Ticket History</button>
      <br>
      <br>
      <button class="btn btn-primary">USER</button>
      <br>
      <br>
      <sec:authorize access='hasAnyAuthority("ADMIN")'>
        <button class="btn btn-primary">ADMIN</button>
      </sec:authorize>
      <br>
      <br>
      <sec:authorize access='hasAnyAuthority("MANAGER")'>
        <button class="btn btn-primary">MANAGER</button>
      </sec:authorize>
      <br>
      <br>
      <div id="result" class="mt-4"></div>
  </div>
  </body>
</html>
