package app.screens.admin;

import app.loaders.*;
import app.screens.AdminMainScreen;
import interfaces.*;
import models.entities.*;
import models.enums.*;
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
import java.util.List;
import java.util.*;

/**
 * Represents the screen for managing the inventory of medicines in the hospital.
 * This class provides functionalities for viewing, adding, updating, and removing stock.
 * It implements the Screen interface.
 */

public class ManageInventoryScreen implements Screen {
    /**
     * Displays the inventory management options for the administrator.
     *
     * @param scanner The Scanner object for reading user input.
     * @param user    The User object representing the currently logged-in user.
     */
    @Override
    public void display(Scanner scanner, User user) {
        if (user instanceof Administrator) {
            Administrator admin = (Administrator) user;
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
                            admin.viewInventory(scanner, user);
                            break;
                        case 2:
                            admin.addStock(scanner,user);
                            break;
                        case 3:
                            admin.updateStock(scanner,user);
                            break;
                        case 4:
                            admin.removeStock(scanner, user);
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
    }
}
