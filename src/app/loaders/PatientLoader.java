package app.loaders;
import interfaces.DataLoader;
import models.entities.*;
import models.enums.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientLoader implements DataLoader {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void loadData(String filePath) {
        List<Patient> patients = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Starting with 1 skips header
                Row row = sheet.getRow(i);

                if (row != null) {
                    String patientId = row.getCell(0).getStringCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    Date dob = row.getCell(2).getDateCellValue();
                    Gender gender = Gender.valueOf(row.getCell(3).getStringCellValue().toUpperCase());
                    BloodType bloodType = BloodType.valueOf(row.getCell(4).getStringCellValue().toUpperCase());
                    String contactInfo = row.getCell(5).getStringCellValue();

                    Patient patient = new Patient("H000", patientId, name, dob, gender, contactInfo, bloodType);
                    patients.add(patient);
                    System.out.println("Loaded patient: " + patient.getName());
                }
            }
            for (Patient patient : patients) {
                System.out.println("Loaded patient: " + patient.getName());
            }
            System.out.println("Successfully loaded patients!");

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error parsing gender: " + e.getMessage());
        }
    }
}
