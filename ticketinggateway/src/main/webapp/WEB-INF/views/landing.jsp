<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ page isELIgnored="false" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib prefix="sec"
uri="http://www.springframework.org/security/tags" %> <%@ taglib
uri="http://www.springframework.org/tags/form" prefix="frm" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Ticket System Landing</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS and CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/myStyles.css">
</head>
<body>
    <%@ include file="navbar.jsp" %>
    <div class="container mt-5 text-center">

        <sec:authorize access="isAuthenticated()">
            <h2>Welcome, <sec:authentication property="name"/>!</h2>
        </sec:authorize>
        <h2>Please choose a profile</h2>
  
        <!-- Flex container for cards -->
        <div class="d-flex justify-content-center gap-3 mt-4">
  
            <!-- ADMIN -->
            <sec:authorize access='hasAnyAuthority("ADMIN")'>
                <a href="/adminDashboard" class="card text-decoration-none text-dark customCardWidth">
                    <img src="${pageContext.request.contextPath}/images/admin.svg" class="card-img-top" alt="Admin Image">
                    <div class="card-body"><h5 class="card-title">Admin Dashboard</h5></div>
                </a>
            </sec:authorize>

            <!-- MANAGER -->
            <sec:authorize access='hasAnyAuthority("MANAGER")'>
                <a href="/managerDashboard" class="card text-decoration-none text-dark" style="width: 18rem;">
                    <img src="${pageContext.request.contextPath}/images/manager.svg" class="card-img-top" alt="Manager Image">
                    <div class="card-body"><h5 class="card-title">Manager Dashboard</h5></div>
                </a>
            </sec:authorize>

            <!-- USER -->
            <sec:authorize access='hasAnyAuthority("USER")'>
                <a href="/userDashboard" class="card text-decoration-none text-dark" style="width: 18rem;">
                    <img src="${pageContext.request.contextPath}/images/user.svg" class="card-img-top" alt="User Image">
                    <div class="card-body"><h5 class="card-title">User Dashboard</h5></div>
                </a>
            </sec:authorize>

        </div>
    </div>
  </body>
</html>
