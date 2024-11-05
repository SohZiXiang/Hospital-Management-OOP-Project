package models.records;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Manages the reading and writing of replenishment requests to and from an Excel file.
 * This class provides methods to add new requests and retrieve all existing requests.
 */
public class ReplenishmentRequestManager {
    private static final String FILE_PATH = "data/ReplenishmentRequests.xlsx";

    /**
     * Adds a new replenishment request to the Excel file.
     * If the file does not exist, it creates the file and adds headers.
     * It assigns a unique request ID to each new request.
     *
     * @param request The ReplenishmentRequest object containing request details.
     * @return The updated ReplenishmentRequest object with the assigned request ID.
     */
    public ReplenishmentRequest addRequest(ReplenishmentRequest request) {
        try {
            File file = new File(FILE_PATH);
            Workbook workbook;
            Sheet sheet;

            if (!file.exists()) {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Requests");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Request ID");
                headerRow.createCell(1).setCellValue("Hospital ID");
                headerRow.createCell(2).setCellValue("Requester Name");
                headerRow.createCell(3).setCellValue("Medicine Name");
                headerRow.createCell(4).setCellValue("Requested Amount");
                headerRow.createCell(5).setCellValue("Status");
                headerRow.createCell(6).setCellValue("Request Date");
            } else {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheet("Requests");
                fis.close();
            }

            int rowCount = sheet.getLastRowNum();
            int highestRequestId = 0;
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                int requestId = (int) row.getCell(0).getNumericCellValue();
                if (requestId > highestRequestId) {
                    highestRequestId = requestId;
                }
            }

            int newRequestId = highestRequestId + 1;
            request = new ReplenishmentRequest(newRequestId, request.getHospitalId(), request.getRequesterName(), request.getMedicineName(), request.getRequestedAmount());

            Row row = sheet.createRow(++rowCount);
            row.createCell(0).setCellValue(request.getRequestId());
            row.createCell(1).setCellValue(request.getHospitalId());
            row.createCell(2).setCellValue(request.getRequesterName());
            row.createCell(3).setCellValue(request.getMedicineName());
            row.createCell(4).setCellValue(request.getRequestedAmount());
            row.createCell(5).setCellValue(request.getStatus());
            row.createCell(6).setCellValue(request.getRequestDate().toString());

            // Write changes back to file
            FileOutputStream fos = new FileOutputStream(FILE_PATH);
            workbook.write(fos);
            fos.close();
            workbook.close();

            System.out.println("Success. Request ID: " + request.getRequestId());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }


    /**
     * Retrieves all replenishment requests from the Excel file.
     * If the file does not exist, an empty list is returned.
     *
     * @return A list of ReplenishmentRequest objects, representing all requests.
     */
    public List<ReplenishmentRequest> getAllRequests() {
        List<ReplenishmentRequest> requests = new ArrayList<>();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                //System.out.println("Requests file not available, creating request file.");
                return requests;
            }

            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("Requests");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                int requestId = (int) row.getCell(0).getNumericCellValue();
                String hospitalId = row.getCell(1).getStringCellValue();
                String requesterName = row.getCell(2).getStringCellValue();
                String medicineName = row.getCell(3).getStringCellValue();
                int requestedAmount = (int) row.getCell(4).getNumericCellValue();
                String status = row.getCell(5).getStringCellValue();
                String requestDate = row.getCell(6).getStringCellValue();

                ReplenishmentRequest request = new ReplenishmentRequest(requestId, hospitalId, requesterName, medicineName, requestedAmount);
                request.setStatus(status);

                requests.add(request);
            }

            workbook.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return requests;
    }
}
