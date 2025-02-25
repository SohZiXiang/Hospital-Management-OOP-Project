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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a Pharmacist who can manage prescriptions, update inventory, and handle replenishment requests.
 * Extends the Staff class and implements specific pharmacist-related functionalities.
 */
public class Pharmacist extends Staff {
    private List<Appointment> apptList;
    private boolean apptsLoaded = false;

    /**
     * Constructs a Pharmacist with specified hospital ID, staff ID, name, gender, and age.
     *
     * @param hospitalID The hospital ID of the pharmacist.
     * @param staffId    The staff ID of the pharmacist.
     * @param name       The name of the pharmacist.
     * @param gender     The gender of the pharmacist.
     * @param age        The age of the pharmacist.
     */
    public Pharmacist(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
    }

    /**
     * Constructs a Pharmacist with specified staff ID, name, gender, and age.
     *
     * @param staffId The staff ID of the pharmacist.
     * @param name    The name of the pharmacist.
     * @param gender  The gender of the pharmacist.
     * @param age     The age of the pharmacist.
     */
    public Pharmacist(String staffId, String name, Gender gender, int age) {
        super(staffId,staffId, name, gender, age);
    }

    /**
     * Gets the role of the pharmacist.
     *
     * @return The Role.PHARMACIST enum.
     */
    @Override
    public Role getRole() {
        return Role.PHARMACIST;
    }

    /**
     * Loads the appointment list if it hasn't been loaded already.
     */
    private void loadApptList() {
        if (!apptsLoaded) {
            ApptAvailLoader apptLoader = new ApptAvailLoader();
            String path = FilePaths.APPT_DATA.getPath();
            this.apptList = apptLoader.loadData(path);
            apptsLoaded = true;
        }
    }

    /**
     * Views all prescription records by loading appointment outcomes and displaying them.
     */
    public void viewAllPrescriptionRecords() {
        loadApptList(); // Ensure appointments are loaded

        ApptOutcomeLoader outcomeLoader = new ApptOutcomeLoader(this.apptList);
        String filePath = FilePaths.APPTOUTCOME.getPath();
        List<AppointmentOutcomeRecord> outcomeRecords = outcomeLoader.loadData(filePath);

        if (outcomeRecords.isEmpty()) {
            System.out.println("No records loaded. Wait for a doctor to input Appointment Outcome");
            return;
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd yyyy");
        // Print headers
        System.out.printf("%-15s %-15s %-15s %-20s %-25s %-10s %-20s %-40s %-30s\n",
                "Appointment ID", "Patient ID", "Doctor ID", "Medication Status", "Medicine Name(s)",
                "Quantity", "Service Type", "Consultation Notes", "Date");

        for (AppointmentOutcomeRecord apptOutRecord : outcomeRecords) {
            for (AppointmentOutcomeRecord.PrescribedMedication prescription : apptOutRecord.getPrescriptions()) {
                String prescribeStatus = prescription.getStatus().toString(); // Convert enum to String
                String formattedDate = dateFormatter.format(apptOutRecord.getAppointmentDate());

                // Print each record
                System.out.printf("%-15s %-15s %-15s %-20s %-25s %-10d %-20s %-40s %-30s\n",
                        apptOutRecord.getAppt().getAppointmentId(),
                        apptOutRecord.getAppt().getPatientId(),
                        apptOutRecord.getAppt().getDoctorId(),
                        prescribeStatus,
                        prescription.getMedicine().getName(),
                        prescription.getQuantityOfMed(),
                        apptOutRecord.getServiceType(),
                        apptOutRecord.getConsultationNotes(),
                        formattedDate);
            }
        }
    }

    /**
     * Checks if the specified appointment ID exists in the Appointment Outcome file.
     *
     * @param appointmentId The appointment ID to check.
     * @return True if the appointment ID is valid, false otherwise.
     */
    public boolean AppointmentIDValid(String appointmentId) {
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

    /**
     * Updates the prescription status for a specified medicine in a given appointment.
     *
     * @param appointmentID The appointment ID for which the prescription status is updated.
     * @param medicineName  The name of the medicine to update.
     */
    public void updatePrescriptionStatus(String appointmentID, String medicineName) {
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

                //apptID Found & !Null
                if (apptIdCell != null && apptIdCell.getStringCellValue().equals(appointmentID)) {
                    appointmentFound = true;

                    // Split the cell values to handle multiple medicines and statuses within a single row
                    List<String> medicines = Arrays.asList(medicineCell.getStringCellValue().split(",\\s*"));
                    List<String> statuses = Arrays.asList(statusCell.getStringCellValue().split(",\\s*"));
                    List<Integer> quantities = ApptOutcomeLoader.splitQuantityClm(quantityCell);

                    //StringBuilder to store updated statuses(dispense,pending)
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
                                    System.out.println("Prescription DISPENSED for Appointment ID: " + appointmentID + ", Medicine: " + medicineName);
                                } else {
                                    System.out.println("Dispense rejected for Medicine: " + medicineName + " in Appointment ID: " + appointmentID);
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
                        System.out.println("All medicines for Appointment ID " + appointmentID + " have already been dispensed or there were no medicines to dispense.");
                        return;
                    }

                    // Join the updated statuses back into a single string
                    for (String status : statuses) {
                        if (newStatuses.length() > 0) newStatuses.append(", ");
                        newStatuses.append(status);
                    }

                    statusCell.setCellValue(newStatuses.toString()); // Update status cell
                    break; // Exit loop after updating
                }
            }

            if (!appointmentFound) {
                System.out.println("No appointment found with ID: " + appointmentID);
            } else if (!medicineFound) {
                System.out.println("No prescription found for Medicine: " + medicineName + " in Appointment ID: " + appointmentID);
            }

            // Save back to Excel
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            System.err.println("Error updating Appointment Outcome Record: " + e.getMessage());
        }

