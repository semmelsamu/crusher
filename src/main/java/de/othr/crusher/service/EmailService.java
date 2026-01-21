package de.othr.crusher.service;

import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.EventEntity;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/** Service for sending emails using Brevo SMTP. */
@Service
public class EmailService {

  @Autowired private JavaMailSender mailSender;

  @Value("${app.mail.from}")
  private String fromEmail;

  /**
   * Sends a simple text email.
   *
   * @param to recipient email address
   * @param subject email subject
   * @param body email body text
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
   * @param to recipient email address
   * @param username the user's name
   */
  public void sendWelcomeEmail(String to, String username) {
    String subject = "Welcome to Crusher!";
    String body =
        String.format(
            "Hi %s,\n\n"
                + "Welcome to Crusher - your boulder tracking app!\n\n"
                + "We're excited to have you on board. You can now:\n"
                + "- Track your boulder sessions\n"
                + "- Monitor your progress\n"
                + "- Discover new routes\n\n"
                + "Happy climbing!\n\n"
                + "Your Crusher Team",
            username);

    sendEmail(to, subject, body);
  }

  /**
   * Sends an email notification about new boulders added to a gym sector.
   *
   * @param to recipient email address
   * @param username the user's name
   * @param gymName name of the gym
   * @param sectorName name of the sector
   * @param boulders list of new boulders
   */
  public void sendNewBouldersEmail(
      String to, String username, String gymName, String sectorName, List<BoulderEntity> boulders) {
    String subject = String.format("New Routes at %s!", gymName);

    StringBuilder body = new StringBuilder();
    body.append(String.format("Hi %s,\n\n", username));
    body.append(
        String.format(
            "Great news! %d new boulder(s) have been added to the %s sector at %s:\n\n",
            boulders.size(), sectorName, gymName));

    for (BoulderEntity boulder : boulders) {
      body.append(
          String.format(
              "• %s - Grade %s (%s)\n",
              boulder.getColor().name().replace("_", " "),
              boulder.getGrade().getName(),
              boulder.getDescription() != null ? boulder.getDescription() : "No description"));
    }

    body.append("\nCome check them out and crush some new routes!\n\n");
    body.append("Happy climbing!\n\n");
    body.append("Your Crusher Team");

    sendEmail(to, subject, body.toString());
  }

  /**
   * Sends an email notification about a new event created by a gym.
   *
   * @param to recipient email address
   * @param username the user's name
   * @param gymName name of the gym
   * @param event event details
   */
  public void sendNewEventEmail(String to, String username, String gymName, EventEntity event) {
    String subject = String.format("New Event at %s: %s", gymName, event.getTitle());

    StringBuilder body = new StringBuilder();
    body.append(String.format("Hi %s,\n\n", username));
    body.append(String.format("%s just created a new event.\n\n", gymName));
    body.append(String.format("Title: %s\n", event.getTitle()));
    body.append(String.format("When: %s\n", formatEventSchedule(event)));
    body.append(String.format("Details:\n%s\n\n", event.getDescription()));
    body.append("See you on the wall!\n\n");
    body.append("Your Crusher Team");

    sendEmail(to, subject, body.toString());
  }

  private String formatEventSchedule(EventEntity event) {
    if (event.isPeriodic()) {
      String weekday =
          event.getWeekday() == null ? "Unknown day" : titleCase(event.getWeekday().name());
      String frequency =
          event.getFrequency() == null ? "Recurring" : event.getFrequency().getLabel();
      return String.format("Every %s (%s) at %s", weekday, frequency, event.getTime());
    }

    if (event.getDate() == null) {
      return String.format("Date TBD at %s", event.getTime());
    }

    String date = event.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy"));
    return String.format("%s at %s", date, event.getTime());
  }

  private String titleCase(String value) {
    String lower = value.toLowerCase(Locale.ENGLISH);
    return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
  }
}
