package app.loaders;
import interfaces.DataLoader;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The AuthLoader class loads authentication data from an Excel file.
 * Each record contains a hospital ID, a salt, and a hashed password.
 */
public class AuthLoader implements DataLoader {
    private String filePath;

    /**
     * Constructs an AuthLoader with the specified file path.
     *
     * @param filePath the path to the Excel file containing authentication data.
     */
    public AuthLoader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads authentication data from the Excel file.
     * Each row is expected to contain a hospital ID, salt, and hashed password.
     *
     * @return a Map where the keys are hospital IDs and the values are arrays containing salt and hashed password.
     */
    public Map<String, String[]> loadData() {
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
