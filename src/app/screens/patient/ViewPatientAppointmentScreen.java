package app.screens.patient;

import interfaces.Screen;
import models.entities.User;

import java.util.Scanner;

public class ViewPatientAppointmentScreen implements Screen {

//    DataLoader appointmentLoader = new AppointmentLoader();
//    List<Appointment> appointmentList = new ArrayList<>();
//    String appointmentPath = FilePaths.APP_DATA.getPath();
//
//    DataLoader staffLoader = new StaffLoader();
//    List<Staff> staffList = new ArrayList<>();
//    String staffPath = FilePaths.STAFF_DATA.getPath();

    @Override
    public void display(Scanner scanner, User user) {

//        System.out.println("Displaying Appointments for patient: " + user.getName());
//
//        try {
//            appointmentList = appointmentLoader.loadData(appointmentPath);
//        }
//        catch (Exception e) {
//            System.err.println("Error loading data: " + e.getMessage());
//        }
//
//        try {
//            staffList = staffLoader.loadData(staffPath);
//        }
//        catch (Exception e) {
//            System.err.println("Error loading data: " + e.getMessage());
//        }
//
//        for (Appointment appointment : appointmentList) {
//            if (appointment.getPatientId().equals(user.getHospitalID())) {
//                System.out.println("Appointment ID: " + appointment.getAppointmentId());
//
//                for (Staff staff : staffList) {
//                    if (staff.getStaffId().equals(appointment.getDoctorId())) {
//                        System.out.println("Doctor: Dr " + staff.getName());
//                    }
//                }
//
//                System.out.println("Appointment Status: " + appointment.getStatus());
//                System.out.println("Appointment Date: " + appointment.getAppointmentDate());
//                System.out.println("Appointment Time: " + appointment.getAppointmentTime());
//            }
//        }

    }
}
