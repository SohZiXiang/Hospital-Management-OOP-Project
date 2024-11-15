package models.services;

import app.loaders.*;
import interfaces.DataLoader;
import interfaces.PatientManager;
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
import utils.StringFormatUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * The PatientService class implements the PatientManager interface and provides comprehensive management
 * for patient-related operations within a hospital management system. It handles loading, updating, displaying,
 * and managing patient data, including scheduling, updating, and cancelling appointments, as well as managing
 * patient medical records and outcome data. The class supports alert notifications, appointment rescheduling, and
 * managing availability statuses.
 */
public class PatientService implements PatientManager {
    private List<Patient> patientList = new ArrayList<>();
    private boolean patientsLoaded = false;

    ApptAvailLoader appointmentLoader = new ApptAvailLoader();
    List<Appointment> appointmentList = new ArrayList<>();
    String appointmentPath = FilePaths.APPT_DATA.getPath();

    String availabilityFilePath = FilePaths.DOCAVAIL_DATA.getPath();

    DataLoader staffLoader = new StaffLoader();
    List<Staff> staffList = new ArrayList<>();
    String staffPath = FilePaths.STAFF_DATA.getPath();

    SimpleDateFormat formatter = new SimpleDateFormat("EEE dd/MM/yyyy");

    DataLoader patientLoader = new PatientLoader();
    String patientPath = FilePaths.PATIENT_DATA.getPath();
    List<PatientOutcomeRecord> patientOutcomeRecordList = new ArrayList<>();

    String patientOutcomePath = FilePaths.APPTOUTCOME.getPath();

    /**
     * Constructs an instance of the PatientService class.
     */
    public PatientService() {
    }

    /* START OF LOAD/UPDATE DATA */
    /**
     * Initializes the patient list by loading data from the data source if it hasn't been loaded already.
     */
    public void getPatientList() {
        if (!patientsLoaded) {
            loadPatientList();
            patientsLoaded = true;
        }
    }

    /**
     * Loads patient data from an external file and populates the patient list.
     */
    private void loadPatientList() {
        String path = FilePaths.PATIENT_DATA.getPath();
        DataLoader loadPatient = new PatientLoader();
        try {
            patientList = loadPatient.loadData(path);
        } catch (Exception e) {
            System.err.println("Error loading patient data: " + e.getMessage());
        }
    }

    /**
     * Updates the patient data by reloading it from the data source.
     */
    public void updateData() {
        resetData();
        getPatientList();
    }

    /**
     * Resets the patient data, clearing the list and setting the loaded flag to false.
     */
    public void resetData() {
        if (patientList != null) {
            patientList.clear();
        }
        patientsLoaded = false;
    }

    /* END OF LOAD/UPDATE DATA */

    /* START OF MAIN METHODS */

