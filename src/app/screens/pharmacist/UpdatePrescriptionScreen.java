package app.screens.pharmacist;

import interfaces.Screen;
import models.entities.Pharmacist;
import models.entities.User;

import java.util.Scanner;

public class UpdatePrescriptionScreen implements Screen {

    private final Pharmacist pharmacist;

    public UpdatePrescriptionScreen(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
    }

    @Override
    public void display(Scanner scanner, User user) {
        if (user instanceof Pharmacist) {
            System.out.print("Enter Appointment ID to Prescribe: ");
            String apptId = scanner.nextLine().trim();

            // Check appointment ID is valid and for pending medicines
            if (!pharmacist.AppointmentIDValid(apptId)) {
                System.out.println("No appointment found with ID: " + apptId);
                return;
            }
            // Early exit if all medicines are already dispensed
            if (!pharmacist.hasPendingMedicines(apptId)) {
                System.out.println("All medicines for Appointment ID " + apptId + " have already been dispensed / no medicines to dispense.");
                return;
            }


            // Prompt for medicine name if there are pending medicines
            System.out.print("Enter Medicine Name: ");
            String medicineName = scanner.nextLine().trim();

            //Update status and inventory
            pharmacist.updatePrescriptionStatus(apptId, medicineName);
        } else {
            System.out.println("Wrong access. Only pharmacists can update prescriptions.");
        }
    }
}
