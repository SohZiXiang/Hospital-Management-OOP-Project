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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdatePersonalInformationScreen implements Screen {

    private final static Pattern VALID_EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final static Pattern VALID_PHONE_REGEX =
            Pattern.compile("(6|8|9)\\d{7}");

    DataLoader patientLoader = new PatientLoader();
    List<Patient> patientList = new ArrayList<>();
    String patientPath = FilePaths.PATIENT_DATA.getPath();
    Patient currentPatient;
    int choice = 0;

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

        Boolean exit = false;

        while(!exit){
            System.out.println("----- Update Personal Records for " + user.getHospitalID() + " -----");
            System.out.println("Please Select the following options");
            System.out.println("1: Return To Menu");
            System.out.println("2: Update Contact");
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        exit = true;
                        break;
                    case 2:
                        System.out.println("Please Enter New Contact (Email Address or Phone Number)");
                        String newContact = scanner.nextLine();
                        Matcher matcherEmail = VALID_EMAIL_REGEX.matcher(newContact);
                        Matcher matcherPhone = VALID_PHONE_REGEX.matcher(newContact);

                        if(matcherEmail.matches() || matcherPhone.matches()){
                            System.out.println("Contact Successfully Updated To " + newContact);
                            currentPatient.setPhoneNumber(newContact);
                        }
                        else{
                            System.out.println("Invalid Contact Format");
                            System.out.println("Valid Email Example: someone@example.com");
                            System.out.println("Valid Phone Example: 81234567, 91234567, 61234567");
                            System.out.println("Contact not updated");
                        }
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
