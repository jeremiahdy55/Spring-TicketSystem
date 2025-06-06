// returns a dynamically-created HTML table with Bootstrap styling for each ticket
// this is a preview of the data ticket.description and ticket.fileAttachmentPaths are omitted
function loadTicketTableHtml(ticketData, authority, adminIdList) {
    // Initialize these as empty to not break the HTML
    let approveBtn = ""
    let rejectBtn = ""
    let resolveBtn = ""
    let deleteBtn = ""
    let closeBtn = ""
    let reopenBtn = ""
    let detailsBtn = ""
    let actionsBody = ""
    let assigneeId = ""

    // Initialize as false to prevent unintended loading of HTMl elements
    let managerAction = false
    let adminAction = false
    let userAction = false
    let useActions = false
    
    // Initialize the table header for the delete columns
    let deleteHdr = (["USER", "MANAGER"].some(role => authority.includes(role))) ? '<th>Delete</th>' : "" 

    let adminSelectElement = ""
    let approveContainer = ""
    let htmlContent = ""

    // Sort the ticket data to show the earliest to latest in a top-down manner
    const sortedIdxs =  ticketData.map((ticket, index) => ({index, date: new Date(ticket.creationDate)}))
                        .sort((obj1, obj2) => obj2.date - obj1.date)
                        .map(obj => obj.index)
    const sortedTicketData = sortedIdxs.map(index => ticketData[index])

    // Dynamically load the div with the response data consisting of ticket(s)
    sortedTicketData.forEach(function (ticket) { 

        // Prevent "Assigned By: 0" in webpage display to prevent confusion
        assigneeId = (ticket.assignee != 0) ? ticket.assignee : "NOT ASSIGNED"

        // Determine which actions are suitable for a ticket depending on status and Employee roles
        managerAction = (authority.includes("MANAGER") && ["OPEN", "PENDING_APPROVAL"].includes(ticket.status))
        adminAction = (authority.includes("ADMIN") && ["APPROVED", "ASSIGNED", "REOPENED"].includes(ticket.status))
        userAction = (authority.includes("USER") && ["RESOLVED"].includes(ticket.status))

        // Check if the actionsHdr is needed
        useActions = useActions || managerAction || adminAction || userAction

        // Get the necessary Employee action buttons depending on conditionals above
        resolveBtn = (adminAction) ? getResolveBtn(ticket.id) : ""
        closeBtn = (userAction) ?  getCloseBtn(ticket.id) : ""
        reopenBtn = (userAction) ?  getReopenBtn(ticket.id) : ""
        deleteBtn = (["USER", "MANAGER"].some(role => authority.includes(role))) ? tableDataWrapper(getDeleteBtn(ticket.id)) : ""

        // Build the approveTicket button with an adminSelect element to assign the ticket to a specified ADMIN employee
        // Only if adminIdList is not null
        if (adminIdList) {
            approveBtn = (managerAction) ?  getApproveBtn(ticket.id) : ""
            rejectBtn = (managerAction) ?  getRejectBtn(ticket.id) : ""
            adminSelectElement = (managerAction && adminIdList.length !== 0) ? getAdminSelectElement(adminIdList) : ""
            approveContainer = (approveBtn && adminSelectElement) ? adminSelectElement + approveBtn : ""
        }

        // Build the HTML element that will hold the Employee action buttons and the details Button
        actionsBody = '<div class="d-flex justify-content-center gap-2 flex-wrap" id="actionsBody">'
        detailsBtn = `<button class="btn btn-small btn-secondary" onClick="window.location.href='/ticketDetails/${ticket.id}'">Details: ${ticket.id}</button>`

        // Wrap all the Employee action buttons inside a single table column (Actions)
        actionsBody += resolveBtn + approveContainer + rejectBtn + closeBtn + reopenBtn + '</div>'
        actionsBody = (useActions) ? tableDataWrapper(actionsBody) : ''

        // Build the actual table row content
        htmlContent += '<tr>' + 
            tableDataWrapper(ticket.id) +
            tableDataWrapper(ticket.title) +
            tableDataWrapper(ticket.createdBy) +
            tableDataWrapper(assigneeId) +
            tableDataWrapper(ticket.priority) +
            tableDataWrapper(ticket.status) +
            tableDataWrapper(ticket.creationDate) +
            tableDataWrapper(ticket.category) +
            tableDataWrapper(detailsBtn) +
            actionsBody +
            deleteBtn + '</tr>'
    })

    // Append closing tags
    htmlContent += '</tbody></table></div>'

    // Finish building htmlHead, add the actionHdr and deleteHdr
    // Create the table header and wrapping divs and intialize the content body as empty
    let actionsHdr = (useActions) ?  '<th>Actions</th>': ''
    let htmlHead =  '<div class="table-responsive"><table class="table table-bordered table-auto-width">' +
                        '<thead><tr>' +
                        '<th>ID</th>' +
                        '<th>Title</th>' +
                        '<th>Created By:</th>' +
                        '<th>Assigned To:</th>' +
                        '<th>Priority</th>' +
                        '<th>Status</th>' +
                        '<th>Creation Date</th>' +
                        '<th>Category</th>' +
                        '<th>Details</th>' +
                        actionsHdr + deleteHdr +
                        '</tr></thead><tbody>'
    
    // Return the finished table HTML
    return htmlHead + htmlContent
}
// Create a table of logged TicketHistory objects, sorted from latest-oldest top-bottom
function loadTicketHistoryTableHtml(ticketHistoryData) {
    var htmlContent =   '<div class="table-responsive"><table class="table table-bordered table-auto-width">' +
                        '<thead><tr>' +
                        '<th>Action Date:</th>' +
                        '<th>Action By:</th>' +
                        '<th>Action Status:</th>' +
                        '<th>Comments</th>' +
                        '</tr></thead><tbody>'

    // Sort the history data to show the earliest to latest in a top-down manner
    const sortedIdxs =  ticketHistoryData.map((actionLog, index) => ({index, date: new Date(actionLog.actionDate)}))
                        .sort((obj1, obj2) => obj2.date - obj1.date)
                        .map(obj => obj.index)
    const sortedTicketHistoryData = sortedIdxs.map(index => ticketHistoryData[index])

    // Dynamically load the div with the response data consisting of ticket history(s)
    sortedTicketHistoryData.forEach(function (historyLog) {        
        // Build the actual table row content
        htmlContent += 
            '<tr><td>' + historyLog.actionDate + '</td>' +
            '<td>' + historyLog.actionBy + '</td>' +
            '<td>' + historyLog.action + '</td>' +
            '<td>' + historyLog.comments + '</td></tr>'
    })

    htmlContent += '</tbody></table></div>'
    return htmlContent
}

