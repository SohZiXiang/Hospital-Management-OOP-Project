package app.screens;
import app.Main;
import app.loaders.*;
import interfaces.*;
import app.Main;
import models.entities.*;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class LoginScreen implements BaseScreen {
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
                switch (user.getRole()) {
                    case PATIENT:
//                        PatientScreen patientScreen = new PatientScreen();
//                        patientScreen.display(scanner, user);
                    case DOCTOR:
//                        DoctorScreen doctorScreen = new DoctorScreen();
//                        doctorScreen.display(scanner, user);
                    case PHARMACIST:
                        PharmacistMainScreen pharmacistScreen = new PharmacistMainScreen();
                        pharmacistScreen.display(scanner, user);
                    case ADMINISTRATOR:
                        AdminMainScreen adminScreen = new AdminMainScreen();
                        adminScreen.display(scanner,user);
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
        boolean passwordValid = false;

        while (!passwordValid) {
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine();
            passwordValid = user.setPassword(newPassword);
        }
        user.storePassword();
        System.out.println("Password has been successfully updated!");
    }

    private User authenticateUser(String hospitalId, String password) {
        DataLoader staffLoader = new StaffLoader();
        DataLoader patientLoader = new PatientLoader();
        List<Patient> patientList = new ArrayList<>();
        List<Staff> staffList = new ArrayList<>();

        String authDataPath = "data/Auth_Data.xlsx";
        String staffPath = "data/Staff_List.xlsx";
        String patientPath = "data/Patient_List.xlsx";

        AuthLoader authLoader = new AuthLoader(authDataPath);
        Map<String, String[]> authData = authLoader.loadAuthData();

        if(authData.containsKey(hospitalId)) {
            String[] storedData = authData.get(hospitalId);
            String storedSalt = storedData[0];
            String storedPassword = storedData[1];
            if (storedPassword == null || storedPassword.isEmpty() || storedSalt == null || storedSalt.isEmpty()) {
                storedSalt = PasswordUtil.generateSalt();
                storedPassword = PasswordUtil.hashPassword("P@ssw0rd123", storedSalt);
            }
            String hashedPassword = PasswordUtil.hashPassword(password, storedSalt);

            try {
                staffList = staffLoader.loadData(staffPath);
                patientList = patientLoader.loadData(patientPath);
            } catch (Exception e) {
                System.err.println("Error loading data: " + e.getMessage());
                return null;
            }
            if (hashedPassword.equals(storedPassword)) {{
                for (Staff staff : staffList) {
                    if (staff.getStaffId().equals(hospitalId)) {
                        return staff;
                    }
                }
            }
                for (Patient patient : patientList) {
                    if (patient.getPatientID().equals(hospitalId)) {
                        return patient;
                    }
                }
            }
        }
        return null;
    }
}