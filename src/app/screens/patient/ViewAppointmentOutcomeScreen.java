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
 * The ViewAppointmentOutcomeScreen class implements the Screen interface to allow patients
 * to view the outcomes of their past appointments.
 */
public class ViewAppointmentOutcomeScreen implements Screen {

    /**
     * Displays the patient's past appointment outcomes and provides an option to return to the main menu.
     *
     * @param scanner The scanner object to capture user input.
     * @param user    The user (patient) accessing the screen.
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
