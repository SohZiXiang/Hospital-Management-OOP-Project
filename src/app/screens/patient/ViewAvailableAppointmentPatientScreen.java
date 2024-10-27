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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ViewAvailableAppointmentPatientScreen implements Screen {

    AppointmentLoader appointmentLoader = new AppointmentLoader();
    String availabilityFilePath = FilePaths.DOCAVAIL_DATA.getPath();
    Map<String, List<Availability>> availabilityMap = appointmentLoader.loadAvailability(availabilityFilePath);

    DataLoader staffLoader = new StaffLoader();
    List<Staff> staffList = new ArrayList<>();
    String staffPath = FilePaths.STAFF_DATA.getPath();

    @Override
    public void display(Scanner scanner, User user) {

        try {
            staffList = staffLoader.loadData(staffPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        System.out.println("Displaying Available Appointment Slots\n");

//        for (List<Availability> availabilityList : availabilityMap.values()) {
//
//            for (Availability availability : availabilityList) {
//
//                if(availability.getStatus() == DoctorAvailability.AVAILABLE){
//
//                    for (Staff staff : staffList) {
//                        if (staff.getStaffId().equals(availability.getDoctorId())) {
//                            System.out.println("Doctor: " + staff.getName());
//                        }
//                    }
//
//                    System.out.println("Date: " + availability.getAvailableDate());
//                    System.out.println("Time: " + availability.getStartTime() + " - " + availability.getEndTime());
//                    System.out.println("Status: " + availability.getStatus());
//
//                }
//            }
//
//        }

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

                    SimpleDateFormat formatter = new SimpleDateFormat("EEE dd/MM/yyyy");

                    System.out.printf("%-35s %-35s %-35s %-35s%n",
                            ("Dr " + doctorName),
                            formatter.format(availability.getAvailableDate()),
                            availability.getStartTime() + " - " + availability.getEndTime(),
                            availability.getStatus());
                }
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
