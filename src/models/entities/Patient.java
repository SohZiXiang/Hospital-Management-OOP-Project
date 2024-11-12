package models.entities;

import app.loaders.ApptAvailLoader;
import app.loaders.PatientLoader;
import app.loaders.StaffLoader;
import interfaces.ApptManager;
import interfaces.DataLoader;
import models.enums.*;
import models.records.PatientOutcomeRecord;
import models.services.AvailService;
import models.services.PatientService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.ActivityLogUtil;
import utils.SMSUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

public class Patient extends User {
    private String patientID;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String  email;
    private BloodType bloodType;
    private List<String> pastDiagnoses;
    private List<String> pastTreatments;

    PatientService patientService = new PatientService();


    public Patient(String hospitalID) {
        super(hospitalID);
    }

    public Patient(String hospitalID, String name, String password,
                   String patientID, LocalDate dateOfBirth, Gender gender,
                   String phoneNumber, String email, BloodType bloodType,
                   List<String> pastDiagnoses, List<String> pastTreatments) {
        super(hospitalID, name, password, gender);
        this.patientID = patientID;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.bloodType = bloodType;
        this.pastDiagnoses = (pastDiagnoses != null) ? new ArrayList<>(pastDiagnoses) : new ArrayList<>();
        this.pastTreatments = (pastTreatments != null) ? new ArrayList<>(pastTreatments) : new ArrayList<>();
    }
    //Changed this part
    public Patient(String hospitalID,
                   String patientID, String name, LocalDate dateOfBirth, Gender gender,
                   String email, BloodType bloodType, List<String> pastDiagnoses, List<String> pastTreatments, String phoneNumber) {
        super(hospitalID = patientID, name,"P@ssw0rd123", gender);
        this.patientID = patientID;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.bloodType = bloodType;
        this.pastDiagnoses = (pastDiagnoses != null) ? new ArrayList<>(pastDiagnoses) : new ArrayList<>();
        this.pastTreatments = (pastTreatments != null) ? new ArrayList<>(pastTreatments) : new ArrayList<>();
        this.phoneNumber = phoneNumber;
    }


    public String getPatientID() {
        return patientID;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public List<String> getPastDiagnoses() {
        return pastDiagnoses;
    }

    public List<String> getPastTreatments() {
        return pastTreatments;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    // Changed this part
    public void addDiagnosis(String diagnosis) {
        if (this.pastDiagnoses == null) {
            this.pastDiagnoses = new ArrayList<>();
        }
        this.pastDiagnoses.add(diagnosis);
    }


    // Method to add a new treatment
    public void addTreatment(String treatment) {
        if (this.pastTreatments == null) {
            this.pastTreatments = new ArrayList<>();
        }
        this.pastTreatments.add(treatment);
    }

    @Override
    public Role getRole() {
        return Role.PATIENT;
    }

    public void createAppointment(User user, int option, String appointmentID, boolean create) {
        patientService.createAppointment(user, option, appointmentID, create);
    }

    public void loadAppointmentData(User user){
        patientService.loadAppointmentData(user);
    }

    public void cancelAppointment(User user, String appointmentID, boolean cancel) {
        patientService.cancelAppointment(user, appointmentID, cancel, 0);
    }

    public void rescheduleAppointment(User user, int option, String appointmentID) {
        patientService.rescheduleAppointment(user, option, appointmentID);
    }

    public void loadOutcomeData(User user){
        patientService.loadOutcomeData(user);
    }

    public void loadMedicalRecordData(User user) {
        patientService.loadMedicalRecordData(user);
    }

    public void updateContact(User user, Patient patient, boolean email) {
        patientService.updateContact(user, patient, email);
    }

}
