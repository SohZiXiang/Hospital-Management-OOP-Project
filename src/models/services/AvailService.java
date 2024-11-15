package models.services;

import app.loaders.*;

import interfaces.ApptManager;
import interfaces.AvailabilityManager;
import models.enums.*;
import models.entities.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * The AvailService class implements the AvailabilityManager interface and provides comprehensive functionality
 * for managing doctor availability within the hospital management system. This service enables loading, updating,
 * and viewing of doctor availability data, and handles the addition, modification, and deletion of availability slots.
 */
public class AvailService implements AvailabilityManager {
    private List<Availability> availList = new ArrayList<>();
    private boolean availLoaded = false;
    private ApptManager apptManager;

    /**
     * Constructs an AvailService instance with the specified appointment manager.
     *
     * @param apptManager The ApptManager instance to manage appointments.
     */
    public AvailService(ApptManager apptManager) {
        this.apptManager = apptManager;
    }

    /**
     * Sets the appointment manager for this availability service.
     *
     * @param apptManager The ApptManager instance to set.
     */
    public void setApptManager(ApptManager apptManager) {
        this.apptManager = apptManager;
    }

    /* START OF METHODS TO LOAD/UPDATE DATA */

    /**
     * Loads the availability list for a specific doctor.
     *
     * @param doctorId The ID of the doctor whose availability list to load.
     */
    public void getAvailList(String doctorId) {
        if (!availLoaded) {
            loadAvailData(doctorId);
            availLoaded = true;
        }
    }

    /**
     * Loads availability data for the specified doctor ID from file.
     *
     * @param doctorId The ID of the doctor whose availability data is loaded.
     */
    private void loadAvailData(String doctorId) {
        ApptAvailLoader availLoader = new ApptAvailLoader();
        String path = FilePaths.DOCAVAIL_DATA.getPath();
        Map<String, List<Availability>> map = availLoader.loadAvailData(path);

        if (map.containsKey(doctorId)) {
            availList.addAll(map.get(doctorId));
        }
    }

    /**
     * Updates the availability data by reloading the data for a specific doctor.
     *
     * @param docID The ID of the doctor whose availability data needs updating.
     */
    public void updateData(String docID) {
        resetData();
        getAvailList(docID);
    }

    /**
     * Resets the availability data by clearing the availability list.
     */
    public void resetData() {
        if (availList != null) {
            availList.clear();
        }
        availLoaded = false;
    }

    /* END OF METHODS TO LOAD/UPDATE DATA */

    /* START OF MAIN METHODS */

