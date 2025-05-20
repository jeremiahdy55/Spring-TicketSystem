<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ page isELIgnored="false" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> 
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="frm" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/myStyles.css">
</head>

  <body style="height: 100vh" class="w-100">
    <div class="mt-5 d-flex justify-content-center">
      <frm:form action="login" method="post">
        <div class="form-group">
          <label> Please Enter Username: </label>
          <input class="form-control" type="text" name="username" />
        </div>

        <div class="form-group">
          <label> Please Enter Password: </label>
          <input class="form-control" type="password" name="password" />
        </div>
        <!-- <div>
          <span class="text-muted">Forgot your password? </span
          ><a href="#">Reset Here!</a>
        </div> -->
        <div>
          <span class="text-muted">Don't Have an Account? </span
          ><a href="/register">Create Here!</a>
        </div>
        <input class="btn btn-primary mt-5" type="submit" value="Login" />
        <sec:csrfInput />
      </frm:form>
    </div>
  </body>
</html>
