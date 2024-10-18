package app;
import interfaces.*;
import app.loaders.*;

import java.util.*;

public class Main {


    public static void main(String[] args) {
        DataLoader staffLoader = new StaffLoader();
        DataLoader patientLoader = new PatientLoader();
        DataLoader inventoryLoader = new InventoryLoader();

        String staffPath = "data/Staff_List.xlsx";
        String patientPath = "data/Patient_List.xlsx";
        String inventoryPath = "data/Medicine_List.xlsx";

        try {
            staffLoader.loadData(staffPath);
            patientLoader.loadData(patientPath);
            //inventoryLoader.loadData(inventoryPath);
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Welcome to the Hospital Management System");
            System.out.println("Please select an option:");
            System.out.println("1. Log in");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    scanner.close();
                    System.out.println("Exiting the application. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your hospital ID: ");
        String hospitalId = scanner.nextLine();

        if (validateUser(hospitalId)) {
            displayUserMenu(hospitalId);
        } else {
            System.out.println("Invalid hospital ID. Please try again.");
        }
    }

    private static boolean validateUser(String hospitalId) {
        return true;
    }

    private static void displayUserMenu(String hospitalId) {
        System.out.println("Displaying menu for user with hospital ID: " + hospitalId);
    }
}
