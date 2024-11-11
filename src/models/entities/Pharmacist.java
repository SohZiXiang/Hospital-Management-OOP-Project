package models.entities;

import app.loaders.ApptAvailLoader;
import app.loaders.ApptOutcomeLoader;
import app.loaders.InventoryLoader;
import models.enums.*;
import models.records.AppointmentOutcomeRecord;
import models.records.ReplenishmentRequest;
import models.records.ReplenishmentRequestManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


public class Pharmacist extends Staff {
    private List<Appointment> apptList;
    private boolean apptsLoaded = false;

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


    private void loadApptList() {
        if (!apptsLoaded) {
            ApptAvailLoader apptLoader = new ApptAvailLoader();
            String path = FilePaths.APPT_DATA.getPath();
            this.apptList = apptLoader.loadData(path);
            apptsLoaded = true;
        }
    }

    public void viewPrescriptionRecords() {
        loadApptList(); // Ensure appointments are loaded

        ApptOutcomeLoader outcomeLoader = new ApptOutcomeLoader(this.apptList);
        String filePath = FilePaths.APPTOUTCOME.getPath();
        List<AppointmentOutcomeRecord> outcomeRecords = outcomeLoader.loadData(filePath);

        if (outcomeRecords.isEmpty()) {
            System.out.println("No records were loaded. Wait for doctor to input Appointment Outcome");
            return;
        }

        System.out.printf("%-15s %-30s %-15s %-15s %-20s %-40s %-25s %-20s %-10s\n",
                "Appointment ID", "Date", "Doctor ID", "Patient ID", "Service Type",
                "Consultation Notes", "Medicine Name(s)", "Medication Status", "Quantity");

        for (AppointmentOutcomeRecord record : outcomeRecords) {
            for (AppointmentOutcomeRecord.PrescribedMedication prescription : record.getPrescriptions()) {
                String status = prescription.getStatus().toString(); // Convert enum to String

                System.out.printf("%-15s %-30s %-15s %-15s %-20s %-40s %-25s %-20s %-10d\n",
                        record.getAppt().getAppointmentId(),
                        record.getAppointmentDate(),
                        record.getAppt().getDoctorId(),
                        record.getAppt().getPatientId(),
                        record.getServiceType(),
                        record.getConsultationNotes(),
                        prescription.getMedicine().getName(),
                        status,
                        prescription.getQuantityOfMed());
            }
        }
    }

    // In Pharmacist.java, add a helper method to validate appointment ID
    public boolean isAppointmentIdValid(String appointmentId) {
        String filePath = FilePaths.APPTOUTCOME.getPath(); // Path to the Appointment Outcome file
        boolean appointmentFound = false;

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell apptIdCell = row.getCell(0);
                if (apptIdCell != null && apptIdCell.getStringCellValue().equals(appointmentId)) {
                    appointmentFound = true;
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading Appointment Outcome file: " + e.getMessage());
        }

        return appointmentFound;
    }

    public void updatePrescriptionStatus(String appointmentId, String medicineName) {
        String filePath = FilePaths.APPTOUTCOME.getPath();
        boolean appointmentFound = false;
        boolean medicineFound = false;
        int dispensedQuantity = 0;

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell apptIdCell = row.getCell(0);
                Cell medicineCell = row.getCell(6);
                Cell statusCell = row.getCell(7);
                Cell quantityCell = row.getCell(8);

                if (apptIdCell != null && apptIdCell.getStringCellValue().equals(appointmentId)) {
                    appointmentFound = true;

                    // Parse multiple medicines and statuses
                    List<String> medicines = Arrays.asList(medicineCell.getStringCellValue().split(",\\s*"));
                    List<String> statuses = Arrays.asList(statusCell.getStringCellValue().split(",\\s*"));
                    List<Integer> quantities = ApptOutcomeLoader.splitQuantityClm(quantityCell);

                    StringBuilder newStatuses = new StringBuilder();
                    boolean updateMade = false;

                    for (int j = 0; j < medicines.size(); j++) {
                        String currentMedicine = medicines.get(j);
                        String currentStatus = statuses.get(j);

                        if (currentMedicine.equalsIgnoreCase(medicineName)) {
                            if (!currentStatus.equalsIgnoreCase("DISPENSED")) {
                                System.out.print("Do you want to dispense " + medicineName + "? (D to dispense, any key to reject): ");
                                Scanner scanner = new Scanner(System.in);
                                String decision = scanner.nextLine().trim().toUpperCase();

                                if (decision.equals("D")) {
                                    statuses.set(j, "DISPENSED");
                                    dispensedQuantity = quantities.get(j);
                                    updateMade = true;
                                    medicineFound = true;
                                    System.out.println("Prescription status updated to DISPENSED for Appointment ID: " + appointmentId + ", Medicine: " + medicineName);
                                } else {
                                    System.out.println("Dispense rejected for Medicine: " + medicineName + " in Appointment ID: " + appointmentId);
                                    return;
                                }
                            } else {
                                System.out.println("Prescription for " + medicineName + " has already been dispensed.");
                                return;
                            }
                        }
                    }

                    if (!updateMade && medicineFound) {
                        // If no pending prescriptions were found to update
                        System.out.println("All medicines for Appointment ID " + appointmentId + " have already been dispensed or there were no medicines to dispense.");
                        return;
                    }

                    // Join the updated statuses back into a single string
                    for (String status : statuses) {
                        if (newStatuses.length() > 0) newStatuses.append(", ");
                        newStatuses.append(status);
                    }

                    statusCell.setCellValue(newStatuses.toString()); // Update cell value
                    break; // Exit loop after updating
                }
            }

