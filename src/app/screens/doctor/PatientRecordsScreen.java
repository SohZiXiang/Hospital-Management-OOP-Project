package app.screens.doctor;

import interfaces.*;
import models.entities.*;
import utils.ActivityLogUtil;

import java.util.*;

/**
 * The PatientRecordsScreen class provides a screen interface for doctors to manage patient records.
 * It allows viewing all patient records, viewing specific patient records, and updating patient records with new consultation notes.
 */
public class PatientRecordsScreen implements Screen {
    /**
     * Displays the Patient Records Screen, allowing the doctor to interact with patient records.
     * Provides options to view all records, view a specific record, update a record, or return to the previous screen.
     *
     * @param scanner The Scanner object for user input.
     * @param user    The User object, which is cast to a Doctor in this context.
     */
    public void display(Scanner scanner, User user) {
        Doctor doc = (Doctor) user;
        doc.getPatientList();

        while (true) {
            System.out.println("\n--- Patient Records Screen ---");
            System.out.println("1. View all patient records");
            System.out.println("2. View a specific patient record");
            System.out.println("3. Update a patient record");
            System.out.println("4. Go back to the previous screen");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> {
                        doc.showAllPatientsRecords();
                        String logMsg = "User " + doc.getName() + " (ID: " + doc.getHospitalID() + ") viewed all " +
                                "patient records under their care";
                        ActivityLogUtil.logActivity(logMsg, doc);
                    }
                    case 2 -> {
                        System.out.print("Enter Patient ID to view record: ");
                        String patientID = scanner.nextLine();
                        doc.filterPatients(patientID);
                        String logMsg = "User " + doc.getName() + " (ID: " + doc.getHospitalID() + ") viewed record " +
                                "details for Patient: " + patientID;
                        ActivityLogUtil.logActivity(logMsg, doc);
                    }
                    case 3 -> addConsulationNotes(scanner, doc);
                    case 4 -> {
                        System.out.println("Returning to the Doctor Main Screen...");
                        doc.resetPatientData();
                        return;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Adds consultation notes to a specific patient's record.
     * Prompts the doctor to enter the patient ID, diagnosis, and treatment plan, then updates the patient record.
     *
     * @param scanner The Scanner object for user input.
     * @param doc     The Doctor object representing the current doctor.
     */
    public void addConsulationNotes(Scanner scanner, Doctor doc) {
        System.out.print("Enter Patient ID to update record: ");
        String patientID = scanner.nextLine();

        Patient isValidPatient = doc.checkWhetherPatientValid(patientID);

        if (isValidPatient != null) {
            System.out.print("Enter doctor diagnosis: ");
            String indDiagnosis = scanner.nextLine();
            System.out.print("Enter treatment plan: ");
            String indTreatment = scanner.nextLine();

            doc.updateMedicalRecord(isValidPatient, indDiagnosis, indTreatment);

            System.out.println("Patient record updated successfully.");
            String logMsg = "User " + doc.getName() + " (ID: " + doc.getHospitalID() + ") updated patient record " +
                    "details for Patient: " + patientID;
            ActivityLogUtil.logActivity(logMsg, doc);
        } else {
            System.out.println("Patient record not found.");
        }
    }
}
