package models.services;

import app.loaders.*;
import interfaces.ApptManager;
import interfaces.AvailabilityManager;
import interfaces.PatientManager;
import models.enums.*;
import models.records.*;
import models.entities.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * The ApptService class implements the ApptManager interface and manages all aspects of appointments for doctors
 * within a hospital management system. This class handles appointment scheduling, cancellations, status updates,
 * outcome recording, and viewing of appointment details for doctors. It also allows viewing of a doctor's schedule
 * and the management of medicine inventory.
 */
public class ApptService implements ApptManager {
    private List<Appointment> apptList = new ArrayList<>();
    private List<AppointmentOutcomeRecord> outcomeRecordsList = new ArrayList<>();
    private List<Medicine> medList;
    private boolean apptsLoaded = false;
    private boolean outcomeRecordsLoaded = false;
    private boolean medLoaded = false;
    private final AvailabilityManager availManager;
    private final PatientManager onePatientManager;

    /**
     * Constructs an instance of ApptService with specified managers.
     *
     * @param availManager       The AvailabilityManager instance for managing availability slots.
     * @param onePatientManager  The PatientManager instance for managing patient information.
     */
    public ApptService(AvailabilityManager availManager, PatientManager onePatientManager) {
        this.availManager = availManager;
        this.onePatientManager = onePatientManager;
    }

    /* START OF METHODS TO LOAD/UPDATE DATA */

    /**
     * Retrieves a list of appointments for the specified doctor.
     *
     * @param doctorId The ID of the doctor whose appointments are requested.
     * @return A list of Appointment objects.
     */
    public List<Appointment> getApptList(String doctorId) {
        if (!apptsLoaded) {
            loadApptData(doctorId);
            apptsLoaded = true;
        }
        return apptList;
    }

    /**
     * Retrieves a list of appointment outcome records for a specified doctor.
     *
     * @param doctorId The ID of the doctor.
     * @return A list of AppointmentOutcomeRecord objects.
     */
    public List<AppointmentOutcomeRecord> getOutcomeRecords(String doctorId) {
        if (!outcomeRecordsLoaded) {
            loadAppointmentOutcomeRecords(doctorId);
            outcomeRecordsLoaded = true;
        }
        return outcomeRecordsList;
    }

    /**
     * Retrieves the inventory of medicines.
     *
     * @return A list of Medicine objects representing current inventory.
     */
    public List<Medicine> getMedData() {
        if (!medLoaded) {
            loadMedData();
            medLoaded = true;
        }
        return medList;
    }

    /**
     * Resets specified data lists based on the type specified.
     *
     * @param type Specifies which list to reset: "appt", "outcome", or "all".
     */
    public void resetData(String type) {
        if (type.equalsIgnoreCase("appt")) {
            if (apptList != null) {
                apptList.clear();
            }
            apptsLoaded = false;
        } else if (type.equalsIgnoreCase("outcome")) {
            if (outcomeRecordsList != null) {
                outcomeRecordsList.clear();
            }
            outcomeRecordsLoaded = false;
        } else if (type.equalsIgnoreCase("all")){
            if (apptList != null && outcomeRecordsList != null && medList != null) {
                apptList.clear();
                outcomeRecordsList.clear();
                medList.clear();
            }
            apptsLoaded = false;
            outcomeRecordsLoaded = false;
            medLoaded = false;
        } else {
            System.out.println("Invalid list type specified. Please use 'appt' or 'outcome'.");
        }
    }

    /**
     * Updates data lists based on the specified type.
     *
     * @param docID The doctor ID for filtering.
     * @param type  The type of data to update: "appt", "outcome", or "both".
     */
    public void updateData(String docID, String type) {
        if (type.equalsIgnoreCase("appt")) {
            resetData("appt");
            getApptList(docID);
        } else if (type.equalsIgnoreCase("outcome")) {
            resetData("avail");
            getOutcomeRecords(docID);
        } else if (type.equalsIgnoreCase("both")){
            resetData("both");
            getApptList(docID);
            getOutcomeRecords(docID);
        } else {
            System.out.println("Invalid list type specified. Please use 'appt' or 'outcome'.");
        }
    }

