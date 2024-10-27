package app.screens.patient;

import app.loaders.AppointmentLoader;
import app.loaders.StaffLoader;
import interfaces.DataLoader;
import interfaces.Screen;
import models.entities.Appointment;
import models.entities.Availability;
import models.entities.Staff;
import models.entities.User;
import models.enums.DoctorAvailability;
import models.enums.FilePaths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;

public class ViewPatientAppointmentScreen implements Screen {

    AppointmentLoader appointmentLoader = new AppointmentLoader();
    List<Appointment> appointmentList = new ArrayList<>();
    String appointmentPath = FilePaths.APPT_DATA.getPath();

    String availabilityFilePath = FilePaths.DOCAVAIL_DATA.getPath();
    Map<String, List<Availability>> availabilityMap = appointmentLoader.loadAvailability(availabilityFilePath);

    DataLoader staffLoader = new StaffLoader();
    List<Staff> staffList = new ArrayList<>();
    String staffPath = FilePaths.STAFF_DATA.getPath();

    @Override
    public void display(Scanner scanner, User user) {

        System.out.println();
        System.out.println("--------- Displaying Appointments for patient: " + user.getName() + " ---------");
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

        System.out.printf("%-35s %-35s %-35s %-35s %-35s%n",
                "Appointment ID", "Doctor", "Status", "Date", "Time");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (Appointment appointment : appointmentList) {
            if (appointment.getPatientId().equals(user.getHospitalID())) {
                String doctorName = "N/A";

                for (Staff staff : staffList) {
                    if (staff.getStaffId().equals(appointment.getDoctorId())) {
                        doctorName = "Dr " + staff.getName();
                        break;
                    }
                }

                System.out.printf("%-35s %-35s %-35s %-35s %-35s%n",
                        appointment.getAppointmentId(), doctorName,
                        appointment.getStatus(), appointment.getAppointmentDate(),
                        appointment.getAppointmentTime());
            }
        }

        System.out.println();
        System.out.println("-------- Displaying Available Appointment Slots ---------\n");

        System.out.printf("%-35s %-35s %-35s %-35s%n",
                "Doctor", "Date", "Time", "Status");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (List<Availability> availabilityList : availabilityMap.values()) {
            for (Availability availability : availabilityList) {
                if (availability.getStatus() == DoctorAvailability.AVAILABLE) {
                    String doctorName = "N/A";

                    for (Staff staff : staffList) {
                        if (staff.getStaffId().equals(availability.getDoctorId())) {
                            doctorName = staff.getName();
                            break;
                        }
                    }

                    System.out.printf("%-35s %-35s %-35s %-35s%n",
                            ("Dr " + doctorName),
                            availability.getAvailableDate(),
                            availability.getStartTime() + " - " + availability.getEndTime(),
                            availability.getStatus());
                }
            }
        }


        Boolean exit = false;

        while(!exit){
            System.out.println();
            System.out.println("Please Select the following options");
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

                        break;
                    case 3:

                        break;
                    case 4:

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
