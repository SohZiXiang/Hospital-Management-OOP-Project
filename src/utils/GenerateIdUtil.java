package utils;

import app.loaders.ApptAvailLoader;
import models.entities.Appointment;
import models.entities.User;
import models.enums.FilePaths;

import java.util.ArrayList;
import java.util.List;

import java.security.SecureRandom;
import java.util.Scanner;

public class GenerateIdUtil {

    /**
     * Generates a unique appointment ID based on existing appointments.
     * The generated ID follows the format:
     * - "AP0X" for single-digit IDs.
     * - "APXX" for two-digit IDs.
     * @return A unique appointment ID as a string.
     */
    public static String genAppointmentID(){

        ApptAvailLoader appointmentLoader = new ApptAvailLoader();
        List<Appointment> appointmentList = new ArrayList<>();
        String appointmentPath = FilePaths.APPT_DATA.getPath();

        List<Integer> existingID = new ArrayList<Integer>();
        Integer uniqueID = 1;

        try {
            appointmentList = appointmentLoader.loadData(appointmentPath);
        }
        catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }

        try {
            for (Appointment appointment : appointmentList) {
                String id = appointment.getAppointmentId();
                String numericValue = id.replaceAll("[^0-9]", "");

                if (!numericValue.isEmpty()) {
                    int numericValueToInt = Integer.parseInt(numericValue);
                    existingID.add(numericValueToInt);
                }
            }

            while(true){
                if(!existingID.contains(uniqueID)){
                    int length = String.valueOf(uniqueID).length();
                    if(length == 1){
                        return "AP0" + uniqueID;
                    }else if(length == 2){
                        return "AP" + uniqueID;
                    }
                }
                uniqueID++;
            }

        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return "";
    }

    /**
     * Generates a One-Time Password (OTP) of a specified length.
     *
     * @param length The desired length of the OTP.
     * @return A randomly generated numeric OTP as a string.
     */
    private static String generateOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Generates a random digit (0-9)
        }
        return sb.toString();
    }

    /**
     * Generates a 2-Factor Authentication (2FA) process for a user.
     * An OTP is sent to the user's registered phone number, and the user
     * is prompted to enter the OTP. If the user enters "exit", the process
     * is terminated, and the user is logged out.
     * @param scanner The scanner object to capture user input.
     * @param user    The user requesting 2FA.
     */
    public static void generate2FA(Scanner scanner, User user){
        String otp = generateOTP(6);
        SMSUtil.sendSms(SMSUtil.numberHX, "Your OTP is: " + otp);
        String input = "";
        do{
            System.out.println("Enter the OTP that is send to your phone");
            System.out.println("Enter \"exit\" to exit");
            input = scanner.nextLine();
            if(input.equals("exit")){
                ActivityLogUtil.logout(scanner, user);
                break;
            }
        }while(!input.equals(otp));
    }
}