    /**
     * Displays all patient records in a formatted table.
     * If no records are available, it shows a message indicating so.
     */
    public void showAllPatientsRecords() {
        formatter = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("\n--- All Patient Records ---");
        if (patientList.isEmpty()) {
            System.out.println("No patient records available.");
            return;
        }

        String formatHeader = "| %-10s | %-15s | %-12s | %-6s | %-10s | %-25s | %-25s %n";
        String formatRow = "| %-10s | %-15s | %-12s | %-6s | %-10s | %-25s | %-25s %n";

        System.out.format("+------------+-----------------+--------------+--------+------------+----------------------+----------------------+\n");
        System.out.format(formatHeader, "Patient ID", "Name", "Date of Birth", "Gender", "Blood Type", "Diagnoses", "Treatment Plan");
        System.out.format("+------------+-----------------+--------------+--------+------------+----------------------+----------------------+\n");

        for (Patient onePatient : patientList) {

            List<String> diagnosisList = onePatient.getPastDiagnoses();
            String formatDiagnoses = (diagnosisList == null || diagnosisList.isEmpty())
                    ? "No records available"
                    : String.join("\n", diagnosisList);


            List<String> treatmentList = onePatient.getPastTreatments();
            String formatTreatments = (treatmentList == null || treatmentList.isEmpty())
                    ? "No records available"
                    : String.join("\n", treatmentList);

            String[] diagnosisLines = formatDiagnoses.split("\n");
            String[] treatmentLines = formatTreatments.split("\n");
            int maxLines = Math.max(diagnosisLines.length, treatmentLines.length);

            String bloodType = (onePatient.getBloodType() != null) ? onePatient.getBloodType().getDisplayValue() : "";
            LocalDate dob = onePatient.getDateOfBirth();
            Date toFormat = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            for (int i = 0; i < maxLines; i++) {
                if (i == 0) {
                    System.out.format(formatRow,
                            onePatient.getPatientID(),
                            onePatient.getName(),
                            formatter.format(toFormat),
                            StringFormatUtil.toCamelCase(onePatient.getGender().toString()),
                            bloodType,
                            diagnosisLines[0],
                            treatmentLines[0]);
                } else {
                    System.out.format(formatRow, "", "", "", "", "",
                            i < diagnosisLines.length ? diagnosisLines[i] : "",
                            i < treatmentLines.length ? treatmentLines[i] : "");
                }
            }

            System.out.format("+------------+-----------------+--------------+--------+------------+----------------------+----------------------+\n");
        }
    }

