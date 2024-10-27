package app.screens.admin;

import app.loaders.*;
import interfaces.*;
import models.entities.*;
import models.enums.*;
import utils.ActivityLogUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class ViewAppointmentsScreen implements Screen {
    private AppointmentLoader apptLoader = new AppointmentLoader();
    private String filePath = FilePaths.APPT_DATA.getPath();

    @Override
    public void display(Scanner scanner, User user) {
        String logMsg = "User " + user.getName() + " (ID: " + user.getHospitalID() + ") viewed all appointment details.";
        ActivityLogUtil.logActivity(logMsg, user);
        System.out.println("=== View Scheduled Appointments ===");
        List<Appointment> appointments = apptLoader.loadData(filePath);

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
        } else {
            displayAppointmentDetailsHeader();
            for (Appointment appointment : appointments) {
                displayAppointmentDetails(appointment);
            }
        }
        System.out.println("\nPress 1 to refresh the appointments or 0 to go back:");
        String choice = scanner.nextLine().trim().toUpperCase();

        if (choice.equals("1")) {
            display(scanner, user);
        } else if (choice.equals("0")) {
            System.out.println("Returning to the previous menu...");
        } else {
            System.out.println("Invalid choice, returning to the previous menu...");
        }
    }

    public void displayAppointmentDetailsHeader() {
        System.out.printf("\n%-15s %-15s %-15s %-15s %-15s %-10s%n",
                "Appointment ID", "Patient ID", "Doctor ID", "Status", "Date", "Time");
        System.out.println("-------------------------------------------------------------------------------------------");
    }

    public void displayAppointmentDetails(Appointment appointment) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        System.out.printf("%-15s %-15s %-15s %-15s %-15s %-10s%n",
                appointment.getAppointmentId(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                appointment.getStatus(),
                dateFormat.format(appointment.getAppointmentDate()),
                appointment.getAppointmentTime());
    }
}
