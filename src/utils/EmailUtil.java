package utils;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    public static void sendEmail(String to, String subject, String body) {
        // Set up properties for the mail session
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.example.com"); // Replace with your SMTP server
        properties.put("mail.smtp.port", "587"); // Port number

        // Create a session with an Authenticator
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your_email@example.com", "your_password"); // Replace with your credentials
            }
        });

        try {
            // Create a MimeMessage
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("your_email@example.com")); // Replace with your email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        // Example usage
//        sendEmail("recipient@example.com", "Test Subject", "Hello, this is a test email!");
//    }
}
