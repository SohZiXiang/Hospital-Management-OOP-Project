package app.screens;

import app.loaders.InventoryLoader;
import app.screens.pharmacist.PharmacistRequestScreen;
import app.screens.pharmacist.UpdatePrescriptionScreen;
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
                            " |_|    |_| |_|\\__,_|_|  |_| |_| |_|\\__,_|\\___|_|___/\\__|\n"
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
                        System.out.println("\nViewing all prescription records...");
                        pharmacist.viewAllPrescriptionRecords();
                        break;
                    case 2:
                        UpdatePrescriptionScreen updateScreen = new UpdatePrescriptionScreen(pharmacist);
                        updateScreen.display(scanner, user);
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
                        PharmacistRequestScreen requestScreen = new PharmacistRequestScreen(pharmacist);
                        requestScreen.display(scanner);
                        break;
                    case 6:
                        ActivityLogUtil.logout(scanner, user);
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
