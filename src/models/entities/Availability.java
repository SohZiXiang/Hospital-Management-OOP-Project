package models.entities;

import models.enums.DoctorAvailability;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Availability {
    // Change variableNames
    private String doctorId;
    private Date availableDate;
    private String startTime;
    private String endTime;
    private DoctorAvailability status; // Use enum instead of String

    public Availability(String doctorId, Date availableDate, String startTime, String endTime, DoctorAvailability status) {
        this.doctorId = doctorId;
        this.availableDate = availableDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public LocalTime getStartTimeAsLocalTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return LocalTime.parse(this.startTime, formatter);
    }

    public String getDoctorId() {
        return doctorId;
    }

    public Date getAvailableDate() {
        return availableDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public DoctorAvailability getStatus() {
        return status;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setAvailableDate(Date availableDate) {
        this.availableDate = availableDate;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStatus(DoctorAvailability status) {
        this.status = status;
    }
}
