package app.screens.doctor;

import interfaces.*;
import models.entities.*;
import models.records.*;
import utils.ActivityLogUtil;

import java.util.*;

/**
 * The AppointmentOutcomeScreen class provides a screen interface for doctors to record and view appointment outcomes.
 * It allows adding new outcome records for appointments and viewing all existing outcome records.
 */
public class AppointmentOutcomeScreen implements Screen {
    private List<AppointmentOutcomeRecord.PrescribedMedication> medicationList = new ArrayList<>();

    /**
     * Displays the Appointment Outcome Screen, allowing the doctor to interact with appointment outcome records.
     * Provides options to add a new record, view all outcome records, or return to the main menu.
     *
     * @param scanner The Scanner object for user input.
     * @param user    The User object, which is cast to a Doctor in this context.
     */
    @Override
    public void display(Scanner scanner, User user) {
        Doctor doc = (Doctor) user;
        doc.getApptList();
        doc.getOutcomeRecords();
        doc.getPatientList();
        doc.getMedData();

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
                    case 2 -> {
                        doc.viewAllAppointmentOutcomes();
                        String logMsg = "User " + doc.getName() + " (ID: " + doc.getHospitalID() + ") viewed all " +
                                "outcome records added by the user";
                        ActivityLogUtil.logActivity(logMsg, doc);
                    }
                    case 3 -> {
                        System.out.println("Returning to Main Menu...");
                        doc.resetApptData("all");
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Adds a new appointment outcome record for a specific appointment.
     * Allows the doctor to enter details such as service type, consultation notes, prescribed medications, and outcome status.
     *
     * @param scanner The Scanner object for user input.
     * @param doc     The Doctor object representing the current doctor.
     */
    private void addOutcome(Scanner scanner, Doctor doc) {
        doc.viewUpcomingAppt();
        System.out.print("\nEnter Appointment ID to record outcome: ");
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
        doc.viewMedsStock();

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
        String logMsg = "User " + doc.getName() + " (ID: " + doc.getHospitalID() + ") added a new appointment outcome" +
                " record.";
        ActivityLogUtil.logActivity(logMsg, doc);
    }
}
