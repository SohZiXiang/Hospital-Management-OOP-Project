package app.screens.patient;

import app.loaders.PatientLoader;
import interfaces.DataLoader;
import interfaces.Screen;
import models.entities.Patient;
import models.entities.User;
import models.enums.FilePaths;

import utils.ActivityLogUtil;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The ViewMedicalRecord class implements the Screen interface to allow patients
 * to view and update their medical records, including contact information such as
 * phone number, email and past diagnosis and treatment.
 */
public class ViewMedicalRecord implements Screen {

    private final static Pattern VALID_EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final static Pattern VALID_PHONE_REGEX =
            Pattern.compile("(6|8|9)\\d{7}");

    DataLoader patientLoader = new PatientLoader();
    List<Patient> patientList = new ArrayList<>();
    String patientPath = FilePaths.PATIENT_DATA.getPath();

    /**
     * Displays the patient's medical record and provides options to update
     * phone number or email.
     *
     * @param scanner The scanner object to capture user input.
     * @param user    The user (patient) accessing the screen.
     */
    @Override
    public void display(Scanner scanner, User user) {
        String logMsg = "Patient " + user.getName() + " (ID: " + user.getHospitalID() + ") viewed medical record.";
        ActivityLogUtil.logActivity(logMsg, user);

        Patient currentPatient = (Patient) user;

        currentPatient.loadMedicalRecordData(user);

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
                        System.out.println("Please Enter New Phone Number (enter 1) to leave");
                        String newNumber = scanner.nextLine();
                        Matcher matcherPhoneNumber = VALID_PHONE_REGEX.matcher(newNumber);

                        if(matcherPhoneNumber.matches()){
                            currentPatient.setPhoneNumber(newNumber);
                            currentPatient.updateContact(user, currentPatient, false);
                        }
                        else{
                            if(!newNumber.equals("1")){
                                System.out.println("Invalid Contact Format");
                                System.out.println("Valid Phone Example: 81234567");
                                System.out.println("Phone number not updated");
                            }
                        }
                        currentPatient.loadMedicalRecordData(user);
                        break;
                    case 3:
                        System.out.println("Please Enter New Email (enter 1) to leave");
                        String newEmail = scanner.nextLine();
                        Matcher matcherEmail = VALID_EMAIL_REGEX.matcher(newEmail);

                        if(matcherEmail.matches()){
                            currentPatient.setEmail(newEmail);
                            currentPatient.updateContact(user, currentPatient, true);
                        }
                        else{
                            if(!newEmail.equals("1")){
                                System.out.println("Invalid Email Format");
                                System.out.println("Valid Email Example: someone@example.com");
                                System.out.println("Email not updated");
                            }
                        }
                        currentPatient.loadMedicalRecordData(user);
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
