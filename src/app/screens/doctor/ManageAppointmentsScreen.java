package app.screens.doctor;

import interfaces.*;
import models.entities.*;
import models.enums.*;
import utils.ActivityLogUtil;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;

/**
 * The ManageAppointmentsScreen class allows a doctor to manage their schedule by setting availability,
 * reviewing appointments, and viewing personal schedules and upcoming appointments.
 * It provides an interface for doctors to add, edit, and review appointment slots,
 * as well as update appointment statuses.
 */
public class ManageAppointmentsScreen implements Screen {
    /**
     * Displays the main appointment management menu for the doctor.
     * Options include viewing the personal schedule, viewing upcoming appointments,
     * setting availability, and reviewing appointment requests.
     *
     * @param scanner The scanner for reading user input.
     * @param user    The current logged-in user, cast to Doctor.
     */
    @Override
    public void display(Scanner scanner, User user) {
        Doctor doc = (Doctor) user;
        doc.getApptList();
        doc.getAvailList();
        doc.getPatientList();

        while (true) {
            System.out.println("\n--- Manage Appointments ---");
            System.out.println("1. View Personal Schedule");
            System.out.println("2. View Upcoming Appointments");
            System.out.println("3. Set Availability");
            System.out.println("4. Accept/Decline Appointment Requests");
            System.out.println("5. Return to Main Menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> doc.viewDoctorSchedule();
                    case 2 -> doc.viewUpcomingAppt();
                    case 3 -> {
                        System.out.println("\n--- Manage your Availability ---");
                        doc.viewAllAvail();
                        System.out.println("\nWould you like to edit or add a new record? (Please enter 'add' or " +
                                "'edit', or 'nil' to exit)");
                        input = scanner.nextLine();
                        if (input.equalsIgnoreCase("add")) addAvailability(scanner, doc);
                        else if (input.equalsIgnoreCase("edit")) editAvailability(scanner, doc);
                    }
                    case 4 -> reviewAppt(scanner, doc);
                    case 5 -> {
                        System.out.println("Returning to Main Menu...");
                        doc.resetApptData("all");
                        doc.resetAvailData();
                        return;
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input, please enter a valid number.");
            }
        }
    }

    /**
     * Adds new availability slots for the doctor.
     *
     * @param scanner The scanner for reading user input.
     * @param doc     The doctor whose availability is being set.
     */
    public void addAvailability(Scanner scanner, Doctor doc) {
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

            System.out.print("Enter start time (hh:mm am/pm, 12-hour format, for example, '11 am/AM' or " +
                            "'03:00 pm/PM'): ");
            String start = scanner.nextLine();

            System.out.print("Enter end time (hh:mm am/pm, 12-hour format, for example, '11 am/AM' or " +
                    "'03:00 pm/PM'): ");
            String end = scanner.nextLine();

            System.out.print("Is the doctor available or busy at this time? (A for Available, B for Busy): ");
            String enteredStatus = scanner.nextLine().toUpperCase();
            DoctorAvailability docAvail = enteredStatus.equals("A") ? DoctorAvailability.AVAILABLE :
                    DoctorAvailability.BUSY;

            doc.addAvail(combinedDate, start, end, docAvail);
            String logMsg = "User " + doc.getName() + " (ID: " + doc.getHospitalID() + ") added new consulation slots";
            ActivityLogUtil.logActivity(logMsg, doc);
        }
    }

    /**
     * Allows the doctor to edit an existing availability slot.
     *
     * @param scanner The scanner for reading user input.
     * @param doc     The doctor whose availability slot is being edited.
     */
    public void editAvailability(Scanner scanner, Doctor doc) {
        LocalDate selectedDate = changeFormat(scanner);
        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Availability> viewSlotsForOneDate = doc.slotsForDate(selectedDate);
        if (viewSlotsForOneDate.isEmpty()) {
            System.out.println("No availability slots found for " + selectedDate.format(displayFormat) + ".");
            return;
        }

        System.out.println("Availability slots for " + selectedDate.format(displayFormat) + ":");
        for (int i = 0; i < viewSlotsForOneDate.size(); i++) {
            Availability oneSlot = viewSlotsForOneDate.get(i);
            System.out.printf("%d. %s - %s : %s%n", i + 1, oneSlot.getStartTime(), oneSlot.getEndTime(), oneSlot.getStatus());
        }

        System.out.print("Enter the start time of the slot to edit (hh:mm AM/PM or h:mm am/pm)" +
                " (Example: 02:00 PM, 11 am, 11:00 AM) or press Enter to cancel: ");
        String desiredStartTime = scanner.nextLine();
        if (desiredStartTime.isEmpty()) {
            System.out.println("Edit cancelled.");
            return;
        }

        Availability userSelectedSlot = viewSlotsForOneDate.stream()
                .filter(slot -> timeMatches(desiredStartTime, slot.getStartTime()))
                .findFirst()
                .orElse(null);

        if (userSelectedSlot == null) {
            System.out.println("No slot found with the start time " + desiredStartTime + ".");
            return;
        }

        System.out.print("Enter new start time (hh:mm AM/PM) or press Enter to keep [" + userSelectedSlot.getStartTime() + "]: ");
        String changeStart = scanner.nextLine();
        changeStart = changeStart.isEmpty() ? null : changeStart;

        System.out.print("Enter new end time (hh:mm AM/PM) or press Enter to keep [" + userSelectedSlot.getEndTime() + "]: ");
        String changedEnd = scanner.nextLine();
        changedEnd = changedEnd.isEmpty() ? null : changedEnd;

        System.out.print("Enter new status (A for Available, B for Busy) or press Enter to keep [" + userSelectedSlot.getStatus() + "]: ");
        String changedStatus = scanner.nextLine().toUpperCase();
        DoctorAvailability updatedStatus = changedStatus.isEmpty() ? null : (changedStatus.equals("A") ? DoctorAvailability.AVAILABLE : DoctorAvailability.BUSY);

        doc.editOneSlot(selectedDate, desiredStartTime, changeStart, changedEnd, updatedStatus);

        String logMsg = "User " + doc.getName() + " (ID: " + doc.getHospitalID() + ") edited a consulation slot";
        ActivityLogUtil.logActivity(logMsg, doc);
    }

