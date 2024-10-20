package models.entities;
import models.enums.*;
import utils.*;
import java.util.regex.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class User {
    private String hospitalID;
    private String password;
    private String salt;
    private String name;
    private Gender gender;

    public User(String hospitalID) {
        this.hospitalID = hospitalID;
        this.password = "P@ssw0rd123";
        this.salt = null;
    }

    public User(String hospitalID, String password, String salt, String name, Gender gender) {
        this.hospitalID = hospitalID;
        this.password = password;
        this.salt = salt;
        this.name = name;
        this.gender = gender;
    }

    public User(String hospitalID, String name, String password, Gender gender) {
        this.hospitalID = hospitalID;
        this.name = name;
        this.password = password;
        this.gender = gender;
    }

    public User(String hospitalID, String name, Gender gender) {
        this.hospitalID = hospitalID;
        this.name = name;
        this.gender = gender;
        this.password = "P@ssw0rd123";
        this.salt = null;
    }

    public static boolean isValidPassword(String password)
    {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(regex);
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // finds matching between given password and regular expression.
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public boolean setPassword(String password) {
        if (isValidPassword(password)) {
            this.salt = PasswordUtil.generateSalt();
            this.password = PasswordUtil.hashPassword(password, salt);
            return true;
        } else {
            System.out.println("Password is too weak!");
            return false;
        }
    }

    public void storePassword() {
        String filePath = "data/Auth_Data.xlsx";
        Workbook workbook = null;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fileInputStream);

            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getStringCellValue().equals(this.hospitalID)) {
                    row.createCell(1).setCellValue(this.salt);
                    row.createCell(2).setCellValue(this.password);
                    found = true;
                    break;
                }
            }

            if (!found) {
                Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
                newRow.createCell(0).setCellValue(this.hospitalID);
                newRow.createCell(1).setCellValue(this.salt);
                newRow.createCell(2).setCellValue(this.password);
            }
            fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean verifyPassword(String password) {
        return PasswordUtil.verifyPassword(password, this.password, this.salt);
    }

    public abstract Role getRole();
}
