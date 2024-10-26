package app.loaders;

import models.entities.Appointment;
import models.enums.AppointmentStatus;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentLoader {

    public List<Appointment> loadData(String filePath) {
        List<Appointment> appointmentList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row != null) {
                    String appointmentID = row.getCell(0).getStringCellValue();
                    String patientID = row.getCell(1).getStringCellValue();
                    String doctorID = row.getCell(2).getStringCellValue();
                    AppointmentStatus status = AppointmentStatus.valueOf(row.getCell(3).getStringCellValue());
                    Date appointmentDate = row.getCell(4).getDateCellValue();
                    String appointmentTime = row.getCell(5).getStringCellValue();
                    String outcomeRecord = row.getCell(6).getStringCellValue();


                    Appointment appointment = new Appointment(appointmentID, patientID, doctorID, status, appointmentDate,
                            appointmentTime, outcomeRecord);
                    appointmentList.add(appointment);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error parsing gender: " + e.getMessage());
        }
        return appointmentList;
    }
}