// Create a card that has all ticket info and actions
function loadTicketCardHtml(ticket, authority, adminIdList) {
    // Declare default values will be overwritten if any of these appropriate values from response are not null
    let fileAttachmentsHTML = ""

    // Determine the necessary actions to display for the current ticket based on ticket.status and Employee.Roles
    let managerAction = (authority.includes("MANAGER") && ["OPEN", "PENDING_APPROVAL"].includes(ticket.status))
    let adminAction = (authority.includes("ADMIN") && ["APPROVED", "ASSIGNED", "REOPENED"].includes(ticket.status))
    let userAction = (authority.includes("USER") && ["RESOLVED"].includes(ticket.status))

    // Get the necessary HTML components
    let rejectBtn = (managerAction) ?  getRejectBtn(ticket.id) : ""
    let resolveBtn = (adminAction) ?  getResolveBtn(ticket.id) : ""
    let closeBtn = (userAction) ?  getCloseBtn(ticket.id) : ""
    let reopenBtn = (userAction) ?  getReopenBtn(ticket.id) : ""
    let deleteBtn = (["USER", "MANAGER"].some(role => authority.includes(role))) ?  getDeleteBtn(ticket.id) : ""

    // Build the approveTicket button with a adminSelect element to assign the ticket to a specified ADMIN employee
    let approveContainer = ""
    let approveBtn = (managerAction) ?  getApproveBtn(ticket.id) : ""
    let adminSelectElement = (managerAction && adminIdList.length !== 0) ? getAdminSelectElement(adminIdList) : ""
    approveContainer = (approveBtn && adminSelectElement) ? adminSelectElement + approveBtn : ""

    // Initialize the card-footer where the buttons for possible actions will go
    let actionsFooter = '<div class="card-footer bg-white"><div class="d-flex justify-content-center gap-2 flex-wrap">' +
                        approveContainer + rejectBtn + resolveBtn + closeBtn + reopenBtn + deleteBtn + '</div></div>'

    // Prevent "Assigned By: 0" to avoid confusion
    let assigneeId = (ticket.assignee != 0) ? ticket.assignee : "NOT ASSIGNED"

    // Construct comments section HTML element
    let commentsSection = '<div class="mb-3"><strong>Comments</strong><textarea class="form-control" id="comments" rows="4" placeholder="Your message..."></textarea></div>'

    // Construct the file attachment links (if any)
    if (ticket.fileAttachmentPaths) {
        fileAttachmentsHTML += '<h6><strong>File Attachments</strong></h6>' + createFileAttachmentsList(ticket.fileAttachmentPaths)
    }
    let htmlContent = '<div class="card"><div class="card-header">Ticket ID: ' + ticket.id + '</div><div class="card-body">' +
        `<p class="card-text"><strong>Title:</strong> ${ticket.title}</p>` +
        `<p class="card-text"><strong>Description:</strong> ${ticket.description}</p>` +
        `<p class="card-text"><strong>Created By:</strong> ${ticket.createdBy}</p>` +
        `<p class="card-text"><strong>Assigned To:</strong> ${assigneeId}</p>` +
        `<p class="card-text"><strong>Priority:</strong> ${ticket.priority}</p>` +
        `<p class="card-text"><strong>Status:</strong> ${ticket.status}</p>` +
        `<p class="card-text"><strong>Creation Date:</strong> ${ticket.creationDate}</p>` +
        `<p class="card-text"><strong>Category:</strong> ${ticket.category}</p>` +
        fileAttachmentsHTML + commentsSection + actionsFooter +'</div></div>'
    return htmlContent
}

