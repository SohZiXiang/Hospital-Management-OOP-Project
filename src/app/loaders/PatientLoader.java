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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The PatientLoader class implements the DataLoader interface to load patient data from an Excel file.
 * It processes each row to create Patient objects, capturing details such as patient ID, name, date of birth,
 * gender, blood type, contact information, past diagnoses, and treatments.
 */
public class PatientLoader implements DataLoader {

    /**
     * Loads patient data from the specified Excel file and creates a list of Patient objects.
     * The first row is expected to be a header and is skipped.
     *
     * @param filePath the path to the Excel file containing patient data.
     * @return a list of Patient objects, each representing a row in the Excel file.
     */
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
                    List<String> pastDiagnoses = splitCommaSeparatedString(row.getCell(6));
                    List<String> pastTreatments = splitCommaSeparatedString(row.getCell(7));

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

    /**
     * Helper method to split a comma-separated string from a cell into a list.
     *
     * @param cell the cell containing a comma-separated string.
     * @return a list of strings parsed from the cell, or an empty list if the cell is null or empty.
     */
    private List<String> splitCommaSeparatedString(Cell cell) {
        if (cell == null || cell.getStringCellValue().isEmpty()) {
            return List.of(); // Return empty list if cell is empty or null
        }
        return Arrays.asList(cell.getStringCellValue().split(",\\s*")); // Split by comma and optional whitespace
    }
}
