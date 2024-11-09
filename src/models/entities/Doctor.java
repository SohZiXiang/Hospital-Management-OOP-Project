package models.entities;

import java.time.*;
import models.enums.*;
import models.records.*;
import models.services.*;
import java.util.*;

import models.services.AvailService;

public class Doctor extends Staff{
    private ApptService apptService;
    private AvailService availService;
    private PatientService patientService;

    public Doctor(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
        this.availService = new AvailService(null);
        this.apptService = new ApptService(this.availService);
        this.availService.setApptManager(this.apptService);
        this.patientService = new PatientService();
    }

    public Doctor(String staffId, String name, Gender gender, int age) {
        super(staffId, staffId, name, gender, age);
        this.availService = new AvailService(null);
        this.apptService = new ApptService(this.availService);
        this.availService.setApptManager(this.apptService);
        this.patientService = new PatientService();
    }


    /* START OF METHODS TO LOAD DATA */

    public void getPatientList() {
        patientService.getPatientList();
    }

    public void getAvailList() {
        availService.getAvailList(this.getStaffId());
    }

    public void getApptList() {
        apptService.getApptList(this.getStaffId());

    }

    public void getOutcomeRecords() {
        apptService.getOutcomeRecords(this.getStaffId());
    }

    /* END OF METHODS TO LOAD DATA */

    /* START OF METHODS TO UPDATE/RESET DATA ONCE NEW RECORD ADDED/USER LOGOUTS */

    public void resetData(String type) {
        apptService.resetData(type);
    }


    /* END OF METHODS TO UPDATE/RESET DATA ONCE NEW RECORD ADDED/USER LOGOUTS */

    /* START OF METHODS TO DO WITH PATIENT DATA */

    public void showAllPatientsRecords() {
        patientService.showAllPatientsRecords();
    }

    public void filterPatients(String patientID) {
        patientService.filterPatients(patientID);
    }

    public Patient checkWhetherPatientValid(String patientID) {
        return patientService.checkWhetherPatientValid(patientID);
    }

    public void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment) {
        patientService.updateMedicalRecord(patient, newDiagnosis, newTreatment);
    }

    /* END OF METHODS TO DO WITH PATIENT DATA */

    /* START OF MANAGEMENT OF DOCTOR AVAILABILITY METHODS */
    public void viewAllAvail() {
        availService.viewAllAvail(this.getStaffId());
    }

    public void addAvail(LocalDate date, String startTime, String endTime,
                         DoctorAvailability status) {
        availService.addAvail(this.getStaffId(), date, startTime, endTime, status);
    }


    public List<Availability> slotsForDate(LocalDate date) {
        return availService.slotsForDate(this.getStaffId(), date);
    }

    public void editOneSlot(LocalDate desiredDate, String oldStart, String changedStart, String changedEnd,
                            DoctorAvailability changedStatus) {
        availService.editOneSlot(this.getStaffId(), desiredDate, oldStart, changedStart, changedEnd,
                changedStatus);
        availService.updateData(this.getStaffId());

    }

    /* END OF MANAGEMENT OF DOCTOR AVAILABILITY METHODS */

    /* START OF DOCTOR APPOINTMENTS METHODS */

    public void viewDoctorSchedule() {
        apptService.viewDoctorSchedule(this.getStaffId());
    }


    public List<Appointment> apptPendingReviews() {
        return apptService.apptPendingReviews();
    }

    public void updateAppointmentStatus(Appointment oneAppt, AppointmentStatus changedStatus) {
        apptService.updateAppointmentStatus(oneAppt, changedStatus);
        apptService.updateData(this.getStaffId(), "appt");
        availService.updateData(this.getStaffId());
    }

    /* END OF DOCTOR APPOINTMENTS METHODS */

    /* START OF RECORDING APPT OUTCOME METHODS */

    public Appointment findApptByID(String apptID) {
        return apptService.findApptByID(apptID);
    }

    public void recordAppointmentOutcome(Appointment appointment, String serviceType,
                                         List<AppointmentOutcomeRecord.PrescribedMedication> prescriptions,
                                         String consultationNotes, String outcomeSts) {
        apptService.recordAppointmentOutcome(appointment, serviceType, prescriptions, consultationNotes, outcomeSts);
    }

    public void viewAllAppointmentOutcomes() {
        apptService.viewAllAppointmentOutcomes(this.getStaffId());
    }

    /* END OF RECORDING APPT OUTCOME METHODS */

    /* NOT IMPLEMENTED YET */
//    public void addPatientUnderCare(Patient patient) {
//        patientsUnderCare.add(patient);
//    }

//    public List<Appointment> viewUpcomingAppointments() {
//        return apptList;
//    }
    /* NOT IMPLEMENTED YET */

    @Override
    public Role getRole() {
        return Role.DOCTOR;
    }
}
