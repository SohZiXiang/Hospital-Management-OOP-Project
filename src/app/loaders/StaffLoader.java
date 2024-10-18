package app.loaders;
import interfaces.DataLoader;
import models.entities.*;
import models.enums.Gender;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffLoader implements DataLoader {
        @Override
        public void loadData(String filePath) {
            List<Staff> staffList = new ArrayList<>();

            try (FileInputStream fis = new FileInputStream(new File(filePath));
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        String staffId = row.getCell(0).getStringCellValue();
                        String name = row.getCell(1).getStringCellValue();
                        String role = row.getCell(2).getStringCellValue();
//                        System.out.println("check: " + role);
                        Gender gender = Gender.valueOf(row.getCell(3).getStringCellValue().toUpperCase());
                        int age = (int) row.getCell(4).getNumericCellValue();

                        Staff staff = createStaff(role, staffId, name, gender, age);
                        if (staff != null) {
                            staffList.add(staff);
                        }
                    }
                }
                System.out.println("Successfully loaded patients!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    private Staff createStaff(String role, String staffId, String name, Gender gender, int age) {
        String defaultHospId = "H000";
        switch (role.toLowerCase()) {
            case "doctor":
                return new Doctor(defaultHospId, staffId, name, gender, age);
            case "pharmacist":
//                return new Pharmacist(defaultHospId, staffId, name, gender, age);
            case "administrator":
//                return new Administrator(defaultHospId, staffId, name, gender, age);
            default:
                System.out.println("Unknown role: " + role);
                return null;
        }
    }
}
