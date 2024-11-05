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

public class ViewAppointmentOutcomeScreen implements Screen {

    ApptAvailLoader appointmentLoader = new ApptAvailLoader();
    List<Appointment> appointmentList = new ArrayList<>();
    String appointmentPath = FilePaths.APPT_DATA.getPath();

    DataLoader staffLoader = new StaffLoader();
    List<Staff> staffList = new ArrayList<>();
    String staffPath = FilePaths.STAFF_DATA.getPath();

    SimpleDateFormat formatter = new SimpleDateFormat("EEE dd/MM/yyyy");

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
