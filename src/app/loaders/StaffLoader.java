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

/**
 * The StaffLoader class implements the DataLoader interface to load staff data from an Excel file.
 * It processes each row to create Staff objects based on the role specified.
 */
public class StaffLoader implements DataLoader {

    /**
     * Loads staff data from the specified Excel file and creates a list of Staff objects.
     * The first row is expected to be a header and is skipped.
     *
     * @param filePath the path to the Excel file containing staff data.
     * @return a list of Staff objects, each representing a row in the Excel file.
     */
    @Override
        public List<Staff> loadData(String filePath) {
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
                            staff.setPassword("P@ssw0rd123");
                            staffList.add(staff);
                        }
                    }
                }
                //System.out.println("Staff loading complete!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return staffList;
        }

    /**
     * Creates a Staff object based on the specified role.
     *
     * @param role    the role of the staff member (doctor, pharmacist, administrator).
     * @param staffId the unique identifier for the staff member.
     * @param name    the name of the staff member.
     * @param gender  the gender of the staff member.
     * @param age     the age of the staff member.
     * @return a Staff object of the appropriate type based on the role, or null if the role is unknown.
     */
    private Staff createStaff(String role, String staffId, String name, Gender gender, int age) {
        String defaultHospId = "H000";
        switch (role.toLowerCase()) {
            case "doctor":
                return new Doctor(defaultHospId, staffId, name, gender, age);
            case "pharmacist":
                  return new Pharmacist(defaultHospId, staffId, name, gender, age);
            case "administrator":
                return new Administrator(defaultHospId, staffId, name, gender, age);
            default:
                System.out.println("Unknown role: " + role);
                return null;
        }
    }
}
