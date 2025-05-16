<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ page isELIgnored="false" %> 
 <%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
 <%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<script src="https://code.jquery.com/jquery-3.6.3.min.js" integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU=" crossorigin="anonymous"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<head>
<meta charset="UTF-8">
<title>Register</title>
</head>
	<body>
		<form action='/signup' method='post'>	
		UserName: <input type='text' name='userName'/></br>
		Email: <input type='text' name='userEmail'/></br>
		Password: <input type='password' name='password'/></br>
		Manager ID: <input type='number' name='managerId'/></br>
		Department: <input type='text' name='department'/></br>
		Project: <input type='text' name='project'/></br>
		<h5>Role:</h5>
		<label>
			<input type='checkbox' name='roles' value="ADMIN"> ADMIN
		</label>
		<br>
		<label>
			<input type='checkbox' name='roles' value="MANAGER"> MANAGER
		</label>
		<br>
		<label>
			<input type='checkbox' name='roles' value="USER"> USER
		</label>
		<br>

		<input type='submit' value='SIGNUP' id='signup'>
		</form>
	</body>
</html>