package app.screens.patient;

//import app.loaders.AppointmentLoader;
import app.loaders.ApptAvailLoader;
import app.loaders.StaffLoader;
import interfaces.DataLoader;
import interfaces.Screen;
import models.entities.*;
import models.enums.AppointmentStatus;
import models.enums.DoctorAvailability;
import models.enums.FilePaths;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.ActivityLogUtil;
import utils.StringFormatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ViewPatientAppointmentScreen implements Screen {

    ApptAvailLoader appointmentLoader = new ApptAvailLoader();
    List<Appointment> appointmentList = new ArrayList<>();
    String appointmentPath = FilePaths.APPT_DATA.getPath();

    String availabilityFilePath = FilePaths.DOCAVAIL_DATA.getPath();
    Map<String, List<Availability>> availabilityMap = appointmentLoader.loadAvailData(availabilityFilePath);

    DataLoader staffLoader = new StaffLoader();
    List<Staff> staffList = new ArrayList<>();
    String staffPath = FilePaths.STAFF_DATA.getPath();

    SimpleDateFormat formatter = new SimpleDateFormat("EEE dd/MM/yyyy");
    int slotCount = 0;

    private String genAppID(){
        List<Integer> existingID = new ArrayList<Integer>();
        Integer uniqueID = 1;

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        try {
            for (Appointment appointment : appointmentList) {
                String id = appointment.getAppointmentId();
                String numericValue = id.replaceAll("[^0-9]", "");

                if (!numericValue.isEmpty()) {
                    int numericValueToInt = Integer.parseInt(numericValue);
                    existingID.add(numericValueToInt);
                }
            }

            while(true){
                if(!existingID.contains(uniqueID)){
                    int length = String.valueOf(uniqueID).length();
                    if(length == 1){
                        return "A00" + uniqueID;
                    }else if(length == 2){
                        return "A0" + uniqueID;
                    }else{
                        return "A" + uniqueID;
                    }
                }
                uniqueID++;
            }

        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return "";
    }

    public static String addOneHour(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        LocalTime localTime = LocalTime.parse(time, formatter);
        LocalTime newTime = localTime.plus(1, ChronoUnit.HOURS);

        return newTime.format(formatter);
    }

    private void loadData(User user){
        int slotCount = 0;
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
                        appointment.getStatus(), formatter.format(appointment.getAppointmentDate()),
                        appointment.getAppointmentTime());
            }
        }

        System.out.println();
        System.out.println("-------- Displaying Available Appointment Slots ---------\n");

        System.out.printf("%-15s %-35s %-35s %-35s %-35s%n",
                "Option", "Doctor", "Date", "Time", "Status");
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

                    System.out.printf("%-15s %-35s %-35s %-35s %-35s%n",
                            ++slotCount,
                            ("Dr " + doctorName),
                            formatter.format(availability.getAvailableDate()),
                            availability.getStartTime() + " - " + availability.getEndTime(),
                            availability.getStatus());
                }
            }
        }
    }

    private void writeAppointmentToExcel(User currentUser, Appointment appointment) {
        String filePath = FilePaths.APPT_DATA.getPath();
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Sheet1");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Appointment ID");
                headerRow.createCell(1).setCellValue("Patient ID");
                headerRow.createCell(2).setCellValue("Doctor ID");
                headerRow.createCell(3).setCellValue("Status");
                headerRow.createCell(4).setCellValue("Appointment Date");
                headerRow.createCell(5).setCellValue("Appointment Time");
                headerRow.createCell(6).setCellValue("Outcome Record");
            } else {
                fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
            }

            Sheet sheet = workbook.getSheet("Sheet1");
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(appointment.getAppointmentId());
            newRow.createCell(1).setCellValue(appointment.getPatientId());
            newRow.createCell(2).setCellValue(appointment.getDoctorId());
            newRow.createCell(3).setCellValue("SCHEDULED");
            newRow.createCell(4).setCellValue(appointment.getAppointmentDate());
            newRow.createCell(5).setCellValue(appointment.getAppointmentTime());
            newRow.createCell(6).setCellValue("");

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            String logMsg = "User " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                    "create appointment " + appointment.getAppointmentId() + ". ";
            ActivityLogUtil.logActivity(logMsg, currentUser);

            loadData(currentUser);
            System.out.println();
            System.out.println("Appointment added successfully.");

        } catch (IOException e) {
            System.err.println("Error storing new appointment data: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private void removeAppointmentInExcel(User currentUser, String appointmentID) {
        String appt_path = FilePaths.APPT_DATA.getPath();
        try (FileInputStream fis = new FileInputStream(appt_path);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowToRemove = -1;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0).getStringCellValue().equals(appointmentID)) {
                    rowToRemove = i;
                    break;
                }
            }

            if (rowToRemove == -1) {
                System.out.println("Appointment ID not found!");
                return;
            }
            sheet.removeRow(sheet.getRow(rowToRemove));
            int lastRowIndex = sheet.getLastRowNum();
            if (rowToRemove < lastRowIndex) {
                sheet.shiftRows(rowToRemove + 1, lastRowIndex, -1);
            }

            try (FileOutputStream fos = new FileOutputStream(appt_path)) {
                workbook.write(fos);
            }
            String logMsg = "User " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                    "cancelled appointment: " + appointmentID + "." ;
            ActivityLogUtil.logActivity(logMsg, currentUser);

            loadData(currentUser);
            System.out.println();
            System.out.println("Appointment cancelled successfully.");

        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error cancelling appointment: " + e.getMessage());
        }
    }

    private void createAppointment(User user, int option) {
        int slotCount = 0;

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        for (List<Availability> availabilityList : availabilityMap.values()) {
            for (Availability availability : availabilityList) {
                if (availability.getStatus() == DoctorAvailability.AVAILABLE) {

                    ++slotCount;
                    if(slotCount == option){
                        Appointment newAppointment = new Appointment(genAppID(), user.getHospitalID(),
                                availability.getDoctorId(), availability.getAvailableDate(), availability.getStartTime());
                        writeAppointmentToExcel(user, newAppointment);
                    }

                }
            }
        }
    }

    private void cancelAppointment(User user, String appointmentID) {

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);
            staffList = staffLoader.loadData(staffPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        try {
            for (Appointment appointment : appointmentList) {
                if (appointment.getAppointmentId().equals(appointmentID) && appointment.getPatientId().equals(user.getHospitalID())) {
                    for (Staff staff : staffList) {
                        if (staff.getStaffId().equals(appointment.getDoctorId())) {
                            Doctor doctor = new Doctor(staff.getStaffId(), staff.getName(), staff.getGender(), staff.getAge());
                            doctor.addAvail(LocalDate.ofInstant(appointment.getAppointmentDate().toInstant(), ZoneId.systemDefault()),
                                    appointment.getAppointmentTime(), addOneHour(appointment.getAppointmentTime()), DoctorAvailability.AVAILABLE);
                            break;
                        }
                    }
                }
            }
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }

        removeAppointmentInExcel(user, appointmentID);

    }

    @Override
    public void display(Scanner scanner, User user) {

        loadData(user);

        Boolean exit = false;

        while(!exit){
            System.out.println();
            System.out.println("What would you like to do?");
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
                        System.out.println("Select A Option For Your Prefer Slot, example 1");
                        String appointmentOption = scanner.nextLine();
                        int option = Integer.parseInt(appointmentOption);
                        createAppointment(user, option);
                        break;
                    case 3:

                        break;
                    case 4:
                        System.out.println("Select an Appointment ID to cancel, example: A001");
                        String appointmentID = scanner.nextLine();
                        cancelAppointment(user, appointmentID);
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
