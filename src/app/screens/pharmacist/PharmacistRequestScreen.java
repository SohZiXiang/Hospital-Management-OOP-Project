package app.screens.pharmacist;

import app.loaders.InventoryLoader;
import models.entities.Medicine;
import models.entities.Pharmacist;
import java.util.List;
import java.util.Scanner;

/**
 * Represents the screen where a pharmacist can request the replenishment of medicine stock.
 * This screen allows the pharmacist to enter the name and quantity of the medicine needed and submits the request.
 * The screen will continue prompting for input until the user enters '0' to exit.
 */
public class PharmacistRequestScreen {
    private Pharmacist pharmacist;

    /**
     * Constructs a PharmacistRequestScreen with the specified pharmacist.
     *
     * @param pharmacist the pharmacist using the screen to request replenishment.
     */
    public PharmacistRequestScreen(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
    }

    /**
     * Displays the replenishment request screen, allowing the pharmacist to enter details for a stock request.
     * The screen continues to prompt the pharmacist for medicine name and quantity until '0' is entered to exit.
     *
     * @param scanner the Scanner instance used to capture user input.
     */
    public void display(Scanner scanner) {
        InventoryLoader loader = new InventoryLoader();
        List<Medicine> medicineStock = loader.loadData("data/Medicine_List.xlsx");

        while (true) {
            System.out.println("\nEnter Medicine Name to request replenishment for (or enter '0' to exit): ");
            String reqMedName = scanner.nextLine().trim();

            if (reqMedName.equals("0")) {
                System.out.println("Exiting replenishment request screen.");
                break;
            }

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
                continue;
            }

            System.out.println("Enter the amount of " + reqMedName + " you want to request: ");
            int reqAmount;
            try {
                reqAmount = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a valid number.");
                continue;
            }

            // Submit the replenishment request
            pharmacist.submitReplenishmentRequest(reqMedName, reqAmount, scanner);
        }
    }
}
