package app.loaders;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuthLoader {
    private String filePath;

    public AuthLoader(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, String[]> loadAuthData() {
        Map<String, String[]> authData = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell hospitalIdCell = row.getCell(0);
                Cell saltCell = row.getCell(1);
                Cell passwordCell = row.getCell(2);

                if (hospitalIdCell != null) {
                    String hospitalId = hospitalIdCell.getStringCellValue();
                    String salt = (saltCell != null)
                            ? saltCell.getStringCellValue()
                            : null;
                    String hashedPassword = (passwordCell != null)
                            ? passwordCell.getStringCellValue()
                            : null;

                    authData.put(hospitalId, new String[]{salt, hashedPassword});
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading authentication data: " + e.getMessage());
        }

        return authData;
    }
}
