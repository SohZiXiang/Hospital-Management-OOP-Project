package models.services;

import app.loaders.*;
import interfaces.DataLoader;
import models.enums.*;
import models.entities.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import models.records.PatientOutcomeRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.ActivityLogUtil;
import utils.SMSUtil;

import java.io.IOException;

public class PatientService {
    private List<Patient> patientsUnderCare = new ArrayList<>();
    private boolean patientsLoaded = false;

    ApptAvailLoader appointmentLoader = new ApptAvailLoader();
    List<Appointment> appointmentList = new ArrayList<>();
    String appointmentPath = FilePaths.APPT_DATA.getPath();

    String availabilityFilePath = FilePaths.DOCAVAIL_DATA.getPath();
    Map<String, List<Availability>> availabilityMap = appointmentLoader.loadAvailData(availabilityFilePath);

    DataLoader staffLoader = new StaffLoader();
    List<Staff> staffList = new ArrayList<>();
    String staffPath = FilePaths.STAFF_DATA.getPath();

    SimpleDateFormat formatter = new SimpleDateFormat("EEE dd/MM/yyyy");

    DataLoader patientLoader = new PatientLoader();
    List<Patient> patientList = new ArrayList<>();
    String patientPath = FilePaths.PATIENT_DATA.getPath();
    List<PatientOutcomeRecord> patientOutcomeRecordList = new ArrayList<>();

    String patientOutcomePath = FilePaths.APPTOUTCOME.getPath();

    public PatientService() {
    }

    /* START OF LOAD/UPDATE DATA */
    public void getPatientList() {
        if (!patientsLoaded) {
            loadPatientList();
            patientsLoaded = true;
        }
    }

    private void loadPatientList() {
        String path = FilePaths.PATIENT_DATA.getPath();
        DataLoader loadPatient = new PatientLoader();
        try {
            patientsUnderCare = loadPatient.loadData(path);
        } catch (Exception e) {
            System.err.println("Error loading patient data: " + e.getMessage());
        }
    }

    // Update patient data
    public void updateData() {
        if (patientsUnderCare != null) {
            patientsUnderCare.clear();
        }
        patientsLoaded = false;
        getPatientList();
    }

    /* END OF LOAD/UPDATE DATA */

    /* START OF MAIN METHODS */

    // Show all patient records
    public void showAllPatientsRecords() {
        if (patientsUnderCare.isEmpty()) {
            System.out.println("No patient records available.");
            return;
        }

        System.out.printf("%-10s %-20s %-15s %-10s %-10s %-30s %-30s%n",
                "Patient ID", "Name", "Date of Birth", "Gender", "Blood Type", "Diagnosis", "Treatment Plan");
        System.out.println("--------------------------------------------------------------------------------------------");

        for (Patient onePatient : patientsUnderCare) {
            List<String> indDiagnosis = onePatient.getPastDiagnoses();
            List<String> indTreatments = onePatient.getPastTreatments();

            String formatDiagnosis = (indDiagnosis == null || indDiagnosis.isEmpty())
                    ? "No records available"
                    : displayInDiffFormat(indDiagnosis, "Diagnosis");
            String formatTreatments = (indTreatments == null || indTreatments.isEmpty())
                    ? "No records available"
                    : displayInDiffFormat(indTreatments, "Treatment");

            System.out.printf("%-10s %-20s %-15s %-10s %-10s %-30s %-30s%n",
                    onePatient.getPatientID(),
                    onePatient.getName(),
                    onePatient.getDateOfBirth(),
                    onePatient.getGender(),
                    onePatient.getBloodType(),
                    formatDiagnosis,
                    formatTreatments);
        }
    }