    /**
     * Loads appointment outcome records from file and filters by doctor ID.
     *
     * @param doctorId The ID of the doctor for filtering records.
     */
    private void loadAppointmentOutcomeRecords(String doctorId) {
        ApptOutcomeLoader outcomeLoader = new ApptOutcomeLoader(apptList);
        String path = FilePaths.APPTOUTCOME.getPath();
        List<AppointmentOutcomeRecord> loadedOutcomeRecords = outcomeLoader.loadData(path);

        for (AppointmentOutcomeRecord outcomeRecord : loadedOutcomeRecords) {
            if (outcomeRecord.getAppt().getDoctorId().equals(doctorId)) {
                outcomeRecordsList.add(outcomeRecord);
            }
        }
    }

    /**
     * Loads appointments data from file and filters by doctor ID.
     *
     * @param doctorId The ID of the doctor for filtering appointments.
     */
    private void loadApptData(String doctorId) {
        ApptAvailLoader apptLoader = new ApptAvailLoader();
        String path = FilePaths.APPT_DATA.getPath();
        List<Appointment> apptsList = apptLoader.loadData(path);

        for (Appointment oneAppt : apptsList) {
            if (oneAppt.getDoctorId().equals(doctorId)) {
                apptList.add(oneAppt);
            }
        }
    }

    /**
     * Loads medicine inventory data from file.
     */
    public void loadMedData() {
        InventoryLoader invLoader = new InventoryLoader();
        String invPath = FilePaths.INV_DATA.getPath();
        medList = invLoader.loadData(invPath);
    }

    /* END OF METHODS TO LOAD/UPDATE DATA */

    /* START OF DOCTOR APPOINTMENTS METHODS */

