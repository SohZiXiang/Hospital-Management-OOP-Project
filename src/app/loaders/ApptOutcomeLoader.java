package app.loaders;

import interfaces.DataLoader;
import models.entities.*;
import models.enums.*;
import models.records.AppointmentOutcomeRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ApptOutcomeLoader implements DataLoader{
    private List<AppointmentOutcomeRecord> apptOutcomes;
    private final Map<String, Appointment> apptList;

    public ApptOutcomeLoader(List<Appointment> appointments) {
        this.apptList = new HashMap<>();
        for (Appointment oneAppt : appointments) {
            this.apptList.put(oneAppt.getAppointmentId(), oneAppt);
        }
    }
    @Override
    public List<AppointmentOutcomeRecord> loadData(String filePath) {
        apptOutcomes = new ArrayList<>();
        try (FileInputStream input = new FileInputStream(filePath);
            Workbook wkBook = new XSSFWorkbook(input)) {

            Sheet theSheet = wkBook.getSheetAt(0);

            for (int i = 1; i <= theSheet.getLastRowNum(); i++) { // Skip header row
                Row eachRow = theSheet.getRow(i);
                if (eachRow != null) {
                    String apptID = eachRow.getCell(0).getStringCellValue();

                    Appointment oneAppt = apptList.get(apptID);
                    if (oneAppt == null) {
                        continue;
                    }
                    Date apptDate = eachRow.getCell(1).getDateCellValue();
//                    String doctorID = eachRow.getCell(2).getStringCellValue();
//                    String patientID = eachRow.getCell(3).getStringCellValue();
                    String svType = eachRow.getCell(4).getStringCellValue();
                    String notes = eachRow.getCell(5).getStringCellValue();

                    List<String> medicineName = splitCommaStringIntoList(eachRow.getCell(6));
                    List<String> medicationStatus = splitCommaStringIntoList(eachRow.getCell(7));
                    List<Integer> quantityClm = splitQuantityClm(eachRow.getCell(8));

                    List<AppointmentOutcomeRecord.PrescribedMedication> prescribedMedications = new ArrayList<>();
                    for (int j = 0; j < medicineName.size(); j++) {
                        String oneMedicineName = medicineName.get(j);
                        String oneStatus = (j < medicationStatus.size()) ? medicationStatus.get(j) : "NIL";
                        PrescriptionStatus status = PrescriptionStatus.valueOf(oneStatus.toUpperCase());
                        int quantityOfMed = (j < quantityClm.size()) ? quantityClm.get(j): 0;

                        Medicine oneMedicine = new Medicine(oneMedicineName);
                        AppointmentOutcomeRecord.PrescribedMedication prescribed =
                                new AppointmentOutcomeRecord.PrescribedMedication(oneMedicine, status, quantityOfMed);
                        prescribedMedications.add(prescribed);
                    }

                    String outcomeOfAppt = eachRow.getCell(9).getStringCellValue();

                    AppointmentOutcomeRecord outcomeRecord = new AppointmentOutcomeRecord(oneAppt,
                            apptDate, svType, prescribedMedications, notes, outcomeOfAppt);

                    apptOutcomes.add(outcomeRecord);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading appointment outcome data: " + e.getMessage());
        }

        return apptOutcomes;
    }

    private List<String> splitCommaStringIntoList(Cell cell) {
        if (cell == null || cell.getStringCellValue().isEmpty()) {
            return List.of(); // Return empty list if cell is empty or null
        }
        return Arrays.asList(cell.getStringCellValue().split(",\\s*")); // Split by comma and optional whitespace
    }

    public static List<Integer> splitQuantityClm(Cell cell) {
        List<Integer> quantityList = new ArrayList<>();
        if (cell == null) {
            return quantityList; // Return an empty list if the cell is null
        }

        // Check if the cell is numeric
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            // Directly add the numeric value to the list as an integer
            quantityList.add((int) cell.getNumericCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            String[] strValue = cell.getStringCellValue().split(",\\s*");
            for (String oneValue : strValue) {
                try {
                    quantityList.add(Integer.parseInt(oneValue.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid integer format in quantity column: " + oneValue);
                }
            }
        }

        return quantityList;
    }
}
