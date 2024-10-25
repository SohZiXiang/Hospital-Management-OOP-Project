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

public class ViewMedicalRecord implements Screen {

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
        System.out.println("Patient Name: " + currentPatient.getName());
        System.out.println("Patient Date Of Birth: " + currentPatient.getDateOfBirth());
        System.out.println("Patient Gender: " + currentPatient.getGender());
        System.out.println("Patient Blood Type: " + currentPatient.getBloodType());
        System.out.println("Patient Phone Number: " + currentPatient.getPhoneNumber());
        System.out.println("Patient Email: " + currentPatient.getEmail());

        try {
            System.out.println("1: Return to menu");
            String input = scanner.nextLine();
            int choice = Integer.parseInt(input);

            while (choice != 1){
                System.out.println("1: Return to menu");
                input = scanner.nextLine();
                choice = Integer.parseInt(input);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

    }

}
