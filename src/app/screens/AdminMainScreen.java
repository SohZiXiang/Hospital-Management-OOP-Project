package app.screens;

import app.Main;
import app.screens.admin.*;
import interfaces.*;
import models.entities.*;
import utils.ActivityLogUtil;

import java.util.*;

public class AdminMainScreen implements Screen{

    @Override
    public void display(Scanner scanner, User user) {
        Administrator admin = (Administrator) user;
        while (true) {
            System.out.println("    _       _           _       ");
            System.out.println("   / \\   __| |_ __ ___ (_)_ __  ");
            System.out.println("  / _ \\ / _` | '_ ` _ \\| | '_ \\ ");
            System.out.println(" / ___ \\ (_| | | | | | | | | | |");
            System.out.println("/_/   \\_\\__,_|_| |_| |_|_|_| |_|");
            System.out.println("\nWelcome, Administrator: " + admin.getName());
            System.out.println("What would you like to do?");
            System.out.println("\n--- Administrator Menu ---");
            System.out.println("1. View and Manage Hospital Staff");
            System.out.println("2. View Appointments details");
            System.out.println("3. View and Manage Medication Inventory");
            System.out.println("4. Approve Replenishment Requests");
            System.out.println("5. View Activity Logs");
            System.out.println("6. View and Manage Hospital Patient");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        ManageStaffScreen manageStaffScreen = new ManageStaffScreen();
                        manageStaffScreen.display(scanner, user);
                        break;
                    case 2:
                        ViewAppointmentsScreen viewAppointmentsScreen = new ViewAppointmentsScreen();
                        viewAppointmentsScreen.display(scanner, user);
                        break;
                    case 3:
                        ManageInventoryScreen manageInventoryScreen = new ManageInventoryScreen();
                        manageInventoryScreen.display(scanner, user);
                        break;
                    case 4:
                        ApproveReplenishmentScreen approveReplenishmentScreen = new ApproveReplenishmentScreen();
                        approveReplenishmentScreen.display(scanner, user);
                        break;
                    case 5:
                        ViewActivityLogScreen logScreen = new ViewActivityLogScreen();
                        logScreen.display(scanner, user);
                    case 6:
                        ManagePatientScreen managePatientScreen = new ManagePatientScreen();
                        managePatientScreen.display(scanner, user);
                        break;
                    case 7:
                        ActivityLogUtil.logout(scanner, user);
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
