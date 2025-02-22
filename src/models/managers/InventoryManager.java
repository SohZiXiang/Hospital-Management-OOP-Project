package models.managers;

import app.loaders.InventoryLoader;
import interfaces.DataLoader;
import interfaces.DeleteExcel;
import interfaces.UpdateExcel;
import interfaces.WriteExcel;
import models.entities.Medicine;
import models.entities.User;
import models.enums.FilePaths;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.ActivityLogUtil;
import utils.EmailUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


/**
 * The {@code InventoryManager} class provides functionality to manage a medicine inventory,
 * including viewing, adding, updating, and removing medicines. It also handles interactions with
 * an Excel file for data storage and updates, and includes logging and notification functionalities.
 * This class implements {@link WriteExcel}, {@link UpdateExcel}, and {@link DeleteExcel} interfaces
 * to manage Excel data operations.
 */
public class InventoryManager implements WriteExcel, UpdateExcel, DeleteExcel {
    private List<Medicine> inventory;

    /**
     * Loads the inventory of medicines from the data file.
     *
     * @return a list of Medicine objects representing the current inventory.
     */
    public static List<Medicine> loadInventory() {
        String invPath = FilePaths.INV_DATA.getPath();
        DataLoader invLoader = new InventoryLoader();
        try {
            return invLoader.loadData(invPath);
        } catch (Exception e) {
            System.err.println("Error loading inventory data: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Displays the current inventory of medicines to the user.
     *
     * @param scanner    The Scanner object for reading user input.
     * @param currentUser The User object representing the currently logged-in user.
     */
    public void viewInventory(Scanner scanner, User currentUser) {
        inventory = loadInventory();
        String logMsg = "User " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") viewed Inventory List.";
        ActivityLogUtil.logActivity(logMsg, currentUser);
        System.out.println("\n--- Current Inventory ---");
        displayMedicineDetailsHeader();
        inventory.stream()
                .forEach(medicine -> displayMedicineDetails(medicine));
    }

    /**
     * Displays the header for the medicine details table.
     */
    public void displayMedicineDetailsHeader(){
        System.out.printf("\n%-15s %-15s %-10s%n", "Medicine Name", "Initial Stock", "Low Stock Level Alert");
        System.out.println("------------------------------------------------------");
    }

    /**
     * Displays the details of a given medicine.
     *
     * @param medicine The Medicine object to display.
     */
    public void displayMedicineDetails(Medicine medicine) {
        System.out.printf("%-15s %-15d %-10d%n",
                medicine.getName(),
                medicine.getStock(),
                medicine.getLowStockAlert());
    }

    /**
     * Finds a medicine in the inventory by its name.
     *
     * @param medName The name of the medicine to find.
     * @return The Medicine object if found, otherwise null.
     */
    public Medicine findMedicine(String medName) {
        inventory = loadInventory();
        return inventory.stream()
                .filter(medicine -> medicine.getName().toLowerCase().equals(medName.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a new stock of medicine to the inventory.
     *
     * @param scanner The Scanner object for reading user input.
     * @param user    The User object representing the currently logged-in user.
     */
    public void addStock(Scanner scanner, User user) {
        while(true) {
            inventory = loadInventory();

            System.out.println("\n--- Add new medicine ---");
            System.out.print("Enter Medicine Name: ");
            String name = scanner.nextLine();
            boolean exists = inventory.stream().anyMatch(medicine -> medicine.getName().equalsIgnoreCase(name));
            if (exists) {
                System.out.println("Medicine with this name already exists in the inventory.");
                continue;
            }

            System.out.print("Enter Initial Stock: ");
            int stock = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Low Stock Level Alert: ");
            int stockAlert = Integer.parseInt(scanner.nextLine());

            Medicine newMedicine = new Medicine(name, stock, stockAlert);
            inventory.add(newMedicine);
            writeToExcel(newMedicine, user);
            EmailUtil.checkInvAndNotify("phclerk00@outlook.com");
            break;
        }
    }

    /**
     * Updates the stock level of an existing medicine from Administrator's Inventory Management Screen.
     *
     * @param scanner The Scanner object for reading user input.
     * @param user    The User object representing the currently logged-in user.
     */
    public void updateStock(Scanner scanner, User user) {
        inventory = loadInventory();
        System.out.println("\n--- Update Stock ---");
        System.out.print("Enter the name of medicine to update: ");
        String name = scanner.nextLine();

        Medicine medicine = findMedicine(name);
        if (medicine == null) {
            System.out.println("Medicine not found.");
            return;
        }

        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. Stock level");
        System.out.println("3. Low stock level alert");
        System.out.println("4. Update all fields");
        System.out.print("Enter your choice (1-4): ");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1:
                System.out.print("Update name (current: " + medicine.getName() + "): ");
                String newName = scanner.nextLine();
                if (!newName.isEmpty()) {
                    medicine.setName(newName);
                }
                break;

            case 2:
                System.out.print("Update initial stock (current: " + medicine.getStock() + "): ");
                String stock = scanner.nextLine();
                if (!stock.isEmpty()) {
                    medicine.setStock(Integer.parseInt(stock));
                }
                break;

            case 3:
                System.out.print("Update low level stock alert (current: " + medicine.getLowStockAlert() + "): ");
                String stockAlert = scanner.nextLine();
                if (!stockAlert.isEmpty()) {
                    medicine.setLowStockAlert(Integer.parseInt(stockAlert));
                }
                break;

            case 4:
                System.out.print("Update name (current: " + medicine.getName() + "): ");
                newName = scanner.nextLine();
                if (!newName.isEmpty()) {
                    medicine.setName(newName);
                }

                System.out.print("Update initial stock (current: " + medicine.getStock() + "): ");
                stock = scanner.nextLine();
                if (!stock.isEmpty()) {
                    medicine.setStock(Integer.parseInt(stock));
                }

                System.out.print("Update low level stock alert (current: " + medicine.getLowStockAlert() + "): ");
                stockAlert = scanner.nextLine();
                if (!stockAlert.isEmpty()) {
                    medicine.setLowStockAlert(Integer.parseInt(stockAlert));
                }
                break;

            default:
                System.out.println("Invalid choice.");
                return;
        }
        updateToExcel(medicine, name, user);
        EmailUtil.checkInvAndNotify("phclerk00@outlook.com");
    }

    /**
     * Updates the stock level of a medicine.
     *
     * @param medicine The Medicine object to update.
     * @param stock    The new stock level to set.
     * @param user     The User object representing the currently logged-in user.
     */
    public void updateStock(Medicine medicine, int stock, User user) {
        medicine.setStock(stock);
        updateToExcel(medicine, medicine.getName(), user);
    }

    /**
     * Removes a medicine stock from the inventory.
     *
     * @param scanner The Scanner object for reading user input.
     * @param user    The User object representing the currently logged-in user.
     */
    public void removeStock(Scanner scanner, User user){
        inventory = loadInventory();
        System.out.println("\n--- Remove Stock ---");
        System.out.print("Enter the name of stock to remove: ");
        String name = scanner.nextLine();

        Medicine medicine = findMedicine(name);
        if (medicine == null) {
            System.out.println("Stock not found.");
            return;
        }
        inventory.remove(medicine);
        removeFromExcel(name, user);
    }

    /**
     * Removes a medicine from the Excel storage.
     *
     * @param name        The name of the medicine to remove.
     * @param currentUser The User object representing the currently logged-in user.
     */
    public void removeFromExcel(String name, User currentUser) {
        String staffPath = FilePaths.INV_DATA.getPath();
        try (FileInputStream fis = new FileInputStream(staffPath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowToRemove = -1;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0).getStringCellValue().toLowerCase().equals(name.toLowerCase())) {
                    rowToRemove = i;
                    break;
                }
            }

            if (rowToRemove == -1) {
                System.out.println("Stock not found in storage.");
                return;
            }
            sheet.removeRow(sheet.getRow(rowToRemove));
            int lastRowIndex = sheet.getLastRowNum();
            if (rowToRemove < lastRowIndex) {
                sheet.shiftRows(rowToRemove + 1, lastRowIndex, -1);
            }

            try (FileOutputStream fos = new FileOutputStream(staffPath)) {
                workbook.write(fos);
            }
            String logMsg = "User " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                    "removed Inventory stock: " + name + "." ;
            ActivityLogUtil.logActivity(logMsg, currentUser);
            System.out.println("Stock removed successfully.");
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error removing stock from storage: " + e.getMessage());
        }
    }


    /**
     * Updates the inventory in Excel based on the provided medicine object.
     *
     * @param medicine    The Medicine object with updated details.
     * @param oldName     The previous name of the medicine.
     * @param currentUser The User object representing the currently logged-in user.
     */
    public void updateToExcel(Medicine medicine, String oldName, User currentUser) {
        String filePath = FilePaths.INV_DATA.getPath();
        int prevStock = 0;
        int prevStockAlert = 0;
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell medicineNameCell = row.getCell(0);
                if (medicineNameCell != null && medicineNameCell.getStringCellValue().toLowerCase()
                        .equals(oldName.toLowerCase())) {
                    prevStock = (int)row.getCell(1).getNumericCellValue();
                    prevStockAlert =(int) row.getCell(2).getNumericCellValue();
                    row.getCell(0).setCellValue(medicine.getName());
                    row.getCell(1).setCellValue(medicine.getStock());
                    row.getCell(2).setCellValue(medicine.getLowStockAlert());

                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        workbook.write(fos);
                    }
                    String logMsg = "User " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                            "updated inventory for medicine: " + medicine.getName() +
                            " from stock: " + prevStock + " to updated stock: " + medicine.getStock() +
                            " with low stock alert from: " + prevStockAlert + " to: " + medicine.getLowStockAlert() + ".";
                    ActivityLogUtil.logActivity(logMsg, currentUser);
                    System.out.println("Stock updated successfully.");
                }
            }
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error updating stock: " + e.getMessage());
        }
    }

    /**
     * Writes a new medicine's details to the Excel inventory.
     *
     * @param medicine    The Medicine object to be added to the inventory.
     * @param currentUser The User object representing the currently logged-in user.
     */
    @Override
    public void writeToExcel(Medicine medicine, User currentUser) {
        String filePath = FilePaths.INV_DATA.getPath();
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Sheet1");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Medicine Name");
                headerRow.createCell(1).setCellValue("Initial Stock");
                headerRow.createCell(2).setCellValue("Low Stock Level Alert");
            } else {
                fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
            }

            Sheet sheet = workbook.getSheet("Sheet1");
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(medicine.getName());
            newRow.createCell(1).setCellValue(medicine.getStock());
            newRow.createCell(2).setCellValue(medicine.getLowStockAlert());

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            String logMsg = "User " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                    "Added Inventory stock: " + medicine.getName() + "." ;
            ActivityLogUtil.logActivity(logMsg, currentUser);
            System.out.println("Stock added successfully.");

        } catch (IOException e) {
            System.err.println("Error storing new stock data: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
