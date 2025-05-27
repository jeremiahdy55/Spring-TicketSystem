package com.notificationmicroservice.email;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String recipient, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);
        // message.setFrom("jeremiah");
        mailSender.send(message);
    }

    public void sendMIMEEmail(String recipient, String subject, String text, List<byte[]> encodedPDFs) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(text, true); // true for HTML

        // Add a PDF attachment
        byte[] pdfData = generatePdf();
        InputStreamSource attachment = new ByteArrayResource(pdfData);
        helper.addAttachment("file.pdf", attachment);

        mailSender.send(mimeMessage);
    }

    public byte[] generatePdf() {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph("Hello World! This is a PDF from MIME message from notfiMS"));
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}