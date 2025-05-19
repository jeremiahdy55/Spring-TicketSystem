<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <%@ page isELIgnored="false" %> 
 <%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
 <%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
 <!DOCTYPE html>
<html>
<head>
    <title>Ticket System Signup</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<body>
	<div class="container mt-5" style="max-width: 500px;">
	  <h2 class="mb-4">Sign Up</h2>
	  <form action="/signup" method="post">
		<div class="form-group mb-3">
		  <label for="userName">User Name</label>
		  <input type="text" class="form-control" id="userName" name="userName" required>
		</div>
  
		<div class="form-group mb-3">
		  <label for="userEmail">Email</label>
		  <input type="email" class="form-control" id="userEmail" name="userEmail" required>
		</div>
  
		<div class="form-group mb-3">
		  <label for="password">Password</label>
		  <input type="password" class="form-control" id="password" name="password" required>
		</div>
  
		<div class="form-group mb-3">
		  <label for="managerId">Manager ID</label>
		  <input type="number" class="form-control" id="managerId" name="managerId">
		</div>
  
		<div class="form-group mb-3">
		  <label for="department">Department</label>
		  <input type="text" class="form-control" id="department" name="department">
		</div>
  
		<div class="form-group mb-4">
		  <label for="project">Project</label>
		  <input type="text" class="form-control" id="project" name="project">
		</div>
  
		<h5>Role:</h5>
		<div class="d-flex gap-3 mb-4">
			<div class="form-check">
			<input class="form-check-input" type="checkbox" value="ADMIN" id="roleAdmin" name="roles">
			<label class="form-check-label" for="roleAdmin">
				ADMIN
			</label>
			</div>
			<div class="form-check">
			<input class="form-check-input" type="checkbox" value="MANAGER" id="roleManager" name="roles">
			<label class="form-check-label" for="roleManager">
				MANAGER
			</label>
			</div>
			<div class="form-check">
			<input class="form-check-input" type="checkbox" value="USER" id="roleUser" name="roles">
			<label class="form-check-label" for="roleUser">
				USER
			</label>
			</div>
		</div>
  
		<button type="submit" class="btn btn-primary w-100" id="signup">SIGNUP</button>
	  </form>
	</div>
  </body>
</html>