    // Filter patients by ID
    public void filterPatients(String patientID) {
        Patient requestedPatient = patientsUnderCare.stream()
                .filter(p -> p.getPatientID().equals(patientID))
                .findFirst()
                .orElse(null);

        if (requestedPatient != null) {
            List<String> indDiagnosis = requestedPatient.getPastDiagnoses();
            List<String> indTreatments = requestedPatient.getPastTreatments();

            String formatDiagnoses = (indDiagnosis == null || indDiagnosis.isEmpty())
                    ? "No records available"
                    : displayInDiffFormat(indDiagnosis, "Diagnosis");
            String formatTreatments = (indTreatments == null || indTreatments.isEmpty())
                    ? "No records available"
                    : displayInDiffFormat(indTreatments, "Treatment");

            System.out.printf("Patient ID: %s%nName: %s%nDate of Birth: %s%nGender: %s%nBlood Type: %s%nContact Information: %s%nDiagnoses: %s%nTreatments: %s%n",
                    requestedPatient.getPatientID(),
                    requestedPatient.getName(),
                    requestedPatient.getDateOfBirth(),
                    requestedPatient.getGender(),
                    requestedPatient.getBloodType(),
                    requestedPatient.getEmail(),
                    formatDiagnoses,
                    formatTreatments);
        } else {
            System.out.println("Patient record not found.");
        }
    }

    // Check if patient is valid
    public Patient checkWhetherPatientValid(String patientID) {
        return patientsUnderCare.stream()
                .filter(p -> p.getPatientID().equals(patientID))
                .findFirst()
                .orElse(null);
    }

