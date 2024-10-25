package models.entities;

import app.loaders.InventoryLoader;
import models.enums.*;
import models.records.ReplenishmentRequest;
import models.records.ReplenishmentRequestManager;

//my imports
import java.util.List;
import java.util.Scanner;


public class Pharmacist extends Staff {
    public Pharmacist(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
    }

    public Pharmacist(String staffId, String name, Gender gender, int age) {
        super(staffId,staffId, name, gender, age);
    }

    @Override
    public Role getRole() {
        return Role.PHARMACIST;
    }

    public void viewAppOutRecord(Appointment appointment) {
        System.out.println("Viewing appointment outcome record for ID: " + appointment.getAppointmentId());
        System.out.println("Outcome record: " + appointment.getOutcomeRecord());
    }

    public void updatePrescriptionStatus(Appointment appointment, String status) {
        System.out.println("Updating prescription status for Appointment ID: " + appointment.getAppointmentId());
        appointment.setOutcomeRecord(status);  // Update the status, dispensed/pending
    }

    public void viewAllMedicineStock(List<Medicine> medicineStock){
        System.out.println("Medicine Inventory:");
        for (Medicine med : medicineStock) {
            System.out.println("Medicine: " + med.getName() + " | Stock: " + med.getQuantity());
        }
    }

    public void viewSpecificMedicineStock(List<Medicine> medicineStock, String medicineName){
        boolean found = false;
        for (Medicine med : medicineStock) {
            if (med.getName().equalsIgnoreCase(medicineName)) {
                System.out.println("Medicine: " + med.getName());
                System.out.println("Stock: " + med.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Medicine " + medicineName + " not found in inventory.");
        }

    }

    public void submitReplenishmentRequest(String medicineName, int requestedAmount, Scanner scanner) {
        if (requestedAmount <= 0) {
            System.out.println("Requested amount must be greater than zero.");
            return;
        }

        // Load the inventory of medicines
        InventoryLoader loader = new InventoryLoader();
        List<Medicine> medicineStock = loader.loadData("data/Medicine_List.xlsx");

        Medicine selectedMedicine = null;
        for (Medicine medicine : medicineStock) {
            if (medicine.getName().equalsIgnoreCase(medicineName)) {
                selectedMedicine = medicine;
                break;
            }
        }

        if (selectedMedicine == null) {
            System.out.println("Medicine " + medicineName + " not found in inventory.");
            return;
        }

        // Check for existing req (same name)
        ReplenishmentRequestManager requestManager = new ReplenishmentRequestManager();
        List<ReplenishmentRequest> existingRequests = requestManager.getAllRequests();
        for (ReplenishmentRequest request : existingRequests) {
            if (request.getMedicineName().equalsIgnoreCase(medicineName) && request.getHospitalId().equals(getHospitalID())) {
                System.out.println("A replenishment request for " + medicineName + " has already been made by you previously.");
                System.out.println("Do you still want to submit this request? (yes/no): ");
                String reconfirmation = scanner.nextLine().trim().toLowerCase();

                if (!reconfirmation.equals("yes")) {
                    System.out.println("Replenishment request cancelled.");
                    return;
                }
                break;
            }
        }

        // Check if current stock > low stock
        if (selectedMedicine.getQuantity() > selectedMedicine.getLowStockAlert()) {
            System.out.println("Current stock for " + medicineName + " is " + selectedMedicine.getQuantity() + ", which is more than the low stock alert (" + selectedMedicine.getLowStockAlert() + ").");
            System.out.println("Are you sure you want to submit a replenishment request? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!confirmation.equals("yes")) {
                System.out.println("Replenishment request cancelled.");
                return;
            }
        }

        // Submit the replenishment request
        ReplenishmentRequest request = new ReplenishmentRequest(0, getHospitalID(), getName(), selectedMedicine.getName(), requestedAmount);
        request = requestManager.addRequest(request);   // Add request and assign a proper ID

        System.out.println("Replenishment request with ID:" +request.getRequestId()+ " for " + requestedAmount + " units of " + selectedMedicine.getName() + " has been submitted.");
    }



}
