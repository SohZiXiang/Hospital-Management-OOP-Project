package app.screens;
import app.loaders.*;
import interfaces.*;
import models.entities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class LoginScreen implements Screen {
    private static final String DEFAULT_PASSWORD = "P@ssw0rd123";

    public void display() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter hospital ID: (or '0' to return to main menu): ");
            String hospitalId = scanner.nextLine();
            if (hospitalId.equals("0")) {
                System.out.println("Returning to the main menu...");
                break;
            }
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            if (authenticateUser(hospitalId, password)) {
                if (password.equals(DEFAULT_PASSWORD)) {
                    System.out.println("Your password is the default. Please change your password.");
                    changePassword(hospitalId);
                }
                System.out.println("Login successful!");
                // Proceed to the main application logic after login
            } else {
                System.out.println("Invalid hospital ID or password. Please try again.");
            }
        }
        scanner.close();
    }

    private void changePassword(String hospitalId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        // add code to update password of user

        scanner.close();
        System.out.println("Password has been successfully updated for Hospital ID: " + hospitalId);
    }

    private boolean authenticateUser(String hospitalId, String password) {
        DataLoader staffLoader = new StaffLoader();
        DataLoader patientLoader = new PatientLoader();
//        DataLoader inventoryLoader = new InventoryLoader();
        List<Patient> patientList = new ArrayList<>();
        List<Staff> staffList = new ArrayList<>();
//        List<Medicine> inventory = new ArrayList<>();

        String staffPath = "data/Staff_List.xlsx";
        String patientPath = "data/Patient_List.xlsx";
//        String inventoryPath = "data/Medicine_List.xlsx";

        try {
            staffList = staffLoader.loadData(staffPath);
            patientList = patientLoader.loadData(patientPath);
            //inventory = inventoryLoader.loadData(inventoryPath);
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            return false;
        }
        for (Staff staff : staffList) {
            if (staff.getStaffId().equals(hospitalId) && staff.getPassword().equals(password)) {
                return true;
            }
        }

        for (Patient patient : patientList) {
            if (patient.getPatientID().equals(hospitalId) && patient.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

}
