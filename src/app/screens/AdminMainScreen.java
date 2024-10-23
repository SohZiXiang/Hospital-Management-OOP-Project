package app.screens;

import app.Main;
import app.screens.admin.*;
import interfaces.*;
import models.entities.*;

import java.util.*;

public class AdminMainScreen implements Screen{

    @Override
    public void display(Scanner scanner, User user) {
        while (true) {
            System.out.println("    _       _           _       ");
            System.out.println("   / \\   __| |_ __ ___ (_)_ __  ");
            System.out.println("  / _ \\ / _` | '_ ` _ \\| | '_ \\ ");
            System.out.println(" / ___ \\ (_| | | | | | | | | | |");
            System.out.println("/_/   \\_\\__,_|_| |_| |_|_|_| |_|");
            System.out.println("\n--- Administrator Menu ---");
            System.out.println("1. View and Manage Hospital Staff");
            System.out.println("2. View Appointments details");
            System.out.println("3. View and Manage Medication Inventory");
            System.out.println("4. Approve Replenishment Requests");
            System.out.println("5. Logout");
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
                        // Navigate to View Appointments Screen
//                        ViewAppointmentsScreen viewAppointmentsScreen = new ViewAppointmentsScreen();
//                        viewAppointmentsScreen.display(scanner, user);
                        break;
                    case 3:
                        ManageInventoryScreen manageInventoryScreen = new ManageInventoryScreen();
                        manageInventoryScreen.display(scanner, user);
                        break;
                    case 4:
//                        // Navigate to Approve Replenishment Requests Screen
//                        ApproveReplenishmentScreen approveReplenishmentScreen = new ApproveReplenishmentScreen();
//                        approveReplenishmentScreen.display(scanner, user);
//                        break;
                    case 5:
                        System.out.println("Logging out...");
                        Main.displayMain(scanner);
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
