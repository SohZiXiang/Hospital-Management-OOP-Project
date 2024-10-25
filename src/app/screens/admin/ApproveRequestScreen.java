package app.screens.admin;

import app.screens.AdminMainScreen;
import interfaces.*;
import models.entities.User;

import java.util.Scanner;

public class ApproveRequestScreen implements Screen {
    @Override
    public void display(Scanner scanner, User user) {

        while (true) {
            System.out.println("\n--- Manage Hospital Staff ---");
            System.out.println("1. View Staff List");
            System.out.println("2. Add Staff Member");
            System.out.println("3. Update Staff Member");
            System.out.println("4. Remove Staff Member");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
//                        viewStaffList(scanner);
                        break;
                    case 2:
//                        addStaff(scanner);
                        break;
                    case 3:
//                        updateStaff(scanner);
                        break;
                    case 4:
//                        removeStaff(scanner, user);
                        break;
                    case 5:
                        AdminMainScreen adminMainScreen = new AdminMainScreen();
                        adminMainScreen.display(scanner, user);
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

    }
}