    /**
     * Displays the doctor's schedule, showing appointments and their statuses.
     *
     * @param docID The doctor ID whose schedule is to be displayed.
     */
    public void viewDoctorSchedule(String docID) {
        LocalDate todayDate = LocalDate.now();
        int year = todayDate.getYear();
        int month = todayDate.getMonthValue();
        YearMonth currentYrMth = YearMonth.of(year, month);
        int noOfDays = currentYrMth.lengthOfMonth();

        DateTimeFormatter desiredDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");

        Set<Integer> onlyConfirmedDates = new HashSet<>();
        List<Appointment> allRelevantAppt = new ArrayList<>();

        for (Appointment oneApt : apptList) {
            LocalDate apptDate = oneApt.getAppointmentDate().toInstant()
                    .atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
            if (apptDate.isAfter(todayDate) && apptDate.getMonthValue() == todayDate.getMonthValue() ) {
                if (oneApt.getStatus() == AppointmentStatus.CONFIRMED) {
                    onlyConfirmedDates.add(apptDate.getDayOfMonth());
                }
                if (oneApt.getStatus() != AppointmentStatus.CANCELLED) {
                    allRelevantAppt.add(oneApt);
                }
            }
        }

        if (onlyConfirmedDates.isEmpty() && allRelevantAppt.isEmpty()) {
            System.out.println();
            System.out.println("No confirmed appointments or appointments to display for this month.");
        }
        else {
            System.out.printf("%n%s %d%n", currentYrMth.getMonth(), year);
            System.out.println("Mon Tue Wed Thu Fri Sat Sun");

            LocalDate firstDay = LocalDate.of(todayDate.getYear(), todayDate.getMonth(), 1);
            int whichDayOfWk = firstDay.getDayOfWeek().getValue() % 7;

            for (int i = 0; i < whichDayOfWk; i++) {
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

            if (allRelevantAppt.isEmpty()) {
                System.out.println("No appointment details to display for this month.");
            }
            else {
                System.out.println();
                for (Appointment oneApt : allRelevantAppt) {
                    LocalDate apptDate = oneApt.getAppointmentDate().toInstant()
                            .atZone(TimeZone.getDefault().toZoneId()).toLocalDate();

                    String date = apptDate.format(desiredDateFormat);
                    System.out.printf("Date: %s | Time: %s | Status: %s | Patient ID: %s%n",
                            date,
                            oneApt.getAppointmentTime(),
                            oneApt.getStatus(),
                            oneApt.getPatientId());
                }
            }
        }
        availManager.viewAllAvail(docID);
    }

    /**
     * Displays a list of upcoming confirmed appointments.
     */
    public void viewUpcomingAppt() {
        LocalDate todayDate = LocalDate.now();
        DateTimeFormatter desiredDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");

        List<Appointment> onlyConfirmedAppt = apptList.stream()
                .filter(oneAppt -> oneAppt.getStatus() == AppointmentStatus.CONFIRMED &&
                        toLocalDateFormat(oneAppt.getAppointmentDate()).isAfter(todayDate) &&
                        toLocalDateFormat(oneAppt.getAppointmentDate()).getMonthValue() == todayDate.getMonthValue())
                .collect(Collectors.toList());

        if (onlyConfirmedAppt.isEmpty()) {
            System.out.println("No upcoming confirmed appointments.");
            return;
        }

        System.out.println();
        System.out.println("--- Upcoming Confirmed Appointments ---\n");

        for (Appointment oneApt : onlyConfirmedAppt) {
            LocalDate apptDate = oneApt.getAppointmentDate().toInstant()
                    .atZone(TimeZone.getDefault().toZoneId()).toLocalDate();

            String date = apptDate.format(desiredDateFormat);
            System.out.printf("ID: %s | Date: %s | Time: %s",
                    oneApt.getAppointmentId(),
                    date,
                    oneApt.getAppointmentTime());
            System.out.println();

            onePatientManager.filterPatients(oneApt.getPatientId());
        }
    }

    /**
     * Cancels a specific appointment by updating its status to CANCELLED.
     *
     * @param oneAppt The appointment to cancel.
     */
    public void cancelAppt(Appointment oneAppt) {
        String aptPath = FilePaths.APPT_DATA.getPath();
        String tempFilePath = aptPath + ".tmp";

        try (FileInputStream inputStm = new FileInputStream(aptPath);
             Workbook wkBk = new XSSFWorkbook(inputStm)) {

            Sheet requestedSheet = wkBk.getSheetAt(0);
            boolean isCorrectAppt = false;

            for (Row oneRow : requestedSheet) {
                if (oneRow.getRowNum() == 0) continue;

                Cell desiredCell = oneRow.getCell(0);
                if (desiredCell != null && desiredCell.getStringCellValue().equals(oneAppt.getAppointmentId())) {
                    oneRow.getCell(3).setCellValue(AppointmentStatus.CANCELLED.name());
                    isCorrectAppt = true;
                    break;
                }
            }

            inputStm.close();

            if (isCorrectAppt) {
                try (FileOutputStream outputStrm = new FileOutputStream(tempFilePath)) {
                    wkBk.write(outputStrm);
                }

                File originFile = new File(aptPath);
                File temFile = new File(tempFilePath);

                if (originFile.delete() && temFile.renameTo(originFile)) {
                    System.out.println("Appointment status updated successfully.");
                } else {
                    System.err.println("Failed to update the appointment data.");
                }
            } else {
                System.out.println("Appointment not found.");
            }

        } catch (IOException e) {
            System.err.println("Error updating appointment in Excel: " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of appointments pending review for the current month.
     *
     * @return A list of Appointment objects pending review.
     */
    public List<Appointment> apptPendingReviews() {
        LocalDate todayDate = LocalDate.now();
        int currentYear = todayDate.getYear();
        int currentMonth = todayDate.getMonthValue();

        return apptList.stream()
                .filter(appt -> appt.getStatus() == AppointmentStatus.SCHEDULED &&
                        appt.getAppointmentDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate().isAfter(todayDate) &&
                        appt.getAppointmentDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate().getYear() == currentYear &&
                        appt.getAppointmentDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() == currentMonth)
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of a specific appointment.
     *
     * @param oneAppt      The appointment to update.
     * @param changedStatus The new status to set.
     */
    public void updateAppointmentStatus(Appointment oneAppt, AppointmentStatus changedStatus) {
        String apptFilePath = FilePaths.APPT_DATA.getPath();
        String availFilePath = FilePaths.DOCAVAIL_DATA.getPath();

        try (FileInputStream apptInputStrm = new FileInputStream(apptFilePath);
             Workbook apptWkbook = new XSSFWorkbook(apptInputStrm)) {

            Sheet apptDesiredSheet = apptWkbook.getSheetAt(0);
            boolean foundAppt = false;

            for (Row desiredRow : apptDesiredSheet) {
                if (desiredRow.getRowNum() == 0) continue;

                Cell apptIdCell = desiredRow.getCell(0);
                if (apptIdCell != null && apptIdCell.getStringCellValue().equals(oneAppt.getAppointmentId())) {
                    desiredRow.getCell(3).setCellValue(changedStatus.name());
                    foundAppt = true;
                    break;
                }
            }

            if (foundAppt) {
                try (FileOutputStream apptOutputStrm = new FileOutputStream(apptFilePath)) {
                    apptWkbook.write(apptOutputStrm);
                }

                if (changedStatus == AppointmentStatus.CONFIRMED) {
                    availManager.updateAvailabilitySlot(oneAppt, DoctorAvailability.BOOKED, availFilePath);
                    System.out.println("Appointment status updated successfully.");
                }
            } else {
                System.out.println("Appointment not found.");
            }

        } catch (IOException e) {
            System.err.println("Error updating appointment in Excel: " + e.getMessage());
        }
    }

    /* END OF DOCTOR APPOINTMENTS METHODS */

    /* START OF RECORDING APPT OUTCOME METHODS */

    /**
     * Finds an appointment by its ID.
     *
     * @param apptID The ID of the appointment.
     * @return An Optional containing the appointment if found, otherwise empty.
     */
    public Appointment findApptByID(String apptID) {
        for (Appointment oneAppt : apptList) {
            if (oneAppt.getAppointmentId().equals(apptID)) {
                return oneAppt;
            }
        }
        return null;
    }

    /**
     * Displays the entire medicine inventory in a formatted table.
     * If the inventory list is empty, it displays a message indicating that no stock is available.
     */
    public void viewMedsStock() {
        System.out.println("\n--- Medicine Inventory ---");

        if (medList.isEmpty()) {
            System.out.println("No medicine stock available.");
            return;
        }

        String headerFormat = "| %-25s | %-10s |%n";
        String rowFormat = "| %-25s | %-10d |%n";

        System.out.format("+---------------------------+------------+%n");
        System.out.format(headerFormat, "Medicine Name", "Stock");
        System.out.format("+---------------------------+------------+%n");

        for (Medicine med : medList) {
            System.out.format(rowFormat, med.getName(), med.getQuantity());
        }

        System.out.format("+---------------------------+------------+%n");
    }

    /**
     * Records the outcome of an appointment, including prescribed medications and consultation notes.
     *
     * @param appointment        The appointment being recorded.
     * @param serviceType        The type of service provided.
     * @param prescriptions      List of prescribed medications.
     * @param consultationNotes  Notes from the consultation.
     * @param outcomeSts         The status of the outcome.
     */
    public void recordAppointmentOutcome(Appointment appointment, String serviceType,
                                         List<AppointmentOutcomeRecord.PrescribedMedication> prescriptions,
                                         String consultationNotes, String outcomeSts) {
        AppointmentOutcomeRecord outcomeRecord = new AppointmentOutcomeRecord(
                appointment,
                new Date(),
                serviceType,
                prescriptions,
                consultationNotes,
                outcomeSts
        );
        uploadNewApptRecord(outcomeRecord);
    }

    /**
     * Uploads a new appointment outcome record to the outcome data file.
     * The method writes the details of the outcome record, including prescribed medications, to an Excel file.
     * If the appointment status is "Completed," it updates the outcome record in the Appointment file.
     *
     * @param record The AppointmentOutcomeRecord to be uploaded.
     */
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
            StringBuilder quantities = new StringBuilder();

            for (AppointmentOutcomeRecord.PrescribedMedication oneMedicine : record.getPrescriptions()) {
                if (names.length() > 0) {
                    names.append(", ");
                    statusOfDispersion.append(", ");
                    quantities.append(", ");
                }
                names.append(oneMedicine.getMedicine().getName());
                statusOfDispersion.append(oneMedicine.getStatus());
                quantities.append(oneMedicine.getQuantityOfMed());
            }

            newRow.createCell(6).setCellValue(names.toString());
            newRow.createCell(7).setCellValue(statusOfDispersion.toString());
            newRow.createCell(8).setCellValue(quantities.toString().isEmpty() ? "0" : quantities.toString());
            newRow.createCell(9).setCellValue(record.getApptStatus());

            wkBook.write(outputStream);

            if ("Completed".equalsIgnoreCase(record.getApptStatus())) {
                String summary = record.getServiceType() + ": " + record.getConsultationNotes();
                updateApptOutcomeRecord(record.getAppt().getAppointmentId(), summary, AppointmentStatus.COMPLETED);
            }

        } catch (IOException e) {
            System.err.println("Error writing appointment outcome to file: " + e.getMessage());
        }
    }

    /**
     * Updates an appointment outcome record in the Appointment Excel file.
     * This method is triggered when the appointment's outcome status is set to "Completed."
     *
     * @param apptID  The ID of the appointment to update.
     * @param outcome The outcome record details to add to the appointment.
     * @param apptSts The updated appointment status.
     */
    public void updateApptOutcomeRecord(String apptID, String outcome, AppointmentStatus apptSts) {
        String apptDataPath = FilePaths.APPT_DATA.getPath();
        try (FileInputStream input = new FileInputStream(apptDataPath);
             Workbook wkBook = new XSSFWorkbook(input)) {

            Sheet desiredSheet = wkBook.getSheetAt(0);
            boolean foundRecord = false;

            for (Row eachRow : desiredSheet) {
                Cell apptIDCell = eachRow.getCell(0);
                if (apptIDCell != null && apptIDCell.getStringCellValue().equals(apptID)) {
                    Cell apptStatusCell = eachRow.getCell(3);
                    if (apptStatusCell == null) {
                        apptStatusCell = eachRow.createCell(3);
                    }
                    apptStatusCell.setCellValue(apptSts.name());

                    Cell outcomeRecordCell = eachRow.getCell(6);
                    if (outcomeRecordCell == null) {
                        outcomeRecordCell = eachRow.createCell(6);
                    }
                    outcomeRecordCell.setCellValue(outcome);
                    foundRecord = true;
                    break;
                }
            }

            if (!foundRecord) {
                System.out.println("Appointment ID not found in the Appointment file.");
            }

            input.close();

            try (FileOutputStream output = new FileOutputStream(apptDataPath)) {
                wkBook.write(output);
            }

        } catch (IOException e) {
            System.err.println("Error updating outcome record in Appointment file: " + e.getMessage());
        }
    }

    /**
     * Displays all appointment outcomes for a specific doctor in a formatted output.
     *
     * @param doctorID The ID of the doctor whose appointment outcomes are to be displayed.
     */
    public void viewAllAppointmentOutcomes(String doctorID) {
        System.out.println("\n--- Appointment Outcomes for Doctor ID: ---" + doctorID);
        for (AppointmentOutcomeRecord oneRecord : outcomeRecordsList) {
            Appointment appt = oneRecord.getAppt();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            System.out.printf("Appointment ID: %s\n", appt.getAppointmentId());
            System.out.printf("Date: %s\n", formatter.format(oneRecord.getAppointmentDate()));
            System.out.printf("Service Type: %s\n", oneRecord.getServiceType());
            System.out.printf("Consultation Notes: %s\n", oneRecord.getConsultationNotes());
            System.out.printf("Outcome Status: %s\n", oneRecord.getApptStatus());

            System.out.println("Prescribed Medications:");
            for (AppointmentOutcomeRecord.PrescribedMedication onePrescribed : oneRecord.getPrescriptions()) {
                System.out.printf(" - Medicine: %s, Quantity: %d, Status: %s\n",
                        onePrescribed.getMedicine().getName(),
                        onePrescribed.getQuantityOfMed(),
                        onePrescribed.getStatus());
            }
            System.out.println("------------------------------------------------------");
        }
    }

    /* END OF RECORDING APPT OUTCOME METHODS */

    /* START OF HELPER METHODS */
    /**
     * Finds an appointment by a specific date and start time.
     *
     * @param desiredDate The date of the appointment.
     * @param startTime   The start time of the appointment as a string.
     * @return An Optional containing the appointment if found, otherwise empty.
     */
    public Optional<Appointment> findAppt(LocalDate desiredDate, String startTime) {
        return apptList.stream()
                .filter(appt -> appt.getAppointmentDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate().equals(desiredDate)
                        && timeMatches(appt.getAppointmentTime(), startTime))
                .findFirst();
    }

    /**
     * Checks if two time strings match.
     *
     * @param appTime The appointment time as a string.
     * @param slot    The slot time as a string.
     * @return True if the times match, false otherwise.
     */
    private boolean timeMatches(String appTime, String slot) {
        DateTimeFormatter formatTheTime = formatTiming();
        try {
            LocalTime formattedApptTime = LocalTime.parse(appTime, formatTheTime);
            LocalTime formattedSlotTime = LocalTime.parse(slot, formatTheTime);
            return formattedApptTime.equals(formattedSlotTime);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing time for comparison: " + appTime + " or " + slot);
            return false;
        }
    }

    /**
     * Creates and returns a DateTimeFormatter for parsing and formatting times in "h:mm a" format.
     *
     * @return A DateTimeFormatter configured for 12-hour format with AM/PM.
     */
    private DateTimeFormatter formatTiming() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mm a")
                .toFormatter();
    }

    /**
     * Converts a Date to LocalDate.
     *
     * @param date The Date to convert.
     * @return The LocalDate representation of the Date.
     */
    private LocalDate toLocalDateFormat(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
