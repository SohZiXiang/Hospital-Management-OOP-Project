package models.services;

import app.loaders.*;
import interfaces.DataLoader;
import models.enums.*;
import models.entities.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class PatientService {
    private List<Patient> patientsUnderCare = new ArrayList<>();
    private boolean patientsLoaded = false;

    public PatientService() {
    }

    /* START OF LOAD/UPDATE DATA */
    public void getPatientList() {
        if (!patientsLoaded) {
            loadPatientList();
            patientsLoaded = true;
        }
    }

    private void loadPatientList() {
        String path = FilePaths.PATIENT_DATA.getPath();
        DataLoader loadPatient = new PatientLoader();
        try {
            patientsUnderCare = loadPatient.loadData(path);
        } catch (Exception e) {
            System.err.println("Error loading patient data: " + e.getMessage());
        }
    }

    // Update patient data
    public void updateData() {
        if (patientsUnderCare != null) {
            patientsUnderCare.clear();
        }
        patientsLoaded = false;
        getPatientList();
    }

    /* END OF LOAD/UPDATE DATA */

    /* START OF MAIN METHODS */

    // Show all patient records
    public void showAllPatientsRecords() {
        if (patientsUnderCare.isEmpty()) {
            System.out.println("No patient records available.");
            return;
        }

        System.out.printf("%-10s %-20s %-15s %-10s %-10s %-30s %-30s%n",
                "Patient ID", "Name", "Date of Birth", "Gender", "Blood Type", "Diagnosis", "Treatment Plan");
        System.out.println("--------------------------------------------------------------------------------------------");

        for (Patient onePatient : patientsUnderCare) {
            List<String> indDiagnosis = onePatient.getPastDiagnoses();
            List<String> indTreatments = onePatient.getPastTreatments();

            String formatDiagnosis = (indDiagnosis == null || indDiagnosis.isEmpty())
                    ? "No records available"
                    : displayInDiffFormat(indDiagnosis, "Diagnosis");
            String formatTreatments = (indTreatments == null || indTreatments.isEmpty())
                    ? "No records available"
                    : displayInDiffFormat(indTreatments, "Treatment");

            System.out.printf("%-10s %-20s %-15s %-10s %-10s %-30s %-30s%n",
                    onePatient.getPatientID(),
                    onePatient.getName(),
                    onePatient.getDateOfBirth(),
                    onePatient.getGender(),
                    onePatient.getBloodType(),
                    formatDiagnosis,
                    formatTreatments);
        }
    }

    // Filter patients by ID
    public void filterPatients(String patientID) {
        Patient requestedPatient = patientsUnderCare.stream()
                .filter(p -> p.getPatientID().equals(patientID))
                .findFirst()
                .orElse(null);

        if (requestedPatient != null) {
            List<String> indDiagnosis = requestedPatient.getPastDiagnoses();
            List<String> indTreatments = requestedPatient.getPastTreatments();

            String formatDiagnoses = (indDiagnosis == null || indDiagnosis.isEmpty())
                    ? "No records available"
                    : displayInDiffFormat(indDiagnosis, "Diagnosis");
            String formatTreatments = (indTreatments == null || indTreatments.isEmpty())
                    ? "No records available"
                    : displayInDiffFormat(indTreatments, "Treatment");

            System.out.printf("Patient ID: %s%nName: %s%nDate of Birth: %s%nGender: %s%nBlood Type: %s%nContact Information: %s%nDiagnoses: %s%nTreatments: %s%n",
                    requestedPatient.getPatientID(),
                    requestedPatient.getName(),
                    requestedPatient.getDateOfBirth(),
                    requestedPatient.getGender(),
                    requestedPatient.getBloodType(),
                    requestedPatient.getEmail(),
                    formatDiagnoses,
                    formatTreatments);
        } else {
            System.out.println("Patient record not found.");
        }
    }

    // Check if patient is valid
    public Patient checkWhetherPatientValid(String patientID) {
        return patientsUnderCare.stream()
                .filter(p -> p.getPatientID().equals(patientID))
                .findFirst()
                .orElse(null);
    }

    // Update medical record
    public void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment) {
        patient.addDiagnosis(newDiagnosis);
        patient.addTreatment(newTreatment);
        updatePatientRecords(patient.getPatientID(), newDiagnosis, newTreatment);
    }

    // Update patient records in Excel
    public void updatePatientRecords(String patientID, String indDiagnosis, String indTreatment) {
        String path = FilePaths.PATIENT_DATA.getPath();

        try (Workbook wkBook = new XSSFWorkbook(new FileInputStream(path));
             FileOutputStream outputFile = new FileOutputStream(path)) {

            Sheet indSheet = wkBook.getSheetAt(0);
            int diagnosisClm = -1;
            int treatmentClm = -1;
            Row header = indSheet.getRow(0);

            for (Cell indCell : header) {
                if (indCell.getStringCellValue().equalsIgnoreCase("Doctor Diagnosis")) {
                    diagnosisClm = indCell.getColumnIndex();
                } else if (indCell.getStringCellValue().equalsIgnoreCase("Treatment Plan")) {
                    treatmentClm = indCell.getColumnIndex();
                }
            }

            if (diagnosisClm == -1) {
                diagnosisClm = header.getLastCellNum();
                header.createCell(diagnosisClm).setCellValue("Doctor Diagnosis");
            }
            if (treatmentClm == -1) {
                treatmentClm = header.getLastCellNum();
                header.createCell(treatmentClm).setCellValue("Treatment Plan");
            }

            for (Row indRow : indSheet) {
                Cell patientIDCell = indRow.getCell(0);
                if (patientIDCell != null && patientIDCell.getStringCellValue().equals(patientID)) {
                    String existingDiagnoses = indRow.getCell(diagnosisClm) != null ? indRow.getCell(diagnosisClm).getStringCellValue() : "";
                    String existingTreatments = indRow.getCell(treatmentClm) != null ? indRow.getCell(treatmentClm).getStringCellValue() : "";

                    List<String> addedDiagnoses = existingDiagnoses.isEmpty()
                            ? new ArrayList<>() : new ArrayList<>(Arrays.asList(existingDiagnoses.split(", ")));
                    List<String> addedTreatments = existingTreatments.isEmpty()
                            ? new ArrayList<>() : new ArrayList<>(Arrays.asList(existingTreatments.split(", ")));

                    if (!indDiagnosis.isEmpty()) addedDiagnoses.add(indDiagnosis);
                    if (!indTreatment.isEmpty()) addedTreatments.add(indTreatment);

                    String formatDiagnosis = String.join(", ", addedDiagnoses);
                    String formatTreatment = String.join(", ", addedTreatments);

                    indRow.createCell(diagnosisClm).setCellValue(formatDiagnosis);
                    indRow.createCell(treatmentClm).setCellValue(formatTreatment);
                    break;
                }
            }

            wkBook.write(outputFile);

        } catch (IOException e) {
            System.err.println("Error updating patient record: " + e.getMessage());
        }
    }

    /* END OF MAIN METHODS */

    // Helper method
    private String displayInDiffFormat(List<String> items, String label) {
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            format.append(label).append(" ").append(i + 1).append(": ").append(items.get(i));
            if (i < items.size() - 1) {
                format.append(", ");
            }
        }
        return format.toString();
    }
}