    /**
     * Reviews pending appointment requests, allowing the doctor to accept or decline them.
     *
     * @param scanner The scanner for reading user input.
     * @param doctor  The doctor reviewing the appointments.
     */
    public void reviewAppt(Scanner scanner, Doctor doctor) {
        List<Appointment> scheduledAppt = doctor.apptPendingReviews();

        if (scheduledAppt.isEmpty()) {
            System.out.println("No scheduled appointments to review.");
            return;
        }

        System.out.println("Scheduled Appointments for Review:");
        for (int i = 0; i < scheduledAppt.size(); i++) {
            Appointment oneAppt = scheduledAppt.get(i);
            System.out.printf("%d. Date: %s | Time: %s | Patient ID: %s%n",
                    i + 1,
                    oneAppt.getAppointmentDate().toInstant()
                            .atZone(TimeZone.getDefault().toZoneId()).toLocalDate(),
                    oneAppt.getAppointmentTime(),
                    oneAppt.getPatientId());
        }

        System.out.println("Enter the number of the appointment you want to review (or 0 to exit):");
        int noOfReviewNeeded = Integer.parseInt(scanner.nextLine()) - 1;

        if (noOfReviewNeeded >= 0 && noOfReviewNeeded < scheduledAppt.size()) {
            Appointment selectedAppointment = scheduledAppt.get(noOfReviewNeeded);

            System.out.println("Do you want to accept or decline this appointment? (A for Accept, D for Decline):");
            String reviewOutcome = scanner.nextLine().trim().toUpperCase();

            if (reviewOutcome.equals("A")) {
                doctor.updateAppointmentStatus(selectedAppointment, AppointmentStatus.CONFIRMED);
            } else if (reviewOutcome.equals("D")) {
                doctor.updateAppointmentStatus(selectedAppointment, AppointmentStatus.CANCELLED);
            } else {
                System.out.println("Invalid input. Please choose A for Accept or D for Decline.");
            }
        } else {
            System.out.println("Invalid selection. Exiting appointment review.");
        }
    }

    /**
     * Parses and validates the input date from the user.
     *
     * @param scanner The scanner for reading user input.
     * @return A LocalDate
     * representing the chosen date.
     */
    private LocalDate changeFormat(Scanner scanner) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate chosenDate = null;

        while (chosenDate == null) {
            System.out.print("Enter date (dd/MM/yyyy): ");
            String date = scanner.nextLine();
            try {
                chosenDate = LocalDate.parse(date, formatDate);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date as dd/MM/yyyy.");
            }
        }

        return chosenDate;
    }

    /**
     * Defines the time format for parsing availability times.
     *
     * @return A DateTimeFormatter instance for parsing times in "h:mm a" format.
     */
    private DateTimeFormatter formatTiming() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h[:mm][ ]a")
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();
    }

    /**
     * Compares two time strings to check if they match.
     *
     * @param appTime The time string of the appointment.
     * @param slot    The time string of the availability slot.
     * @return true if the times match, false otherwise.
     */
    private boolean timeMatches(String appTime, String slot) {
        DateTimeFormatter formatTheTime = formatTiming();
        try {
            LocalTime formattedApptTime = LocalTime.parse(appTime, formatTheTime);
            LocalTime formattedSlotTime = LocalTime.parse(slot, formatTheTime);
            return formattedApptTime.equals(formattedSlotTime);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing time for comparison: " + appTime + " or " + slot);
            return false;
        }
    }

}
