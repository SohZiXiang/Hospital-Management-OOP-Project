package models.entities;

import app.loaders.ApptAvailLoader;
import app.loaders.StaffLoader;
import interfaces.DataLoader;
import models.enums.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.ActivityLogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Patient extends User {
    private String patientID;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String  email;
    private BloodType bloodType;
    private List<String> pastDiagnoses;
    private List<String> pastTreatments;


    public Patient(String hospitalID) {
        super(hospitalID);
    }

    public Patient(String hospitalID, String name, String password,
                   String patientID, LocalDate dateOfBirth, Gender gender,
                   String phoneNumber, String email, BloodType bloodType,
                   List<String> pastDiagnoses, List<String> pastTreatments) {
        super(hospitalID, name, password, gender);
        this.patientID = patientID;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.bloodType = bloodType;
        this.pastDiagnoses = (pastDiagnoses != null) ? new ArrayList<>(pastDiagnoses) : new ArrayList<>();
        this.pastTreatments = (pastTreatments != null) ? new ArrayList<>(pastTreatments) : new ArrayList<>();
    }
    //Changed this part
    public Patient(String hospitalID,
                   String patientID, String name, LocalDate dateOfBirth, Gender gender,
                   String email, BloodType bloodType, List<String> pastDiagnoses, List<String> pastTreatments, String phoneNumber) {
        super(hospitalID = patientID, name,"P@ssw0rd123", gender);
        this.patientID = patientID;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.bloodType = bloodType;
        this.pastDiagnoses = (pastDiagnoses != null) ? new ArrayList<>(pastDiagnoses) : new ArrayList<>();
        this.pastTreatments = (pastTreatments != null) ? new ArrayList<>(pastTreatments) : new ArrayList<>();
        this.phoneNumber = phoneNumber;
    }


    public String getPatientID() {
        return patientID;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public List<String> getPastDiagnoses() {
        return pastDiagnoses;
    }

    public List<String> getPastTreatments() {
        return pastTreatments;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    // Changed this part
    public void addDiagnosis(String diagnosis) {
        if (this.pastDiagnoses == null) {
            this.pastDiagnoses = new ArrayList<>();
        }
        this.pastDiagnoses.add(diagnosis);
    }


    // Method to add a new treatment
    public void addTreatment(String treatment) {
        if (this.pastTreatments == null) {
            this.pastTreatments = new ArrayList<>();
        }
        this.pastTreatments.add(treatment);
    }

    @Override
    public Role getRole() {
        return Role.PATIENT;
    }

    public void createAppointment(User user, int option, String appointmentID) {

        ApptAvailLoader appointmentLoader = new ApptAvailLoader();
        List<Appointment> appointmentList = new ArrayList<>();
        String appointmentPath = FilePaths.APPT_DATA.getPath();

        String availabilityFilePath = FilePaths.DOCAVAIL_DATA.getPath();
        Map<String, List<Availability>> availabilityMap = appointmentLoader.loadAvailData(availabilityFilePath);

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
                        Appointment newAppointment = new Appointment(appointmentID, getPatientID(),
                                availability.getDoctorId(), availability.getAvailableDate(), availability.getStartTime(), "");
                        writeAppointmentToExcel(user, newAppointment);
                        //SMSUtil.sendSms("82849085", "HMS SYSTEM", "Your appointment is scheduled.");
                    }
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
            newRow.createCell(3).setCellValue(AppointmentStatus.SCHEDULED.toString());
            newRow.createCell(4).setCellValue(appointment.getAppointmentDate());
            newRow.createCell(5).setCellValue(appointment.getAppointmentTime());
            newRow.createCell(6).setCellValue("");

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            String logMsg = "Patient " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
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

    public void loadData(User user){

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
            if (appointment.getPatientId().equals(user.getHospitalID()) && appointment.getStatus() != AppointmentStatus.COMPLETED) {
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

    public void cancelAppointment(User user, String appointmentID) {

        ApptAvailLoader appointmentLoader = new ApptAvailLoader();
        List<Appointment> appointmentList = new ArrayList<>();
        String appointmentPath = FilePaths.APPT_DATA.getPath();

        Boolean removeAppointment = false;

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);

            for (Appointment appointment : appointmentList) {
                if (appointment.getPatientId().equals(user.getHospitalID())
                        && appointment.getStatus() != AppointmentStatus.COMPLETED
                        && appointment.getAppointmentId().equals(appointmentID))
                {
                    removeAppointmentInExcel(user, appointmentID);
                    removeAppointment = true;
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        if(!removeAppointment){
            System.out.println("Appointment cancelled unsuccessful.");
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
            String logMsg = "Patient " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                    "cancelled appointment: " + appointmentID + "." ;
            ActivityLogUtil.logActivity(logMsg, currentUser);

            loadData(currentUser);
            System.out.println();
            System.out.println("Appointment cancelled successfully.");

        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error cancelling appointment: " + e.getMessage());
        }
    }

    public void rescheduleAppointment(User user, int option, String appointmentID) {
        try {
            cancelAppointment(user, appointmentID);
            createAppointment(user, option, appointmentID);
            System.out.println("Appointment rescheduled successfully.");
            String logMsg = "Patient " + user.getName() + " (ID: " + user.getHospitalID() + ") rescheduled appointment for ." + appointmentID;
            ActivityLogUtil.logActivity(logMsg, user);
            loadData(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
