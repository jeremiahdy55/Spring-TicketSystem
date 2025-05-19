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
  <style>
    /* need to define the spacing manually
       something to do with how boostrap interacts with Spring security elements */
    .btn-wrapper {
      margin: 0 10px;
    }
  </style>
  <head>
    <meta charset="UTF-8" />
    <title>HomePage</title>
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
          <a href="/your-target-url" class="card text-decoration-none text-dark" style="width: 18rem;">
            <img src="${pageContext.request.contextPath}/images/admin.svg" class="card-img-top" alt="Admin Image">
            <div class="card-body">
              <h5 class="card-title">Admin Dashboard</h5>
            </div>
          </a>
        </sec:authorize>

        <!-- MANAGER -->
        <sec:authorize access='hasAnyAuthority("MANAGER")'>
          <a href="/your-target-url" class="card text-decoration-none text-dark" style="width: 18rem;">
            <img src="${pageContext.request.contextPath}/images/manager.svg" class="card-img-top" alt="Manager Image">
            <div class="card-body">
              <h5 class="card-title">Manager Dashboard</h5>
            </div>
          </a>
        </sec:authorize>

  
        <!-- USER -->
        <sec:authorize access='hasAnyAuthority("USER")'>
          <a href="/your-target-url" class="card text-decoration-none text-dark" style="width: 18rem;">
            <img src="${pageContext.request.contextPath}/images/user.svg" class="card-img-top" alt="User Image">
            <div class="card-body">
              <h5 class="card-title">User Dashboard</h5>
            </div>
          </a>
        </sec:authorize>
      </div>

    </div>
  </body>
</html>