            if (!appointmentFound) {
                System.out.println("No appointment found with ID: " + appointmentId);
            } else if (!medicineFound) {
                System.out.println("No prescription found for Medicine: " + medicineName + " in Appointment ID: " + appointmentId);
            }

            // Save changes to the Excel file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            System.err.println("Error updating Appointment Outcome Record: " + e.getMessage());
        }

        // Update the inventory if needed
        if (medicineFound && dispensedQuantity > 0) {
            updateInventory(medicineName, dispensedQuantity);
        }
    }

    // Helper function to update inventory
    private void updateInventory(String medicineName, int dispensedQuantity) {
        String inventoryPath = FilePaths.INV_DATA.getPath(); // Path to the Medicine Inventory file
        boolean medicineFound = false;

        try (FileInputStream fis = new FileInputStream(inventoryPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String currentMedicineName = row.getCell(0).getStringCellValue();

                if (currentMedicineName.equalsIgnoreCase(medicineName)) {
                    int currentStock = (int) row.getCell(1).getNumericCellValue();
                    int newStock = currentStock - dispensedQuantity;

                    if (newStock < 0) {
                        System.out.println("Warning: Insufficient stock for " + medicineName);
                        newStock = 0;
                    }

                    row.getCell(1).setCellValue(newStock); // Update the stock quantity
                    medicineFound = true;
                    System.out.println("Inventory updated. " + medicineName + " stock is now " + newStock);
                    break;
                }
            }

            // Save changes to the Inventory file
            try (FileOutputStream fos = new FileOutputStream(inventoryPath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            System.err.println("Error updating inventory: " + e.getMessage());
        }

        if (!medicineFound) {
            System.out.println("Medicine " + medicineName + " not found in inventory.");
        }
    }

    public boolean hasPendingMedicines(String appointmentId) {
        String filePath = FilePaths.APPTOUTCOME.getPath();
        boolean hasPending = false;

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell apptIdCell = row.getCell(0);
                Cell statusCell = row.getCell(7);

                if (apptIdCell != null && apptIdCell.getStringCellValue().equals(appointmentId)) {
                    if (statusCell == null || statusCell.getStringCellValue().trim().isEmpty()) {
                        // If statusCell is null or empty, assume it's pending or set a default value
                        hasPending = false;
                        continue;
                    }

                    // Split the statuses if there are multiple medicines for the appointment
                    List<String> statuses = Arrays.asList(statusCell.getStringCellValue().split(",\\s*"));

                    // Check each status to see if any medicine is still pending
                    for (String status : statuses) {
                        if (!status.equalsIgnoreCase("DISPENSED")) {
                            hasPending = true;
                            break;
                        }
                    }

                    // Exit early if any pending status is found
                    if (hasPending) {
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading Appointment Outcome file: " + e.getMessage());
        }

        return hasPending;
    }


    public void viewAllMedicineStock(List<Medicine> medicineStock) {
        System.out.println("Medicine Inventory:");
        System.out.printf("%-25s %-10s\n", "Medicine Name", "Stock");

        for (Medicine med : medicineStock) {
            System.out.printf("%-25s %-10d\n", med.getName(), med.getQuantity());
        }
    }

    public void viewSpecificMedicineStock(List<Medicine> medicineStock, String inputMedicine) {
        boolean found = false;
        for (Medicine med : medicineStock) {
            if (med.getName().equalsIgnoreCase(inputMedicine)) {
                System.out.printf("Medicine: %-25s\n", med.getName());
                System.out.printf("Stock: %-25d\n", med.getQuantity());
                System.out.printf("Low Stock Alert Level: %-25d\n", med.getLowStockAlert());
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Medicine " + inputMedicine + " not found in inventory.");
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
            if (request.getMedicineName().equalsIgnoreCase(medicineName)) {
                System.out.println("A replenishment request for " + medicineName + " has already been made previously.");
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
        request = requestManager.addRequest(request);

        System.out.println("Replenishment request with ID:" +request.getRequestId()+ " for " + requestedAmount + " units of " + selectedMedicine.getName() + " has been submitted.");
    }



}
