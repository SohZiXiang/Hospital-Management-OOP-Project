package app.screens;

import app.loaders.InventoryLoader;
import models.entities.*;
import utils.ActivityLogUtil;

import java.util.*;

public class PharmacistMainScreen {

    public void display(Scanner scanner, User user) {
        Pharmacist pharmacist = (Pharmacist) user;
        while (true) {
            System.out.println(
                    "  _____  _                                     _     _   \n" +
                            " |  __ \\| |                                   (_)   | |  \n" +
                            " | |__) | |__   __ _ _ __ _ __ ___   __ _  ___ _ ___| |_ \n" +
                            " |  ___/| '_ \\ / _` | '__| '_ ` _ \\ / _` |/ __| / __| __|\n" +
                            " | |    | | | | (_| | |  | | | | | | (_| | (__| \\__ \\ |_ \n" +
                            " |_|    |_| |_|\\__,_|_|  |_| |_| |_|\\__,_|\\___|_|___/\\__|\n" +
                            "                                                         \n" +
                            "                                                         \n"
            );

            System.out.println("\nWelcome, Pharmacist: " + pharmacist.getName());
            System.out.println("What would you like to do?");
            System.out.println("1. View Existing Prescription Records");
            System.out.println("2. Update Existing Prescription Status");
            System.out.println("3. View All Medicine Stock");
            System.out.println("4. View Specific Medicine Stock");
            System.out.println("5. Submit a Replenishment Request");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            InventoryLoader loader = new InventoryLoader();
            List<Medicine> medicineStock = loader.loadData("data/Medicine_List.xlsx");//Excel Path

            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        //viewPrescriptionRecords(pharmacist, scanner);
                        break;
                    case 2:
                        //updatePrescriptionStatus(pharmacist, scanner);
                        break;
                    case 3:
                        System.out.println("\nViewing all medicine stock...");
                        pharmacist.viewAllMedicineStock(medicineStock);
                        break;
                    case 4:
                        System.out.println("\nEnter medicine name to view current stock: ");
                        String viewMedName = scanner.nextLine().trim();
                        pharmacist.viewSpecificMedicineStock(medicineStock, viewMedName);
                        break;
                    case 5:
                        System.out.println("\nEnter Medicine Name to request replenishment for: ");
                        String reqMedName = scanner.nextLine().trim();
                        System.out.println("Enter the amount of " + reqMedName + " you want to request: ");
                        int reqAmount;
                        try {
                            reqAmount = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Enter a valid number");
                            return;
                        }
                        pharmacist.submitReplenishmentRequest(reqMedName, reqAmount, scanner);
                        break;
                    case 6:
                        ActivityLogUtil.logout(scanner, user);
                        return;
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /*private void submitReplenishmentRequest(Pharmacist pharmacist, Scanner scanner) {
        System.out.println("\nEnter Medicine Name to request replenishment: ");
        String medicineName = scanner.nextLine().trim();

        System.out.println("Enter the amount of " + medicineName + " you want to request: ");
        int requestedAmount;
        try {
            requestedAmount = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number for the requested amount.");
            return;
        }
        //Submit req
        pharmacist.submitReplenishmentRequest(medicineName, requestedAmount, scanner);
    }

    private void viewPrescriptionRecords(Pharmacist pharmacist, Scanner scanner) {
        System.out.println("\nViewing all prescription records...");
        //NOT IMPLEMENTED YET
    }

    private void updatePrescriptionStatus(Pharmacist pharmacist, Scanner scanner) {
//        System.out.println("\nEnter Appointment ID to update prescription status: ");
//        String appointmentId = scanner.nextLine();
//        System.out.println("Enter new prescription status (e.g., 'dispensed', 'pending'):");
//        String status = scanner.nextLine();
//        System.out.println("Prescription status for appointment ID " + appointmentId + " has been updated to " + status);
//        NOT IMPLEMENTED YET
    }

    private void viewAllMedicineStock(Pharmacist pharmacist) {
        System.out.println("\nViewing all medicine stock...");
        InventoryLoader loader = new InventoryLoader();
        List<Medicine> medicineStock = loader.loadData("data/Medicine_List.xlsx");//Excel Path
        pharmacist.viewAllMedicineStock(medicineStock);
    }

    private void viewSpecificMedicineStock(Pharmacist pharmacist, Scanner scanner) {
        System.out.println("\nEnter medicine name to view current stock: ");
        String medicineName = scanner.nextLine().trim();
        InventoryLoader loader = new InventoryLoader();
        List<Medicine> medicineStock = loader.loadData("data/Medicine_List.xlsx");//Excel Path
        pharmacist.viewSpecificMedicineStock(medicineStock, medicineName);
    }*/
}
