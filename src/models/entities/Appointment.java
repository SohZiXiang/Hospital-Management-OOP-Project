package models.entities;
import java.util.*;

import models.enums.AppointmentStatus;

public class Appointment {
    private Integer appointmentId;
    private Integer patientId;
    private Integer doctorId;
    private AppointmentStatus status;
    private Date appointmentDate;
    private String appointmentTime;
    private String outcomeRecord;

    public Appointment(Integer appointmentId, Integer patientId, Integer doctorId, Date appointmentDate, String appointmentTime) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.status = AppointmentStatus.SCHEDULED;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.outcomeRecord = "";
    }

    public Integer getPatientId() {
        return patientId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getOutcomeRecord() {
        return outcomeRecord;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setOutcomeRecord(String outcomeRecord) {
        this.outcomeRecord = outcomeRecord;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }
}
