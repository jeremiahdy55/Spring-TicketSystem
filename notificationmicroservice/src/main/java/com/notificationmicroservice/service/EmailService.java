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
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
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

    public void sendResolutionEmail(String recipient, String subject, String body, List<JsonNode> tableData) {
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
            byte[] pdfData = generateTicketHistoryTablePdf(tableData);
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

    public byte[] generateTicketHistoryTablePdf(List<JsonNode> data) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            // document.add(new Paragraph("Hello World! This is a PDF from MIME message from
            // notfiMS"));
            document.add(Chunk.NEWLINE);

            if (!data.isEmpty()) {
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