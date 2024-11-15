package app.screens.pharmacist;

import interfaces.Screen;
import models.entities.Pharmacist;
import models.entities.User;

import java.util.Scanner;

/**
 * A screen that allows pharmacists to update the prescription status for a specific appointment.
 * Provides functionality to verify the appointment ID, check pending medications, and update
 * prescription status for the specified medicine.
 */
public class UpdatePrescriptionScreen implements Screen {

    private final Pharmacist pharmacist;

    /**
     * Constructs an UpdatePrescriptionScreen with the specified pharmacist.
     *
     * @param pharmacist the pharmacist performing the update operations.
     */
    public UpdatePrescriptionScreen(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
    }

    /**
     * Displays the screen to update a prescription status, prompting the pharmacist to enter an
     * appointment ID and medicine name. The screen continues to prompt until the pharmacist enters '0'
     * as the appointment ID to exit. This includes validating the appointment ID, checking for pending
     * medicines, and updating the prescription status of the specified medicine.
     *
     * @param scanner the Scanner instance used for reading user input.
     * @param user    the current user; must be an instance of Pharmacist to access this screen.
     */
    @Override
    public void display(Scanner scanner, User user) {
        if (user instanceof Pharmacist) {
            while (true) {
                System.out.print("Enter Appointment ID to Prescribe (or '0' to exit): ");
                String apptId = scanner.nextLine().trim();

                // Exit if user enters '0'
                if (apptId.equals("0")) {
                    System.out.println("Exiting prescription update screen.");
                    break;
                }

                // Check appointment ID is valid and for pending medicines
                if (!pharmacist.AppointmentIDValid(apptId)) {
                    System.out.println("No appointment found with ID: " + apptId);
                    continue;
                }
                if (!pharmacist.hasPendingMedicines(apptId)) {
                    System.out.println("All medicines for Appointment ID " + apptId + " have already been dispensed / no medicines to dispense.");
                    continue;
                }

                // Prompt for medicine name if there are pending medicines
                System.out.print("Enter Medicine Name (or '0' to exit): ");
                String medicineName = scanner.nextLine().trim();

                // Exit if user enters '0' as medicine name
                if (medicineName.equals("0")) {
                    System.out.println("Exiting prescription update screen.");
                    break;
                }

                // Update prescription status and inventory
                pharmacist.updatePrescriptionStatus(apptId, medicineName);
            }
        } else {
            System.out.println("Wrong access. Only pharmacists can update prescriptions.");
        }
    }
}
