package models.entities;

import models.enums.*;

public class Pharmacist extends Staff {
    public Pharmacist(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
    }

    public Pharmacist(String staffId, String name, Gender gender, int age) {
        super(staffId,staffId, name, gender, age);
    }

    @Override
    public Role getRole() {
        return Role.PHARMACIST;
    }

    public void viewAppOutRecord(Appointment appointment) {
        System.out.println("Viewing appointment outcome record for ID: " + appointment.getAppointmentId());
        System.out.println("Outcome record: " + appointment.getOutcomeRecord());
    }

    public void updatePrescriptionStatus(Appointment appointment, String status) {
        appointment.setOutcomeRecord(status);
    }

    public void viewAllMedicineStock(){

    }

    public void viewMedicineStock(Medicine medicine){

    }

    public void submitReplenishmentRequest(){

    }
}
