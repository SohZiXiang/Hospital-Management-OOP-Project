package app.screens.DoctorScreens;

import app.loaders.*;
import interfaces.*;
import models.entities.*;
import models.enums.*;

import java.io.FileInputStream;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;


public class PatientRecordsScreen implements Screen {
    private List<Patient> patientList;

    public void display(Scanner scanner, User user) {
        patientList = loadPatientList();
        while (true) {
            System.out.println("\nPatient Records Screen - Please choose an option:");
            System.out.println("1. View all patient records");
            System.out.println("2. View a specific patient record");
            System.out.println("3. Update a patient record");
            System.out.println("4. Go back to the previous screen");

            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> showAllPatientRecords();
                    case 2 -> filterPatients(scanner);
                    case 3 -> addConsulationNotes(scanner);
                    case 4 -> {
                        System.out.println("Returning to the Doctor Main Screen...");
//                        DoctorMainScreen doctorMainScreen = new DoctorMainScreen();
//                        doctorMainScreen.display(scanner, user);
                        return;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
// Change variableNames
    private List<Patient> loadPatientList() {
        String patientPath = FilePaths.PATIENT_DATA.getPath();
        DataLoader patientLoader = new PatientLoader();
        try {
            return patientLoader.loadData(patientPath);
        } catch (Exception e) {
            System.err.println("Error loading patient data: " + e.getMessage());
            return List.of();
        }
    }

    private void showAllPatientRecords() {
        if (patientList.isEmpty()) {
            System.out.println("No patient records available.");
            return;
        }

        System.out.printf("%-10s %-20s %-15s %-10s %-10s %-30s %-30s%n",
                "Patient ID", "Name", "Date of Birth", "Gender", "Blood Type", "Diagnosis", "Treatment Plan");
        System.out.println("--------------------------------------------------------------------------------------------");

        for (Patient patient : patientList) {
            // Get diagnosis and treatment lists from patient
            List<String> diagnoses = patient.getPastDiagnoses(); // Implement parseCommaSeparatedList below
            List<String> treatments = patient.getPastTreatments(); // Implement parseCommaSeparatedList below

            String formattedDiagnoses = (diagnoses == null || diagnoses.isEmpty())
                    ? "No records available"
                    : formatListWithLabels(diagnoses, "Diagnosis");
            String formattedTreatments = (treatments == null || treatments.isEmpty())
                    ? "No records available"
                    : formatListWithLabels(treatments, "Treatment");

            System.out.printf("%-10s %-20s %-15s %-10s %-10s %-30s %-30s%n",
                    patient.getPatientID(),
                    patient.getName(),
                    patient.getDateOfBirth(),
                    patient.getGender(),
                    patient.getBloodType(),
                    formattedDiagnoses,
                    formattedTreatments);
        }
    }


    private void filterPatients(Scanner scanner) {
        System.out.print("Enter Patient ID to view record: ");
        String patientID = scanner.nextLine();

        Patient patient = patientList.stream()
                .filter(p -> p.getPatientID().equals(patientID))
                .findFirst()
                .orElse(null);

        if (patient != null) {
            List<String> diagnoses = patient.getPastDiagnoses();
            List<String> treatments = patient.getPastTreatments();

            String formattedDiagnoses = (diagnoses == null || diagnoses.isEmpty())
                    ? "No records available"
                    : formatListWithLabels(diagnoses, "Diagnosis");
            String formattedTreatments = (treatments == null || treatments.isEmpty())
                    ? "No records available"
                    : formatListWithLabels(treatments, "Treatment");

            System.out.printf("Patient ID: %s%nName: %s%nDate of Birth: %s%nGender: %s%nBlood Type: %s%nContact Information: %s%nDiagnoses: %s%nTreatments: %s%n",
                    patient.getPatientID(),
                    patient.getName(),
                    patient.getDateOfBirth(),
                    patient.getGender(),
                    patient.getBloodType(),
                    patient.getEmail(),
                    formattedDiagnoses,
                    formattedTreatments);
        } else {
            System.out.println("Patient record not found.");
        }
    }

    private String formatListWithLabels(List<String> items, String label) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            formatted.append(label).append(" ").append(i + 1).append(": ").append(items.get(i));
            if (i < items.size() - 1) {
                formatted.append(", ");
            }
        }
        return formatted.toString();
    }

    private void addConsulationNotes(Scanner scanner) {
        System.out.print("Enter Patient ID to update record: ");
        String patientID = scanner.nextLine();

        Patient patient = patientList.stream()
                .filter(p -> p.getPatientID().equals(patientID))
                .findFirst()
                .orElse(null);

        if (patient != null) {
            // Prompt for doctor diagnosis and treatment plan
            System.out.print("Enter doctor diagnosis: ");
            String diagnosis = scanner.nextLine();
            System.out.print("Enter treatment plan: ");
            String treatment = scanner.nextLine();

            // Update the patient instance
            patient.addDiagnosis(diagnosis);
            patient.addTreatment(treatment);

            // Save updates to the Excel file
            updatePatientRecords(patientID, diagnosis, treatment);
            System.out.println("Patient record updated successfully.");
        } else {
            System.out.println("Patient record not found.");
        }
    }
    private void updatePatientRecords(String patientID, String diagnosis, String treatment) {
        String patientPath = FilePaths.PATIENT_DATA.getPath();

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(patientPath));
             FileOutputStream fileOut = new FileOutputStream(patientPath)) {

            Sheet sheet = workbook.getSheetAt(0);
            int diagnosisColumn = -1;
            int treatmentColumn = -1;
            Row headerRow = sheet.getRow(0);

            // Check if Diagnosis and Treatment Plan columns already exist
            for (Cell cell : headerRow) {
                if (cell.getStringCellValue().equalsIgnoreCase("Doctor Diagnosis")) {
                    diagnosisColumn = cell.getColumnIndex();
                } else if (cell.getStringCellValue().equalsIgnoreCase("Treatment Plan")) {
                    treatmentColumn = cell.getColumnIndex();
                }
            }

            // Add new columns if necessary
            if (diagnosisColumn == -1) {
                diagnosisColumn = headerRow.getLastCellNum();
                headerRow.createCell(diagnosisColumn).setCellValue("Doctor Diagnosis");
            }
            if (treatmentColumn == -1) {
                treatmentColumn = headerRow.getLastCellNum();
                headerRow.createCell(treatmentColumn).setCellValue("Treatment Plan");
            }

            // Find the patient row and update diagnosis and treatment
            for (Row row : sheet) {
                Cell idCell = row.getCell(0); // Assuming Patient ID is in the first column
                if (idCell != null && idCell.getStringCellValue().equals(patientID)) {
                    // Get existing diagnoses and treatments if they exist
                    String existingDiagnoses = row.getCell(diagnosisColumn) != null ? row.getCell(diagnosisColumn).getStringCellValue() : "";
                    String existingTreatments = row.getCell(treatmentColumn) != null ? row.getCell(treatmentColumn).getStringCellValue() : "";

                    // Add the new diagnosis and treatment to the list
                    List<String> updatedDiagnoses = new ArrayList<>(Arrays.asList(existingDiagnoses.split(", ")));
                    List<String> updatedTreatments = new ArrayList<>(Arrays.asList(existingTreatments.split(", ")));

                    if (!diagnosis.isEmpty()) updatedDiagnoses.add(diagnosis);
                    if (!treatment.isEmpty()) updatedTreatments.add(treatment);

                    // Convert the lists back to comma-separated strings
                    String diagnosesString = String.join(", ", updatedDiagnoses);
                    String treatmentsString = String.join(", ", updatedTreatments);

                    // Update the cells in the Excel row
                    row.createCell(diagnosisColumn).setCellValue(diagnosesString);
                    row.createCell(treatmentColumn).setCellValue(treatmentsString);
                    break;
                }
            }

            // Write changes to the file
            workbook.write(fileOut);

        } catch (IOException e) {
            System.err.println("Error updating patient record: " + e.getMessage());
        }
    }


}
