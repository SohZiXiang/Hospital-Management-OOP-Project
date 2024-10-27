package app.loaders;

import interfaces.DataLoader;
import models.entities.Medicine;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class InventoryLoader implements DataLoader {

    @Override
    public List<Medicine> loadData(String filePath) {
        List<Medicine> medicineList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            // Assuming the first sheet contains the medicine data
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate through each row (starting after the header row)
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Skip the header row
                    continue;
                }

                // Reading cell values from the row
                Cell nameCell = row.getCell(0);
                Cell quantityCell = row.getCell(1);
                Cell lowStockAlertCell = row.getCell(2);

                String name = nameCell.getStringCellValue();
                int quantity = (int) quantityCell.getNumericCellValue();
                int lowStockAlert = (int) lowStockAlertCell.getNumericCellValue();

                // Create Medicine object and add to list
                Medicine medicine = new Medicine(name, quantity, lowStockAlert);
                medicineList.add(medicine);
            }

        } catch (Exception e) {
            System.err.println("Error loading authentication data: " + e.getMessage());
        }

        return medicineList;
    }
}
