package app.loaders;
import interfaces.*;
import models.entities.Medicine;
import models.entities.Patient;
import models.entities.Staff;
import models.enums.Gender;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryLoader implements DataLoader {
    @Override
    public List<Medicine> loadData(String filePath) {
        List<Medicine> MedicineList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String name = row.getCell(0).getStringCellValue();
                    int stock = (int) row.getCell(1).getNumericCellValue();
                    int stockAlert = (int) row.getCell(2).getNumericCellValue();

                    Medicine medicine = new Medicine(name, stock, stockAlert);
                    MedicineList.add(medicine);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return MedicineList;
    }
}
