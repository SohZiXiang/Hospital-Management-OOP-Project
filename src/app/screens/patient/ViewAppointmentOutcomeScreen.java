package app.screens.patient;

import app.loaders.ApptAvailLoader;
import app.loaders.StaffLoader;
import interfaces.DataLoader;
import interfaces.Screen;
import models.entities.Appointment;
import models.entities.Availability;
import models.entities.Staff;
import models.entities.User;
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

    private void loadData(User user){
        int slotCount = 0;
        System.out.println();
        System.out.println("--------- Displaying Past Appointments Outcome for patient: " + user.getName() + " ---------");
        System.out.println();

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        try {
            staffList = staffLoader.loadData(staffPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        System.out.printf("%-20s %-35s %-30s %-30s %-35s%n",
                "Appointment ID", "Doctor", "Date", "Time", "Outcome");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (Appointment appointment : appointmentList) {
            if (appointment.getPatientId().equals(user.getHospitalID()) && appointment.getStatus() == AppointmentStatus.COMPLETED) {
                String doctorName = "N/A";

                for (Staff staff : staffList) {
                    if (staff.getStaffId().equals(appointment.getDoctorId())) {
                        doctorName = "Dr " + staff.getName();
                        break;
                    }
                }

                System.out.printf("%-20s %-35s %-30s %-30s %-35s%n",
                        appointment.getAppointmentId(), doctorName,
                        formatter.format(appointment.getAppointmentDate()),
                        appointment.getAppointmentTime(), appointment.getOutcomeRecord());
            }
        }
    }

    @Override
    public void display(Scanner scanner, User user) {
        loadData(user);
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
