package app.screens;
import app.loaders.InventoryLoader;
import app.loaders.PatientLoader;
import app.loaders.StaffLoader;
import interfaces.DataLoader;
import models.entities.Medicine;
import models.entities.Patient;
import models.entities.Staff;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoginScreen {
    public void display() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter hospital ID: ");
        String hospitalId = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();


        if (authenticateUser(hospitalId, password)) {
            System.out.println("Login successful!");
            // Proceed to the main application logic after login
        } else {
            System.out.println("Invalid hospital ID or password.");
        }
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
            if (staff.getHospitalID().equals(hospitalId) && staff.getPassword().equals(password)) {
                return true;
            }
        }

        for (Patient patient : patientList) {
            if (patient.getHospitalID().equals(hospitalId) && patient.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

}
