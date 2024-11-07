package app.screens.doctor;

import interfaces.*;
import models.entities.*;
import models.records.*;

import java.util.*;

public class AppointmentOutcomeScreen implements Screen {
    private List<AppointmentOutcomeRecord.PrescribedMedication> medicationList = new ArrayList<>();

    @Override
    public void display(Scanner scanner, User user) {
        Doctor doc = (Doctor) user;
        doc.getApptList();
        doc.getAppointmentOutcomeRecords();

        while (true) {
            System.out.println("\n--- Record Appointment Outcome ---");
            System.out.println("1. Add a new record");
            System.out.println("2. View all outcome results");
            System.out.println("3. Return to main menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> addOutcome(scanner, doc);
                    case 2 -> doc.viewAllAppointmentOutcomes();
                    case 3 -> {
                        System.out.println("Returning to Main Menu...");
                        doc.resetData("appt");
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void addOutcome(Scanner scanner, Doctor doc) {
        System.out.print("Enter Appointment ID to record outcome: ");
        String apptID = scanner.nextLine();

        Appointment appt = doc.findApptByID(apptID);
        if (appt == null) {
            System.out.println("Appointment not found. Please check the ID and try again.");
            return;
        }

        System.out.print("Enter service type (e.g., consultation, x-ray, blood test): ");
        String svType = scanner.nextLine();

        System.out.print("Enter consultation notes: ");
        String notes = scanner.nextLine();

        while (true) {
            System.out.print("Enter medication name (or press Enter to finish): ");
            String medicineName = scanner.nextLine();
            if (medicineName.isEmpty()) {
                break;
            }
            System.out.print("Enter quantity needed for " + medicineName + ": ");
            int quantity = Integer.parseInt(scanner.nextLine());

            Medicine medicine = new Medicine(medicineName);
            AppointmentOutcomeRecord.PrescribedMedication prescribed =
                    new AppointmentOutcomeRecord.PrescribedMedication(medicine, quantity);
            medicationList.add(prescribed);
        }

        System.out.print("Enter outcome status (e.g., Completed, Follow-up Needed): ");
        String outcome = scanner.nextLine();

        doc.recordAppointmentOutcome(appt, svType, medicationList, notes, outcome);

        System.out.println("Appointment outcome recorded successfully.");
    }
}
