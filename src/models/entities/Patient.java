package models.entities;

import models.enums.BloodType;
import models.enums.Gender;
import models.enums.Role;

import java.time.LocalDate;
import java.util.*;

public class Patient extends User {
    private String patientID;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String  email;
    private BloodType bloodType;
    private List<String> pastDiagnoses;
    private List<String> pastTreatments;


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
}
