package app.loaders;

import interfaces.DataLoader;
import models.entities.Medicine;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The InventoryLoader class implements the DataLoader interface to load inventory data from an Excel file.
 * It reads each row to create Medicine objects, storing information about the medicine's name,
 * quantity, and low stock alert level.
 */
public class InventoryLoader implements DataLoader {

    /**
     * Loads inventory data from the specified Excel file and creates a list of Medicine objects.
     * The first row is expected to be a header and is skipped.
     *
     * @param filePath the path to the Excel file containing inventory data.
     * @return a list of Medicine objects, each representing a row in the Excel file.
     */
    @Override
    public List<Medicine> loadData(String filePath) {
        List<Medicine> medicineList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                Cell nameCell = row.getCell(0);
                Cell quantityCell = row.getCell(1);
                Cell lowStockAlertCell = row.getCell(2);

                String name = nameCell.getStringCellValue();
                int quantity = (int) quantityCell.getNumericCellValue();
                int lowStockAlert = (int) lowStockAlertCell.getNumericCellValue();

                Medicine medicine = new Medicine(name, quantity, lowStockAlert);
                medicineList.add(medicine);
            }

        } catch (Exception e) {
            System.err.println("Error loading authentication data: " + e.getMessage());
        }

        return medicineList;
    }
}