    /**
     * Filters patients by their ID and displays their details.
     *
     * @param patientID The ID of the patient to be filtered.
     */
    @Override
    public void filterPatients(String patientID) {
        formatter = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("--- Patient Details ---");
        Patient requestedPatient = patientList.stream()
                .filter(p -> p.getPatientID().equals(patientID))
                .findFirst()
                .orElse(null);

        if (requestedPatient != null) {
            List<String> indDiagnosis = requestedPatient.getPastDiagnoses();
            List<String> indTreatments = requestedPatient.getPastTreatments();

            String bloodType = (requestedPatient.getBloodType() != null) ? requestedPatient.getBloodType().getDisplayValue() : "";
            LocalDate dob = requestedPatient.getDateOfBirth();
            Date toFormat = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());

            String headerFormat = "| %-10s | %-15s | %-12s | %-6s | %-10s | %-25s | %-25s %n";
            String rowFormat = "| %-10s | %-15s | %-12s | %-6s | %-10s | %-25s | %-25s %n";

            System.out.format("+------------+-----------------+--------------+--------+------------+----------------------+----------------------+\n");
            System.out.format(headerFormat, "Patient ID", "Name", "Date of Birth", "Gender", "Blood Type", "Diagnoses", "Treatment Plan");
            System.out.format("+------------+-----------------+--------------+--------+------------+----------------------+----------------------+\n");

            String[] formatDiagnoses = (indDiagnosis == null || indDiagnosis.isEmpty())
                    ? new String[]{"No records available"}
                    : indDiagnosis.toArray(new String[0]);

            String[] formatTreatments = (indTreatments == null || indTreatments.isEmpty())
                    ? new String[]{"No records available"}
                    : indTreatments.toArray(new String[0]);

            int maxLines = Math.max(formatDiagnoses.length, formatTreatments.length);

            for (int i = 0; i < maxLines; i++) {
                if (i == 0) {
                    System.out.format(rowFormat,
                            requestedPatient.getPatientID(),
                            requestedPatient.getName(),
                            formatter.format(toFormat),
                            StringFormatUtil.toCamelCase(requestedPatient.getGender().toString()),
                            bloodType,
                            formatDiagnoses[0],
                            formatTreatments[0]);
                } else {
                    System.out.format(rowFormat, "", "", "", "", "",
                            i < formatDiagnoses.length ? formatDiagnoses[i] : "",
                            i < formatTreatments.length ? formatTreatments[i] : "");
                }
            }

            System.out.format("+------------+-----------------+--------------+--------+------------+----------------------+----------------------+\n");

        } else {
            System.out.println("Patient record not found.");
        }
    }

    /**
     * Checks if a patient exists in the list by their ID.
     *
     * @param patientID The ID of the patient to be checked.
     * @return The Patient object if found, otherwise null.
     */
    public Patient checkWhetherPatientValid(String patientID) {
        return patientList.stream()
                .filter(p -> p.getPatientID().equals(patientID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates a patient's medical record by adding a new diagnosis and treatment.
     *
     * @param patient       The Patient object whose record needs updating.
     * @param newDiagnosis  The new diagnosis to add.
     * @param newTreatment  The new treatment plan to add.
     */
    public void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment) {
        patient.addDiagnosis(newDiagnosis);
        patient.addTreatment(newTreatment);
        updatePatientRecords(patient.getPatientID(), newDiagnosis, newTreatment);
    }

    /**
     * Updates the specified patient's record in the data file with a new diagnosis and treatment plan.
     *
     * @param patientID    The ID of the patient to update.
     * @param indDiagnosis The new diagnosis to be recorded.
     * @param indTreatment The new treatment plan to be recorded.
     */
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

    /**
     * Formats and returns a list of items with labels, e.g., "Diagnosis 1: <item>".
     *
     * @param items The list of items to format.
     * @param label The label to prefix each item.
     * @return A formatted string with each item labeled and separated by commas.
     */
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

    /**
     * Create an appointment for a user.
     *
     * @param user           The user scheduling the appointment.
     * @param option         The selected option for appointment slot.
     * @param appointmentID  The unique ID of the appointment.
     * @param create         Boolean indicating if this creating or reschedule instead.
     */
    public void createAppointment(User user, int option, String appointmentID, boolean create) {

        Map<String, List<Availability>> availabilityMap = appointmentLoader.loadAvailData(availabilityFilePath);
        int slotCount = 0;
        boolean exist = false;

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
                        for (Appointment appointment : appointmentList) {
                            if (appointment.getPatientId().equals(user.getHospitalID())
                                    && appointment.getAppointmentDate().equals(availability.getAvailableDate())
                                    && appointment.getAppointmentTime().equals(availability.getStartTime())) {
                                exist = true;
                                break;
                            }
                        }

                        if(!exist){
                            Appointment newAppointment = new Appointment(appointmentID, user.getHospitalID(),
                                    availability.getDoctorId(), availability.getAvailableDate(), availability.getStartTime(), "");
                            writeAppointmentToExcel(user, newAppointment, create);
                        }
                        else{
                            System.out.println("You already scheduled a slot on " + formatter.format(availability.getAvailableDate())
                            + " at " + availability.getStartTime());
                        }
                    }
                }
            }
        }
    }

    /**
     * Writes appointment details to the Excel file.
     *
     * @param currentUser  The current user creating the appointment.
     * @param appointment  The appointment details.
     * @param create       Boolean indicating if this creating or reschedule instead.
     */
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

    /**
     * Loads and displays appointment data for a given user.
     * Includes both upcoming appointments and available slots.
     *
     * @param user The user whose appointments are being retrieved. The user's hospital ID is used to filter relevant data.
     */
    public void loadAppointmentData(User user){

        int slotCount = 0;
        Map<String, List<Availability>> availabilityMap = appointmentLoader.loadAvailData(availabilityFilePath);
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
        System.out.println();
    }

    /**
     * Cancels an existing appointment for the user.
     *
     * @param user           The user canceling the appointment.
     * @param appointmentID  The unique ID of the appointment.
     * @param cancel         Boolean indicating if the appointment is to be canceled or for reschedule.
     * @param option         The selected option for rescheduling (if applicable).
     */
    public void cancelAppointment(User user, String appointmentID, boolean cancel, int option) {

        Boolean removeAppointment = false;
        AvailService availService = new AvailService(null);

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);

            for (Appointment appointment : appointmentList) {
                if (appointment.getPatientId().equals(user.getHospitalID())
                        && appointment.getStatus() != AppointmentStatus.COMPLETED
                        && appointment.getAppointmentId().equals(appointmentID))
                {
                    if(appointment.getStatus() == AppointmentStatus.CONFIRMED) {
                        updateAvailInExcel(user, appointment.getDoctorId(), appointment.getAppointmentDate(),
                                appointment.getAppointmentTime());
                    }

                    removeAppointmentInExcel(user, appointmentID, cancel, option);
                    removeAppointment = true;
                }
            }


        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        if(!removeAppointment){
            System.out.println("Please pick a valid appointment ID");
        }
    }

    /**
     * Removes an appointment from the Excel file and optionally cancels or reschedules it.
     *
     * @param currentUser   The user canceling or rescheduling the appointment.
     * @param appointmentID The ID of the appointment to be removed.
     * @param cancel        Boolean indicating if the appointment should be canceled or reschedule.
     * @param option        The selected option for rescheduling (if applicable).
     */
    private void removeAppointmentInExcel(User currentUser, String appointmentID, boolean cancel, int option) {
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

            System.out.println();

            if(cancel){
                loadAppointmentData(currentUser);

                System.out.println("Appointment cancelled successfully.");

                String logMsg = "Patient " + currentUser.getName() + " (ID: " + currentUser.getHospitalID() + ") " +
                        "cancelled appointment: " + appointmentID + "." ;
                ActivityLogUtil.logActivity(logMsg, currentUser);

                SMSUtil.sendSms(SMSUtil.numberHX, "Your Appointment: " + appointmentID + " is cancelled");

            }
            else{
                createAppointment(currentUser, option, appointmentID, false);
            }


        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error cancelling appointment: " + e.getMessage());
        }
    }

    /**
     * Reschedules an existing appointment for the user.
     *
     * @param user           The user rescheduling the appointment.
     * @param option         The selected option for the new appointment slot.
     * @param appointmentID  The unique ID of the appointment to be rescheduled.
     */
    public void rescheduleAppointment(User user, int option, String appointmentID) {

        boolean exist = false;
        int slotCount = 0;
        Map<String, List<Availability>> availabilityMap = appointmentLoader.loadAvailData(availabilityFilePath);

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        try {
            for (List<Availability> availabilityList : availabilityMap.values()) {
                for (Availability availability : availabilityList) {
                    if (availability.getStatus() == DoctorAvailability.AVAILABLE) {

                        ++slotCount;
                        if (slotCount == option) {
                            for (Appointment appointment : appointmentList) {
                                if (appointment.getPatientId().equals(user.getHospitalID())
                                        && appointment.getAppointmentDate().equals(availability.getAvailableDate())
                                        && appointment.getAppointmentTime().equals(availability.getStartTime())) {
                                    exist = true;
                                    break;
                                }
                            }

                            if (!exist) {
                                cancelAppointment(user, appointmentID, false, option);
                                System.out.println("Appointment rescheduled successfully.");
                                String logMsg = "Patient " + user.getName() + " (ID: " + user.getHospitalID() + ") rescheduled appointment for ." + appointmentID;
                                ActivityLogUtil.logActivity(logMsg, user);
                                loadAppointmentData(user);
                                SMSUtil.sendSms(SMSUtil.numberHX, "Your Appointment: " + appointmentID + " is successfully rescheduled");
                            } else {
                                System.out.println("You already scheduled a slot on " + formatter.format(availability.getAvailableDate())
                                        + " at " + availability.getStartTime());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads patient outcome data from an Excel file.
     *
     * @param path The file path to the patient outcome data.
     * @return A list of PatientOutcomeRecord objects containing the outcome details.
     */
    private List<PatientOutcomeRecord> loadPatientOutcomeData(String path) {
        List<PatientOutcomeRecord> outcomeRecordList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(path));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {

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

    /**
     * Loads and displays past appointment outcomes for a given patient.
     *
     * @param user The user (patient) whose appointment outcomes are being retrieved.
     *             The user's hospital ID is used to filter the outcomes.
     */
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

    /**
     * Displays and loads medical record data for a user.
     *
     * @param user The patient whose medical records are to be displayed.
     */
    public void loadMedicalRecordData(User user) {
        Patient currentPatient = (Patient) user;
        try {
            patientList = patientLoader.loadData(patientPath);
        } catch (Exception e) {
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

        String formatHeader = "| %-25s | %-25s %n";
        String formatRow = "| %-25s | %-25s %n";

        System.out.format("+----------------------+----------------------+\n");
        System.out.format(formatHeader, "Diagnoses", "Treatment Plan");
        System.out.format("+----------------------+----------------------+\n");

        List<String> diagnosisList = currentPatient.getPastDiagnoses();
        String formatDiagnoses = (diagnosisList == null || diagnosisList.isEmpty())
                ? "No records available"
                : String.join("\n", diagnosisList);


        List<String> treatmentList = currentPatient.getPastTreatments();
        String formatTreatments = (treatmentList == null || treatmentList.isEmpty())
                ? "No records available"
                : String.join("\n", treatmentList);

        String[] diagnosisLines = formatDiagnoses.split("\n");
        String[] treatmentLines = formatTreatments.split("\n");
        int maxLines = Math.max(diagnosisLines.length, treatmentLines.length);

        for (int i = 0; i < maxLines; i++) {
            if (i == 0) {
                System.out.format(formatRow,
                        diagnosisLines[0],
                        treatmentLines[0]);
            } else {
                System.out.format(formatRow,
                        i < diagnosisLines.length ? diagnosisLines[i] : "",
                        i < treatmentLines.length ? treatmentLines[i] : "");
            }
        }

        System.out.format("+----------------------+----------------------+\n");
    }

    /**
     * Updates a user's contact details (email or phone number).
     *
     * @param user     The user updating their contact details.
     * @param patient  The patient object.
     * @param email    Boolean indicating if the update is for the email or phone number.
     */
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

    /**
     * Updates the availability status of slot in the Excel file.
     *
     * @param user      The user performing the update.
     * @param doctorID  The ID of the doctor whose availability is being updated.
     * @param date      The date of the availability to be updated.
     * @param startTime The start time of the availability to be updated.
     */
    private void updateAvailInExcel(User user, String doctorID, Date date, String startTime) {
        try (FileInputStream fis = new FileInputStream(availabilityFilePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell doctorIdCell = row.getCell(0);
                Cell availableDateCell = row.getCell(1);
                Cell startTimeCell = row.getCell(2);
                if (doctorIdCell.getStringCellValue().equals(doctorID)
                        && availableDateCell.getDateCellValue().equals(date)
                        && startTimeCell.getStringCellValue().equals(startTime))
                {
                    row.getCell(4).setCellValue(DoctorAvailability.AVAILABLE.toString());

                    try (FileOutputStream fos = new FileOutputStream(availabilityFilePath)) {
                        workbook.write(fos);
                    }
                    break;
                }

            }
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error updating availability in Excel: " + e.getMessage());
        }
    }

    /**
     * Send an appointment alerts to the user phone if their appointment are 2 days away.
     *
     * @param user The patient to whom the alerts are sent.
     */
    public void loadAppointmentAlert(User user) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
        Date date = new Date();

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
            long diffInMillies = appointment.getAppointmentDate().getTime() - date.getTime();
            long daysDifference = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (daysDifference <= 2 && daysDifference >= 0 && appointment.getStatus() == AppointmentStatus.CONFIRMED
            && appointment.getPatientId().equals(user.getHospitalID())) {
                String doctorName = "N/A";

                for (Staff staff : staffList) {
                    if (staff.getStaffId().equals(appointment.getDoctorId())) {
                        doctorName = "Dr " + staff.getName();
                        break;
                    }
                }

                SMSUtil.sendSms(SMSUtil.numberHX, "Alert! you have a appointment " +
                        appointment.getAppointmentId() + " scheduled on " + formatter.format(appointment.getAppointmentDate())
                        + " at " + appointment.getAppointmentTime() + " with " + doctorName);
            }
        }
    }
}
