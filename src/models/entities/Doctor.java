package models.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;

import app.loaders.*;
import interfaces.DataLoader;
import models.enums.*;
import models.records.*;

import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class Doctor extends Staff{
    private List<Appointment> apptList;
    private List<Availability> availList;
    private List<Patient> patientsUnderCare;
    private boolean apptsLoaded = false;
    private boolean availLoaded = false;
    private boolean patientsLoaded = false;

    public Doctor(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
        this.apptList = new ArrayList<>();
        this.availList = new ArrayList<>();
        this.patientsUnderCare = new ArrayList<>();
    }

    public Doctor(String staffId, String name, Gender gender, int age) {
        super(staffId, staffId, name, gender, age);
        this.apptList = new ArrayList<>();
        this.availList = new ArrayList<>();
        this.patientsUnderCare = new ArrayList<>();
    }

    public void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment) {
        patient.addDiagnosis(newDiagnosis);
        patient.addTreatment(newTreatment);
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

    public void getPatientList() {
        if (!patientsLoaded) {
            loadPatientList();
            patientsLoaded = true;
        }
    }

    public void showAllRecords() {
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
    // Each different diagnosis/treatments is separated by a comma, I want to display it in
    // a way that show Diagnosis 1: ---, Diagnosis 2: ---
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

    public Patient checkWhetherPatientValid(String patientID) {
        Patient forThisPatient = patientsUnderCare.stream()
                .filter(p -> p.getPatientID().equals(patientID))
                .findFirst()
                .orElse(null);
        return forThisPatient;
    }

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

            // Find the patient row and update diagnosis and treatment
            for (Row indRow : indSheet) {
                Cell patientIDCell = indRow.getCell(0); // Assuming Patient ID is in the first column
                if (patientIDCell != null && patientIDCell.getStringCellValue().equals(patientID)) {
                    // Get existing diagnoses and treatments if they exist
                    String existingDiagnoses = indRow.getCell(diagnosisClm) != null ? indRow.getCell(diagnosisClm).getStringCellValue() : "";
                    String existingTreatments = indRow.getCell(treatmentClm) != null ? indRow.getCell(treatmentClm).getStringCellValue() : "";

                    List<String> addedDiagnoses = existingDiagnoses.isEmpty()
                            ? new ArrayList<>() : new ArrayList<>(Arrays.asList(existingDiagnoses.split(", ")));
                    List<String> addedTreatments = existingTreatments.isEmpty()
                            ? new ArrayList<>() : new ArrayList<>(Arrays.asList(existingTreatments.split(", ")));

                    if (!indDiagnosis.isEmpty()) addedDiagnoses.add(indDiagnosis);
                    if (!indTreatment.isEmpty()) addedTreatments.add(indTreatment);

                    // Convert the lists back to comma-separated strings
                    String formatDiagnosis = String.join(", ", addedDiagnoses);
                    String formatTreatment = String.join(", ", addedTreatments);

                    // Update the cells in the Excel row
                    indRow.createCell(diagnosisClm).setCellValue(formatDiagnosis);
                    indRow.createCell(treatmentClm).setCellValue(formatTreatment);
                    break;
                }
            }

            // Write changes to the file
            wkBook.write(outputFile);

        } catch (IOException e) {
            System.err.println("Error updating patient record: " + e.getMessage());
        }
    }



    public void viewDoctorSchedule() {
        LocalDate todayDate = LocalDate.now();
        int year = todayDate.getYear();
        int month = todayDate.getMonthValue();
        YearMonth currentYrMth = YearMonth.of(year, month);
        int noOfDays = currentYrMth.lengthOfMonth();

        // Define a formatter for the appointment date
        DateTimeFormatter desiredDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");

        // Collect days with appointments in a set for quick lookup
        Set<Integer> onlyConfirmedDates = new HashSet<>();
        for (Appointment oneApt : apptList) {
            LocalDate apptDate = oneApt.getAppointmentDate().toInstant()
                    .atZone(TimeZone.getDefault().toZoneId()).toLocalDate();

            if (oneApt.getStatus() == AppointmentStatus.CONFIRMED &&
                    apptDate.getYear() == year && apptDate.getMonthValue() == month) {
                onlyConfirmedDates.add(apptDate.getDayOfMonth());
            }
        }

        // Print the month and year
        System.out.printf("%n%s %d%n", currentYrMth.getMonth(), year);
        System.out.println("Mon Tue Wed Thu Fri Sat Sun");

        // Determine the starting day of the week for the 1st of the month
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int whichDayOfWk = firstDay.getDayOfWeek().getValue(); // 1 (Mon) to 7 (Sun)

        // Print leading spaces for the first row alignment (each day takes up 3 spaces, including the marker space)
        for (int i = 1; i < whichDayOfWk; i++) {
            System.out.print("   ");
        }

        // Print each day of the month with the marker beside the date if it has an appointment
        for (int day = 1; day <= noOfDays; day++) {
            String crossApptDate = onlyConfirmedDates.contains(day) ? "X" : " ";
            System.out.printf("%2d%s ", day, crossApptDate);

            // Move to a new line after Sunday (accounting for Monday as the start of the week)
            if ((day + whichDayOfWk - 1) % 7 == 0) {
                System.out.println();
            }
        }
        System.out.println(); // Final newline after calendar output

        // Print the details of each appointment after the calendar
        System.out.println("\nAppointment Details:");
        for (Appointment oneApt : apptList) {
            LocalDate apptDate = oneApt.getAppointmentDate().toInstant()
                    .atZone(TimeZone.getDefault().toZoneId()).toLocalDate();

            if (apptDate.getYear() == year && apptDate.getMonthValue() == month) {
                String date = apptDate.format(desiredDateFormat);
                System.out.printf("Date: %s | Time: %s | Status: %s | Patient ID: %s | Doctor ID: %s%n",
                        date,
                        oneApt.getAppointmentTime(),
                        oneApt.getStatus(),
                        oneApt.getPatientId(),
                        oneApt.getDoctorId());
            }
        }
    }

    private void loadApptData() {
        ApptAvailLoader apptLoader = new ApptAvailLoader();
        String path = FilePaths.APPT_DATA.getPath();
        List<Appointment> apptsList = apptLoader.loadData(path);

        // Filter appointments relevant to this doctor
        for (Appointment oneAppt : apptsList) {
            if (oneAppt.getDoctorId().equals(this.getStaffId())) {
                apptList.add(oneAppt);
            }
        }
    }

    // Method to load availability data
    private void loadAvailData() {
        ApptAvailLoader availLoader = new ApptAvailLoader();
        String path = FilePaths.DOCAVAIL_DATA.getPath();
        Map<String, List<Availability>> map = availLoader.loadAvailData(path);

        // Load this doctor’s availability from the map
        if (map.containsKey(this.getStaffId())) {
            availList.addAll(map.get(this.getStaffId()));
        }
    }

    // Method to reset flags if data changes, ensuring reload on next access - // Change variableName
    public void resetData() {
        apptsLoaded = false;
        availLoaded = false;
    }


    //Helper method to check if the time format is valid
    private boolean checkValidTimeFormat(String time, SimpleDateFormat timeFormat) {
        try {
            timeFormat.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Helper method to check if availability is already set for a specific date
    public boolean compareDate(LocalDate addedDate) {
        return availList.stream().anyMatch(availability ->
                availability.getAvailableDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .equals(addedDate)
        );
    }

    public void addAvail(LocalDate date, String startTime, String endTime,
                         DoctorAvailability status) {
        SimpleDateFormat desiredTimeFormat = new SimpleDateFormat("hh:mm a");
        desiredTimeFormat.setLenient(false);

        if (!checkValidTimeFormat(startTime, desiredTimeFormat)) {
            System.out.println("Invalid start time format. Please enter start time as hh:mm AM/PM");
            return;
        }

        if (!checkValidTimeFormat(endTime, desiredTimeFormat)) {
            System.out.println("Invalid end time format. Please enter end time as hh:mm AM/PM");
            return;
        }

        if (compareDate(date)) {
            System.out.println("Availability already set for this date. Please edit the existing entry if needed.");
            return;
        }

        Date formattedNewDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Availability newAvail = new Availability(this.getStaffId(), formattedNewDate, startTime, endTime, status);

        // Write the availability to the Excel file
        uploadAvail(newAvail);
        System.out.println("Availability saved for " + date);
    }

    public void uploadAvail(Availability newAvailability) {
        String path = FilePaths.DOCAVAIL_DATA.getPath();

        try (FileInputStream input = new FileInputStream(path);
             Workbook wkbook = new XSSFWorkbook(input)) {

            // Check if the workbook has at least one sheet; if not, create a new sheet
            Sheet sheet = wkbook.getNumberOfSheets() > 0 ? wkbook.getSheetAt(0) : wkbook.createSheet("Availability");

            int latestRow = sheet.getLastRowNum() + 1;
            Row newRow = sheet.createRow(latestRow);

            // Populate the newRow with the new availability data
            newRow.createCell(0).setCellValue(newAvailability.getDoctorId());

            Cell dateCell = newRow.createCell(1);
            dateCell.setCellValue(newAvailability.getAvailableDate());

            // Create a date format style with "dd/MM/yy"
            CellStyle desiredStyle = wkbook.createCellStyle();
            CreationHelper crHelper = wkbook.getCreationHelper();
            desiredStyle.setDataFormat(crHelper.createDataFormat().getFormat("dd/MM/yy"));
            dateCell.setCellStyle(desiredStyle);  // Apply the custom date format style

            newRow.createCell(2).setCellValue(newAvailability.getStartTime());
            newRow.createCell(3).setCellValue(newAvailability.getEndTime());
            newRow.createCell(4).setCellValue(newAvailability.getStatus().toString());

            try (FileOutputStream newEntry = new FileOutputStream(path)) {
                wkbook.write(newEntry);
                System.out.println("Availability saved to file.");
            }
        } catch (IOException e) {
            System.err.println("Error saving availability to file: " + e.getMessage());
        }
    }

    public void getAvailList() {
        if (!availLoaded) {
            loadAvailData();
            availLoaded = true;
        }
    }

    public void getApptList() {
        if (!apptsLoaded) {
            loadApptData();
            apptsLoaded = true;
        }
    }

    public void respondAppointmentRequest(Appointment appointment, boolean accept) {
        //implement update appointment request accepted use cases
    }

    public List<Appointment> viewUpcomingAppointments() {
        return apptList;
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
