package app.screens;
import app.loaders.*;
import interfaces.DataLoader;
import models.entities.Patient;
import models.entities.Staff;
import models.entities.User;
import java.util.*;

/**
 * This class represents the screen for changing a user's password.
 * It prompts the user to enter their hospital ID, verifies their existence,
 * and allows them to set a new password if the ID is valid.
 */
public class ChangePwScreen {
    /**
     * Displays the password change screen and handles user input for changing the password.
     *
     * @param scanner The scanner instance used for reading user input.
     */
    public void display(Scanner scanner) {
        DataLoader staffLoader = new StaffLoader();
        DataLoader patientLoader = new PatientLoader();
        List<Patient> patientList = new ArrayList<>();
        List<Staff> staffList = new ArrayList<>();

        System.out.print("Enter your hospital ID: ");
        String hospitalId = scanner.nextLine();

        AuthLoader authLoader = new AuthLoader("data/Auth_Data.xlsx");
        Map<String, String[]> authData = authLoader.loadData();

        if (!authData.containsKey(hospitalId)) {
            System.out.println("User not found. Please check your hospital ID.");
            return;
        }

        String[] userData = authData.get(hospitalId);
        String salt = userData[0];
        String hashedPassword = userData[1];

        try {
            staffList = staffLoader.loadData("data/Staff_List.xlsx");
            patientList = patientLoader.loadData("data/Patient_List.xlsx");
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        User user = null;
        for (Staff staff : staffList) {
            if (staff.getStaffId().equals(hospitalId)) {
                user = staff;
                break;
            }
        }
        if(user == null) {
            for (Patient patient : patientList) {
                if (patient.getPatientID().equals(hospitalId)) {
                    user = patient;
                }
            }
        }
        if(user != null) {
            System.out.print("Enter your new password: ");
            String newPassword = scanner.nextLine();
            if (user.setPassword(newPassword)) {
                user.storePassword();
                System.out.println("Password changed successfully.");
            } else {
                System.out.println("Failed to change password. Password does not meet requirements.");
            }
        }
    }
}
