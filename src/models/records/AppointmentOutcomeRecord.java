package models.records;

import com.twilio.rest.microvisor.v1.App;
import models.entities.Appointment;
import models.entities.Medicine;
import models.enums.*;

import java.util.*;
import java.util.ArrayList;

 public class AppointmentOutcomeRecord {
    private Appointment appt;
    private Date appointmentDate;
    private String serviceType;
    private List<PrescribedMedication> prescriptions;
    private String consultationNotes;
    private String apptStatus;

    // wrapper class to keep track of prescribed medication for pharmacist and doctor
    public static class PrescribedMedication {
        private Medicine medicine;
        private PrescriptionStatus status;

        public PrescribedMedication(Medicine medicine) {
            this.medicine = medicine;
            this.status = PrescriptionStatus.PENDING;
        }

        public PrescribedMedication(Medicine medicine, PrescriptionStatus status) {
            this.medicine = medicine;
            this.status = status;
        }

        public Medicine getMedicine() {
            return medicine;
        }

        public PrescriptionStatus getStatus() {
            return status;
        }

        public void setStatus(PrescriptionStatus status) {
            this.status = status;
        }
    }

    public AppointmentOutcomeRecord(Appointment appt, Date appointmentDate, String serviceType,
                                    List<Medicine> medicines,
                                    String consultationNotes, String outcomeStatus) {
        this.appt = appt;
        this.appointmentDate = appointmentDate;
        this.serviceType = serviceType;
        this.consultationNotes = consultationNotes;
        this.prescriptions = new ArrayList<>();
        for (Medicine medicine : medicines) {
            this.prescriptions.add(new PrescribedMedication(medicine));
        }
        this.apptStatus = outcomeStatus;

    }

    public Appointment getAppt() { return appt; }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }


    public List<PrescribedMedication> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Medicine> medicines) {
        this.prescriptions.clear();
        for (Medicine medicine : medicines) {
            this.prescriptions.add(new PrescribedMedication(medicine));
        }
    }

     public void addPrescription(Medicine medicine) {
         this.prescriptions.add(new PrescribedMedication(medicine));
     }

     public void changePrescriptionStatus(String medicineName, PrescriptionStatus newStatus) {
         for (PrescribedMedication prescribedMedication : prescriptions) {
             if (prescribedMedication.getMedicine().getName().equals(medicineName)) {
                 prescribedMedication.setStatus(newStatus);
                 break;
             }
         }
     }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes;
    }

    public String getApptStatus() { return apptStatus; }

     public void setApptStatus(String apptStatus) {
        this.apptStatus = apptStatus;
     }
}
