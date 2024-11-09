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

    private static String generateOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Generates a random digit (0-9)
        }
        return sb.toString();
    }

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