// Call the matching TicketMicroserviceClient.java method to change ticket status, with comments optional
function changeTicketStatus(ticketId, comments, baseURL) {
    // construct the RequestParam "comments" if needed
    let modifedComments = (comments && (comments.trim() !== "")) ? ("?comments=" + encodeURIComponent(comments)) : ""
    let requestURL = baseURL + ticketId + modifedComments
    $.ajax({
        url : requestURL,
        method: 'PUT',
        accepts: {
            json: "application/json",
            text: "text/plain"
        },
        success: function () {
            location.reload() // reload the page
        },
        error: function (xhr, status, error) {
            console.log(error)
        }
    })
}

// Call the matching TicketMicroserviceClient.java method to change ticket status, with comments optional
function approveTicket(ticketId, assigneeId, comments) {
    let baseURL = '/approveTicket/'
    // construct the RequestParam "comments" if needed
    let modifiedAssigneeId = (assigneeId) ? ("assigneeId=" + assigneeId) : ""
    let modifiedComments = (comments && (comments.trim() !== "")) ? ("comments=" + encodeURIComponent(comments)) : ""
    let requestParams = ""
    if (modifiedAssigneeId || modifiedComments) {
        requestParams += "?";
        if (modifiedAssigneeId) {
            requestParams += modifiedAssigneeId;
        }
        if (modifiedComments) {
            if (modifiedAssigneeId) requestParams += "&";
            requestParams += modifiedComments;
        }
    }
    let requestURL = baseURL + ticketId + requestParams
    $.ajax({
        url : requestURL,
        method: 'PUT',
        accepts: {
            json: "application/json",
            text: "text/plain"
        },
        success: function () {
            location.reload() // reload the page
        },
        error: function (xhr, status, error) {
            console.log(error)
        }
    })
}

// Delete the ticket
function deleteTicket(ticketId) {
    let baseURL = '/deleteTicket/'
    let requestURL = baseURL + ticketId
    $.ajax({
        url : requestURL,
        method: 'DELETE',
        accepts: {
            json: "application/json",
            text: "text/plain"
        },
        success: function () {
            if (window.location.href.includes('ticketDetails')) {
                // If on the /ticketDetails page, go back (it holds data on one ticket)
                window.history.back() 
            } else {
                location.reload() // reload the page (all other cases show multiple tickets)
            }        
        },
        error: function (xhr, status, error) {
            console.log(error)
        }
    })
}

// Create a list of downloadable links for any file attachments
function createFileAttachmentsList(fileAttachmentPaths) {
    var fileAttachmentsHTML = ""
    if (fileAttachmentPaths) {
        fileAttachmentsHTML = '<ul class="list-group">'
        for (const filePath of fileAttachmentPaths) {
            fileAttachmentsHTML += '<li class="list-group-item"><a href="/uploads/' + filePath + '" download>' + filePath + '</a></li>'
        }
        fileAttachmentsHTML += '</ul>'
    }
    return fileAttachmentsHTML
}

// Simple wrapper function to make a <td> element
function tableDataWrapper(htmlContent) {
    return '<td class="vertical-align-table-cells">' + htmlContent + '</td>'
}

////// Declare the html content for Employee action buttons here //////
function getAdminSelectElement(adminIdList) {
    let selectElement = ""
    if (adminIdList) {
        selectElement = `<select class="form-select" id="adminSelect" name="adminSelect">`
        for (const adminId of adminIdList) { // this is a list of numeric values of each Employee's IDs with Role = ADMIN
            selectElement += `<option value="${adminId}">${adminId}</option>`
        }
        selectElement += '<option value="" selected disabled hidden>Choose Assignee</option></select>'
    }
    return selectElement
}

function getApproveBtn(ticketId) {
    return `<button class="btn btn-small btn-info" onClick="approveTicket(${ticketId},$('#adminSelect').val(),$('#comments').val())">Approve</button>`
}

function getRejectBtn(ticketId) {
    return `<button class="btn btn-small btn-warning" onClick="changeTicketStatus(${ticketId},$('#comments').val(),'/rejectTicket/')">Reject</button>`
}

function getResolveBtn(ticketId) {
    return `<button class="btn btn-small btn-primary" onClick="changeTicketStatus(${ticketId},$('#comments').val(),'/resolveTicket/')">Resolve</button>`
}

function getCloseBtn(ticketId) {
    return `<button class="btn btn-small btn-success" onClick="changeTicketStatus(${ticketId},$('#comments').val(),'/closeTicket/')">Close</button>`
}

function getReopenBtn(ticketId) {
    return `<button class="btn btn-small btn-warning" onClick="changeTicketStatus(${ticketId},$('#comments').val(),'/reopenTicket/')">Reopen</button>`
}

function getDeleteBtn(ticketId) {
    return `<button class="btn btn-small btn-danger" onClick="deleteTicket(${ticketId})">Delete</button>`
}