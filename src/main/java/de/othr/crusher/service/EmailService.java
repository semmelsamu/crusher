package de.othr.crusher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails using Brevo SMTP.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    /**
     * Sends a simple text email.
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param body    email body text
     */
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    /**
     * Sends a welcome email to a newly registered user.
     *
     * @param to       recipient email address
     * @param username the user's name
     */
    public void sendWelcomeEmail(String to, String username) {
        String subject = "Welcome to Crusher!";
        String body = String.format(
            "Hi %s,\n\n" +
            "Welcome to Crusher - your boulder tracking app!\n\n" +
            "We're excited to have you on board. You can now:\n" +
            "- Track your boulder sessions\n" +
            "- Monitor your progress\n" +
            "- Discover new routes\n\n" +
            "Happy climbing!\n\n" +
            "Your Crusher Team",
            username
        );

        sendEmail(to, subject, body);
    }
}
