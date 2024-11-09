package utils;

import app.loaders.*;
import models.entities.*;
import models.enums.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * Utility class for sending emails and notifying about inventory status.
 * This class supports sending email messages using SMTP and checks inventory levels to notify when stock is low.
 */
public class EmailUtil {

    /**
     * Sends an email using the Mailtrap SMTP service.
     *
     * @param recipient The email address of the recipient.
     * @param subject   The subject of the email.
     * @param body      The body content of the email.
     */
    public static void sendEmail(String recipient, String subject, String body) {
        final String username = "ce5ac92f0e5561"; //use mailtrap username,pw
        final String password = "be9161717bdf85";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.mailtrap.io");
        properties.put("mail.smtp.port", "587"); // any port specified by Mailtrap
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
//            System.out.println("Mail successfully sent via Mailtrap");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks the inventory levels and sends a notification email if any medicine is below the low stock alert level.
     *
     * @param recipient The email address to receive the low stock alert.
     */
    public static void checkInvAndNotify(String recipient) {
        String filePath = FilePaths.INV_DATA.getPath();
        InventoryLoader loader = new InventoryLoader();
        List<Medicine> medicineList = loader.loadData(filePath);

        StringBuilder alertBody = new StringBuilder("Alert! There is low stock for the following medicines:\n\n");
        boolean lowStockFound = false;

        for (Medicine medicine : medicineList) {
            if (medicine.getQuantity() < medicine.getLowStockAlert()) {
                alertBody.append("Medicine: ").append(medicine.getName())
                        .append(", Current Stock: ").append(medicine.getQuantity())
                        .append(", Low Stock Alert Level: ").append(medicine.getLowStockAlert())
                        .append("\n");
                lowStockFound = true;
            }
        }

        if (lowStockFound) {
            String subject = "Low Stock Alert";
            sendEmail(recipient, subject, alertBody.toString());
        } else {
//            System.out.println("No medicines below low stock alert.");
        }
    }
}
