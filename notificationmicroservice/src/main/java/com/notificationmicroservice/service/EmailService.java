package com.notificationmicroservice.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String recipient, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);

        // Set the originator email address in secrets.properties
        // This email will be used for all notification emails sent out
        // message.setFrom("something@gmail.com")

        mailSender.send(message);
    }

    public void sendResolutionEmail(String recipient, String subject, String body, JsonNode ticket, List<JsonNode> tableData) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true); // true for HTML

            // Knowing the subject line's formula is "<STATUS> ticket ID: <ticketID>"", get
            // the ticketID value
            String ticketId = subject.substring(subject.lastIndexOf(' ') + 1);

            // Add the PDF attachment
            byte[] pdfData = generateTicketHistoryTablePdf(ticket, tableData);
            InputStreamSource attachment = new ByteArrayResource(pdfData);
            String fileName = "ticket_" + ticketId + "_history.pdf";
            helper.addAttachment(fileName, attachment);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendManagerReminderEmail(String recipient, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true); // true for HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public byte[] generateTicketHistoryTablePdf(JsonNode ticket, List<JsonNode> data) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Make title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
            Paragraph title = new Paragraph("TICKET ID: " + ticket.get("id").asText(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Add the ticket details
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font textFont = new Font(Font.FontFamily.HELVETICA, 12);
            Paragraph ticketDetailsSection = new Paragraph("Ticket Details", sectionFont);
            ticketDetailsSection.setSpacingAfter(10f);
            document.add(ticketDetailsSection);

            document.add(new Paragraph("Title: " + ticket.get("title").asText(), textFont));
            document.add(new Paragraph("Creation Date: " + ticket.get("creationDate").asText(), textFont));
            document.add(new Paragraph("Resolved By: " + ticket.get("assignee").asText(), textFont));
            document.add(new Paragraph("Priority: " + ticket.get("priority").asText(), textFont));
            document.add(new Paragraph("Category: " + ticket.get("category").asText(), textFont));
            document.add(Chunk.NEWLINE);

            // Add the description paragraph
            Paragraph descriptionSection = new Paragraph("Description", sectionFont);
            descriptionSection.add(new Paragraph(ticket.get("description").asText()));
            document.add(descriptionSection);
            document.add(Chunk.NEWLINE);

            // Create the ticketHistory table
            if (!data.isEmpty()) {
                // Table header
                Paragraph ticketHistoryHeader = new Paragraph("Ticket History", sectionFont);
                ticketHistoryHeader.setSpacingAfter(10f);
                document.add(ticketHistoryHeader);

                List<String> columns = List.of("actionDate", "actionBy", "action", "comments");
                PdfPTable table = new PdfPTable(columns.size());
                float[] columnWidths = { 4f, 2f, 3f, 6f };
                table.setWidths(columnWidths);
                table.setWidthPercentage(100);

                // Build header
                for (String col : columns) {
                    table.addCell(buildHeaderCell(col));
                }

                // Build rows
                for (JsonNode row : data) {
                    for (String col : columns) {
                        String cellValue = row.has(col) ? row.get(col).asText() : "";
                        boolean allowWrap = col.equals("comments");
                        table.addCell(buildDataCell(cellValue, allowWrap));
                    }
                }
                document.add(table);
            } else {
                document.add(new Paragraph("No ticket history data available."));
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public PdfPCell buildDataCell(String cellValue, boolean allowWrap) {
        PdfPCell cell = new PdfPCell(new Phrase(cellValue));
        cell.setPadding(5f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);

        // Allow wrapping on the comments column
        if (allowWrap) { cell.setNoWrap(false); }

        return cell;
    }

    public PdfPCell buildHeaderCell(String cellValue) {
        PdfPCell headerCell = new PdfPCell(new Phrase(cellValue));
        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(5f);
        return headerCell;
    }

}