package app.screens.patient;

import app.loaders.ApptAvailLoader;
import app.loaders.StaffLoader;
import interfaces.DataLoader;
import interfaces.Screen;
import models.entities.*;
import models.enums.AppointmentStatus;
import models.enums.DoctorAvailability;
import models.enums.FilePaths;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This screen allows a patient to view the outcome of their appointments.
 * It loads the appointment outcome data for the patient and provides an option to return to the main menu.
 */
public class ViewAppointmentOutcomeScreen implements Screen {

    /**
     * Displays the appointment outcome screen for the patient.
     * It loads the appointment outcome data for the patient and presents an option to return to the main menu.
     *
     * @param scanner the Scanner instance used to capture user input.
     * @param user    the currently logged-in user, expected to be an instance of Patient.
     */
    @Override
    public void display(Scanner scanner, User user) {
        Patient patient = (Patient) user;
        patient.loadOutcomeData(user);
        int choice = 0;

        while(choice != 1){
            try {
                System.out.println();
                System.out.println("What would you like to do?");
                System.out.println("1: Return To Menu");
                String input = scanner.nextLine();
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        System.out.println();
    }
}
