package utils;

import app.loaders.*;
import models.entities.*;
import models.enums.*;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class EmailUtil {
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

    public static void checkInventoryAndNotify(String recipient) {
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
