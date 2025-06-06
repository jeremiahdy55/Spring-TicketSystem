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
	<!-- Bootstrap JS Bundle -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</head>
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
			<select class="form-control" id="department" name="department">
			  <option value="IT">IT</option>
			  <option value="Sales">Sales</option>
			  <option value="HR">HR</option>
			  <option value="Accounting">Accounting</option>
			  <option value="Marketing">Marketing</option>
			</select>
		  </div>
  
		<div class="form-group mb-4">
			<label for="project">Project</label>
			<input type="text" class="form-control" id="project" name="project">
		</div>
  
		<h5>Role:</h5>
		<div class="d-flex gap-3 mb-4">
			<div class="form-check">
			    <input class="form-check-input" type="checkbox" value="ADMIN" id="roleAdmin" name="roles">
				<label class="form-check-label" for="roleAdmin">ADMIN</label>
			</div>

			<div class="form-check">
				<input class="form-check-input" type="checkbox" value="MANAGER" id="roleManager" name="roles">
				<label class="form-check-label" for="roleManager">MANAGER</label>
			</div>

			<div class="form-check">
				<input class="form-check-input" type="checkbox" value="USER" id="roleUser" name="roles">
				<label class="form-check-label" for="roleUser">USER</label>
			</div>
		</div>
  
		<button type="submit" class="btn btn-primary w-100" id="signup">SIGNUP</button>
	  </form>
	</div>
  </body>
</html>