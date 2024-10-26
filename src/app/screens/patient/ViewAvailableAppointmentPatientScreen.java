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

        for (List<Availability> availabilityList : availabilityMap.values()) {

            for (Availability availability : availabilityList) {

                if(availability.getStatus() == DoctorAvailability.AVAILABLE){

                    System.out.println("----Appointment Slot----");

                    for (Staff staff : staffList) {
                        if (staff.getStaffId().equals(availability.getDoctorId())) {
                            System.out.println("Doctor: Dr " + staff.getName());
                        }
                    }

                    System.out.println("Date: " + availability.getAvailableDate());
                    System.out.println("Time: " + availability.getStartTime() + " - " + availability.getEndTime());
                    System.out.println("Status: " + availability.getStatus());

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