        // Update the inventory if medicine found
        if (medicineFound && dispensedQuantity > 0) {
            updateInventory(medicineName, dispensedQuantity);
        }
    }

    /**
     * Updates the inventory by decrementing the stock of a specified medicine.
     *
     * @param medicineName      The name of the medicine.
     * @param dispensedQuantity The quantity to be deducted from the stock.
     */
    private void updateInventory(String medicineName, int dispensedQuantity) {
        String inventoryPath = FilePaths.INV_DATA.getPath();
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
                        System.out.println("Insufficient stock for " + medicineName);
                        newStock = 0;
                    }

                    row.getCell(1).setCellValue(newStock); // Update the stock quantity
                    medicineFound = true;
                    System.out.println("Updated inventory. " + medicineName + " stock is now " + newStock);
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

    /**
     * Checks if there are any pending medicines for the specified appointment ID.
     * This method searches through the Appointment Outcome file to see if any medications
     * associated with the given appointment have a status other than "DISPENSED" (ie. PENDING).
     *
     * @param appointmentId the ID of the appointment to check for pending medicines.
     * @return {@code true} if there are any pending medicines for the appointment;
     *         {@code false} otherwise.
     */
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

    /**
     * Displays the stock of all medicines in a formatted table.
     *
     * @param medicineStock the list of medicines with their stock levels.
     */
    public void viewAllMedicineStock(List<Medicine> medicineStock) {
        System.out.println("Medicine Inventory:");
        System.out.printf("%-25s %-10s\n", "Medicine Name", "Stock");

        for (Medicine med : medicineStock) {
            System.out.printf("%-25s %-10d\n", med.getName(), med.getQuantity());
        }
    }

    /**
     * Continuously prompts the user to enter the name of a specific medicine to view its stock details.
     * This includes displaying the medicine name, current stock level, and low stock alert level.
     * The method continues to prompt for input until the user enters '0' to exit.
     *
     * @param medicineStock the list of medicines with their stock levels.
     * @param scanner       the Scanner instance used to capture user input.
     */
    public void viewSpecificMedicineStock(List<Medicine> medicineStock, Scanner scanner) {
        while (true) {
            System.out.println("\nEnter medicine name to view current stock (or enter '0' to exit): ");
            String inputMedicine = scanner.nextLine().trim();

            if (inputMedicine.equals("0")) {
                System.out.println("Exiting view specific medicine stock.");
                break;
            }

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
    }


    /**
     * Submits a replenishment request for a specific medicine if it meets the required conditions.
     * If the current stock exceeds the low stock alert level, the user is asked for confirmation.
     * A replenishment request is then created and submitted to the request manager.
     *
     * @param medicineName    the name of the medicine to request replenishment for.
     * @param requestedAmount the amount to be replenished.
     * @param scanner         a Scanner instance for reading user input.
     */
    public void submitReplenishmentRequest(String medicineName, int requestedAmount, Scanner scanner) {
        if (requestedAmount <= 0) {
            System.out.println("Value must be more than 0.");
            return;
        }

        InventoryLoader loader = new InventoryLoader();// Load the inventory of medicines
        List<Medicine> medicineStock = loader.loadData("data/Medicine_List.xlsx");

        Medicine selectedMedicine = null;//Find medicine from inventory
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


        ReplenishmentRequestManager requestManager = new ReplenishmentRequestManager();
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
