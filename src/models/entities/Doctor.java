package models.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;

import app.loaders.*;
import models.enums.*;
import models.records.*;

import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class Doctor extends Staff{
    private List<Appointment> appointments;
    private List<Availability> availabilityList;
    private List<Patient> patientsUnderCare;
    private boolean appointmentsLoaded = false;
    private boolean availabilityLoaded = false;

    public Doctor(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
        this.appointments = new ArrayList<>();
        this.availabilityList = new ArrayList<>();
        this.patientsUnderCare = new ArrayList<>();
    }

    public Doctor(String staffId, String name, Gender gender, int age) {
        super(staffId, staffId, name, gender, age);
        this.appointments = new ArrayList<>();
        this.availabilityList = new ArrayList<>();
        this.patientsUnderCare = new ArrayList<>();
    }

    public void viewMedicalRecord(Patient patient){
        System.out.println("Viewing medical records for: " + patient.getName());
        System.out.println("Patient ID: " + patient.getPatientID());
        System.out.println("Date of Birth: " + patient.getDateOfBirth());
        System.out.println("Gender: " + patient.getGender());
        System.out.println("Phone Number: " + patient.getPhoneNumber());
        System.out.println("Email: " + patient.getEmail());
        System.out.println("Blood Type: " + patient.getBloodType());

        System.out.println("Past Diagnoses: ");
        for (String diagnosis : patient.getPastDiagnoses()) {
            System.out.println(" - " + diagnosis);
        }

        System.out.println("Past Treatments: ");
        for (String treatment : patient.getPastTreatments()) {
            System.out.println(" - " + treatment);
        }
    }

    public void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment) {
        patient.addDiagnosis(newDiagnosis);
        patient.addTreatment(newTreatment);
    }

    // Change variableNames
    public void viewSchedule(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Define a formatter for the appointment date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        // Collect days with appointments in a set for quick lookup
        Set<Integer> appointmentDays = new HashSet<>();
        for (Appointment appointment : appointments) {
            LocalDate appointmentDate = appointment.getAppointmentDate().toInstant()
                    .atZone(TimeZone.getDefault().toZoneId()).toLocalDate();

            if (appointmentDate.getYear() == year && appointmentDate.getMonthValue() == month) {
                appointmentDays.add(appointmentDate.getDayOfMonth());
            }
        }

        // Print the month and year
        System.out.printf("%n%s %d%n", yearMonth.getMonth(), year);
        System.out.println("Mon Tue Wed Thu Fri Sat Sun");

        // Determine the starting day of the week for the 1st of the month
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue(); // 1 (Mon) to 7 (Sun)

        // Print leading spaces for the first row alignment (each day takes up 3 spaces, including the marker space)
        for (int i = 1; i < dayOfWeekValue; i++) {
            System.out.print("   ");
        }

        // Print each day of the month with the marker beside the date if it has an appointment
        for (int day = 1; day <= daysInMonth; day++) {
            String marker = appointmentDays.contains(day) ? "X" : " ";
            System.out.printf("%2d%s ", day, marker);

            // Move to a new line after Sunday (accounting for Monday as the start of the week)
            if ((day + dayOfWeekValue - 1) % 7 == 0) {
                System.out.println();
            }
        }
        System.out.println(); // Final newline after calendar output

        // Print the details of each appointment after the calendar
        System.out.println("\nAppointment Details:");
        for (Appointment appointment : appointments) {
            LocalDate appointmentDate = appointment.getAppointmentDate().toInstant()
                    .atZone(TimeZone.getDefault().toZoneId()).toLocalDate();

            if (appointmentDate.getYear() == year && appointmentDate.getMonthValue() == month) {
                String formattedDate = appointmentDate.format(dateFormatter);
                System.out.printf("Date: %s | Time: %s | Status: %s | Patient ID: %s | Doctor ID: %s%n",
                        formattedDate,
                        appointment.getAppointmentTime(),
                        appointment.getStatus(),
                        appointment.getPatientId(),
                        appointment.getDoctorId());
            }
        }
    }
    // Change variableName
    private void loadAppointments() {
        AppointmentLoader appointmentLoader = new AppointmentLoader();
        String appointmentFilePath = FilePaths.APPT_DATA.getPath();
        List<Appointment> loadedAppointments = appointmentLoader.loadData(appointmentFilePath);

        // Filter appointments relevant to this doctor
        for (Appointment appointment : loadedAppointments) {
            if (appointment.getDoctorId().equals(this.getStaffId())) {
                appointments.add(appointment);
            }
        }
    }

    // Method to load availability data - Change variableName
    private void loadAvailability() {
        AppointmentLoader appointmentLoader = new AppointmentLoader();
        String availabilityFilePath = FilePaths.DOCAVAIL_DATA.getPath();
        Map<String, List<Availability>> availabilityMap = appointmentLoader.loadAvailability(availabilityFilePath);

        // Load this doctor’s availability from the map
        if (availabilityMap.containsKey(this.getStaffId())) {
            availabilityList.addAll(availabilityMap.get(this.getStaffId()));
        }
    }

    // Method to reset flags if data changes, ensuring reload on next access - // Change variableName
    public void resetDataFlags() {
        appointmentsLoaded = false;
        availabilityLoaded = false;
    }

    // Change variableName
    public void saveAvailabilityToExcel() {
        String filePath = FilePaths.DOCAVAIL_DATA.getPath();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Check if the workbook has at least one sheet; if not, create a new sheet
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : workbook.createSheet("Availability");

            int lastRow = sheet.getLastRowNum() + 1;

            // Write each availability entry to the Excel file
            for (Availability availability : availabilityList) {
                Row row = sheet.createRow(lastRow++);

                row.createCell(0).setCellValue(availability.getDoctorId());
                row.createCell(1).setCellValue(availability.getAvailableDate().toString());
                row.createCell(2).setCellValue(availability.getStartTime());
                row.createCell(3).setCellValue(availability.getEndTime());
                row.createCell(4).setCellValue(availability.getStatus().toString());
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                System.out.println("Availability saved to file.");
            }
        } catch (IOException e) {
            System.err.println("Error saving availability to file: " + e.getMessage());
        }
    }
    // Change variableName
    public void validateAndSetAvailability(LocalDate date, String startTime, String endTime,
                                           DoctorAvailability status) {
        // Check if the date is valid (current or future)
        if (date.isBefore(LocalDate.now())) {
            System.out.println("Cannot set availability for past dates.");
            return;
        }

        // Check for conflicting availability on the same date
        if (hasAvailabilityOnDate(date)) {
            System.out.println("Availability already set for this date. Please edit the existing entry if needed.");
            return;
        }

        // Convert LocalDate to Date
        Date availableDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Use the existing setAvailability method to add the availability to the list
        setAvailability(availableDate, startTime, endTime, status);

        // Write the availability to the Excel file
        saveAvailabilityToExcel();
        System.out.println("Availability saved for " + date);
    }

    // Helper method to check if availability is already set for a specific date - Change variableName
    public boolean hasAvailabilityOnDate(LocalDate date) {
        return availabilityList.stream().anyMatch(availability ->
                availability.getAvailableDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .equals(date)
        );
    }

    // Helper method to check if the time format is valid
