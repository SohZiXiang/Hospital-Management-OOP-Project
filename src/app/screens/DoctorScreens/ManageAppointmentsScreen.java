package app.screens.DoctorScreens;

import interfaces.*;
import models.entities.*;
import models.enums.*;

import java.time.*;
import java.util.*;

public class ManageAppointmentsScreen implements Screen {

    @Override
    public void display(Scanner scanner, User user) {
        Doctor doc = (Doctor) user;
        doc.getApptList();
        doc.getAvailList();

        while (true) {
            System.out.println("\n--- Manage Appointments ---");
            System.out.println("1. View Schedule and Pending Appointments");
            System.out.println("2. Set Availability");
            System.out.println("3. Accept/Decline Appointment Requests");
            System.out.println("4. Return to Main Menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> doc.viewDoctorSchedule();
                    case 2 -> manageAvailability(scanner, doc);
                    // case 3 -> acceptDeclineAppointments(scanner, user);
                    case 4 -> {
                        System.out.println("Returning to Main Menu...");
                        doc.resetData();
                        return;
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input, please enter a valid number.");
            }
        }
    }

    public void manageAvailability(Scanner scanner, Doctor doc) {
        System.out.println("\n--- Manage your Availability ---");

        System.out.print("Enter year for availability (e.g., 2024): ");
        int chosenYr = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter month for availability (1-12): ");
        int chosenMth = Integer.parseInt(scanner.nextLine());

        while (true) {
            System.out.print("Enter day (1-31, or 0 to stop): ");
            int chosenDay = Integer.parseInt(scanner.nextLine());

            if (chosenDay == 0) {
                System.out.println("Exiting availability setting.");
                break;
            }

            LocalDate combinedDate = LocalDate.of(chosenYr, chosenMth, chosenDay);

            System.out.print("Enter start time (hh:mm, 12-hour format): ");
            String start = scanner.nextLine();

            System.out.print("Enter end time (hh:mm, 12-hour format): ");
            String end = scanner.nextLine();

            System.out.print("Is the doctor available or busy at this time? (A for Available, B for Busy): ");
            String enteredStatus = scanner.nextLine().toUpperCase();
            DoctorAvailability docAvail = enteredStatus.equals("A") ? DoctorAvailability.AVAILABLE :
                    DoctorAvailability.BUSY;

            doc.addAvail(combinedDate, start, end, docAvail);
        }
    }

}
