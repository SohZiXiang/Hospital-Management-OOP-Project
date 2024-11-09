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

public class AvailService implements AvailabilityManager {
    private List<Availability> availList = new ArrayList<>();
    private boolean availLoaded = false;
    private ApptManager apptManager;

    public AvailService(ApptManager apptManager) {
        this.apptManager = apptManager;
    }

    public void setApptManager(ApptManager apptManager) {
        this.apptManager = apptManager;
    }

    /* START OF METHODS TO LOAD/UPDATE DATA */

    public void getAvailList(String doctorId) {
        if (!availLoaded) {
            loadAvailData(doctorId);
            availLoaded = true;
        }
    }

    private void loadAvailData(String doctorId) {
        ApptAvailLoader availLoader = new ApptAvailLoader();
        String path = FilePaths.DOCAVAIL_DATA.getPath();
        Map<String, List<Availability>> map = availLoader.loadAvailData(path);

        if (map.containsKey(doctorId)) {
            availList.addAll(map.get(doctorId));
        }
    }

    // Update availability data
    public void updateData(String doctorId) {
        if (availList != null) {
            availList.clear();
        }
        availLoaded = false;
        getAvailList(doctorId);
    }

    /* END OF METHODS TO LOAD/UPDATE DATA */

    /* START OF MAIN METHODS */

    public void viewAllAvail(String doctorId) {
        try {
            DateTimeFormatter formatTheDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatTheTime = formatTiming();

            Map<LocalDate, List<Availability>> allAvail = availList.stream()
                    .collect(Collectors.groupingBy(
                            avail -> avail.getAvailableDate().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate(),
                            TreeMap::new,
                            Collectors.toList()
                    ));

            System.out.println("Doctor ID: " + doctorId);
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

    public void addAvail(String doctorId, LocalDate date, String startTime, String endTime, DoctorAvailability status) {
        getAvailList(doctorId);

        Date formattedNewDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalTime formatStartTime = checkValidTimeFormat(startTime);
        if (formatStartTime == null) {
            return;
        }

        LocalTime formatEndTime = checkValidTimeFormat(endTime);
        if (formatEndTime == null) {
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

    public List<Availability> slotsForDate(String doctorId, LocalDate date) {
        return availList.stream()
                .filter(avail -> avail.getDoctorId().equals(doctorId) &&
                        avail.getAvailableDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate().equals(date))
                .sorted(Comparator.comparing(this::getStartTimeAsLocalTime))
                .collect(Collectors.toList());
    }

    public void editOneSlot(String docID, LocalDate desiredDate, String oldStart, String changedStart,
                            String changedEnd,
                            DoctorAvailability changedStatus) {
        boolean isUpdated;
        DateTimeFormatter formatTheTime = formatTiming();

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

            LocalTime formatStartTime = (changedStart != null) ? checkValidTimeFormat(changedStart) : null;
            LocalTime formatEndTime = (changedEnd != null) ? checkValidTimeFormat(changedEnd) : null;

            if (formatStartTime != null) {
                oneSlot.setStartTime(formatStartTime.format(formatTheTime));
            }
            if (formatEndTime != null) {
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

            // Close input before writing to avoid conflicts
            input.close();

            if (updated) {
                // Write changes to a temporary file first
                try (FileOutputStream output = new FileOutputStream(availTempPath)) {
                    wkBook.write(output);
                }

                // Rename the temporary file to the original file
                java.nio.file.Files.delete(java.nio.file.Paths.get(availPath));
                java.nio.file.Files.move(java.nio.file.Paths.get(availTempPath), java.nio.file.Paths.get(availPath));

//                System.out.println("Availability updated successfully.");
                return updated;
            } else {
                return updated;
            }

        } catch (IOException e) {
            System.err.println("Error updating availability data: " + e.getMessage());
            return false;
        }
    }


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
    private LocalTime getStartTimeAsLocalTime(Availability availability) {
        DateTimeFormatter formatter = formatTiming();
        return LocalTime.parse(availability.getStartTime(), formatter);
    }

    private DateTimeFormatter formatTiming() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h[:mm][ ]a")
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();
    }

    private LocalTime checkValidTimeFormat(String checkedTime) {
        DateTimeFormatter formatTheTime = formatTiming();

        try {
            return LocalTime.parse(checkedTime, formatTheTime);
        } catch (Exception e) {
            System.out.println("Please enter a valid time, for example \"10 am/AM etc.\"");
            return null;
        }
    }

    private List<Availability> getAllSlots(String doctorId, LocalDate date) {
        return availList.stream()
                .filter(avail -> avail.getDoctorId().equals(doctorId) &&
                        avail.getAvailableDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate().equals(date))
                .collect(Collectors.toList());
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
}
