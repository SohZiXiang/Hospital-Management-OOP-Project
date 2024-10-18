package app;
import app.screens.*;
import interfaces.*;
import app.loaders.*;
import models.entities.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        DataLoader staffLoader = new StaffLoader();
        DataLoader patientLoader = new PatientLoader();
        DataLoader inventoryLoader = new InventoryLoader();
        List<Patient> patientList = new ArrayList<>();
        List<Staff> staffList = new ArrayList<>();
        List<Medicine> inventory = new ArrayList<>();

        String staffPath = "data/Staff_List.xlsx";
        String patientPath = "data/Patient_List.xlsx";
        String inventoryPath = "data/Medicine_List.xlsx";

        try {
            staffList = staffLoader.loadData(staffPath);
            patientList = patientLoader.loadData(patientPath);
            //inventory = inventoryLoader.loadData(inventoryPath);
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        String blue = "\u001B[34m";
        String reset = "\u001B[0m";

        System.out.println(blue + " _   _ __  __ ____  ");
        System.out.println("| | | |  \\/  / ___| ");
        System.out.println("| |_| | |\\/| \\___ \\ ");
        System.out.println("|  _  | |  | |___) |");
        System.out.println("|_| |_|_|  |_|____/ ");
        System.out.println(reset);

        System.out.println("Please select an option:");
        System.out.println("1. Log in");
        System.out.println("2. Exit");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.display();
                System.out.println("Proceeding to log in...");
                break;
            case 2:
                System.out.println("Thank you for using the system!");
                break;
            default:
                System.out.println("Invalid choice, please try again.");
                break;
        }

        scanner.close();
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
