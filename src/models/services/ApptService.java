package models.services;

import app.loaders.*;
import interfaces.ApptManager;
import interfaces.AvailabilityManager;
import models.enums.*;
import models.records.*;
import models.entities.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.stream.Collectors;

public class ApptService implements ApptManager {
    private List<Appointment> apptList = new ArrayList<>();
    private List<AppointmentOutcomeRecord> outcomeRecordsList = new ArrayList<>();
    private boolean apptsLoaded = false;
    private boolean outcomeRecordsLoaded = false;
    private final AvailabilityManager availManager;

    public ApptService(AvailabilityManager availManager) {
        this.availManager = availManager;
    }

    /* START OF METHODS TO LOAD/UPDATE DATA */

    public List<Appointment> getApptList(String doctorId) {
        if (!apptsLoaded) {
            loadApptData(doctorId);
            apptsLoaded = true;
        }
        return apptList;
    }

    public void resetData(String type) {
        if (type.equalsIgnoreCase("appt")) {
            apptsLoaded = false;
        } else if (type.equalsIgnoreCase("outcome")) {
            outcomeRecordsLoaded = false;
        } else if (type.equalsIgnoreCase("both")){
            apptsLoaded = false;
            outcomeRecordsLoaded = false;
        } else {
            System.out.println("Invalid list type specified. Please use 'appt' or 'outcome'.");
        }
    }

    public void updateData(String docID, String type) {
        if (type.equalsIgnoreCase("appt")) {
            if (apptList != null) {
                apptList.clear();
            }
            resetData("appt");
            getApptList(docID);
        } else if (type.equalsIgnoreCase("outcome")) {
            if (outcomeRecordsList != null) {
                outcomeRecordsList.clear();
            }
            resetData("avail");
            getOutcomeRecords(docID);
        } else {
            System.out.println("Invalid list type specified. Please use 'appt' or 'outcome'.");
        }
    }

    public List<AppointmentOutcomeRecord> getOutcomeRecords(String doctorId) {
        if (!outcomeRecordsLoaded) {
            loadAppointmentOutcomeRecords(doctorId);
            outcomeRecordsLoaded = true;
        }
        return outcomeRecordsList;
    }

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

    /* END OF METHODS TO LOAD/UPDATE DATA */

    /* START OF DOCTOR APPOINTMENTS METHODS */

    public void viewDoctorSchedule(String doctorId) {
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

            if (oneApt.getStatus() == AppointmentStatus.CONFIRMED &&
                    apptDate.getYear() == year && apptDate.getMonthValue() == month) {
                onlyConfirmedDates.add(apptDate.getDayOfMonth());
            }

            if (oneApt.getStatus() != AppointmentStatus.CANCELLED &&
                    apptDate.getYear() == year && apptDate.getMonthValue() == month) {
                allRelevantAppt.add(oneApt);
            }
        }

        if (onlyConfirmedDates.isEmpty() && allRelevantAppt.isEmpty()) {
            System.out.println();
            System.out.println("No confirmed appointments or appointments to display for this month.");
            return;
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

        if (allRelevantAppt.isEmpty()) {
            System.out.println("No appointment details to display for this month.");
        }
        else {
            System.out.println();
            for (Appointment oneApt : allRelevantAppt) {
                LocalDate apptDate = oneApt.getAppointmentDate().toInstant()
                        .atZone(TimeZone.getDefault().toZoneId()).toLocalDate();

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
    // Change variableName
    public void cancelAppt(Appointment oneAppt) {
        String apptPath = FilePaths.APPT_DATA.getPath();
        String tempPath = apptPath + ".tmp";

        try (FileInputStream input = new FileInputStream(apptPath);
             Workbook wkBook = new XSSFWorkbook(input)) {

            Sheet desiredSheet = wkBook.getSheetAt(0);
            boolean foundAppt = false;

            for (Row row : desiredSheet) {
                if (row.getRowNum() == 0) continue;

                Cell cell = row.getCell(0);
                if (cell != null && cell.getStringCellValue().equals(oneAppt.getAppointmentId())) {
                    row.getCell(3).setCellValue(AppointmentStatus.CANCELLED.name());
                    foundAppt = true;
                    break;
                }
            }

            input.close();

            if (foundAppt) {
                try (FileOutputStream output = new FileOutputStream(tempPath)) {
                    wkBook.write(output);
                }

                File originalFile = new File(apptPath);
                File tempFile = new File(tempPath);

                if (originalFile.delete() && tempFile.renameTo(originalFile)) {
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

    public List<Appointment> apptPendingReviews() {
        LocalDate todayDate = LocalDate.now();
        int currentYear = todayDate.getYear();
        int currentMonth = todayDate.getMonthValue();

        return apptList.stream()
                .filter(appt -> appt.getStatus() == AppointmentStatus.SCHEDULED &&
                        appt.getAppointmentDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate().getYear() == currentYear &&
                        appt.getAppointmentDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() == currentMonth)
                .collect(Collectors.toList());
    }

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
                }
                System.out.println("Appointment status updated successfully.");
            } else {
                System.out.println("Appointment not found.");
            }

        } catch (IOException e) {
            System.err.println("Error updating appointment in Excel: " + e.getMessage());
        }
    }

    /* END OF DOCTOR APPOINTMENTS METHODS */

    /* START OF RECORDING APPT OUTCOME METHODS */

    public Appointment findApptByID(String apptID) {
        for (Appointment oneAppt : apptList) {
            if (oneAppt.getAppointmentId().equals(apptID)) {
                return oneAppt;
            }
        }
        return null;
    }

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

    public void viewAllAppointmentOutcomes(String doctorID) {
        System.out.println("Appointment Outcomes for Doctor ID: " + doctorID);
        for (AppointmentOutcomeRecord outcomeRecord : outcomeRecordsList) {
            Appointment appointment = outcomeRecord.getAppt();

            System.out.printf("Appointment ID: %s\n", appointment.getAppointmentId());
            System.out.printf("Date: %s\n", outcomeRecord.getAppointmentDate());
            System.out.printf("Service Type: %s\n", outcomeRecord.getServiceType());
            System.out.printf("Consultation Notes: %s\n", outcomeRecord.getConsultationNotes());
            System.out.printf("Outcome Status: %s\n", outcomeRecord.getApptStatus());

            System.out.println("Prescribed Medications:");
            for (AppointmentOutcomeRecord.PrescribedMedication prescription : outcomeRecord.getPrescriptions()) {
                System.out.printf(" - Medicine: %s, Quantity: %d, Status: %s\n",
                        prescription.getMedicine().getName(),
                        prescription.getQuantityOfMed(),
                        prescription.getStatus());
            }
            System.out.println("------------------------------------------------------");
        }
    }

    /* END OF RECORDING APPT OUTCOME METHODS */

    /* START OF HELPER METHODS */
    public Optional<Appointment> findAppt(LocalDate desiredDate, String startTime) {
        return apptList.stream()
                .filter(appt -> appt.getAppointmentDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate().equals(desiredDate)
                        && timeMatches(appt.getAppointmentTime(), startTime))
                .findFirst();
    }

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

    private DateTimeFormatter formatTiming() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mm a")
                .toFormatter();
    }
}
