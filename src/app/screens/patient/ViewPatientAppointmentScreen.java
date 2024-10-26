package app.screens.patient;

import app.loaders.AppointmentLoader;
import app.loaders.StaffLoader;
import interfaces.DataLoader;
import interfaces.Screen;
import models.entities.Appointment;
import models.entities.Staff;
import models.entities.User;
import models.enums.FilePaths;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ViewPatientAppointmentScreen implements Screen {

    DataLoader appointmentLoader = new AppointmentLoader();
    List<Appointment> appointmentList = new ArrayList<>();
    String appointmentPath = FilePaths.APPT_DATA.getPath();

    DataLoader staffLoader = new StaffLoader();
    List<Staff> staffList = new ArrayList<>();
    String staffPath = FilePaths.STAFF_DATA.getPath();

    @Override
    public void display(Scanner scanner, User user) {

        System.out.println("Displaying Appointments for patient: " + user.getName());

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

        for (Appointment appointment : appointmentList) {
            if (appointment.getPatientId().equals(user.getHospitalID())) {
                System.out.println("Appointment ID: " + appointment.getAppointmentId());

                for (Staff staff : staffList) {
                    if (staff.getStaffId().equals(appointment.getDoctorId())) {
                        System.out.println("Doctor: Dr " + staff.getName());
                    }
                }

                System.out.println("Appointment Status: " + appointment.getStatus());
                System.out.println("Appointment Date: " + appointment.getAppointmentDate());
                System.out.println("Appointment Time: " + appointment.getAppointmentTime());
            }
        }

        try {
            String input = "0";
            int choice = 0;

            while (choice != 1){
                System.out.println("Please Select the following options");
                System.out.println("1: Return To Menu");
                input = scanner.nextLine();
                choice = Integer.parseInt(input);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

    }
}
