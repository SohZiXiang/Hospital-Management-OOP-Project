package app.screens.admin;

import app.loaders.InventoryLoader;
import interfaces.*;
import models.entities.*;
import models.records.*;
import models.enums.FilePaths;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ApproveReplenishmentScreen implements Screen {
    List<Medicine> inventory = ManageInventoryScreen.loadInventory();
    String repReqPath = FilePaths.REPLENISH_REQ_DATA.getPath();
    List<ReplenishmentRequest> repReqList = new ArrayList<>();

    @Override
    public void display(Scanner scanner, User user) {
        processRequests(scanner);
    }

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
        System.out.printf("%-5s %-12s %-20s %-25s %-10s %-10s %-15s%n",
                "No", "Hospital ID", "Requester Name", "Medicine Name", "Amount", "Status", "Request Date");
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        if (requestsToDisplay.isEmpty()) {
            System.out.println("No pending requests.");
        } else {
            for (int i = 0; i < requestsToDisplay.size(); i++) {
                ReplenishmentRequest request = requestsToDisplay.get(i);
                System.out.printf("%-5d %-12s %-20s %-25s %-10d %-10s %-15s%n",
                        i + 1,
                        request.getHospitalId(),
                        request.getRequesterName(),
                        request.getMedicineName(),
                        request.getRequestedAmount(),
                        request.getStatus(),
                        request.getRequestDate());
            }
        }
    }

    private void processRequests(Scanner scanner) {
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

                if (decision.equals("A")) {
                    approveRequest(choice - 1);
                } else if (decision.equals("R")) {
                    rejectRequest(choice - 1);
                } else {
                    System.out.println("Invalid choice. Please enter A or R.");
                }
            } else {
                System.out.println("Invalid request number. Please try again.");
            }
        }
    }

    private void approveRequest(int index) {
        ReplenishmentRequest request = repReqList.get(index);
        Medicine medData = findMedicine(request.getMedicineName());
        if (medData != null) {
            int addedStock = request.getRequestedAmount();
            int initialStock = medData.getStock();
            int newStock = addedStock + initialStock;

            ManageInventoryScreen manageInventoryScreen = new ManageInventoryScreen();
            manageInventoryScreen.updateStock(medData, newStock);

            updateRequestStatus(request, "approved");
            System.out.println("Request for " + request.getRequestedAmount() + " of " + request.getMedicineName() + " approved.");
        } else {
            System.out.println("Medicine not found for request: " + request.getMedicineName());
        }
    }

    private void rejectRequest(int index) {
        ReplenishmentRequest request = repReqList.get(index);
        updateRequestStatus(request, "rejected");

        System.out.println("Request for " + request.getRequestedAmount() + " of " + request.getMedicineName() + " rejected.");
    }

    private void updateRequestStatus(ReplenishmentRequest request, String status) {
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

    private Medicine findMedicine(String medName) {
        return inventory.stream()
                .filter(medicine -> medicine.getName().equals(medName))
                .findFirst()
                .orElse(null);
    }
}
