package app.screens.admin;

import app.loaders.*;
import app.screens.*;
import interfaces.*;
import models.entities.*;
import models.enums.*;
import utils.ActivityLogUtil;
import utils.StringFormatUtil;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static models.enums.Gender.*;

/**
 * Manages the display and interactions for the hospital staff management screen.
 * This screen allows administrators to view, add, update, and remove staff members.
 */
public class ManageStaffScreen implements Screen {
    private List<Staff> staffList;
    private User currentUser;

    /**
     * Displays the manage staff options to the administrator and processes user input.
     *
     * @param scanner The Scanner object for reading user input.
     * @param user    The User object representing the currently logged-in user.
     */
    @Override
    public void display(Scanner scanner, User user) {
        if (user instanceof Administrator) {
            Administrator admin = (Administrator) user;

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
                            admin.viewStaffList(scanner, user);
                            break;
                        case 2:
                            admin.addStaff(scanner, user);
                            break;
                        case 3:
                            admin.updateStaff(scanner, user);
                            break;
                        case 4:
                            admin.removeStaff(scanner, user);
                            break;
                        case 5:
                            AdminMainScreen adminMainScreen = new AdminMainScreen();
                            adminMainScreen.display(scanner, user);
                            return;
                        default:
                            System.out.println("Invalid choice, please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
        } else {
            System.out.println("User does not have administrator privileges.");
        }
    }
}
