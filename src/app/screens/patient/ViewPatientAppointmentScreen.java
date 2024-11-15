package app.screens.patient;

import interfaces.Screen;
import models.entities.*;
import utils.GenerateIdUtil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * The ViewPatientAppointmentScreen class implements the Screen interface to manage
 * patient appointments. It provides options to view, schedule, reschedule, and cancel appointments.
 */
public class ViewPatientAppointmentScreen implements Screen {

    /**
     * Displays the appointment management menu for the patient and handles user actions.
     *
     * @param scanner The scanner object to capture user input.
     * @param user    The user (patient) accessing the screen.
     */
    @Override
    public void display(Scanner scanner, User user) {

        Patient patient = (Patient) user;
        patient.loadAppointmentData(user);

        Boolean exit = false;

        while(!exit){
            System.out.println();
            System.out.println("What would you like to do?");
            System.out.println("1: Return To Menu");
            System.out.println("2: Schedule An Appointment");
            System.out.println("3: Reschedule An Appointment");
            System.out.println("4: Cancel Appointment");
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        exit = true;
                        break;
                    case 2:
                        System.out.println("Select A Option For Your Prefer Slot, example: 1 (enter 0 to exit)");
                        String appointmentOption = scanner.nextLine();
                        int option = Integer.parseInt(appointmentOption);
                        if(option != 0){
                            patient.createAppointment(user, option, GenerateIdUtil.genAppointmentID(), true);
                        }
                        break;
                    case 3:
                        System.out.println("Select an Appointment ID to reschedule, example: AP01(enter \"exit\" to leave)");
                        String selectAppointmentID = scanner.nextLine();

                        if(selectAppointmentID.equals("exit")){
                            break;
                        }

                        System.out.println("Select A Option For Your Prefer Slot, example: 1 (enter 0 to exit)");
                        String newAppointmentOption = scanner.nextLine();

                        if(newAppointmentOption.equals("0")){
                            break;
                        }

                        int newOption = Integer.parseInt(newAppointmentOption);
                        patient.rescheduleAppointment(user, newOption, selectAppointmentID);
                        break;
                    case 4:
                        System.out.println("Select an Appointment ID to cancel, example: AP01 (enter \"exit\" to leave)");
                        String appointmentID = scanner.nextLine();
                        if(!appointmentID.equals("exit")){
                            patient.cancelAppointment(user, appointmentID, true);
                        }
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

    }
}
