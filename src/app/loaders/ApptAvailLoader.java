package app.loaders;

import interfaces.DataLoader;
import models.entities.*;
import models.enums.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * The ApptAvailLoader class implements the DataLoader interface to load appointment and availability data
 * from an Excel file.
 */
public class ApptAvailLoader implements DataLoader {

    /**
     * Loads appointment data from the specified Excel file and creates a list of Appointment objects.
     * The first row is expected to be a header and is skipped.
     *
     * @param filePath the path to the Excel file containing appointment data.
     * @return a list of Appointment objects, each representing a row in the Excel file.
     */
    @Override
    public List<Appointment> loadData(String filePath) {
        List<Appointment> apts = new ArrayList<>();

        try (FileInputStream input = new FileInputStream(filePath);
             Workbook wkbook = new XSSFWorkbook(input)) {

            Sheet desiredSheet = wkbook.getSheetAt(0);
            DataFormatter desiredDateFormat = new DataFormatter();

            for (int i = 1; i <= desiredSheet.getLastRowNum(); i++) { // Skip header row
                Row newRow = desiredSheet.getRow(i);
                if (newRow != null) {
                    // Parse each field from the Appointment_List sheet
                    String apptID = newRow.getCell(0).getStringCellValue();
                    String patientID = newRow.getCell(1).getStringCellValue();
                    String doctorID = newRow.getCell(2).getStringCellValue();

                    String status = newRow.getCell(3).getStringCellValue();
                    AppointmentStatus aptStatus = AppointmentStatus.valueOf(status.toUpperCase());

                    Date apptDate = newRow.getCell(4).getDateCellValue();
                    String apptTime = desiredDateFormat.formatCellValue(newRow.getCell(5));

                    String outcomeRecord = newRow.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    Appointment newAppt = new Appointment(apptID, patientID, doctorID, apptDate, apptTime, outcomeRecord);
                    newAppt.setStatus(aptStatus);

                    apts.add(newAppt);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading appointment data: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing status or data format: " + e.getMessage());
        }

        return apts;
    }

    /**
     * Loads availability data from the specified Excel file and creates a map of doctor IDs to their available slots.
     * The first row is expected to be a header and is skipped.
     *
     * @param path the path to the Excel file containing availability data.
     * @return a map where the key is the doctor ID and the value is a list of Availability objects.
     */
    public Map<String, List<Availability>> loadAvailData(String path) {
        Map<String, List<Availability>> availList = new HashMap<>();

        try (FileInputStream input = new FileInputStream(path);
            Workbook wkBook = new XSSFWorkbook(input)) {
            DataFormatter desiredDateFormat = new DataFormatter();
            Sheet desiredSheet = wkBook.getSheetAt(0);
            for (int i = 1; i <= desiredSheet.getLastRowNum(); i++) {
                Row newRow = desiredSheet.getRow(i);
                if (newRow != null) {
                    String doctorID = newRow.getCell(0).getStringCellValue();
                    Date dateAvail = newRow.getCell(1).getDateCellValue();
                    String start = desiredDateFormat.formatCellValue(newRow.getCell(2));
                    String end = desiredDateFormat.formatCellValue(newRow.getCell(3));

                    String currentStatus = newRow.getCell(4).getStringCellValue().toUpperCase();
                    DoctorAvailability doctorAvail;
                    try {
                        doctorAvail = DoctorAvailability.valueOf(currentStatus);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid availability status in file: " + currentStatus);
                        continue;
                    }
                    Availability newAvailability = new Availability(doctorID, dateAvail, start, end, doctorAvail);

                    availList.computeIfAbsent(doctorID, k -> new ArrayList<>()).add(newAvailability);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading availability data: " + e.getMessage());
        }

        return availList;
    }
}
