package app.screens.doctor;

import interfaces.*;
import models.entities.*;
import utils.ActivityLogUtil;

import java.util.*;

public class PatientRecordsScreen implements Screen {
    private List<Patient> patientList;

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
                        return;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

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
