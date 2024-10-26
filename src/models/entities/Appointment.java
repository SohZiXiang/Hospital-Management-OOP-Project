package models.entities;
import java.util.*;

import models.enums.AppointmentStatus;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private AppointmentStatus status;
    private Date appointmentDate;
    private String appointmentTime;
    private String outcomeRecord;

    public Appointment(String appointmentId, String patientId, String doctorId, Date appointmentDate, String appointmentTime) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.status = AppointmentStatus.SCHEDULED;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.outcomeRecord = "";
    }

    public String getPatientId() {
        return patientId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getDoctorId() {
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

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
}
