package models.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;

import app.loaders.*;
import interfaces.DataLoader;
import models.enums.*;
import models.records.*;

import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.stream.Collectors;

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


    /* START OF METHODS TO LOAD DATA */

    public void getPatientList() {
        if (!patientsLoaded) {
            loadPatientList();
            patientsLoaded = true;
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

    private void loadPatientList() {
        String path = FilePaths.PATIENT_DATA.getPath();
        DataLoader loadPatient = new PatientLoader();
        try {
            patientsUnderCare = loadPatient.loadData(path);
        } catch (Exception e) {
            System.err.println("Error loading patient data: " + e.getMessage());
        }
    }

    private void loadApptData() {
        ApptAvailLoader apptLoader = new ApptAvailLoader();
        String path = FilePaths.APPT_DATA.getPath();
        List<Appointment> apptsList = apptLoader.loadData(path);

        for (Appointment oneAppt : apptsList) {
            if (oneAppt.getDoctorId().equals(this.getStaffId())) {
                apptList.add(oneAppt);
            }
        }
    }

    private void loadAvailData() {
        ApptAvailLoader availLoader = new ApptAvailLoader();
        String path = FilePaths.DOCAVAIL_DATA.getPath();
        Map<String, List<Availability>> map = availLoader.loadAvailData(path);

        if (map.containsKey(this.getStaffId())) {
            availList.addAll(map.get(this.getStaffId()));
        }
    }

    /* END OF METHODS TO LOAD DATA */

    /* START OF METHODS TO UPDATE/RESET DATA ONCE NEW RECORD ADDED/USER LOGOUTS */

    public void resetData(String type) {
        if (type.equalsIgnoreCase("appt")) {
            apptsLoaded = false;
        } else if (type.equalsIgnoreCase("avail")) {
            availLoaded = false;
        } else if (type.equalsIgnoreCase("both")){
            apptsLoaded = false;
            availLoaded = false;
        } else {
            System.out.println("Invalid list type specified. Please use 'appt' or 'avail'.");
        }
    }

    public void updateData(String type) {
        if (type.equalsIgnoreCase("appt")) {
            if (apptList != null) {
                apptList.clear();
            }
            resetData("appt");
            getApptList();
        } else if (type.equalsIgnoreCase("avail")) {
            if (availList != null) {
                availList.clear();
            }
            resetData("avail");
            getAvailList();
        } else {
            System.out.println("Invalid list type specified. Please use 'appt' or 'avail'.");
        }
    }

    /* END OF METHODS TO UPDATE/RESET DATA ONCE NEW RECORD ADDED/USER LOGOUTS */

    /* START OF METHODS TO DO WITH PATIENT DATA */

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

    public void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment) {
        patient.addDiagnosis(newDiagnosis);
        patient.addTreatment(newTreatment);
        updatePatientRecords(patient.getPatientID(), newDiagnosis, newTreatment);
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

    /* END OF METHODS TO DO WITH PATIENT DATA */

    /* START OF MANAGEMENT OF DOCTOR APPOINTMENTS/AVAILABILITY METHODS */

    public void viewDoctorSchedule() {
        LocalDate todayDate = LocalDate.now();
        int year = todayDate.getYear();
        int month = todayDate.getMonthValue();
        YearMonth currentYrMth = YearMonth.of(year, month);
        int noOfDays = currentYrMth.lengthOfMonth();

        DateTimeFormatter desiredDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");

        Set<Integer> onlyConfirmedDates = new HashSet<>();
        for (Appointment oneApt : apptList) {
            LocalDate apptDate = oneApt.getAppointmentDate().toInstant()
                    .atZone(TimeZone.getDefault().toZoneId()).toLocalDate();

            if (oneApt.getStatus() == AppointmentStatus.CONFIRMED &&
                    apptDate.getYear() == year && apptDate.getMonthValue() == month) {
                onlyConfirmedDates.add(apptDate.getDayOfMonth());
            }
        }

        System.out.printf("%n%s %d%n", currentYrMth.getMonth(), year);
        System.out.println("Mon Tue Wed Thu Fri Sat Sun");

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int whichDayOfWk = firstDay.getDayOfWeek().getValue();

        for (int i = 1; i < whichDayOfWk; i++) {
            System.out.print("   ");
        }

        for (int day = 1; day <= noOfDays; day++) {
            String crossApptDate = onlyConfirmedDates.contains(day) ? "X" : " ";
            System.out.printf("%2d%s ", day, crossApptDate);

            if ((day + whichDayOfWk - 1) % 7 == 0) {
                System.out.println();
            }
        }
        System.out.println();

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

    public void viewAllAvail() {
        try {
            DateTimeFormatter formatTheDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatTheTime = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("h:mm a")
                    .toFormatter();

            Map<LocalDate, List<Availability>> allAvail = availList.stream()
                    .collect(Collectors.groupingBy(
                            avail -> avail.getAvailableDate().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate(),
                            TreeMap::new,
                            Collectors.toList()
                    ));

            System.out.println("Doctor ID: " + this.getStaffId());
            System.out.println("\n-- All Dates: --");

            for (LocalDate oneDate: allAvail.keySet()) {
                System.out.printf("Date: %s\n", oneDate.format(formatTheDate));

                List<Availability> filterByDate = allAvail.get(oneDate);
                filterByDate.sort(Comparator.comparing(oneAvail -> LocalTime.parse(oneAvail.getStartTime(),
                        formatTheTime)));

                LocalTime startTiming = null;
                LocalTime endTiming = null;
                DoctorAvailability docStatus = null;

                for (Availability oneAvail : filterByDate) {
                    LocalTime slotStartTime = LocalTime.parse(oneAvail.getStartTime(), formatTheTime);
                    LocalTime slotEndTime = LocalTime.parse(oneAvail.getEndTime(), formatTheTime);

                    // If starting a new period or status changes, print the previous period
                    if (docStatus == null || docStatus != oneAvail.getStatus() || !slotStartTime.equals(endTiming)) {
                        if (docStatus != null) {
                            System.out.printf("   - %s from %s to %s%n", docStatus, startTiming.format(formatTheTime), endTiming.format(formatTheTime));
                        }
                        startTiming = slotStartTime;
                        docStatus = oneAvail.getStatus();
                    }

                    endTiming = slotEndTime;
                }

                if (docStatus != null) {
                    System.out.printf("   - %s from %s to %s%n", docStatus, startTiming.format(formatTheTime), endTiming.format(formatTheTime));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private LocalTime checkValidTimeFormat(String checkedTime) {
        DateTimeFormatter formatTheTime = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h[:mm][ ]a")
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();

        try {
            return LocalTime.parse(checkedTime, formatTheTime);
        } catch (Exception e) {
            System.out.println("Please enter a valid time, for example \"10 am/AM etc.\"");
            return null;
        }
    }

    public void addAvail(LocalDate date, String startTime, String endTime,
                         DoctorAvailability status) {

        Date formattedNewDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalTime formatStartTime = checkValidTimeFormat(startTime);
        if (formatStartTime == null) {
            return;
        }

        LocalTime formatEndTime = checkValidTimeFormat(endTime);
        if (formatEndTime == null) {
            return;
        }

        DateTimeFormatter formatTheTime = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mm a")
                .toFormatter();

        List<Availability> currentSlots = availList.stream()
                .filter(avail -> avail.getAvailableDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate().equals(date))
                .collect(Collectors.toList());

        for (Availability oneSlot : currentSlots) {
            LocalTime currentStart = LocalTime.parse(oneSlot.getStartTime(), formatTheTime);
            LocalTime currentEnd = LocalTime.parse(oneSlot.getEndTime(), formatTheTime);

            if (formatStartTime.isBefore(currentEnd) && formatEndTime.isAfter(currentStart)) {
                System.out.println("Time conflict: The new availability overlaps with an existing time slot.");
                return;
            }
        }

        List<Availability> allSlots = separateIntoConsulationSlots(formattedNewDate, formatStartTime, formatEndTime, status);
        for (Availability oneSlot : allSlots) {
            uploadAvail(oneSlot);
        }
        System.out.println("Availability saved for " + date);
        updateData("avail");
    }

    private List<Availability> separateIntoConsulationSlots(Date desiredDate, LocalTime startTiming,
                                                            LocalTime endTiming,
                                                            DoctorAvailability docStatus) {
        List<Availability> allSlots = new ArrayList<>();
        LocalTime currentTiming = startTiming;

        while (currentTiming.isBefore(endTiming)) {
            LocalTime nextHour = currentTiming.plusHours(1);

            if (nextHour.isAfter(endTiming)) {
                nextHour = endTiming;
            }

            Availability newSlot = new Availability(
                    this.getStaffId(),
                    desiredDate,
                    currentTiming.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    nextHour.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    docStatus
            );

            allSlots.add(newSlot);
            currentTiming = nextHour;
        }
        return allSlots;
    }

    private void uploadAvail(Availability newAvailability) {
        String path = FilePaths.DOCAVAIL_DATA.getPath();

        try (FileInputStream input = new FileInputStream(path);
             Workbook wkbook = new XSSFWorkbook(input)) {

            Sheet sheet = wkbook.getNumberOfSheets() > 0 ? wkbook.getSheetAt(0) : wkbook.createSheet("Availability");

            int latestRow = sheet.getLastRowNum() + 1;
            Row newRow = sheet.createRow(latestRow);

            newRow.createCell(0).setCellValue(newAvailability.getDoctorId());

            Cell dateCell = newRow.createCell(1);
            dateCell.setCellValue(newAvailability.getAvailableDate());

            CellStyle desiredStyle = wkbook.createCellStyle();
            CreationHelper crHelper = wkbook.getCreationHelper();
            desiredStyle.setDataFormat(crHelper.createDataFormat().getFormat("dd/MM/yy"));
            dateCell.setCellStyle(desiredStyle);

            newRow.createCell(2).setCellValue(newAvailability.getStartTime());
            newRow.createCell(3).setCellValue(newAvailability.getEndTime());
            newRow.createCell(4).setCellValue(newAvailability.getStatus().toString());

            try (FileOutputStream newEntry = new FileOutputStream(path)) {
                wkbook.write(newEntry);
            }
        } catch (IOException e) {
            System.err.println("Error saving availability to file: " + e.getMessage());
        }
    }

    public void respondAppointmentRequest(Appointment appointment, boolean accept) {
        //implement update appointment request accepted use cases
    }

    public List<Appointment> viewUpcomingAppointments() {
        return apptList;
    }

    /* END OF MANAGEMENT OF DOCTOR APPOINTMENTS/AVAILABILITY METHODS */

    /* START OF RECORDING APPT OUTCOME METHODS */

    public Appointment findApptByID(String apptID) {
        for (Appointment oneAppt : apptList) {
            if (oneAppt.getAppointmentId().equals(apptID)) {
                return oneAppt;
            }
        }
        return null;
    }

    public void recordAppointmentOutcome(Appointment appointment, String serviceType, List<Medicine> prescriptions,
                                         String consultationNotes, String outcomeSts) {
        AppointmentOutcomeRecord outcomeRecord = new AppointmentOutcomeRecord(
                appointment,
                new Date(), // Current date as appointment date
                serviceType,
                prescriptions,
                consultationNotes,
                outcomeSts
        );
        uploadNewApptRecord(outcomeRecord);
    }

    private void uploadNewApptRecord(AppointmentOutcomeRecord record) {
        String outcomePath = FilePaths.APPTOUTCOME.getPath();

        try (FileInputStream input = new FileInputStream(outcomePath);
             Workbook wkBook = new XSSFWorkbook(input);
             FileOutputStream outputStream = new FileOutputStream(outcomePath)) {

            Sheet desiredSheet = wkBook.getSheetAt(0);
            int lastRow = desiredSheet.getLastRowNum();

            Row newRow = desiredSheet.createRow(++lastRow);

            newRow.createCell(0).setCellValue(record.getAppt().getAppointmentId());

            Cell dateCell = newRow.createCell(1);
            dateCell.setCellValue(record.getAppt().getAppointmentDate());

            CellStyle desiredStyle = wkBook.createCellStyle();
            CreationHelper crHelper = wkBook.getCreationHelper();
            desiredStyle.setDataFormat(crHelper.createDataFormat().getFormat("dd/MM/yy"));
            dateCell.setCellStyle(desiredStyle);

            newRow.createCell(2).setCellValue(record.getAppt().getDoctorId());
            newRow.createCell(3).setCellValue(record.getAppt().getPatientId());
            newRow.createCell(4).setCellValue(record.getServiceType());
            newRow.createCell(5).setCellValue(record.getConsultationNotes());

            StringBuilder names = new StringBuilder();
            StringBuilder statusOfDispersion = new StringBuilder();
            for (AppointmentOutcomeRecord.PrescribedMedication oneMedicine : record.getPrescriptions()) {
                if (names.length() > 0) {
                    names.append(", ");
                    statusOfDispersion.append(", ");
                }
                names.append(oneMedicine.getMedicine().getName());
                statusOfDispersion.append(oneMedicine.getStatus());
            }

            newRow.createCell(6).setCellValue(names.toString());
            newRow.createCell(7).setCellValue(statusOfDispersion.toString());
            newRow.createCell(8).setCellValue(record.getApptStatus());

            wkBook.write(outputStream);

        } catch (IOException e) {
            System.err.println("Error writing appointment outcome to file: " + e.getMessage());
        }
    }

    /* END OF RECORDING APPT OUTCOME METHODS */

    // NOT IMPLEMENTED YET
    public void addPatientUnderCare(Patient patient) {
        patientsUnderCare.add(patient);
    }

    @Override
    public Role getRole() {
        return Role.DOCTOR;
    }
}
