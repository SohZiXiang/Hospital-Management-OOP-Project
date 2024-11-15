package models.entities;
import models.enums.*;
import utils.*;
import java.util.regex.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Abstract class representing a user in the system.
 * This class contains attributes and methods related to user
 * authentication, including hospital ID, password, salt,
 * name, and gender.
 */
public abstract class User {
    private String hospitalID;
    private String password;
    private String salt;
    private String name;
    private Gender gender;

    /**
     * Constructs a User object with the specified hospital ID and
     * a default password.
     *
     * @param hospitalID the hospital ID associated with the user
     */
    public User(String hospitalID) {
        this.hospitalID = hospitalID;
        this.password = "P@ssw0rd123";
        this.salt = null;
    }

    /**
     * Constructs a User object with the specified details.
     *
     * @param hospitalID the hospital ID associated with the user
     * @param password the password for the user
     * @param salt the salt used for password hashing
     * @param name the name of the user
     * @param gender the gender of the user
     */
    public User(String hospitalID, String password, String salt, String name, Gender gender) {
        this.hospitalID = hospitalID;
        this.password = password;
        this.salt = salt;
        this.name = name;
        this.gender = gender;
    }

    /**
     * Constructs a User object with the specified details.
     *
     * @param hospitalID the hospital ID associated with the user
     * @param name the name of the user
     * @param password the password for the user
     * @param gender the gender of the user
     */
    public User(String hospitalID, String name, String password, Gender gender) {
        this.hospitalID = hospitalID;
        this.name = name;
        this.password = password;
        this.gender = gender;
    }


    /**
     * Constructs a User object with the specified details and
     * a default password.
     *
     * @param hospitalID the hospital ID associated with the user
     * @param name the name of the user
     * @param gender the gender of the user
     */
    public User(String hospitalID, String name, Gender gender) {
        this.hospitalID = hospitalID;
        this.name = name;
        this.gender = gender;
        this.password = "P@ssw0rd123";
        this.salt = null;
    }

    /**
     * Validates the given password against the required criteria.
     *
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    public static boolean isValidPassword(String password)
    {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[*()@#$%^&+=!])"
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

    /**
     * Returns the hospital ID of the user.
     *
     * @return the hospital ID
     */
    public String getHospitalID() {
        return hospitalID;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the salt used for the user's password.
     *
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Returns the name of the user.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the new name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the gender of the user.
     *
     * @return the gender
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the gender of the user.
     *
     * @param gender the new gender to set
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Sets the hospital ID of the user.
     *
     * @param hospitalID the new hospital ID to set
     */
    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    /**
     * Sets the password for the user after validating its strength.
     *
     * @param password the new password to set
     * @return true if the password is successfully set, false if it is weak
     */
    public boolean setPassword(String password) {
        if (isValidPassword(password)) {
            this.salt = PasswordUtil.generateSalt();
            this.password = PasswordUtil.hashPassword(password, salt);
            return true;
        } else {
            System.out.println("Password must be 8-20 characters, with at least one lowercase, uppercase, digit, special character, and no spaces.");
            return false;
        }
    }

    /**
     * Stores the user's password and salt in an Excel file.
     */
    public void storePassword(User currentUser) {
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
                if (cell != null && cell.getStringCellValue().equals(currentUser.hospitalID)) {
                    row.createCell(1).setCellValue(currentUser.salt);
                    row.createCell(2).setCellValue(currentUser.password);
                    found = true;
                    break;
                }
            }

            if (!found) {
                Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
                newRow.createCell(0).setCellValue(currentUser.hospitalID);
                newRow.createCell(1).setCellValue(currentUser.salt);
                newRow.createCell(2).setCellValue(currentUser.password);
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

    /**
     * Abstract method to get the role of the user.
     *
     * @return the role of the user
     */
    public abstract Role getRole();
}
