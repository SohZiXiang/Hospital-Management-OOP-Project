package models.entities;

import app.loaders.InventoryLoader;
import app.loaders.StaffLoader;
import app.screens.admin.ViewActivityLogScreen;
import app.screens.admin.ViewAppointmentsScreen;
import interfaces.DataLoader;
import models.enums.FilePaths;
import models.enums.Gender;
import models.enums.Role;
import models.records.ReplenishmentRequest;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.ActivityLogUtil;
import utils.EmailUtil;
import utils.StringFormatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static models.enums.Gender.FEMALE;
import static models.enums.Gender.MALE;

/**
 * The Administrator class represents a user with administrative privileges
 * within the hospital management system. It extends the Staff class and
 * provides methods for managing staff members, including adding, updating,
 * removing, and viewing staff details etc.
 */
public class Administrator extends Staff{
    private List<Staff> staffList;
    private List<Medicine> inventory;
    String repReqPath = FilePaths.REPLENISH_REQ_DATA.getPath();
    List<ReplenishmentRequest> repReqList = new ArrayList<>();
    /**
     * Constructs an Administrator instance with the specified details.
     *
     * @param hospitalID The hospital ID of the administrator.
     * @param staffId    The staff ID of the administrator.
     * @param name       The name of the administrator.
     * @param gender     The gender of the administrator.
     * @param age        The age of the administrator.
     */
    public Administrator(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
    }

    /**
     * Gets the role of the administrator.
     *
     * @return The role of the administrator as an enumeration value.
     */
    @Override
    public Role getRole(){
        return Role.ADMINISTRATOR;
    }

