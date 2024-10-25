package models.entities;

import models.enums.BloodType;
import models.enums.FilePaths;
import models.enums.Gender;
import models.enums.Role;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class Patient extends User {
    private String patientID;
    private LocalDate dateOfBirth;
    private String contact;
    private BloodType bloodType;
    private List<String> pastDiagnoses;
    private List<String> pastTreatments;


    public Patient(String hospitalID) {
        super(hospitalID);
    }

    public Patient(String hospitalID, String name, String password,
                   String patientID, LocalDate dateOfBirth, Gender gender,
                   String contact, BloodType bloodType,
                   List<String> pastDiagnoses, List<String> pastTreatments) {
        super(hospitalID, name, password, gender);
        this.patientID = patientID;
        this.dateOfBirth = dateOfBirth;
        this.contact = contact;
        this.bloodType = bloodType;
        this.pastDiagnoses = pastDiagnoses;
        this.pastTreatments = pastTreatments;
    }

    public Patient(String hospitalID,
                   String patientID, String name, LocalDate dateOfBirth, Gender gender,
                   String contact, BloodType bloodType) {
        super(hospitalID = patientID, name,"P@ssw0rd123", gender);
        this.patientID = patientID;
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
        this.contact = contact;
        this.pastDiagnoses = pastDiagnoses;
        this.pastTreatments = pastTreatments;
    }


    public String getPatientID() {
        return patientID;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getContact() {
        return contact;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public List<String> getPastDiagnoses() {
        return pastDiagnoses;
    }

    public List<String> getPastTreatments() {
        return pastTreatments;
    }

    public void setContact(String contact) {
        this.contact = contact;

        String filePath = FilePaths.PATIENT_DATA.getPath();
        Workbook workbook = null;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fileInputStream);

            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getStringCellValue().equals(this.patientID)) {
                    row.createCell(5).setCellValue(contact);
                    break;
                }
            }

            fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addDiagnosis(String diagnosis) {
        this.pastDiagnoses.add(diagnosis);
    }

    // Method to add a new treatment
    public void addTreatment(String treatment) {
        this.pastTreatments.add(treatment);
    }

    @Override
    public Role getRole() {
        return Role.PATIENT;
    }
}
