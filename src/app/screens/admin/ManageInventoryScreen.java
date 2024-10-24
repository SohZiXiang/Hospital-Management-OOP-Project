package app.screens.admin;

import app.loaders.*;
import app.screens.AdminMainScreen;
import interfaces.*;
import models.entities.*;
import models.enums.FilePaths;
import models.enums.Gender;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.StringFormatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class ManageInventoryScreen implements Screen {
    private List<Medicine> inventory;
    @Override
    public void display(Scanner scanner, User user) {
        inventory = loadInventory();
        while (true) {
            System.out.println("\n--- Manage Inventory---");
            System.out.println("1. View Inventory");
            System.out.println("2. Add Stock");
            System.out.println("3. Update Stock Levels");
            System.out.println("4. Remove Stock");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        viewInventory(scanner);
                        break;
                    case 2:
                        addStock(scanner);
                        break;
                    case 3:
                        updateStock(scanner);
                        break;
                    case 4:
                        removeStock(scanner);
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

    private List<Medicine> loadInventory() {
        String invPath = FilePaths.INV_DATA.getPath();
        DataLoader invLoader = new InventoryLoader();
        try {
            return invLoader.loadData(invPath);
        } catch (Exception e) {
            System.err.println("Error loading inventory data: " + e.getMessage());
            return List.of();
        }
    }

    private void viewInventory(Scanner scanner) {
        inventory = loadInventory();
        System.out.println("\n--- Current Inventory ---");
        displayMedicineDetailsHeader();
        inventory.stream()
                .forEach(medicine -> displayMedicineDetails(medicine));
    }

    private void displayMedicineDetailsHeader(){
        System.out.printf("\n%-15s %-15s %-10s%n", "Medicine Name", "Initial Stock", "Low Stock Level Alert");
        System.out.println("------------------------------------------------------");
    }

    private void displayMedicineDetails(Medicine medicine) {
        System.out.printf("%-15s %-15d %-10d%n",
                medicine.getName(),
                medicine.getStock(),
                medicine.getLowStockAlert());
    }

    private Medicine findMedicine(String medName) {
        return inventory.stream()
                .filter(medicine -> medicine.getName().equals(medName))
                .findFirst()
                .orElse(null);
    }

    private void addStock(Scanner scanner) {
        System.out.println("\n--- Add new medicine ---");
        System.out.print("Enter Medicine Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Initial Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Low Stock Level Alert: ");
        int stockAlert = Integer.parseInt(scanner.nextLine());

        Medicine newMedicine = new Medicine(name, stock, stockAlert);
        inventory.add(newMedicine);
        writeMedicineToExcel(newMedicine);
    }

    private void updateStock(Scanner scanner) {
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

        updateInventoryInExcel(medicine, name);
    }

    private void removeStock(Scanner scanner){
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
        removeMedicineFromExcel(name);
    }

    private void removeMedicineFromExcel(String name) {
        String staffPath = FilePaths.INV_DATA.getPath();
        try (FileInputStream fis = new FileInputStream(staffPath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowToRemove = -1;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0).getStringCellValue().equals(name)) {
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
            System.out.println("Stock removed successfully.");
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error removing stock from storage: " + e.getMessage());
        }
    }


    private void updateInventoryInExcel(Medicine medicine, String oldName) {
        String filePath = FilePaths.INV_DATA.getPath();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
             Sheet sheet = workbook.getSheetAt(0);
             for (Row row : sheet) {
                 Cell medicineNameCell = row.getCell(0);
                 if (medicineNameCell != null && medicineNameCell.getStringCellValue().toLowerCase()
                        .equals(oldName.toLowerCase())) {
                     row.getCell(0).setCellValue(medicine.getName());
                     row.getCell(1).setCellValue(medicine.getStock());
                     row.getCell(2).setCellValue(medicine.getLowStockAlert());

                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        workbook.write(fos);
                    }
                    System.out.println("Stock updated successfully.");
                }
            }
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error updating stock: " + e.getMessage());
        }
    }

    private void writeMedicineToExcel(Medicine medicine) {
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
