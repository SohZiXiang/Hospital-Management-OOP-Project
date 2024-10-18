package models.entities;

import java.util.regex.*;

public abstract class User {
    private String hospitalID;
    private String password;
    private String name;

    public User(String hospitalID) {
        this.hospitalID = hospitalID;
        this.password = "password";
    }

    public User(String hospitalID, String password) {
        this.hospitalID = hospitalID;
        this.password = password;
    }

    public User(String hospitalID, String name, String password) {
        this.hospitalID = hospitalID;
        this.name = name;
        this.password = password;
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

        // Return if the password matched the ReGex
        return m.matches();
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public boolean setPassword(String password) {
        if (isValidPassword(password)) {
            this.password = password;
            return true;
        } else {
            System.out.println("Password is too weak!");
            return false;
        }
    }

    public abstract String getRole();
}
