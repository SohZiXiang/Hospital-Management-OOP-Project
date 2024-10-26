package app.screens.DoctorScreens;

import interfaces.*;
import models.entities.*;
import models.enums.*;

import java.time.*;
import java.util.*;

public class ManageAppointmentsScreen implements Screen {
    // Change variableName
    private List<Appointment> appointmentList;
    private List<Availability> availabilityList;
    //Change variableNames
    @Override
    public void display(Scanner scanner, User user) {
        Doctor doctor = (Doctor) user;
        appointmentList = doctor.getAppointments();
        availabilityList = doctor.getAvailabilityList();

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
                    // Change variableName
                    case 1 -> {
                        LocalDate currentDate = LocalDate.now();
                        int year = currentDate.getYear();
                        int month = currentDate.getMonthValue();
                        doctor.viewSchedule(year, month);
                    }
                    case 2 -> checkAvailability(scanner, doctor);
                    // case 3 -> acceptDeclineAppointments(scanner, user);
                    case 4 -> {
                        System.out.println("Returning to Main Menu...");
                        return;
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input, please enter a valid number.");
            }
        }
    }
    // Change variableName
    public void checkAvailability(Scanner scanner, Doctor doctor) {
        System.out.println("\n--- Set Availability ---");

        System.out.print("Enter year for availability (e.g., 2024): ");
        int year = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter month for availability (1-12): ");
        int month = Integer.parseInt(scanner.nextLine());

        while (true) {
            System.out.print("Enter day (1-31, or 0 to stop): ");
            int day = Integer.parseInt(scanner.nextLine());

            if (day == 0) {
                System.out.println("Exiting availability setting.");
                break;
            }

            LocalDate date = LocalDate.of(year, month, day);
            if (date.isBefore(LocalDate.now())) {
                System.out.println("Invalid date. Please enter a current or future date.");
                continue;
            }

            System.out.print("Enter start time (HH:MM, 24-hour format): ");
            String startTime = scanner.nextLine();

            System.out.print("Enter end time (HH:MM, 24-hour format): ");
            String endTime = scanner.nextLine();

            System.out.print("Is the doctor available or busy at this time? (A for Available, B for Busy): ");
            String statusInput = scanner.nextLine().toUpperCase();
            DoctorAvailability status = statusInput.equals("A") ? DoctorAvailability.AVAILABLE : DoctorAvailability.BUSY;

            // Call validateAndSetAvailability in Doctor class with the collected input
            doctor.validateAndSetAvailability(date, startTime, endTime, status);
        }
    }

}
