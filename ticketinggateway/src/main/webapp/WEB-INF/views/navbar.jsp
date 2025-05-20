<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!-- Bootstrap Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container-fluid">
    <!-- Navbar toggler/collapse for mobile (optional)
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
        <span class="navbar-toggler-icon"></span>
    </button> -->

    <div class="collapse navbar-collapse d-flex" id="navbarContent">
        <!-- nav links based on ROLE -->
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <!-- Ticket form accessible to all -->
            <li class="nav-item">
                <a class="nav-link active" href="/ticketForm">Create Ticket</a>
            </li>

          <sec:authorize access='hasAnyAuthority("ADMIN")'>
            <li class="nav-item">
              <a class="nav-link active" href="/adminDashboard">Admin Dashboard</a>
            </li>
          </sec:authorize>
  
          <sec:authorize access='hasAnyAuthority("MANAGER")'>
            <li class="nav-item">
              <a class="nav-link active" href="/managerDashboard">Manager Dashboard</a>
            </li>
          </sec:authorize>

          <sec:authorize access='hasAnyAuthority("USER")'>
            <li class="nav-item">
              <a class="nav-link active" href="/userDashboard">User Dashboard</a>
            </li>
          </sec:authorize>
        </ul>
  
        <!-- Flush right -->
        <sec:authorize access="isAuthenticated()">
            <form action="/logout" method="post" class="form-inline ml-auto">
                <button type="submit" class="btn btn-danger btn-sm">Logout</button>
            </form>
        </sec:authorize>
      </div>
  </div>
</nav>