package app.loaders;

import interfaces.DataLoader;
import models.entities.*;
import models.enums.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppointmentLoader implements DataLoader {
    // Need to change the variableNames
    @Override
    public List<Appointment> loadData(String filePath) {
        List<Appointment> appointments = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row != null) {
                    // Parse each field from the Appointment_List sheet
                    String appointmentId = row.getCell(0).getStringCellValue();
                    String patientId = row.getCell(1).getStringCellValue();
                    String doctorId = row.getCell(2).getStringCellValue();

                    String statusStr = row.getCell(3).getStringCellValue();
                    AppointmentStatus status = AppointmentStatus.valueOf(statusStr.toUpperCase());

                    Date appointmentDate = row.getCell(4).getDateCellValue();
                    String appointmentTime = formatter.formatCellValue(row.getCell(5));

                    // Create a new Appointment object
                    Appointment appointment = new Appointment(appointmentId, patientId, doctorId, appointmentDate, appointmentTime);
                    appointment.setStatus(status);

                    // Add to appointments list
                    appointments.add(appointment);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading appointment data: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing status or data format: " + e.getMessage());
        }

        return appointments;
    }

    // Additional method to load doctor availability from Availability_List.xlsx
    public Map<String, List<Availability>> loadAvailability(String availabilityFilePath) {
        Map<String, List<Availability>> availabilityMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(availabilityFilePath);
            Workbook workbook = new XSSFWorkbook(fis)) {
            DataFormatter formatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row != null) {
                    String doctorId = row.getCell(0).getStringCellValue();
                    Date availableDate = row.getCell(1).getDateCellValue();
                    String startTime = formatter.formatCellValue(row.getCell(2));
                    String endTime = formatter.formatCellValue(row.getCell(3));

                    String statusStr = row.getCell(4).getStringCellValue().toUpperCase();
                    DoctorAvailability status;
                    try {
                        status = DoctorAvailability.valueOf(statusStr); // Converts to enum
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid availability status in file: " + statusStr);
                        continue; // Skip this row if status is invalid
                    }
                    // Create Availability object for each row
                    Availability availability = new Availability(doctorId, availableDate, startTime, endTime, status);

                    // Add to map based on doctorId
                    availabilityMap.computeIfAbsent(doctorId, k -> new ArrayList<>()).add(availability);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading availability data: " + e.getMessage());
        }

        return availabilityMap;
    }
}
