package app.screens;

import app.Main;
import interfaces.*;
import models.entities.*;
import app.screens.doctor.*;
import utils.ActivityLogUtil;

import java.util.*;

/**
 * The DoctorMainScreen class provides the main screen interface for doctors, allowing them to navigate
 * through options such as viewing and updating patient medical records, managing appointments, recording
 * appointment outcomes, and logging out.
 */
public class DoctorMainScreen implements Screen{

    /**
     * Displays the Doctor Main Screen, showing a menu with options for the doctor to manage their activities.
     * Doctors can choose to view/update patient records, manage appointments, record appointment outcomes, or logout.
     *
     * @param scanner The Scanner object for user input.
     * @param user    The User object, which is cast to a Doctor in this context.
     */
    @Override
    public void display(Scanner scanner, User user) {
        String roleName =
                user.getRole().name().substring(0, 1).toUpperCase() + user.getRole().name().substring(1).toLowerCase();

        while (true) {
            System.out.println(" ____             _             ");
            System.out.println("|  _ \\  ___   ___| |_ ___  _ __ ");
            System.out.println("| | | |/ _ \\ / __| __/ _ \\| '__|");
            System.out.println("| |_| | (_) | (__| || (_) | |   ");
            System.out.println("|____/ \\___/ \\___|\\__\\___/|_|   ");
            System.out.println("\n");
            System.out.printf("Welcome, %s: %s\n", roleName, user.getName());
            System.out.println("What would you like you to do?");
            System.out.println("1. View/Update Patient Medical Records");
            System.out.println("2. Manage your appointments");
            System.out.println("3. Record Appointment Outcome");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> {
                        PatientRecordsScreen recordsScreen = new PatientRecordsScreen();
                        recordsScreen.display(scanner, user);
                    }
                    case 2 -> {
                        ManageAppointmentsScreen manageAppt = new ManageAppointmentsScreen();
                        manageAppt.display(scanner, user);
                    }
                    case 3 -> {
                        AppointmentOutcomeScreen recordOutcome = new AppointmentOutcomeScreen();
                        recordOutcome.display(scanner, user);
                    }
                    case 4 -> {
                        ActivityLogUtil.logout(scanner, user);
                        return;
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
