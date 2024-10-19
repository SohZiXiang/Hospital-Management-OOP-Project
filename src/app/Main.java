package app;
import app.screens.*;
import interfaces.*;
import app.loaders.*;
import models.entities.*;

import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        displayMain(scanner);
    }

    public static void displayMain(Scanner scanner){
        while (true) {
            String blue = "\u001B[34m";
            String reset = "\u001B[0m";

            System.out.println(blue + " _   _ __  __ ____  ");
            System.out.println("| | | |  \\/  / ___| ");
            System.out.println("| |_| | |\\/| \\___ \\ ");
            System.out.println("|  _  | |  | |___) |");
            System.out.println("|_| |_|_|  |_|____/ ");
            System.out.println(reset);

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
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            }catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1 or 2).");
            }
        }
    }

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

    private static boolean validateUser(String hospitalId) {
        return true;
    }

    private static void displayUserMenu(String hospitalId) {
        System.out.println("Displaying menu for user with hospital ID: " + hospitalId);
    }
}
