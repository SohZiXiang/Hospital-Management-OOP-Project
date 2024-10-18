package app.screens;
import app.Main;
import app.loaders.*;
import interfaces.*;
import app.Main;
import models.entities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class LoginScreen implements Screen {
    private static final String DEFAULT_PASSWORD = "P@ssw0rd123";

    public void display(Scanner scanner) {
        while (true) {
            System.out.print("\nEnter hospital ID: (or '0' to return to main menu): ");
            String hospitalId = scanner.nextLine();
            if (hospitalId.equals("0")) {
                Main.displayMain(scanner);
                break;
            }
            System.out.print("Enter password: \n");
            String password = scanner.nextLine();

            User user = authenticateUser(hospitalId, password);
            if (user != null) {
                if (password.equals(DEFAULT_PASSWORD)) {
                    System.out.println("Your password is the default. Please change your password.");
                    changePassword(user);
                }
                System.out.println("Login successful! Welcome " + user.getName());
                switch (user.getRole().toLowerCase()) {
                    case "patient":
//                        PatientScreen patientScreen = new PatientScreen();
//                        patientScreen.display();
                    case "doctor":
//                        DoctorScreen doctorScreen = new DoctorScreen();
//                        doctorScreen.display();
                    case "pharmacist":
//                        PharmacistScreen pharmacistScreen = new PharmacistScreen();
//                        pharmacistScreen.display();
                    case "administrator":
//                        AdminScreen adminScreen = new AdminScreen();
//                        adminScreen.display();
                    default:
                        System.out.println("Error: Unknown role. Redirecting to main menu...");
                        Main.displayMain(scanner); // Redirect to the main menu or home screen
                        break;
                }
            } else {
                System.out.println("Invalid hospital ID or password. Please try again.");
            }
        }
    }

    private void changePassword(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        // Code to update password of user

        System.out.println("Password has been successfully updated!");
    }

    private User authenticateUser(String hospitalId, String password) {
        DataLoader staffLoader = new StaffLoader();
        DataLoader patientLoader = new PatientLoader();
        List<Patient> patientList = new ArrayList<>();
        List<Staff> staffList = new ArrayList<>();

        String staffPath = "data/Staff_List.xlsx";
        String patientPath = "data/Patient_List.xlsx";

        try {
            staffList = staffLoader.loadData(staffPath);
            patientList = patientLoader.loadData(patientPath);
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            return null;
        }

        for (Staff staff : staffList) {
            if (staff.getStaffId().equals(hospitalId) && staff.getPassword().equals(password)) {
                return staff;
            }
        }
        for (Patient patient : patientList) {
            if (patient.getPatientID().equals(hospitalId) && patient.getPassword().equals(password)) {
                return patient;
            }
        }

        return null;
    }
}
