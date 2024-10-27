package app.screens.admin;

import app.screens.*;
import interfaces.*;
import models.entities.*;
import java.util.Scanner;

import models.enums.FilePaths;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.ActivityLogUtil;

import java.io.FileInputStream;
import java.io.IOException;

public class ViewActivityLogScreen implements Screen {
    private static final String file_path = FilePaths.ACTIVITY_LOG.getPath();

    @Override
    public void display(Scanner scanner, User user) {
        String logMsg = "User " + user.getName() + " (ID: " + user.getHospitalID() + ") viewed activity logs.";
        ActivityLogUtil.logActivity(logMsg, user);
        System.out.println("=== View Activity Logs ===");
        try {
            FileInputStream file = new FileInputStream(file_path);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            displayActivityLogHeader();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                displayActivityLogDetails(row);
            }

            workbook.close();
            file.close();
            while (true) {
                System.out.println("\nPress '0' to return to the Admin Menu.");
                String input = scanner.nextLine();
                if ("0".equals(input)) {
                    AdminMainScreen adminMainScreen = new AdminMainScreen();
                    adminMainScreen.display(scanner, user);
                    break;
                } else {
                    System.out.println("Invalid input. Please press '0' to return.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error getting activity logs: " + e.getMessage());
        }
    }

    private void displayActivityLogHeader() {
        System.out.printf("%-20s %-10s %-60s%n", "Date of Activity", "User ID", "Activity Message");
        System.out.println("---------------------------------------------------------------------------");
    }

    private void displayActivityLogDetails(Row row) {
        Cell dateCell = row.getCell(0);
        Cell userIdCell = row.getCell(2);
        Cell messageCell = row.getCell(1);

        String date = dateCell.getStringCellValue();
        String userId = userIdCell.getStringCellValue();
        String message = messageCell.getStringCellValue();

        String formattedMessage = formatMessage(message, 60);

        System.out.printf("%-20s %-10s %-60s%n", date, userId, formattedMessage);
    }

    private String formatMessage(String message, int maxLength) {
        StringBuilder formattedMsg = new StringBuilder();
        String[] words = message.split(" ");
        int curLineLength = 0;

        for (String word : words) {
            if (curLineLength + word.length() + 1 > maxLength) {
                formattedMsg.append("\n").append(String.format("%-32s", ""));
                curLineLength = 0;
            }
            formattedMsg.append(word).append(" ");
            curLineLength += word.length() + 1;
        }
        return formattedMsg.toString().trim();
    }
}