    /**
     * Displays all available slots for a specific doctor for the current month.
     *
     * @param doctorId The ID of the doctor whose availability is displayed.
     */
    public void viewAllAvail(String doctorId) {
        try {
            LocalDateTime rightNow = LocalDateTime.now();
            YearMonth thisMonth = YearMonth.of(rightNow.getYear(), rightNow.getMonth());
            DateTimeFormatter formatTheDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatTheTime = formatTiming();

            Map<LocalDate, List<Availability>> allAvail = availList.stream()
                    .filter(oneAvail -> {
                        LocalDateTime availDateAndTime =
                                LocalDateTime.ofInstant(oneAvail.getAvailableDate().toInstant(),
                                        ZoneId.systemDefault())
                                .with(LocalTime.parse(oneAvail.getStartTime(), formatTheTime));
                        YearMonth availMonth = YearMonth.from(availDateAndTime);
                        return availDateAndTime.isAfter(rightNow) && availMonth.equals(thisMonth);
                    })
                    .collect(Collectors.groupingBy(
                            oneAvail -> oneAvail.getAvailableDate().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate(),
                            TreeMap::new,
                            Collectors.toList()
                    ));

            if (allAvail.isEmpty()) {
                System.out.println("No availability slots for the remaining days of this month.");
                return;
            }

            System.out.println("\n-- All Availability --");

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

    /**
     * Adds new availability slots for a doctor, ensuring no overlapping times.
     *
     * @param doctorId  The ID of the doctor.
     * @param date      The date of the availability.
     * @param startTime The start time of the availability.
     * @param endTime   The end time of the availability.
     * @param status    The availability status (e.g., AVAILABLE or BUSY).
     */
    public void addAvail(String doctorId, LocalDate date, String startTime, String endTime, DoctorAvailability status) {
        Date formattedNewDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Optional<LocalTime> checkStartTime = checkValidTimeFormat(startTime);
        Optional<LocalTime> checkEndTime = checkValidTimeFormat(endTime);

        if (!checkStartTime.isPresent() || !checkEndTime.isPresent()) {
            return;
        }

        LocalTime formatStartTime = checkStartTime.get();
        LocalTime formatEndTime = checkEndTime.get();

        LocalDateTime startDateWithTime = LocalDateTime.of(date, formatStartTime);
        LocalDateTime endDateWithTime = LocalDateTime.of(date, formatEndTime);
        LocalDateTime now = LocalDateTime.now();

        if (startDateWithTime.isBefore(now)) {
            System.out.println("Error: The start time must be in the future.");
            return;
        }
        if (endDateWithTime.isBefore(startDateWithTime)) {
            System.out.println("Error: The end time must be after the start time.");
            return;
        }

        DateTimeFormatter formatTheTime = formatTiming();

        List<Availability> currentSlots = getAllSlots(doctorId, date);

        for (Availability oneSlot : currentSlots) {
            LocalTime currentStart = LocalTime.parse(oneSlot.getStartTime(), formatTheTime);
            LocalTime currentEnd = LocalTime.parse(oneSlot.getEndTime(), formatTheTime);

            if (formatStartTime.isBefore(currentEnd) && formatEndTime.isAfter(currentStart)) {
                System.out.println("Time conflict: The new availability overlaps with an existing time slot.");
                return;
            }
        }

        List<Availability> allSlots = separateIntoConsultationSlots(doctorId, formattedNewDate, formatStartTime, formatEndTime, status);
        for (Availability oneSlot : allSlots) {
            uploadAvail(oneSlot);
        }
        System.out.println("Availability saved for " + date);
        updateData(doctorId);
    }

    /**
     * Splits a longer availability period into hourly consultation slots.
     *
     * @param doctorId     The ID of the doctor.
     * @param desiredDate  The date of the availability.
     * @param startTiming  The start time of the availability.
     * @param endTiming    The end time of the availability.
     * @param docStatus    The availability status (e.g., AVAILABLE or BUSY).
     * @return A list of Availability objects representing hourly slots.
     */
    private List<Availability> separateIntoConsultationSlots(String doctorId, Date desiredDate, LocalTime startTiming,
                                                             LocalTime endTiming, DoctorAvailability docStatus) {
        List<Availability> allSlots = new ArrayList<>();
        LocalTime currentTiming = startTiming;

        while (currentTiming.isBefore(endTiming)) {
            LocalTime nextHour = currentTiming.plusHours(1);

            if (nextHour.isAfter(endTiming)) {
                nextHour = endTiming;
            }

            Availability newSlot = new Availability(
                    doctorId,
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

    /**
     * Uploads a new availability slot to the data file.
     *
     * @param newAvailability The Availability object representing the new slot.
     */
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

    /**
     * Retrieves all availability slots for a specific date and doctor, filtered to show only future times.
     *
     * @param doctorId The ID of the doctor.
     * @param date     The specific date for which slots are retrieved.
     * @return A list of Availability objects that match the criteria.
     */
    public List<Availability> slotsForDate(String doctorId, LocalDate date) {
        return availList.stream()
                .filter(oneAvail -> {
                    LocalDateTime dateTime = LocalDateTime.of(
                            oneAvail.getAvailableDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                            getStartTimeAsLocalTime(oneAvail)
                    );

                    LocalDateTime rightNow = LocalDateTime.now();

                    return oneAvail.getDoctorId().equals(doctorId) &&
                            oneAvail.getAvailableDate().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate().equals(date) &&
                            dateTime.isAfter(rightNow);
                })
                .sorted(Comparator.comparing(this::getStartTimeAsLocalTime))
                .collect(Collectors.toList());
    }

    /**
     * Edits an existing availability slot for a specific doctor, date, and time.
     * Validates the start and end times and ensures they are in the future.
     *
     * @param docID         The ID of the doctor.
     * @param desiredDate   The date of the availability slot to edit.
     * @param oldStart      The original start time of the slot.
     * @param changedStart  The new start time of the slot.
     * @param changedEnd    The new end time of the slot.
     * @param changedStatus The new status of the slot (e.g., AVAILABLE or BUSY).
     */
    public void editOneSlot(String docID, LocalDate desiredDate, String oldStart, String changedStart,
                            String changedEnd,
                            DoctorAvailability changedStatus) {
        boolean isUpdated;
        DateTimeFormatter formatTheTime = formatTiming();
        LocalDateTime rightNow = LocalDateTime.now();

        try {
            List<Availability> matchTheDesiredDate = getAllSlots(docID, desiredDate);

            Availability oneSlot = matchTheDesiredDate.stream()
                    .filter(slot -> timeMatches(oldStart, slot.getStartTime()))
                    .findFirst()
                    .orElse(null);

            if (oneSlot == null) {
                System.out.println("Invalid start time selection.");
                return;
            }

            String oldEnd = oneSlot.getEndTime();

            Optional<LocalTime> checkStartFormat = (changedStart != null) ? checkValidTimeFormat(changedStart) :
                    Optional.of(LocalTime.parse(oneSlot.getStartTime(), formatTheTime));
            Optional<LocalTime> checkEndFormat = (changedEnd != null) ? checkValidTimeFormat(changedEnd) :
                    Optional.of(LocalTime.parse(oneSlot.getEndTime(), formatTheTime));

            if (!checkStartFormat.isPresent() || !checkEndFormat.isPresent()) {
                return;
            }

            LocalTime formatStartTime = checkStartFormat.get();
            LocalTime formatEndTime = checkEndFormat.get();

            if (changedStart != null) {
                LocalDateTime newStartDateTime = LocalDateTime.of(desiredDate, formatStartTime);
                if (!newStartDateTime.isAfter(rightNow)) {
                    System.out.println("Cannot set availability to a past time before now.");
                    return;
                }
                oneSlot.setStartTime(formatStartTime.format(formatTheTime));
            }
            if (changedEnd != null) {
                if (changedStart == null) formatStartTime = LocalTime.parse(oneSlot.getStartTime(), formatTheTime);
                if (!formatEndTime.isAfter(formatStartTime)) {
                    System.out.println("End time must be after start time.");
                    return;
                }
                oneSlot.setEndTime(formatEndTime.format(formatTheTime));
            }

            if (oneSlot.getStatus() == DoctorAvailability.BOOKED && changedStatus == DoctorAvailability.BUSY) {
                Optional<Appointment> oneAppt = apptManager.findAppt(desiredDate, oneSlot.getStartTime());

                if (oneAppt.isPresent()) {
                    oneAppt.get().setStatus(AppointmentStatus.CANCELLED);
                    System.out.println("Appointment on " + desiredDate + " at " + oneSlot.getStartTime() + " has been canceled.");
                    apptManager.cancelAppt(oneAppt.get());
                }
            }

            if (changedStatus != null) {
                oneSlot.setStatus(changedStatus);
            }

            isUpdated = updateSlot(oneSlot, oldStart, oldEnd);

            if (isUpdated) {
                System.out.println("Availability updated successfully.");
            } else {
                System.out.println("Record not found");
            }

        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date or time format. Please ensure you entered the correct format (e.g., hh:mm AM/PM).");
        } catch (NullPointerException e) {
            System.out.println("Error: One of the required values is missing or null. Please check your inputs.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Updates an existing availability slot in the data file.
     *
     * @param newlyEdited The Availability object with updated details.
     * @param oldStart    The original start time of the slot.
     * @param oldEnd      The original end time of the slot.
     * @return true if the slot was updated successfully, false otherwise.
     */
    public boolean updateSlot(Availability newlyEdited, String oldStart, String oldEnd) {
        String availPath = FilePaths.DOCAVAIL_DATA.getPath();
        String availTempPath = availPath + ".tmp";
        boolean updated = false;

        try (FileInputStream input = new FileInputStream(availPath);
             Workbook wkBook = new XSSFWorkbook(input)) {

            Sheet desiredSheet = wkBook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= desiredSheet.getLastRowNum(); i++) {
                Row desiredRow = desiredSheet.getRow(i);
                if (desiredRow != null) {
                    String doctorID = desiredRow.getCell(0).getStringCellValue();
                    Date date = desiredRow.getCell(1).getDateCellValue();
                    String start = formatter.formatCellValue(desiredRow.getCell(2));
                    String end = formatter.formatCellValue(desiredRow.getCell(3));

                    if (doctorID.equals(newlyEdited.getDoctorId()) &&
                            date.equals(newlyEdited.getAvailableDate()) &&
                            timeMatches(oldStart, start) &&
                            timeMatches(oldEnd, end)) {

                        desiredRow.getCell(2).setCellValue(newlyEdited.getStartTime());
                        desiredRow.getCell(3).setCellValue(newlyEdited.getEndTime());
                        desiredRow.getCell(4).setCellValue(newlyEdited.getStatus().toString());
                        updated = true;
                        break;
                    }
                }
            }

            input.close();

            if (updated) {
                try (FileOutputStream output = new FileOutputStream(availTempPath)) {
                    wkBook.write(output);
                }

                java.nio.file.Files.delete(java.nio.file.Paths.get(availPath));
                java.nio.file.Files.move(java.nio.file.Paths.get(availTempPath), java.nio.file.Paths.get(availPath));

                return updated;
            } else {
                return updated;
            }

        } catch (IOException e) {
            System.err.println("Error updating availability data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the availability status of a specific slot when an appointment is modified.
     *
     * @param oneAppt      The appointment that affects the availability slot.
     * @param changedStatus The new status of the slot (e.g., BOOKED or AVAILABLE).
     * @param availFilePath The file path of the availability data.
     */
    public void updateAvailabilitySlot(Appointment oneAppt, DoctorAvailability changedStatus, String availFilePath) {
        try (FileInputStream availInput = new FileInputStream(availFilePath);
             Workbook availWkBook = new XSSFWorkbook(availInput)) {

            Sheet availDesiredSheet = availWkBook.getSheetAt(0);
            boolean foundAvail = false;

            for (Row desiredRow : availDesiredSheet) {
                if (desiredRow.getRowNum() == 0) continue;

                Cell docID = desiredRow.getCell(0);
                Cell date = desiredRow.getCell(1);
                Cell start = desiredRow.getCell(2);

                if (docID != null && date != null && start != null &&
                        docID.getStringCellValue().equals(oneAppt.getDoctorId()) &&
                        date.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                .equals(oneAppt.getAppointmentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) &&
                        timeMatches(start.getStringCellValue(), oneAppt.getAppointmentTime())) {

                    desiredRow.getCell(4).setCellValue(changedStatus.name());
                    foundAvail = true;
                    break;
                }
            }

            if (foundAvail) {
                try (FileOutputStream availOutputStrm = new FileOutputStream(availFilePath)) {
                    availWkBook.write(availOutputStrm);
                }
                System.out.println("Availability status updated successfully.");
            } else {
                System.out.println("No matching availability slot found.");
            }

        } catch (IOException e) {
            System.err.println("Error updating availability in Excel: " + e.getMessage());
        }
    }

    /* END OF MAIN METHODS */


    /* Helper Methods */

    /**
     * Retrieves the start time of an availability slot as a LocalTime object.
     *
     * @param availability The Availability object from which the start time is retrieved.
     * @return The start time of the availability slot.
     */
    private LocalTime getStartTimeAsLocalTime(Availability availability) {
        DateTimeFormatter formatter = formatTiming();
        return LocalTime.parse(availability.getStartTime(), formatter);
    }

    /**
     * Defines the time format for parsing availability times.
     *
     * @return A DateTimeFormatter instance for parsing times in "h:mm a" format.
     */
    private DateTimeFormatter formatTiming() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h[:mm][ ]a")
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();
    }

    /**
     * Validates the provided time string format.
     *
     * @param checkedTime The time string to validate.
     * @return An Optional containing the parsed LocalTime if valid, or empty if invalid.
     */
    private Optional<LocalTime> checkValidTimeFormat(String checkedTime) {
        DateTimeFormatter formatTheTime = formatTiming();

        try {
            return Optional.of(LocalTime.parse(checkedTime, formatTheTime));
        } catch (Exception e) {
            System.out.println("Please enter a valid time, for example \"10 am/AM etc.\"");
            return Optional.empty();
        }
    }

    /**
     * Retrieves all availability slots for a given doctor on a specific date.
     *
     * @param doctorId The ID of the doctor.
     * @param date     The date for which slots are retrieved.
     * @return A list of Availability objects for the specified date.
     */
    private List<Availability> getAllSlots(String doctorId, LocalDate date) {
        return availList.stream()
                .filter(avail -> avail.getDoctorId().equals(doctorId) &&
                        avail.getAvailableDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Checks if two time strings match.
     *
     * @param appTime The appointment time as a string.
     * @param slot    The slot time as a string.
     * @return true if the times match, false otherwise.
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
}
