package models.managers;

import models.entities.Administrator;
import models.entities.Medicine;
import models.enums.FilePaths;
import models.records.ReplenishmentRequest;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages replenishment requests, including the ability to approve or reject requests,
 * update stock levels for medicines, and modify request statuses within an Excel file.
 */

public class ReplenishReqManager {
    InventoryManager inventoryManager = new InventoryManager();
    String repReqPath = FilePaths.REPLENISH_REQ_DATA.getPath();
    List<ReplenishmentRequest> repReqList = new ArrayList<>();

    /**
     * Approves a replenishment request, updates the stock of the requested medicine,
     * and changes the request status to "approved".
     *
     * @param index       The index of the request in the list of requests.
     * @param currentUser The Administrator currently approving the request.
     * @param request     The ReplenishmentRequest to be approved.
     */
    public void approveRequest(int index, Administrator currentUser, ReplenishmentRequest request) {
        Medicine medData = inventoryManager.findMedicine(request.getMedicineName());
        if (medData != null) {
            int addedStock = request.getRequestedAmount();
            int initialStock = medData.getStock();
            int newStock = addedStock + initialStock;

            inventoryManager.updateStock(medData, newStock, currentUser);

            updateRequestStatus(request, "approved");
            request.setStatus("approved");
            System.out.println("Request for " + request.getRequestedAmount() + " of " + request.getMedicineName() + " approved.");
        } else {
            System.out.println("Medicine not found for request: " + request.getMedicineName());
        }
    }

    /**
     * Rejects a replenishment request and updates the request status to "rejected".
     *
     * @param index   The index of the request in the list of requests.
     * @param request The ReplenishmentRequest to be rejected.
     */
    public void rejectRequest(int index, ReplenishmentRequest request) {
        updateRequestStatus(request, "rejected");
        request.setStatus("rejected");

        System.out.println("Request for " + request.getRequestedAmount() + " of " + request.getMedicineName() + " rejected.");
    }

    /**
     * Updates the status of a replenishment request in the Excel file.
     *
     * @param request The ReplenishmentRequest to update.
     * @param status  The new status to set for the request (e.g., "approved" or "rejected").
     */
    public void updateRequestStatus(ReplenishmentRequest request, String status) {
        try (FileInputStream fis = new FileInputStream(repReqPath);
             Workbook workbook = WorkbookFactory.create(fis);
             FileOutputStream fos = new FileOutputStream(repReqPath)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && (int) row.getCell(0).getNumericCellValue() == request.getRequestId()) {
                    row.getCell(5).setCellValue(status);
                    break;
                }
            }
            workbook.write(fos);
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error updating request status: " + e.getMessage());
        }
    }
}
