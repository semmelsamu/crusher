package de.othr.crusher.service;

import de.othr.crusher.model.BoulderEntity;
import java.util.List;
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

    /**
     * Sends an email notification about new boulders added to a gym sector.
     *
     * @param to         recipient email address
     * @param username   the user's name
     * @param gymName    name of the gym
     * @param sectorName name of the sector
     * @param boulders   list of new boulders
     */
    public void sendNewBouldersEmail(String to, String username, String gymName, String sectorName, List<BoulderEntity> boulders) {
        String subject = String.format("New Routes at %s!", gymName);

        StringBuilder body = new StringBuilder();
        body.append(String.format("Hi %s,\n\n", username));
        body.append(String.format("Great news! %d new boulder(s) have been added to the %s sector at %s:\n\n",
            boulders.size(), sectorName, gymName));

        for (BoulderEntity boulder : boulders) {
            body.append(String.format("• %s - Grade %s (%s)\n",
                boulder.getColor().name().replace("_", " "),
                boulder.getGrade().getName(),
                boulder.getDescription() != null ? boulder.getDescription() : "No description"
            ));
        }

        body.append("\nCome check them out and crush some new routes!\n\n");
        body.append("Happy climbing!\n\n");
        body.append("Your Crusher Team");

        sendEmail(to, subject, body.toString());
    }
}
