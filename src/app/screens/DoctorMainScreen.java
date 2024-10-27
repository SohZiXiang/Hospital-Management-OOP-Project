package app.screens;

import app.Main;
import interfaces.*;
import models.entities.*;
import app.screens.DoctorScreens.*;

import java.text.NumberFormat;
import java.util.*;

public class DoctorMainScreen implements Screen{

    @Override
    public void display(Scanner scanner, User user) {
        while (true) {
            System.out.printf("Welcome, %s: %s\n", user.getRole(), user.getName());
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
