package app.screens.admin;

import app.screens.*;
import interfaces.*;
import models.entities.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import models.enums.FilePaths;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.ActivityLogUtil;

import java.io.FileInputStream;
import java.io.IOException;


/**
 * This class represents a screen that allows administrators to view and manage activity logs.
 * It provides various options to display, filter, and search through the logs.
 */
public class ViewActivityLogScreen implements Screen {
    private static final String file_path = FilePaths.ACTIVITY_LOG.getPath();

    /**
     * Displays the activity logs to the administrator and allows for interaction.
     *
     * @param scanner Scanner instance for reading user input.
     * @param user    The currently logged-in user, who should be an Administrator.
     */
    @Override
    public void display(Scanner scanner, User user) {
        String logMsg = "User " + user.getName() + " (ID: " + user.getHospitalID() + ") viewed activity logs.";
        ActivityLogUtil.logActivity(logMsg, user);
        System.out.println("\n=== View Activity Logs ===");

        try {
            FileInputStream file = new FileInputStream(file_path);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            List<Row> logRows = new ArrayList<>();

            for (int i = sheet.getLastRowNum(); i > 0; i--) {
                Row row = sheet.getRow(i);
                logRows.add(row);
            }

            workbook.close();
            file.close();

            boolean running = true;
            while (running) {
                System.out.println("\nSelect an option:");
                System.out.println("1. Show latest 100 records");
                System.out.println("2. Show all records from latest to oldest");
                System.out.println("3. Show all records from oldest to latest");
                System.out.println("4. Search by date");
                System.out.println("0. Return to Admin Menu");

                String input = scanner.nextLine();
                switch (input) {
                    case "1":
                        displayLatestRecords(logRows, 100);
                        break;
                    case "2":
                        displayAllRecords(logRows);
                        break;
                    case "3":
                        displayOldestRecords(logRows, 100);
                        break;
                    case "4":
                        searchByDate(scanner, logRows);
                        break;
                    case "0":
                        AdminMainScreen adminMainScreen = new AdminMainScreen();
                        adminMainScreen.display(scanner, user);
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid input. Please choose a valid option.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error getting activity logs: " + e.getMessage());
        }
    }

    /**
     * Displays the header for the activity log records.
     */
    private void displayActivityLogHeader() {
        System.out.printf("%-20s %-10s %-60s%n", "Date of Activity", "User ID", "Activity Message");
        System.out.println("---------------------------------------------------------------------------");
    }

    /**
     * Displays the details of a single activity log entry.
     *
     * @param row The row representing a single log entry.
     */
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


    /**
     * Displays the latest records from the activity log, limited to a specified number.
     *
     * @param logRows The list of log rows to display records from.
     * @param limit   The maximum number of records to display.
     */
    private void displayLatestRecords(List<Row> logRows, int limit) {
        displayActivityLogHeader();
        int count = 0;
        for (Row row : logRows) {
            if (count >= limit) break;
            displayActivityLogDetails(row);
            count++;
        }
    }

    /**
     * Displays all records from the activity log in the order they were loaded.
     *
     * @param logRows The list of log rows to display records from.
     */
    private void displayAllRecords(List<Row> logRows) {
        displayActivityLogHeader();
        for (Row row : logRows) {
            displayActivityLogDetails(row);
        }
    }

    /**
     * Displays the oldest records from the activity log, limited to a specified number.
     *
     * @param logRows The list of log rows to display records from.
     * @param limit   The maximum number of records to display.
     */
    private void displayOldestRecords(List<Row> logRows, int limit) {
        displayActivityLogHeader();
        int count = 0;
        for (int i = logRows.size() - 1; i >= 0 && count < limit; i--) {
            displayActivityLogDetails(logRows.get(i));
            count++;
        }
    }

    /**
     * Searches for activity log records by date and displays the matching entries.
     *
     * @param scanner  Scanner instance for reading user input.
     * @param logRows  The list of log rows to search through.
     */
    private void searchByDate(Scanner scanner, List<Row> logRows) {
        System.out.print("Enter date to search (format: YYYY-MM-DD): ");
        String dateToSearch = scanner.nextLine();
        displayActivityLogHeader();
        boolean found = false;

        for (Row row : logRows) {
            Cell dateCell = row.getCell(0);
            String date = dateCell.getStringCellValue();

            if (date.startsWith(dateToSearch)) {
                displayActivityLogDetails(row);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No records found for the specified date.");
        }
    }

    /**
     * Formats a message to fit within a specified line length by wrapping text.
     *
     * @param message   The message to format.
     * @param maxLength The maximum length of a line.
     * @return The formatted message as a string.
     */
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
