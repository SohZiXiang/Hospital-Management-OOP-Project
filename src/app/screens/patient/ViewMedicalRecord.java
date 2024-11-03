package app.screens.patient;

import app.loaders.PatientLoader;
import interfaces.DataLoader;
import interfaces.Screen;
import models.entities.Patient;
import models.entities.User;
import models.enums.FilePaths;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import utils.ActivityLogUtil;
import utils.StringFormatUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewMedicalRecord implements Screen {

    private final static Pattern VALID_EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final static Pattern VALID_PHONE_REGEX =
            Pattern.compile("(6|8|9)\\d{7}");

    DataLoader patientLoader = new PatientLoader();
    List<Patient> patientList = new ArrayList<>();
    String patientPath = FilePaths.PATIENT_DATA.getPath();
    Patient currentPatient;

    private void loadData(User user) {
        try {
            patientList = patientLoader.loadData(patientPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        for (Patient patient : patientList) {
            if (patient.getPatientID().equals(user.getHospitalID())) {
                currentPatient = patient;
            }
        }

        System.out.println("----- Displaying Medical Record for " + user.getHospitalID() + " -----");
        System.out.println();
        System.out.printf("%-30s %-30s%n", "Attribute", "Details");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");

        System.out.printf("%-30s %-30s%n", "Name:", currentPatient.getName());
        System.out.printf("%-30s %-30s%n", "DOB:", currentPatient.getDateOfBirth());
        System.out.printf("%-30s %-30s%n", "Gender:", currentPatient.getGender());
        System.out.printf("%-30s %-30s%n", "Blood Type:", currentPatient.getBloodType());
        System.out.printf("%-30s %-30s%n", "Phone Number:", currentPatient.getPhoneNumber());
        System.out.printf("%-30s %-30s%n", "Email:", currentPatient.getEmail());

        System.out.println();
    }

    private void updateContact(User user, Patient patient, boolean email) {
        String type = "";
        String changeValue = "";

        try (FileInputStream fis = new FileInputStream(patientPath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell patientIdCell = row.getCell(0);
                if (patientIdCell != null && patientIdCell.getStringCellValue().equals(user.getHospitalID())) {
                    if(email){
                        row.getCell(5).setCellValue(patient.getEmail());
                        type = "Email";
                        changeValue = patient.getEmail();
                    }
                    else{
                        row.getCell(8).setCellValue(patient.getPhoneNumber());
                        type = "Phone Number";
                        changeValue = patient.getPhoneNumber();
                    }

                    try (FileOutputStream fos = new FileOutputStream(patientPath)) {
                        workbook.write(fos);
                    }
                }
            }
            String logMsg = "Patient " + patient.getName() + " (ID: " + patient.getHospitalID() + ") " +
                    "changed " + type + " to " + changeValue + ". " ;
            ActivityLogUtil.logActivity(logMsg, user);
            System.out.println(type + " Successfully Updated To " + changeValue);
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error updating staff in Excel: " + e.getMessage());
        }
    }

    @Override
    public void display(Scanner scanner, User user) {
        String logMsg = "Patient " + user.getName() + " (ID: " + user.getHospitalID() + ") viewed medical record.";
        ActivityLogUtil.logActivity(logMsg, user);
        loadData(user);

        Boolean exit = false;

        while(!exit){
            System.out.println();
            System.out.println("Please Select the following options");
            System.out.println("1: Return To Menu");
            System.out.println("2: Update Phone Number");
            System.out.println("3: Update Email");
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        exit = true;
                        break;
                    case 2:
                        System.out.println("Please Enter New Phone Number");
                        String newNumber = scanner.nextLine();
                        Matcher matcherPhoneNumber = VALID_PHONE_REGEX.matcher(newNumber);

                        if(matcherPhoneNumber.matches()){
                            currentPatient.setPhoneNumber(newNumber);
                            updateContact(user, currentPatient, false);
                        }
                        else{
                            System.out.println("Invalid Contact Format");
                            System.out.println("Valid Phone Example: 81234567");
                            System.out.println("Phone number not updated");
                        }
                        loadData(user);
                        break;
                    case 3:
                        System.out.println("Please Enter New Email");
                        String newEmail = scanner.nextLine();
                        Matcher matcherEmail = VALID_EMAIL_REGEX.matcher(newEmail);

                        if(matcherEmail.matches()){
                            currentPatient.setEmail(newEmail);
                            updateContact(user, currentPatient, true);
                        }
                        else{
                            System.out.println("Invalid Email Format");
                            System.out.println("Valid Email Example: someone@example.com");
                            System.out.println("Email not updated");
                        }
                        loadData(user);
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

    }

}
