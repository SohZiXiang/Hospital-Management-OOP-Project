package app.screens.admin;

import app.loaders.InventoryLoader;
import interfaces.*;
import models.entities.*;
import models.records.*;
import models.enums.FilePaths;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import utils.ActivityLogUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This screen provides functionality for administrators to approve or reject replenishment requests.
 * It loads, displays, and allows actions on requests using commands from the administrator.
 */
public class ApproveReplenishmentScreen implements Screen {
    String repReqPath = FilePaths.REPLENISH_REQ_DATA.getPath();
    List<ReplenishmentRequest> repReqList = new ArrayList<>();

    /**
     * Displays the replenishment approval screen, allowing administrators to approve or reject requests.
     *
     * @param scanner Scanner instance for reading user input.
     * @param user    The currently logged-in user; only works if user is an Administrator.
     */
    @Override
    public void display(Scanner scanner, User user) {
        if (user instanceof Administrator) {
            Administrator admin = (Administrator) user;
            processRequests(scanner, admin);
        }
    }

    /**
     * Processes the administrator's inputs to approve or reject replenishment requests.
     *
     * @param scanner Scanner instance for reading user input.
     * @param admin   The administrator handling the replenishment requests.
     */
    private void processRequests(Scanner scanner, Administrator admin) {
        boolean showAllRequests = false;
        while (true) {
            loadReplenishmentRequests();
            displayRequests(showAllRequests);
            System.out.print("\nSelect a request number to approve/reject (0 to exit, -1 to view all requests): ");
            int choice = scanner.nextInt();

            if (choice == 0) break;
            if (choice == -1) {
                showAllRequests = true;
                continue;
            }

            if (choice > 0 && choice <= repReqList.size()) {
                ReplenishmentRequest request = repReqList.get(choice - 1);

                if ("approved".equalsIgnoreCase(request.getStatus()) || "rejected".equalsIgnoreCase(request.getStatus())) {
                    System.out.println("This request has already been " + request.getStatus() + ". Please select another request.");
                    continue;
                }

                System.out.print("Do you want to (A)pprove or (R)eject this request? ");
                String decision = scanner.next().trim().toUpperCase();
                String logMsg = null;

                if (decision.equals("A")) {
                    admin.approveRequest(choice - 1, admin, request);
                    logMsg = "User " + admin.getName() + " (ID: " + admin.getHospitalID() + ") approved request form " + repReqList.get(choice-1).getRequestId();
                } else if (decision.equals("R")) {
                    admin.rejectRequest(choice - 1, request);
                    logMsg = "User " + admin.getName() + " (ID: " + admin.getHospitalID() + ") rejected request form " + repReqList.get(choice-1).getRequestId();
                } else {
                    System.out.println("Invalid choice. Please enter A or R.");
                }
                ActivityLogUtil.logActivity(logMsg, admin);
            } else {
                System.out.println("Invalid request number. Please try again.");
            }
        }
    }

    /**
     * Loads replenishment requests from the specified Excel file path.
     * Populates the list of requests for display and processing.
     */
    private void loadReplenishmentRequests() {
        repReqList.clear();
        try (FileInputStream fis = new FileInputStream(repReqPath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    int requestId = (int) row.getCell(0).getNumericCellValue();
                    String hospitalId = row.getCell(1).getStringCellValue();
                    String requesterName = row.getCell(2).getStringCellValue();
                    String medicineName = row.getCell(3).getStringCellValue();
                    int requestedAmount = (int) row.getCell(4).getNumericCellValue();
                    String status = row.getCell(5).getStringCellValue();
                    String requestDate = row.getCell(6).getStringCellValue();

                    ReplenishmentRequest request = new ReplenishmentRequest(requestId, hospitalId, requesterName, medicineName, requestedAmount, status, requestDate);
                    repReqList.add(request);
                }
            }
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error reading replenishment requests: " + e.getMessage());
        }
    }

    /**
     * Displays the list of replenishment requests, administrator can approve or reject these requests here.
     *
     * @param showAll If true, displays all requests; if false, displays only pending requests.
     */
    private void displayRequests(boolean showAll) {
        List<ReplenishmentRequest> requestsToDisplay;
        if (showAll) {
            requestsToDisplay = repReqList;
        } else {
            requestsToDisplay = repReqList.stream()
                    .filter(request -> "pending".equalsIgnoreCase(request.getStatus()))
                    .collect(Collectors.toList());
        }

        requestsToDisplay.sort(Comparator.comparing(ReplenishmentRequest::getRequestDate).reversed());

        System.out.println("\n--- Replenishment Requests ---");
        System.out.printf("%-12s %-12s %-20s %-25s %-10s %-10s %-15s%n",
                "Request ID", "Hospital ID", "Requester Name", "Medicine Name", "Amount", "Status", "Request Date");
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        if (requestsToDisplay.isEmpty()) {
            System.out.println("No pending requests.");
        } else {
            for (int i = 0; i < requestsToDisplay.size(); i++) {
                ReplenishmentRequest request = requestsToDisplay.get(i);
                System.out.printf("%-12d %-12s %-20s %-25s %-10d %-10s %-15s%n",
                        request.getRequestId(),
                        request.getHospitalId(),
                        request.getRequesterName(),
                        request.getMedicineName(),
                        request.getRequestedAmount(),
                        request.getStatus(),
                        request.getRequestDate());
            }
        }
    }
}
