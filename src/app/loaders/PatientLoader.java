package app.loaders;
import interfaces.DataLoader;
import models.entities.*;
import models.enums.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatientLoader implements DataLoader {

    @Override
    public List<Patient> loadData(String filePath) {
        List<Patient> patientList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Starting with 1 skips header
                Row row = sheet.getRow(i);
//                System.out.println("Processing row " + (i + 1));
                if (row != null) {
                    String patientId = row.getCell(0).getStringCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    LocalDate dob = LocalDate.parse(row.getCell(2).getStringCellValue());
                    Gender gender = Gender.valueOf(row.getCell(3).getStringCellValue().toUpperCase());
                    BloodType bloodType = BloodType.fromString(row.getCell(4).getStringCellValue().toUpperCase());
                    String contactInfo = row.getCell(5).getStringCellValue();

                    DataFormatter formatter = new DataFormatter();
                    Cell phoneNumberCell = row.getCell(8);
                    String phoneNumber = formatter.formatCellValue(phoneNumberCell);

                    // Added the below 2 lines
                    // Parse comma-separated diagnosis and treatment into lists
                    List<String> pastDiagnoses = splitCommaStringIntoList(row.getCell(6));
                    List<String> pastTreatments = splitCommaStringIntoList(row.getCell(7));

                    // changed the constructor values, pastdiagnosis and pasttreatments
                    Patient patient = new Patient("H000", patientId, name, dob, gender, contactInfo, bloodType, pastDiagnoses, pastTreatments, phoneNumber);
                    patientList.add(patient);
                }
            }
//            for (Patient patient : patients) {
//                System.out.println("Loaded patient: " + patient.getName());
//            }
            //System.out.println("Patient loading complete!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error parsing gender: " + e.getMessage());
        }
        return patientList;
    }

    private List<String> splitCommaStringIntoList(Cell cell) {
        if (cell == null || cell.getStringCellValue().isEmpty()) {
            return List.of(); // Return empty list if cell is empty or null
        }
        return Arrays.asList(cell.getStringCellValue().split(",\\s*")); // Split by comma and optional whitespace
    }
}
