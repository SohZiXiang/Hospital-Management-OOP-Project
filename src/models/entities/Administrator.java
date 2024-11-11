package models.entities;

import app.loaders.InventoryLoader;
import app.loaders.StaffLoader;
import app.screens.admin.ViewActivityLogScreen;
import app.screens.admin.ViewAppointmentsScreen;
import interfaces.DataLoader;
import models.enums.FilePaths;
import models.enums.Gender;
import models.enums.Role;
import models.managers.InventoryManager;
import models.managers.ReplenishReqManager;
import models.managers.StaffManager;
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
    private StaffManager staffManager = new StaffManager();
    private InventoryManager invManager = new InventoryManager();
    private ReplenishReqManager replenishReqManager = new ReplenishReqManager();


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
        staffManager.viewStaffList(scanner, currentUser);
    }
    /**
     * Adds a new staff member based on user input.
     *
     * @param scanner A Scanner instance for user input.
     * @param user    The currently logged-in user.
     */
    public void addStaff(Scanner scanner, User user) {
        staffManager.addStaff(scanner, user);
    }

    /**
     * Updates an existing staff member's information based on user input.
     *
     * @param scanner    A Scanner instance for user input.
     * @param currentUser The currently logged-in user.
     */
    public void updateStaff(Scanner scanner, User currentUser) {
        staffManager.updateStaff(scanner, currentUser);
    }


    /**
     * Removes a staff member based on their ID.
     *
     * @param scanner A Scanner instance for user input.
     * @param user    The currently logged-in user.
     */
    public void removeStaff(Scanner scanner, User user) {
        staffManager.removeStaff(scanner, user);
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
        invManager.viewInventory(scanner, currentUser);
    }

    /**
     * Adds a new stock of medicine to the inventory.
     *
     * @param scanner The Scanner object for reading user input.
     * @param user    The User object representing the currently logged-in user.
     */
    public void addStock(Scanner scanner, User user) {
       invManager.addStock(scanner, user);
    }

    /**
     * Updates the stock level of an existing medicine from Administrator's Inventory Management Screen.
     *
     * @param scanner The Scanner object for reading user input.
     * @param user    The User object representing the currently logged-in user.
     */
    public void updateStock(Scanner scanner, User user) {
        invManager.updateStock(scanner, user);
    }

    /**
     * Updates the stock level of a medicine.
     *
     * @param medicine The Medicine object to update.
     * @param stock    The new stock level to set.
     * @param user     The User object representing the currently logged-in user.
     */
    public void updateStock(Medicine medicine, int stock, User user) {
        invManager.updateStock(medicine, stock, user);
    }

    /**
     * Removes a medicine stock from the inventory.
     *
     * @param scanner The Scanner object for reading user input.
     * @param user    The User object representing the currently logged-in user.
     */
    public void removeStock(Scanner scanner, User user){
        invManager.removeStock(scanner, user);
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
        replenishReqManager.approveRequest(index, currentUser, request);
    }

    /**
     * Rejects a replenishment request and updates the request status to "rejected".
     *
     * @param index   The index of the request in the list of requests.
     * @param request The ReplenishmentRequest to be rejected.
     */
    public void rejectRequest(int index, ReplenishmentRequest request) {
        replenishReqManager.rejectRequest(index, request);
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
