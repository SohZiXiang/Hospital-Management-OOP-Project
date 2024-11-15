package models.entities;

import models.enums.DoctorAvailability;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The Availability class represents the availability schedule of a doctor,
 * including the doctor ID, available date, start and end times, and availability status.
 */
public class Availability {

    private String doctorId;
    private Date availableDate;
    private String startTime;
    private String endTime;
    private DoctorAvailability status;

    /**
     * Constructs an Availability instance with the specified doctor ID, available date,
     * start and end times, and status.
     *
     * @param doctorId      The unique ID of the doctor.
     * @param availableDate The date of the availability.
     * @param startTime     The start time of the availability (in hh:mm a format).
     * @param endTime       The end time of the availability (in hh:mm a format).
     * @param status        The availability status, represented by the DoctorAvailability enum.
     */
    public Availability(String doctorId, Date availableDate, String startTime, String endTime, DoctorAvailability status) {
        this.doctorId = doctorId;
        this.availableDate = availableDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    /**
     * Gets the doctor ID associated with this availability.
     *
     * @return The doctor ID as a String.
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * Gets the date on which the doctor is available.
     *
     * @return The available date as a Date object.
     */
    public Date getAvailableDate() {
        return availableDate;
    }

    /**
     * Gets the start time of the availability.
     *
     * @return The start time as a String (in hh:mm a format).
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of the availability.
     *
     * @return The end time as a String (in hh:mm a format).
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Gets the status of the availability.
     *
     * @return The status as a DoctorAvailability enum (e.g., AVAILABLE, BUSY).
     */
    public DoctorAvailability getStatus() {
        return status;
    }

    /**
     * Sets the doctor ID for this availability.
     *
     * @param doctorId The new doctor ID to set.
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * Sets the date for this availability.
     *
     * @param availableDate The new available date to set.
     */
    public void setAvailableDate(Date availableDate) {
        this.availableDate = availableDate;
    }

    /**
     * Sets the start time for this availability.
     *
     * @param startTime The new start time to set (in hh:mm a format).
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the end time for this availability.
     *
     * @param endTime The new end time to set (in hh:mm a format).
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets the status of this availability.
     *
     * @param status The new status to set (e.g., AVAILABLE or BUSY or BOOKED).
     */
    public void setStatus(DoctorAvailability status) {
        this.status = status;
    }
}