//    private boolean isValidTime(String time, SimpleDateFormat timeFormat) {
//        try {
//            timeFormat.parse(time);
//            return true;
//        } catch (ParseException e) {
//            return false;
//        }
//    }

    // Set availability for appointments - Change variableName
    public void setAvailability(Date availableDate, String startTime, String endTime, DoctorAvailability status) {
        Availability availability = new Availability(this.getStaffId(), availableDate, startTime, endTime,
                status);
        availabilityList.add(availability);
        System.out.printf("Availability set for %s from %s to %s%n", availableDate, startTime, endTime);
    }

    public List<Availability> getAvailabilityList() {
        if (!availabilityLoaded) {
            loadAvailability();
            availabilityLoaded = true;
        }
        return availabilityList;
    }

    // Lazy load for appointments
    public List<Appointment> getAppointments() {
        if (!appointmentsLoaded) {
            loadAppointments();
            appointmentsLoaded = true;
        }
        return appointments;
    }

    public void respondAppointmentRequest(Appointment appointment, boolean accept) {
        //implement update appointment request accepted use cases
    }

    public List<Appointment> viewUpcomingAppointments() {
        return appointments;
    }


    public void recordAppointmentOutcome(Appointment appointment, String serviceType, List<Medicine> prescriptions, String consultationNotes) {
        AppointmentOutcomeRecord outcomeRecord = new AppointmentOutcomeRecord(
                new Date(), // Current date as appointment date
                serviceType,
                prescriptions,
                consultationNotes
        );
    }

    public void addPatientUnderCare(Patient patient) {
        patientsUnderCare.add(patient);
    }

    @Override
    public Role getRole() {
        return Role.DOCTOR;
    }
}
