package utils;
import app.Main;
import models.enums.FilePaths;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import models.entities.*;

/**
 * This utility class provides methods for logging user activities and managing the activity log.
 * It allows for logging specific activities of users and provides functionality for user logout.
 */
public class ActivityLogUtil {
    private static final String logFilePath = FilePaths.ACTIVITY_LOG.getPath();

    /**
     * Logs an activity message along with the current timestamp and the user ID into the activity log file.
     *
     * @param activityLog The activity message to log.
     * @param user        The user associated with the activity, which provides the user ID.
     */
    public static void logActivity(String activityLog, User user) {
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            File file = new File(logFilePath);
            if (!file.exists()) {
                workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Logs");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Date of Activity");
                headerRow.createCell(1).setCellValue("Activity Message");
                headerRow.createCell(2).setCellValue("User ID");
            } else {
                fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
            }

            Sheet sheet = workbook.getSheet("Logs");
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(getCurrentTimestamp());
            newRow.createCell(1).setCellValue(activityLog);
            newRow.createCell(2).setCellValue(user.getHospitalID());

            try (FileOutputStream fos = new FileOutputStream(logFilePath)) {
                workbook.write(fos);
            }
            //System.out.println("Activity logged successfully.");

        } catch (IOException e) {
            System.err.println("Error logging activity: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Logs out the specified user and records the logout event in the activity log.
     *
     * @param scanner The scanner instance for reading input.
     * @param user    The user who is logging out.
     */
    public static void logout(Scanner scanner, User user){
        System.out.println("Logging out...");
        String logMsg = "User " + user.getName() + " (ID: " + user.getHospitalID()+ ") has logged out.";
        ActivityLogUtil.logActivity(logMsg, user);
        Main.displayMain(scanner);
    }

    /**
     * Gets the current timestamp formatted as "yyyy-MM-dd HH:mm:ss".
     *
     * @return A string representation of the current timestamp.
     */
    private static String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
}

