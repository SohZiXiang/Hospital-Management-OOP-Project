package models.entities;

import java.util.ArrayList;
import java.util.List;
import models.enums.*;
import models.records.*;

import java.util.*;

public class Doctor extends Staff{
    private List<Appointment> appointments;
    private List<Patient> patientsUnderCare;

    public Doctor(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
        this.appointments = new ArrayList<>();
        this.patientsUnderCare = new ArrayList<>();
    }

    public Doctor(String staffId, String name, Gender gender, int age) {
        super(staffId, staffId, name, gender, age);
        this.appointments = new ArrayList<>();
        this.patientsUnderCare = new ArrayList<>();
    }

    public void viewMedicalRecord(Patient patient){
        System.out.println("Viewing medical records for: " + patient.getName());
        System.out.println("Patient ID: " + patient.getPatientID());
        System.out.println("Date of Birth: " + patient.getDateOfBirth());
        System.out.println("Gender: " + patient.getGender());
        System.out.println("Phone Number: " + patient.getContact());
        System.out.println("Email: " + patient.getContact());
        System.out.println("Blood Type: " + patient.getBloodType());

        System.out.println("Past Diagnoses: ");
        for (String diagnosis : patient.getPastDiagnoses()) {
            System.out.println(" - " + diagnosis);
        }

        System.out.println("Past Treatments: ");
        for (String treatment : patient.getPastTreatments()) {
            System.out.println(" - " + treatment);
        }
    }

    public void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment) {
        patient.addDiagnosis(newDiagnosis);
        patient.addTreatment(newTreatment);
    }
    
    public void viewSchedule() {
        // Display appointments for the doctor
    }

    // Set availability for appointments
    public void setAvailability(Date date, String time) {
        // Logic to set availability for appointments
    }

    public void respondAppointmentRequest(Appointment appointment, boolean accept) {
        //implement update appointment request accepted use cases
    }

    public List<Appointment> viewUpcomingAppointments() {
        return appointments;
    }


    public void recordAppointmentOutcome(Appointment appointment, String serviceType, List<Medicine> prescriptions, String consultationNotes) {
        AppointmentOutcomeRecord outcomeRecord = new AppointmentOutcomeRecord(
                new Date(), // Current date as appointment date
                serviceType,
                prescriptions,
                consultationNotes
        );
    }

    public void addPatientUnderCare(Patient patient) {
        patientsUnderCare.add(patient);
    }

    @Override
    public Role getRole() {
        return Role.DOCTOR;
    }
}