    /**
     * Displays the staff list based on the selected filter criteria.
     *
     * @param scanner    A Scanner instance for user input.
     * @param currentUser The currently logged-in user.
     */
    public void viewStaffList(Scanner scanner, User currentUser) {
        staffList = loadStaffList();
        String logMsg = "User " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") viewed staff list.";
        ActivityLogUtil.logActivity(logMsg, currentUser);
        while (true) {
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
                            continue;
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
                    continue;
            }
            break;
        }
    }
    /**
     * Adds a new staff member based on user input.
     *
     * @param scanner A Scanner instance for user input.
     * @param user    The currently logged-in user.
     */
    public void addStaff(Scanner scanner, User user) {
        Staff newStaff = null;
        while (true) {
            System.out.println("\n--- Add Staff Member ---");
            System.out.print("Enter staff ID: ");
            String staffId = scanner.nextLine();
            boolean validID = false;
            if (staffId.matches("^[PD]\\d+$")) {
                validID = true;
                System.out.println("Valid staff ID entered: " + staffId);
            } else {
                System.out.println("Invalid input. Please enter a valid staff ID");
                continue;
            }
            if (findStaffById(staffId) != null) {
                System.out.println("Error: Staff ID already exists. Please use a different ID.");
                continue;
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
                    System.out.println("Invalid gender choice.");
                    continue;
            }

            System.out.println("Select role:");
            System.out.println("1. Doctor");
            System.out.println("2. Pharmacist");
            System.out.print("Enter your choice (1 or 2): ");
            int role = Integer.parseInt(scanner.nextLine());

            switch (role) {
                case 1:
                    newStaff = new Doctor(staffId, name, gender, age);
                    break;
                case 2:
                    newStaff = new Pharmacist(staffId, name, gender, age);
                    break;
                default:
                    System.out.println("Invalid role entered. Staff member not added.");
                    continue;
            }
            break;
        }

        staffList.add(newStaff);
        newStaff.setPassword("P@ssw0rd123");
        writeStaffToExcel(newStaff, user);
        writeStaffToAuth(newStaff);
    }

    /**
     * Updates an existing staff member's information based on user input.
     *
     * @param scanner    A Scanner instance for user input.
     * @param currentUser The currently logged-in user.
     */
    public void updateStaff(Scanner scanner, User currentUser) {
        staffList = loadStaffList();
        Staff staff = null;
        while (true) {
            System.out.println("\n--- Update Staff Member ---");
            System.out.print("Enter the staff ID of the member to update: ");
            String staffId = scanner.nextLine();
            boolean validID = false;
            if (staffId.matches("^[PD]\\d+$")) {
                validID = true;
                System.out.println("Valid staff ID entered: " + staffId);
            } else {
                System.out.println("Invalid input. Please enter a valid staff ID");
                continue;
            }

            staff = findStaffById(staffId);
            if (staff == null) {
                System.out.println("Staff member not found.");
                continue;
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
                    System.out.println("Invalid gender choice. Please try again.");
                    continue;
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
                    }
                    break;
                case 2:
                    if (!(staff instanceof Pharmacist)) {
                        staff = new Pharmacist(staff.getStaffId(), staff.getName(), staff.getGender(), staff.getAge());
                    }
                    break;
                default:
                    System.out.println("Invalid role entered. Staff member not added.");
                    continue;
            }
            break;
        }
        updateStaffInExcel(staff, currentUser);
    }


    /**
     * Removes a staff member based on their ID.
     *
     * @param scanner A Scanner instance for user input.
     * @param user    The currently logged-in user.
     */
    public void removeStaff(Scanner scanner, User user) {
        staffList = loadStaffList();
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
        removeStaffFromExcel(staffId, user);
        removeStaffAuthData(staffId);
    }
    /**
     * Finds a staff member by their ID.
     *
     * @param staffId The staff ID to search for.
     * @return The Staff object if found, otherwise null.
     */
    public Staff findStaffById(String staffId) {
        staffList = loadStaffList();
        return staffList.stream()
                .filter(staff -> staff.getStaffId().equals(staffId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Loads the list of staff members from an external data source.
     *
     * @return A list of staff members.
     */
    public List<Staff> loadStaffList() {
        String staffPath = FilePaths.STAFF_DATA.getPath();
        DataLoader staffLoader = new StaffLoader();
        try {
            return staffLoader.loadData(staffPath);
        } catch (Exception e) {
            System.err.println("Error loading staff data: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Displays the header for staff details.
     */
    public void displayStaffDetailsHeader(){
        System.out.printf("\n%-15s %-20s %-20s %-10s %-10s%n", "Staff ID", "Name", "Role", "Gender", "Age");
        System.out.println("--------------------------------------------------------------------------");
    }

    /**
     * Displays details of a given staff member.
     *
     * @param staff The staff member whose details are to be displayed.
     */
    public void displayStaffDetails(Staff staff) {
        System.out.printf("%-15s %-20s %-20s %-10s %-10d%n",
                staff.getStaffId(),
                staff.getName(),
                StringFormatUtil.toCamelCase(staff.getRole().toString()),
                StringFormatUtil.toCamelCase(staff.getGender().toString()),
                staff.getAge());
    }

    /**
     * Writes the details of a new staff member to the staff data Excel file.
     *
     * @param staff      The Staff object containing the details of the staff member to be added.
     * @param currentUser The User object representing the currently logged-in user.
     */
    public void writeStaffToExcel(Staff staff, User currentUser) {
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
            String logMsg = "User " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                    "added staff " + staff.getName() + " (ID: " + staff.getHospitalID() + "). " ;
            ActivityLogUtil.logActivity(logMsg, currentUser);
        } catch (IOException e) {
            System.err.println("Error storing new staff data: " + e.getMessage());
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

    /**
     * Writes authentication data (salt and password) for a staff member to the authentication data Excel file.
     *
     * @param staff The Staff object containing the authentication details to be added.
     */
    public void writeStaffToAuth(Staff staff) {
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
            System.err.println("Error storing staff login data: " + e.getMessage());
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

    /**
     * Updates an existing staff member's information in the staff data Excel file.
     *
     * @param staff      The Staff object containing the updated details of the staff member.
     * @param currentUser The User object representing the currently logged-in user.
     */
    public void updateStaffInExcel(Staff staff, User currentUser) {
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
                }
            }
            String logMsg = "User " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                    "updated staff " + staff.getName() + " (ID: " + staff.getHospitalID()+ "). " ;
            ActivityLogUtil.logActivity(logMsg, currentUser);
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error updating staff in Excel: " + e.getMessage());
        }
    }

    /**
     * Removes a staff member from the staff data Excel file based on their staff ID.
     *
     * @param staffId    The staff ID of the member to be removed.
     * @param currentUser The User object representing the currently logged-in user.
     */
    public void removeStaffFromExcel(String staffId, User currentUser) {
        String staffPath = FilePaths.STAFF_DATA.getPath();
        String staffName  = null;
        try (FileInputStream fis = new FileInputStream(staffPath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowToRemove = -1;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0).getStringCellValue().equals(staffId)) {
                    staffName = row.getCell(1).getStringCellValue();
                    rowToRemove = i;
                    break;
                }
            }

            if (rowToRemove == -1) {
                System.out.println("Staff ID not found.");
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
                    "removed staff " + staffName + " (ID: " + staffId + "). " ;
            ActivityLogUtil.logActivity(logMsg, currentUser);
            System.out.println("Staff removed Successfully!");
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error removing staff from storage: " + e.getMessage());
        }
    }

    /**
     * Removes authentication data for a staff member from the authentication data Excel file based on their staff ID.
     *
     * @param staffId The staff ID of the member whose authentication data is to be removed.
     */
    public void removeStaffAuthData(String staffId) {
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
                System.out.println("Could not find staff in storage.");
                return;
            }
            sheet.removeRow(sheet.getRow(rowToRemove));
            int lastRowIndex = sheet.getLastRowNum();
            if (rowToRemove < lastRowIndex) {
                sheet.shiftRows(rowToRemove + 1, lastRowIndex, -1);
            }
            try (FileOutputStream fos = new FileOutputStream(authDataPath)) {
                workbook.write(fos);
            }
            return;
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error removing account data" + e.getMessage());
        }
    }

    /**
     * Loads the ViewAppointmentsScreen page that will list all appointments.
     *
     */
    public void viewAppointments(Scanner scanner, User user) {
        ViewAppointmentsScreen viewAppointmentsScreen = new ViewAppointmentsScreen();
        viewAppointmentsScreen.display(scanner, user);
    }

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
            writeMedicineToExcel(newMedicine, user);
            //EmailUtil.checkInvAndNotify("phclerk00@outlook.com");
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
        updateInventoryInExcel(medicine, name, user);
        //EmailUtil.checkInvAndNotify("phclerk00@outlook.com");
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
        updateInventoryInExcel(medicine, medicine.getName(), user);
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
        removeMedicineFromExcel(name, user);
    }

    /**
     * Removes a medicine from the Excel storage.
     *
     * @param name        The name of the medicine to remove.
     * @param currentUser The User object representing the currently logged-in user.
     */
    public void removeMedicineFromExcel(String name, User currentUser) {
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
    public void updateInventoryInExcel(Medicine medicine, String oldName, User currentUser) {
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
    public void writeMedicineToExcel(Medicine medicine, User currentUser) {
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

    /**
     * Approves a replenishment request, updates the stock of the requested medicine,
     * and changes the request status to "approved".
     *
     * @param index       The index of the request in the list of requests.
     * @param currentUser The Administrator currently approving the request.
     * @param request     The ReplenishmentRequest to be approved.
     */
    public void approveRequest(int index, Administrator currentUser, ReplenishmentRequest request) {
        Medicine medData = findMedicine(request.getMedicineName());
        if (medData != null) {
            int addedStock = request.getRequestedAmount();
            int initialStock = medData.getStock();
            int newStock = addedStock + initialStock;

            updateStock(medData, newStock, currentUser);

            updateRequestStatus(request, "approved");
            request.setStatus("approved");
            System.out.println("Request for " + request.getRequestedAmount() + " of " + request.getMedicineName() + " approved.");
        } else {
            System.out.println("Medicine not found for request: " + request.getMedicineName());
        }
    }

    /**
     * Rejects a replenishment request and updates the request status to "rejected".
     *
     * @param index   The index of the request in the list of requests.
     * @param request The ReplenishmentRequest to be rejected.
     */
    public void rejectRequest(int index, ReplenishmentRequest request) {
        updateRequestStatus(request, "rejected");
        request.setStatus("rejected");

        System.out.println("Request for " + request.getRequestedAmount() + " of " + request.getMedicineName() + " rejected.");
    }

    /**
     * Updates the status of a replenishment request in the Excel file.
     *
     * @param request The ReplenishmentRequest to update.
     * @param status  The new status to set for the request (e.g., "approved" or "rejected").
     */
    public void updateRequestStatus(ReplenishmentRequest request, String status) {
        try (FileInputStream fis = new FileInputStream(repReqPath);
             Workbook workbook = WorkbookFactory.create(fis);
             FileOutputStream fos = new FileOutputStream(repReqPath)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && (int) row.getCell(0).getNumericCellValue() == request.getRequestId()) {
                    row.getCell(5).setCellValue(status);
                    break;
                }
            }
            workbook.write(fos);
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error updating request status: " + e.getMessage());
        }
    }

    /**
     * Displays the activity log screen for the Administrator.
     *
     * @param scanner The Scanner instance used for user input.
     * @param user    The Administrator requesting to view the activity log.
     */
    public void viewActivityLogScreen(Scanner scanner, Administrator user) {
        ViewActivityLogScreen logScreen = new ViewActivityLogScreen();
        logScreen.display(scanner, user);
    }
}
