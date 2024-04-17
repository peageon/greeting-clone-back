package com.greeting.greetingcloneback.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    public void sendSignupMail(String to, String firstName, String lastName) {
        MimeMessage mail = javaMailSender.createMimeMessage();
        try {
            mail.setRecipients(MimeMessage.RecipientType.TO, to);
            mail.setSubject("Welcome to greeting!");
            String body = "";
            body += "<h3>" + "Thanks for Signing up with Greeting!" + "</h3>";
            mail.setText(body,"UTF-8", "html");
        } catch(MessagingException e) {
            e.printStackTrace();
        }
        javaMailSender.send(mail);
    }
}