    // Update medical record
    public void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment) {
        patient.addDiagnosis(newDiagnosis);
        patient.addTreatment(newTreatment);
        updatePatientRecords(patient.getPatientID(), newDiagnosis, newTreatment);
    }

    // Update patient records in Excel
    public void updatePatientRecords(String patientID, String indDiagnosis, String indTreatment) {
        String path = FilePaths.PATIENT_DATA.getPath();

        try (Workbook wkBook = new XSSFWorkbook(new FileInputStream(path));
             FileOutputStream outputFile = new FileOutputStream(path)) {

            Sheet indSheet = wkBook.getSheetAt(0);
            int diagnosisClm = -1;
            int treatmentClm = -1;
            Row header = indSheet.getRow(0);

            for (Cell indCell : header) {
                if (indCell.getStringCellValue().equalsIgnoreCase("Doctor Diagnosis")) {
                    diagnosisClm = indCell.getColumnIndex();
                } else if (indCell.getStringCellValue().equalsIgnoreCase("Treatment Plan")) {
                    treatmentClm = indCell.getColumnIndex();
                }
            }

            if (diagnosisClm == -1) {
                diagnosisClm = header.getLastCellNum();
                header.createCell(diagnosisClm).setCellValue("Doctor Diagnosis");
            }
            if (treatmentClm == -1) {
                treatmentClm = header.getLastCellNum();
                header.createCell(treatmentClm).setCellValue("Treatment Plan");
            }

            for (Row indRow : indSheet) {
                Cell patientIDCell = indRow.getCell(0);
                if (patientIDCell != null && patientIDCell.getStringCellValue().equals(patientID)) {
                    String existingDiagnoses = indRow.getCell(diagnosisClm) != null ? indRow.getCell(diagnosisClm).getStringCellValue() : "";
                    String existingTreatments = indRow.getCell(treatmentClm) != null ? indRow.getCell(treatmentClm).getStringCellValue() : "";

                    List<String> addedDiagnoses = existingDiagnoses.isEmpty()
                            ? new ArrayList<>() : new ArrayList<>(Arrays.asList(existingDiagnoses.split(", ")));
                    List<String> addedTreatments = existingTreatments.isEmpty()
                            ? new ArrayList<>() : new ArrayList<>(Arrays.asList(existingTreatments.split(", ")));

                    if (!indDiagnosis.isEmpty()) addedDiagnoses.add(indDiagnosis);
                    if (!indTreatment.isEmpty()) addedTreatments.add(indTreatment);

                    String formatDiagnosis = String.join(", ", addedDiagnoses);
                    String formatTreatment = String.join(", ", addedTreatments);

                    indRow.createCell(diagnosisClm).setCellValue(formatDiagnosis);
                    indRow.createCell(treatmentClm).setCellValue(formatTreatment);
                    break;
                }
            }

            wkBook.write(outputFile);

        } catch (IOException e) {
            System.err.println("Error updating patient record: " + e.getMessage());
        }
    }

    /* END OF MAIN METHODS */

    // Helper method
    private String displayInDiffFormat(List<String> items, String label) {
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            format.append(label).append(" ").append(i + 1).append(": ").append(items.get(i));
            if (i < items.size() - 1) {
                format.append(", ");
            }
        }
        return format.toString();
    }

    public void createAppointment(User user, int option, String appointmentID, boolean create) {

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
                        Appointment newAppointment = new Appointment(appointmentID, user.getHospitalID(),
                                availability.getDoctorId(), availability.getAvailableDate(), availability.getStartTime(), "");
                        writeAppointmentToExcel(user, newAppointment, create);

                    }
                }
            }
        }
    }

    private void writeAppointmentToExcel(User currentUser, Appointment appointment, boolean create) {
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


            loadAppointmentData(currentUser);
            System.out.println();

            if(create){
                String logMsg = "Patient " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                        "create appointment " + appointment.getAppointmentId() + ". ";
                ActivityLogUtil.logActivity(logMsg, currentUser);

                System.out.println("Appointment added successfully.");

                String doctorName = "N/A";

                for (Staff staff : staffList) {
                    if (staff.getStaffId().equals(appointment.getDoctorId())) {
                        doctorName = "Dr " + staff.getName();
                        break;
                    }
                }

                SMSUtil.sendSms(SMSUtil.numberHX, "Your appointment is scheduled for Appointment ID: " +
                        appointment.getAppointmentId() + " on " + formatter.format(appointment.getAppointmentDate())
                        + " at " + appointment.getAppointmentTime() + " with " + doctorName);
            }

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

    public void loadAppointmentData(User user){

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

    public static String addOneHour(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        LocalTime localTime = LocalTime.parse(time, formatter);
        LocalTime newTime = localTime.plus(1, ChronoUnit.HOURS);
        return newTime.format(formatter);
    }

    public void cancelAppointment(User user, String appointmentID, boolean cancel) {

        Boolean removeAppointment = false;
        AvailService availService = new AvailService(null);

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);

            for (Appointment appointment : appointmentList) {
                if (appointment.getPatientId().equals(user.getHospitalID())
                        && appointment.getStatus() != AppointmentStatus.COMPLETED
                        && appointment.getAppointmentId().equals(appointmentID))
                {
                    removeAppointmentInExcel(user, appointmentID, cancel);
                    removeAppointment = true;

                    availService.editOneSlot(appointment.getDoctorId(), LocalDate.ofInstant(appointment.getAppointmentDate().toInstant(), ZoneId.systemDefault()),
                            appointment.getAppointmentTime().toUpperCase(), appointment.getAppointmentTime().toUpperCase(), addOneHour(appointment.getAppointmentTime()).toUpperCase(),
                            DoctorAvailability.AVAILABLE);
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

    private void removeAppointmentInExcel(User currentUser, String appointmentID, boolean cancel) {
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

            loadAppointmentData(currentUser);
            System.out.println();

            if(cancel){
                System.out.println("Appointment cancelled successfully.");

                String logMsg = "Patient " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                        "cancelled appointment: " + appointmentID + "." ;
                ActivityLogUtil.logActivity(logMsg, currentUser);

                SMSUtil.sendSms(SMSUtil.numberHX, "Your Appointment: " + appointmentID + " is cancelled");

            }


        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error cancelling appointment: " + e.getMessage());
        }
    }

    public void rescheduleAppointment(User user, int option, String appointmentID) {
        try {
            cancelAppointment(user, appointmentID, false);
            createAppointment(user, option, appointmentID, false);
            System.out.println("Appointment rescheduled successfully.");
            String logMsg = "Patient " + user.getName() + " (ID: " + user.getHospitalID() + ") rescheduled appointment for ." + appointmentID;
            ActivityLogUtil.logActivity(logMsg, user);
            loadAppointmentData(user);
            SMSUtil.sendSms(SMSUtil.numberHX, "Your Appointment: " + appointmentID + " is successfully rescheduled");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<PatientOutcomeRecord> loadPatientOutcomeData(String path) {
        List<PatientOutcomeRecord> outcomeRecordList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(path));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    // Appointment ID
                    String appointmentID = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    String serviceType = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    String consultationNotes = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    String medicine = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    String medicineStatus = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    String outcomeStatus = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    PatientOutcomeRecord patientOutcomeRecord = new PatientOutcomeRecord(appointmentID, serviceType, consultationNotes, medicine, medicineStatus, outcomeStatus);

                    outcomeRecordList.add(patientOutcomeRecord);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading patient outcome data: " + e.getMessage());
        }

        return outcomeRecordList;
    }

    public void loadOutcomeData(User user){
        System.out.println();
        System.out.println("--------- Displaying Past Appointments Outcome for patient: " + user.getName() + " ---------");
        System.out.println();

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);
            patientOutcomeRecordList = loadPatientOutcomeData(patientOutcomePath);
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

        System.out.printf("%-15s %-20s %-20s %-20s %-30s %-30s %-30s %-30s %-30s%n",
                "Appointment ID", "Doctor", "Date", "Time", "Consultation Notes", "Service", "Medicine", "Medicine Status", "Outcome");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (Appointment appointment : appointmentList) {
            if (appointment.getPatientId().equals(user.getHospitalID()) && appointment.getStatus() == AppointmentStatus.COMPLETED) {
                String doctorName = "N/A";
                String serviceType = "N/A";
                String consultationNotes = "N/A";
                String medicine = "N/A";
                String medicineStatus = "N/A";
                String outcomeStatus = "N/A";

                for (Staff staff : staffList) {
                    if (staff.getStaffId().equals(appointment.getDoctorId())) {
                        doctorName = "Dr " + staff.getName();
                        break;
                    }
                }

                for (PatientOutcomeRecord record : patientOutcomeRecordList) {
                    if (record.getAppointmentId().equals(appointment.getAppointmentId())) {
                        serviceType = record.getServiceType();
                        consultationNotes = record.getConsultationNote();
                        medicine = record.getMedicine();
                        medicineStatus = record.getMedicineStatus();
                        outcomeStatus = record.getOutcomeStatus();
                        break;
                    }
                }

                System.out.printf("%-15s %-20s %-20s %-20s %-30s %-30s %-30s %-30s %-30s%n",
                        appointment.getAppointmentId(), doctorName,
                        formatter.format(appointment.getAppointmentDate()),
                        appointment.getAppointmentTime(), serviceType, consultationNotes, medicine, medicineStatus, outcomeStatus);
            }
        }
    }

    public void loadMedicalRecordData(User user) {
        Patient currentPatient = (Patient) user;
        try {
            patientList = patientLoader.loadData(patientPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        for (Patient patient : patientList) {
            if (patient.getPatientID().equals(user.getHospitalID())) {
                currentPatient = patient;
            }
        }

        System.out.println("----- Displaying Medical Record for " + user.getHospitalID() + " -----");
        System.out.println();
        System.out.printf("%-30s %-30s%n", "Attribute", "Details");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");

        System.out.printf("%-30s %-30s%n", "Name:", currentPatient.getName());
        System.out.printf("%-30s %-30s%n", "DOB:", currentPatient.getDateOfBirth());
        System.out.printf("%-30s %-30s%n", "Gender:", currentPatient.getGender());
        System.out.printf("%-30s %-30s%n", "Blood Type:", currentPatient.getBloodType());
        System.out.printf("%-30s %-30s%n", "Phone Number:", currentPatient.getPhoneNumber());
        System.out.printf("%-30s %-30s%n", "Email:", currentPatient.getEmail());

        System.out.println();
    }

    public void updateContact(User user, Patient patient, boolean email) {
        String type = "";
        String changeValue = "";

        try (FileInputStream fis = new FileInputStream(patientPath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell patientIdCell = row.getCell(0);
                if (patientIdCell != null && patientIdCell.getStringCellValue().equals(user.getHospitalID())) {
                    if(email){
                        row.getCell(5).setCellValue(patient.getEmail());
                        type = "Email";
                        changeValue = patient.getEmail();
                        SMSUtil.sendSms(SMSUtil.numberHX, "Your Email is successfully changed to " + changeValue);
                    }
                    else{
                        row.getCell(8).setCellValue(patient.getPhoneNumber());
                        type = "Phone Number";
                        changeValue = patient.getPhoneNumber();
                        SMSUtil.sendSms(SMSUtil.numberHX, "Your Phone Number is successfully changed to " + changeValue);
                    }

                    try (FileOutputStream fos = new FileOutputStream(patientPath)) {
                        workbook.write(fos);
                    }
                }
            }
            String logMsg = "Patient " + patient.getName() + " (ID: " + patient.getHospitalID() + ") " +
                    "changed " + type + " to " + changeValue + ". " ;
            ActivityLogUtil.logActivity(logMsg, user);
            System.out.println(type + " Successfully Updated To " + changeValue);
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error updating staff in Excel: " + e.getMessage());
        }
    }
}
