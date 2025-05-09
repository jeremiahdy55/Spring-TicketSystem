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
  <body style="height: 100vh" class="w-100">
    <div class="mt-5 d-flex justify-content-center">
      <button class="btn btn-primary mt-5">USER</button>
      <sec:authorize access='hasAnyAuthority("ADMIN")'>
        <button class="btn btn-primary mt-5">ADMIN</button>
      </sec:authorize>
      <sec:authorize access='hasAnyAuthority("MANAGER")'>
        <button class="btn btn-primary mt-5">MANAGER</button>
      </sec:authorize>
    </div>
  </body>
</html>
