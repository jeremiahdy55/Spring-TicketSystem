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

    public void sendResolutionEmail(String recipient, String subject, String body, List<JsonNode> tableData) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true); // true for HTML

        // Knowing the subject line's formula is "<STATUS> ticket ID: <ticketID>"", get the ticketID value
        String ticketId = subject.substring(subject.lastIndexOf(' ') + 1);

        // Add a PDF attachment
        byte[] pdfData = generateTablePdf(tableData);
        InputStreamSource attachment = new ByteArrayResource(pdfData);
        String fileName = "ticket_" + ticketId +"_history.pdf";
        helper.addAttachment(fileName, attachment);

        mailSender.send(mimeMessage);
    }

    public byte[] generateTablePdf(List<JsonNode> data) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            // document.add(new Paragraph("Hello World! This is a PDF from MIME message from notfiMS"));
            document.add(Chunk.NEWLINE);

            if (!data.isEmpty()) {
                // JsonNode firstRow = data.get(0);
                List<String> columns = List.of("actionDate", "actionBy", "action", "comments");
                // data.get(0).fieldNames().forEachRemaining(columns::add);

                PdfPTable table = new PdfPTable(columns.size());
                float[] columnWidths = {4f, 2f, 3f, 6f};
                table.setWidths(columnWidths);
                table.setWidthPercentage(100);

                // Build header
                for (String col : columns) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(col));
                    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCell.setPadding(5f);
                    table.addCell(headerCell);
                }

                // Build rows
                for (JsonNode row : data) {
                    for (String col : columns) {
                        String cellValue = row.has(col) ? row.get(col).asText() : "";
                        PdfPCell cell = new PdfPCell(new Phrase(cellValue));
                        cell.setPadding(5f);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);

                        // Allow wrapping on the comments column
                        if (col.equals("comments")) {
                            cell.setNoWrap(false);
                        }

                        table.addCell(cellValue);
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
}