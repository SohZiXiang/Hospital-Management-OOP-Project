package app.screens;
import app.screens.patient.ViewAppointmentOutcomeScreen;
import app.screens.patient.ViewMedicalRecord;
import app.screens.patient.ViewPatientAppointmentScreen;
import interfaces.Screen;
import models.entities.User;
import utils.ActivityLogUtil;
import utils.SMSUtil;

import java.util.Scanner;

public class PatientScreen implements Screen {
    public void display(Scanner scanner, User user) {

        while (true) {
            System.out.println();
            System.out.println(" ____       _   _            _   ");
            System.out.println("|  _ \\ __ _| |_(_) ___ _ __ | |_ ");
            System.out.println("| |_) / _` | __| |/ _ \\ '_ \\| __|");
            System.out.println("|  __/ (_| | |_| |  __/ | | | |_ ");
            System.out.println("|_|   \\__,_|\\__|_|\\___|_| |_|\\__|");
            System.out.println();
            System.out.println("Welcome, Patient " + user.getName());
            System.out.println("What would you like to do?");
            System.out.println("1. Manage Medical Records");
            System.out.println("2. Manage Appointment");
            System.out.println("3. View Past Appointment Outcome Records");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        ViewMedicalRecord viewMedicalRecord = new ViewMedicalRecord();
                        viewMedicalRecord.display(scanner,user);
                        break;
                    case 2:
                        ViewPatientAppointmentScreen viewPatientAppointmentScreen = new ViewPatientAppointmentScreen();
                        viewPatientAppointmentScreen.display(scanner,user);
                        break;
                    case 3:
                        ViewAppointmentOutcomeScreen viewAppointmentOutcomeScreen = new ViewAppointmentOutcomeScreen();
                        viewAppointmentOutcomeScreen.display(scanner,user);
                        break;
                    case 4:
                        ActivityLogUtil.logout(scanner, user);
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
