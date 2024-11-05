package app.screens.patient;

import interfaces.Screen;
import models.entities.*;
import utils.GenerateIdUtil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ViewPatientAppointmentScreen implements Screen {

    public static String addOneHour(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        LocalTime localTime = LocalTime.parse(time, formatter);
        LocalTime newTime = localTime.plus(1, ChronoUnit.HOURS);

        return newTime.format(formatter);
    }

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
                        System.out.println("Select A Option For Your Prefer Slot, example: 1");
                        String appointmentOption = scanner.nextLine();
                        int option = Integer.parseInt(appointmentOption);
                        patient.createAppointment(user, option, GenerateIdUtil.genAppointmentID());
                        break;
                    case 3:
                        System.out.println("Select an Appointment ID to cancel, example: A001");
                        String selectAppointmentID = scanner.nextLine();

                        System.out.println("Select A Option For Your Prefer Slot, example: 1");
                        String newAppointmentOption = scanner.nextLine();
                        int newOption = Integer.parseInt(newAppointmentOption);
                        patient.rescheduleAppointment(user, newOption, selectAppointmentID);
                        break;
                    case 4:
                        System.out.println("Select an Appointment ID to cancel, example: A001");
                        String appointmentID = scanner.nextLine();
                        patient.cancelAppointment(user, appointmentID);
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
