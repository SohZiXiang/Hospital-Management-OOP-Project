package models.records;

import models.entities.Medicine;

import java.util.*;
import java.util.ArrayList;

 public class AppointmentOutcomeRecord {
    private Date appointmentDate;
    private String serviceType;
    private List<PrescribedMedication> prescriptions;
    private String consultationNotes;

    // wrapper class to keep track of prescribed medication for pharmacist and doctor
    public static class PrescribedMedication {
        private Medicine medicine;
        private String status;

        public PrescribedMedication(Medicine medicine) {
            this.medicine = medicine;
            this.status = "pending";
        }

        public PrescribedMedication(Medicine medicine, String status) {
            this.medicine = medicine;
            this.status = status;
        }

        public Medicine getMedicine() {
            return medicine;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public AppointmentOutcomeRecord(Date appointmentDate, String serviceType, List<Medicine> medicines, String consultationNotes) {
        this.appointmentDate = appointmentDate;
        this.serviceType = serviceType;
        this.consultationNotes = consultationNotes;
        this.prescriptions = new ArrayList<>();
        for (Medicine medicine : medicines) {
            this.prescriptions.add(new PrescribedMedication(medicine));
        }
    }

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

     public void changePrescriptionStatus(String medicineName, String newStatus) {
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
}
