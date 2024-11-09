package app.screens.pharmacist;

import app.loaders.InventoryLoader;
import models.entities.Medicine;
import models.entities.Pharmacist;
import models.records.ReplenishmentRequest;
import models.records.ReplenishmentRequestManager;

import java.util.List;
import java.util.Scanner;

public class PharmacistRequestScreen {
    private Pharmacist pharmacist;

    public PharmacistRequestScreen(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
    }

    public void display(Scanner scanner) {
        InventoryLoader loader = new InventoryLoader();
        List<Medicine> medicineStock = loader.loadData("data/Medicine_List.xlsx");

        System.out.println("\nEnter Medicine Name to request replenishment for: ");
        String reqMedName = scanner.nextLine().trim();

        // Check if the entered medicine name exists in the stock
        boolean medicineExists = false;
        for (Medicine med : medicineStock) {
            if (med.getName().equalsIgnoreCase(reqMedName)) {
                medicineExists = true;
                break;
            }
        }

        if (!medicineExists) {
            System.out.println("Medicine " + reqMedName + " not found in inventory. Cannot request replenishment.");
            return;
        }

        System.out.println("Enter the amount of " + reqMedName + " you want to request: ");
        int reqAmount;
        try {
            reqAmount = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter a valid number.");
            return;
        }

        // Submit the replenishment request
        pharmacist.submitReplenishmentRequest(reqMedName, reqAmount, scanner);
    }
}
