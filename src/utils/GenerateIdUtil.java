package utils;

import app.loaders.ApptAvailLoader;
import models.entities.Appointment;
import models.enums.FilePaths;

import java.util.ArrayList;
import java.util.List;

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
                        return "A00" + uniqueID;
                    }else if(length == 2){
                        return "A0" + uniqueID;
                    }else{
                        return "A" + uniqueID;
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
}
