package app.screens.admin;

import app.loaders.PatientLoader;
import app.loaders.StaffLoader;
import app.screens.AdminMainScreen;
import interfaces.DataLoader;
import interfaces.Screen;
import models.entities.Patient;
import models.entities.Staff;
import models.entities.User;
import models.enums.FilePaths;
import models.enums.Gender;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManagePatientScreen implements Screen {
    List<Patient> patientList = new ArrayList<>();

    private boolean isLetters(String name) {
        return name.matches("[a-zA-Z]+");
    }

    private boolean isDate(String date) {
        return date.matches("\\d{2}-\\d{2}-\\d{4}");
    }

    private void loadPatientList() {
        String patientPath = FilePaths.PATIENT_DATA.getPath();
        DataLoader patientLoader = new PatientLoader();
        try {
            patientList = patientLoader.loadData(patientPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private void loadPatient(){
        System.out.printf("%-15s %-25s %-25s %-15s %-15s %-35s %-20s%n",
                "Patient ID", "Name", "DOB", "Gender", "Blood Type", "Email", "Phone Number");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (Patient patient : patientList) {
            System.out.printf("%-15s %-25s %-25s %-15s %-15s %-35s %-20s%n",
                    patient.getPatientID(),
                    patient.getName(),
                    patient.getDateOfBirth(),
                    patient.getGender(),
                    patient.getBloodType(),
                    patient.getEmail(),
                    patient.getPhoneNumber());
        }
    }

    public void display(Scanner scanner, User user) {

        loadPatientList();

        while (true) {
            System.out.println("\n--- Manage Hospital Staff ---");
            System.out.println("1. View Patient List");
            System.out.println("2. Add Patient");
            System.out.println("3. Update Patient Details");
            System.out.println("4. Remove Patient");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        loadPatient();
                        break;
                    case 2:
                        String patientName = "";

                        do{
                            System.out.println("Enter Patient Name: (only letters allowed)");
                            patientName = scanner.nextLine();
                        }while(!isLetters(patientName));

                        String patientDOB = "";

                        do{
                            System.out.println("Enter Patient DOB: (DD-MM-YYYY)");
                            patientDOB = scanner.nextLine();
                        }while(!isDate(patientDOB));

                        String patientGender = "";

                        do{
                            System.out.println("Enter Patient Gender: (M/F)");
                            patientGender = scanner.nextLine();
                        }while(!patientGender.equals("M") || !patientGender.equals("F"));

                        String patientBloodType = "";

                        do{
                            System.out.println("Enter Patient Blood Type: (M/F)");
                            patientGender = scanner.nextLine();
                        }while(!patientGender.equals("M") || !patientGender.equals("F"));

                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                    case 5:
                        AdminMainScreen adminMainScreen = new AdminMainScreen();
                        adminMainScreen.display(scanner, user);
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
