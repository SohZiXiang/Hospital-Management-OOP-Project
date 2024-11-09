package app;

import app.screens.*;
import interfaces.*;
import app.loaders.*;
import models.entities.*;
import utils.EmailUtil;

import java.util.*;

/**
 * Main class for the application, providing the primary entry point and the main menu display.
 */
public class Main {

    /**
     * Flag to determine if an email alert has been sent.
     */
    public static boolean emailAlert = false;

    /**
     * Scanner object for user input.
     */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Main method which acts as the entry point for the application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        displayMain(scanner);
    }

    /**
     * Displays the main menu and handles user choices.
     *
     * @param scanner Scanner object for capturing user input.
     */
    public static void displayMain(Scanner scanner) {
        while (true) {
            if (!emailAlert) {
                // Uncomment the line below to enable inventory notification.
                // EmailUtil.checkInventoryAndNotify("phclerk00@outlook.com");
                emailAlert = true;
            }
            String blue = "\u001B[34m";
            String reset = "\u001B[0m";

            // Display ASCII art for title
            System.out.println(blue + " _   _ __  __ ____  ");
            System.out.println("| | | |  \\/  / ___| ");
            System.out.println("| |_| | |\\/| \\___ \\ ");
            System.out.println("|  _  | |  | |___) |");
            System.out.println("|_| |_|_|  |_|____/ ");
            System.out.println(reset);

            // Display menu options
            System.out.println("Please select an option:");
            System.out.println("1. Log in");
            System.out.println("2. Change Password");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        LoginScreen loginScreen = new LoginScreen();
                        loginScreen.display(scanner);
                        break;
                    case 2:
                        ChangePwScreen changePasswordScreen = new ChangePwScreen();
                        changePasswordScreen.display(scanner);
                        break;
                    case 3:
                        System.out.println("Thank you for using the system!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Handles the login process by prompting the user for a hospital ID and validating it.
     */
    private static void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your hospital ID: ");
        String hospitalId = scanner.nextLine();

        if (validateUser(hospitalId)) {
            displayUserMenu(hospitalId);
        } else {
            System.out.println("Invalid hospital ID. Please try again.");
        }
    }

    /**
     * Validates the hospital ID of the user.
     *
     * @param hospitalId The hospital ID entered by the user.
     * @return true if the hospital ID is valid, false otherwise.
     */
    private static boolean validateUser(String hospitalId) {
        return true;
    }

    /**
     * Displays the user menu after successful login.
     *
     * @param hospitalId The hospital ID of the logged-in user.
     */
    private static void displayUserMenu(String hospitalId) {
        System.out.println("Displaying menu for user with hospital ID: " + hospitalId);
    }
}
