package app.screens.admin;

import app.loaders.*;
import app.screens.*;
import interfaces.*;
import models.entities.*;
import models.enums.*;
import utils.StringFormatUtil;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell; // Make sure to import Cell
import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static models.enums.Gender.*;

public class ManageStaffScreen implements Screen {
    private List<Staff> staffList;

    @Override
    public void display(Scanner scanner, User user) {
        staffList = loadStaffList();
        while (true) {
            System.out.println("\n--- Manage Hospital Staff ---");
            System.out.println("1. View Staff List");
            System.out.println("2. Add Staff Member");
            System.out.println("3. Update Staff Member");
            System.out.println("4. Remove Staff Member");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        viewStaffList(scanner);
                        break;
                    case 2:
                        addStaff(scanner);
                        break;
                    case 3:
                        updateStaff(scanner);
                        break;
                    case 4:
                        removeStaff(scanner, user);
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

    private void viewStaffList(Scanner scanner) {
        System.out.println("\n--- View Staff List ---");
        System.out.println("Filter by: ");
        System.out.println("1. Role");
        System.out.println("2. Gender");
        System.out.println("3. Age Range");
        System.out.println("4. No Filter (View All)");
        System.out.print("Enter your choice: ");

        String filterChoice = scanner.nextLine();
        switch (filterChoice) {
            case "1":
                System.out.println("\nSelect role:");
                System.out.println("1. Doctor");
                System.out.println("2. Pharmacist");
                System.out.print("Enter your choice (1 or 2): ");
                int role = Integer.parseInt(scanner.nextLine());

                Staff newStaff = null;
                switch (role) {
                    case 1:
                        displayStaffDetailsHeader();
                        staffList.stream()
                                .filter(staff -> staff.getRole().toString().toLowerCase().equals("doctor"))
                                .forEach(staff -> displayStaffDetails(staff));
                        break;
                    case 2:
                        displayStaffDetailsHeader();
                        staffList.stream()
                                .filter(staff -> staff.getRole().toString().toLowerCase().equals("pharmacist"))
                                .forEach(staff -> displayStaffDetails(staff));
                        break;
                    default:
                        System.out.println("Invalid role entered. Staff member not added.");
                        return;
                }
                break;
            case "2":
                System.out.println("\nSelect gender:");
                System.out.println("1. Male");
                System.out.println("2. Female");
                System.out.print("Enter your choice (1 or 2): ");
                int genderChoice = Integer.parseInt(scanner.nextLine());
                switch (genderChoice) {
                    case 1:
                        displayStaffDetailsHeader();
                        staffList.stream()
                                .filter(staff -> staff.getGender() == MALE)
                                .forEach(staff -> displayStaffDetails(staff));
                        break;
                    case 2:
                        displayStaffDetailsHeader();
                        staffList.stream()
                                .filter(staff -> staff.getGender() == FEMALE)
                                .forEach(staff -> displayStaffDetails(staff));
                        break;
                    default:
                        System.out.println("Invalid gender choice. Defaulting to Male.");
                        staffList.stream()
                                .filter(staff -> staff.getGender() == MALE)
                                .forEach(staff -> displayStaffDetails(staff));

                }
                break;
            case "3":
                System.out.print("\nEnter minimum age: ");
                int minAge = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter maximum age: ");
                int maxAge = Integer.parseInt(scanner.nextLine());
                displayStaffDetailsHeader();
                staffList.stream()
                        .filter(staff -> staff.getAge() >= minAge && staff.getAge() <= maxAge)
                        .forEach(staff -> displayStaffDetails(staff));
                break;
            case "4":
                displayStaffDetailsHeader();
                staffList.forEach(this::displayStaffDetails);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void addStaff(Scanner scanner) {
        System.out.println("\n--- Add Staff Member ---");
        System.out.print("Enter staff ID: ");
        String staffId = scanner.nextLine();
        if (findStaffById(staffId) != null) {
            System.out.println("Error: Staff ID already exists. Please use a different ID.");
            return;
        }

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        System.out.println("Select gender:");
        System.out.println("1. Male");
        System.out.println("2. Female");
        System.out.print("Enter your choice (1 or 2): ");
        int genderChoice = Integer.parseInt(scanner.nextLine());
        Gender gender = null;

        switch (genderChoice) {
            case 1:
                 gender = MALE;
                 break;
            case 2:
                gender = Gender.FEMALE;
                break;
            default:
                System.out.println("Invalid gender choice. Defaulting to Male.");
                gender = MALE;
        }

        System.out.println("Select role:");
        System.out.println("1. Doctor");
        System.out.println("2. Pharmacist");
        System.out.print("Enter your choice (1 or 2): ");
        int role = Integer.parseInt(scanner.nextLine());

        Staff newStaff = null;
        switch (role) {
            case 1:
                newStaff = new Doctor(staffId, name, gender, age);
                break;
            case 2:
                newStaff = new Pharmacist(staffId, name, gender, age);
                break;
            default:
                System.out.println("Invalid role entered. Staff member not added.");
                return;
        }

        staffList.add(newStaff);
        newStaff.setPassword("P@ssw0rd123");
        writeStaffToExcel(newStaff);
        writeStaffToAuth(newStaff);
    }

    private void updateStaff(Scanner scanner) {
        System.out.println("\n--- Update Staff Member ---");
        System.out.print("Enter the staff ID of the member to update: ");
        String staffId = scanner.nextLine();

        Staff staff = findStaffById(staffId);
        if (staff == null) {
            System.out.println("Staff member not found.");
            return;
        }

        System.out.print("Update name (current: " + staff.getName() + "): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            staff.setName(name);
        }

        System.out.println("\nSelect gender:");
        System.out.println("1. Male");
        System.out.println("2. Female");
        System.out.print("Enter your choice (1 or 2): ");
        int genderChoice = Integer.parseInt(scanner.nextLine());
        switch (genderChoice) {
            case 1:
                staff.setGender(Gender.MALE);
                break;
            case 2:
                staff.setGender(Gender.FEMALE);
                break;
            default:
                System.out.println("Invalid gender choice. Defaulting to Male.");
        }
        System.out.print("Update age (current: " + staff.getAge() + "): ");
        String ageInput = scanner.nextLine();
        if (!ageInput.isEmpty()) {
            int age = Integer.parseInt(ageInput);
            staff.setAge(age);
        }
        System.out.println("\nSelect role:");
        System.out.println("1. Doctor");
        System.out.println("2. Pharmacist");
        System.out.print("Enter your choice (1 or 2): ");
        int role = Integer.parseInt(scanner.nextLine());
        switch (role) {
            case 1:
                if (!(staff instanceof Doctor)) {
                    staff = new Doctor(staff.getStaffId(), staff.getName(), staff.getGender(), staff.getAge());
                } else {
                    System.out.println("Staff member is already a Doctor.");
                }
                break;
            case 2:
                if (!(staff instanceof Pharmacist)) {
                    staff = new Pharmacist(staff.getStaffId(), staff.getName(), staff.getGender(), staff.getAge());
                } else {
                    System.out.println("Staff member is already a Pharmacist.");
                }
                break;
            default:
                System.out.println("Invalid role entered. Staff member not added.");
                return;
        }

        if (updateStaffInExcel(staff)) {
            System.out.println("Staff member updated successfully.");
        } else {
            System.out.println("Failed to update staff member in the Excel file.");
        }
    }



    private void removeStaff(Scanner scanner, User user) {
        System.out.println("\n--- Remove Staff Member ---");
        System.out.print("Enter the staff ID of the member to remove: ");
        String staffId = scanner.nextLine();

        Staff staff = findStaffById(staffId);
        if (staff == null) {
            System.out.println("Staff member not found.");
            return;
        }
        if(staff.getHospitalID().equals(user.getHospitalID())){
            System.out.println("Can't remove current user.");
            return;
        }

        staffList.remove(staff);
        if (removeStaffFromExcel(staffId)) {
            System.out.println("Staff member removed successfully from Staff_List.xlsx.");
        } else {
            System.out.println("Failed to remove staff member from Staff_List.xlsx.");
        }

        if (removeStaffAuthData(staffId)) {
            System.out.println("Staff account data removed successfully from auth_data.");
        } else {
            System.out.println("Failed to remove staff account data from auth_data.");
        }
    }

    private Staff findStaffById(String staffId) {
        return staffList.stream()
                .filter(staff -> staff.getStaffId().equals(staffId))
                .findFirst()
                .orElse(null);
    }

    private List<Staff> loadStaffList() {
        String staffPath = FilePaths.STAFF_DATA.getPath();
        DataLoader staffLoader = new StaffLoader();
        try {
            return staffLoader.loadData(staffPath);
        } catch (Exception e) {
            System.err.println("Error loading staff data: " + e.getMessage());
            return List.of();
        }
    }

    private void displayStaffDetailsHeader(){
        System.out.printf("\n%-15s %-20s %-20s %-10s %-10s%n", "Staff ID", "Name", "Role", "Gender", "Age");
        System.out.println("--------------------------------------------------------------------------");
    }

    private void displayStaffDetails(Staff staff) {
        System.out.printf("%-15s %-20s %-20s %-10s %-10d%n",
                staff.getStaffId(),
                staff.getName(),
                staff.getRole().toString(),
                staff.getGender().toString(),
                staff.getAge());
    }

    private void writeStaffToExcel(Staff staff) {
        String filePath = FilePaths.STAFF_DATA.getPath();
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Sheet1");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Staff ID");
                headerRow.createCell(1).setCellValue("Name");
                headerRow.createCell(2).setCellValue("Role");
                headerRow.createCell(3).setCellValue("Gender");
                headerRow.createCell(4).setCellValue("Age");
            } else {
                fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
            }

            Sheet sheet = workbook.getSheet("Sheet1");
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(staff.getStaffId());
            newRow.createCell(1).setCellValue(staff.getName());
            newRow.createCell(2).setCellValue(StringFormatUtil.toCamelCase(staff.getRole().toString()));
            newRow.createCell(3).setCellValue(StringFormatUtil.toCamelCase(staff.getGender().toString()));
            newRow.createCell(4).setCellValue(staff.getAge());

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Staff member added successfully.");

        } catch (IOException e) {
            System.err.println("Error writing to Excel: " + e.getMessage());
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

    private void writeStaffToAuth(Staff staff) {
        String authDataPath = FilePaths.AUTH_DATA.getPath();
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            File file = new File(authDataPath);
            if (!file.exists()) {
                workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Sheet1");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Hospital ID");
                headerRow.createCell(1).setCellValue("Salt");
                headerRow.createCell(1).setCellValue("Password");
            } else {
                fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
            }

            Sheet sheet = workbook.getSheet("Sheet1");
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(staff.getStaffId());
            newRow.createCell(1).setCellValue(staff.getSalt());
            newRow.createCell(2).setCellValue(staff.getPassword());

            try (FileOutputStream fos = new FileOutputStream(authDataPath)) {
                workbook.write(fos);
            }

            System.out.println("Authentication data added successfully for staff ID: " + staff.getStaffId());

        } catch (IOException e) {
            System.err.println("Error writing to Auth_Data: " + e.getMessage());
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

    private boolean updateStaffInExcel(Staff staff) {
        String staffPath = FilePaths.STAFF_DATA.getPath();
        try (FileInputStream fis = new FileInputStream(staffPath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell staffIdCell = row.getCell(0);
                if (staffIdCell != null && staffIdCell.getStringCellValue().equals(staff.getStaffId())) {
                    row.getCell(1).setCellValue(staff.getName());
                    row.getCell(2).setCellValue(StringFormatUtil.toCamelCase(staff.getRole().toString()));
                    row.getCell(3).setCellValue(StringFormatUtil.toCamelCase(staff.getGender().toString()));
                    row.getCell(4).setCellValue(staff.getAge());

                    try (FileOutputStream fos = new FileOutputStream(staffPath)) {
                        workbook.write(fos);
                    }
                    return true;
                }
            }
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error updating staff in Excel: " + e.getMessage());
        }
        return false;
    }

    private boolean removeStaffFromExcel(String staffId) {
        String staffPath = FilePaths.STAFF_DATA.getPath();
        try (FileInputStream fis = new FileInputStream(staffPath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowToRemove = -1;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0).getStringCellValue().equals(staffId)) {
                    rowToRemove = i;
                    break;
                }
            }

            if (rowToRemove == -1) {
                System.out.println("Staff ID not found.");
                return false;
            }
            sheet.removeRow(sheet.getRow(rowToRemove));
            int lastRowIndex = sheet.getLastRowNum();
            if (rowToRemove < lastRowIndex) {
                sheet.shiftRows(rowToRemove + 1, lastRowIndex, -1); // Shift rows up
            }

            try (FileOutputStream fos = new FileOutputStream(staffPath)) {
                workbook.write(fos);
            }
            return true;
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error removing staff in Excel: " + e.getMessage());
        }
        return false;
    }

    private boolean removeStaffAuthData(String staffId) {
        String authDataPath = FilePaths.AUTH_DATA.getPath();
        try (FileInputStream fis = new FileInputStream(authDataPath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowToRemove = -1;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0).getStringCellValue().equals(staffId)) {
                    rowToRemove = i;
                    break;
                }
            }

            if (rowToRemove == -1) {
                return false;
            }
            sheet.removeRow(sheet.getRow(rowToRemove));
            int lastRowIndex = sheet.getLastRowNum();
            if (rowToRemove < lastRowIndex) {
                sheet.shiftRows(rowToRemove + 1, lastRowIndex, -1); // Shift rows up
            }
            try (FileOutputStream fos = new FileOutputStream(authDataPath)) {
                workbook.write(fos);
            }
            return true;
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error removing account data in auth_data: " + e.getMessage());
        }
        return false;
    }
}
