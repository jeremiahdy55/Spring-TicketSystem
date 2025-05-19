<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Ticket Form</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    $(document).ready(function () {
        $("#ticketForm").on("submit", function (e) {
            e.preventDefault(); // prevent normal form submission

            var formData = new FormData(this);
            console.log(formData)
            $.ajax({
                url: "/postTicket",
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                success: function () {
                    console.log("Ticket submitted successfully:");
                    window.location.href = "/landing" //TODO change to userPage
                },
                error: function (xhr, status, error) {
                    console.error("Error submitting ticket:", error);
                }
            });
        });
    });
</script>


<body>
<div class="container mt-5">
    <sec:authorize access="isAuthenticated()">
        <h3>User ID: ${userId}</h3>
    </sec:authorize>
    <h1>Create ticket</h1>
    <form id="ticketForm">
        <div class="mb-3">
            <label for="title" class="form-label">Title</label>
            <input type="text" class="form-control" id="title" name="title" placeholder="Default Title">
        </div>

        <div class="mb-3">
            <label for="description" class="form-label">Description</label>
            <input type="text" class="form-control" id="description" name="description" placeholder="Default Description">
        </div>

        <div class="mb-3">
            <label for="priority" class="form-label">Priority</label>
            <select class="form-select" id="priority" name="priority">
                <option value="LOW">LOW</option>
                <option value="MEDIUM">MEDIUM</option>
                <option value="HIGH">HIGH</option>
                <!-- this is the placeholder and will disappear once interacted -->
                <option value="" selected disabled hidden>Choose Priority</option>
            </select>
        </div>
        <div class="mb-3">
            <label for="formCategory" class="form-label">Category</label>
            <input type="text" class="form-control" id="category" name="category" placeholder="Default Category">
        </div>

        <div class="mb-3">
            <label for="files" class="form-label">Choose file</label>
            <input class="form-control" type="file" id="files" name="files" multiple>
        </div>

        <div class="mb-3">
            <label for="message" class="form-label">Comments</label>
            <textarea class="form-control" id="comments" rows="4" placeholder="Comments" name="comments"></textarea>
        </div>

        <button type="submit" id="postTicketBtn" class="btn btn-primary w-100">Submit</button>
    </form>
</div>
</body>
</html>