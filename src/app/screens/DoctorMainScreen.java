package app.screens;

import app.Main;
import interfaces.*;
import models.entities.*;
import app.screens.doctor.*;

import java.util.*;

public class DoctorMainScreen implements Screen{

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
                        System.out.println("Logging out...");
                        Main.displayMain(scanner);
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
