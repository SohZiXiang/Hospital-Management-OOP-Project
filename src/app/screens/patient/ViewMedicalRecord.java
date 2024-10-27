package app.screens.patient;

import app.loaders.PatientLoader;
import interfaces.DataLoader;
import interfaces.Screen;
import models.entities.Patient;
import models.entities.User;
import models.enums.FilePaths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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

    @Override
    public void display(Scanner scanner, User user) {

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
                        System.out.println("Please Enter New Contact (Email Address or Phone Number)");
                        break;
                    case 3:
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
