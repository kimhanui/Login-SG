package com.smilegate.loginsg.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class MailingServivce {

    private final JavaMailSender mailSender;
    private static String FROM_ADDRESS;
    private static final String DEFAULT_SUBJECT = "Email Verification";
    private Random random = new Random();

    @Autowired
    public MailingServivce(JavaMailSender mailSender, @Value("${spring.mail.dev4kiki@gmail.com") String from) {
        this.mailSender = mailSender;
        this.FROM_ADDRESS = from;
    }

    public void sendMailForResetPW(String toAddress) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toAddress);
        message.setFrom(FROM_ADDRESS);
        message.setSubject(DEFAULT_SUBJECT);
        message.setText(textTemplate(getRandom6code()));
        mailSender.send(message);
    }

    private String textTemplate(String resetCode) {
        return String.format("<div>Hi, please verify your email" +
                "<br>code: <span>%s</span>" +
                "<br>Thank you." +
                "</div>", resetCode);
    }

    private String getRandom6code() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            char c = (char) (random.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }
}