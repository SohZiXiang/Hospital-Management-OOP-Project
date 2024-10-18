package models.entities;

import models.enums.Gender;

public class Administrator extends Staff{
    public Administrator(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
    }

    @Override
    public String getRole(){
        return "administrator";
    }

    public void addDoctor(String hospitalID, String staffId, String name, Gender gender, int age){
        Doctor newDoctor = new Doctor(hospitalID, staffId, name, gender, age);
    }

    public void updateDoctor(Doctor doctor){

    }

    public void removeDoctor(Doctor doctor){

    }

    public void addPharmacists(){

    }

    public void updatePharmacists(){

    }

    public void removePharmacists(){

    }

    public void displayStaff(String filter){
        switch (filter){
            case "role":
                break;
            case "gender":
                break;
            case "age":
                break;
            default:
        }
    }

    public void retrieveAppointment(Appointment appointment) {
        System.out.println("Viewing appointment records for: " + appointment.getAppointmentId());
        System.out.println("Patient ID: " + appointment.getPatientId());
        System.out.println("Doctor ID: " + appointment.getDoctorId());
        System.out.println("Appointment Status: " + appointment.getStatus());
        System.out.println("Appointment Date: " + appointment.getAppointmentDate());
        System.out.println("Appointment Time: " + appointment.getAppointmentTime());
        System.out.println("Appointment Outcome Record: " + appointment.getOutcomeRecord());
    }

    public void updateMedicineStock(Medicine medicine, int stock){
        medicine.setStock(stock);
        System.out.println("Stock for medicine: " + medicine.getName() + " is updated to " + stock);
    }

    public void removeMedicineStock(Medicine medicine, int stock){
        int currentStock = medicine.getStock();

        if(currentStock - stock > 0){
            currentStock = currentStock - stock;
        }
        else{
            currentStock = 0;
        }

        medicine.setStock(currentStock);

        System.out.println("Stock for medicine: " + medicine.getName() + " is updated to " + currentStock);
    }

    public void addMedicineStock(Medicine medicine, int stock){
        int currentStock = medicine.getStock();
        medicine.setStock(currentStock + stock);

        System.out.println("Stock for medicine: " + medicine.getName() + " is updated to " + currentStock);
    }

    public void updateMedicineLowStockLevelAlert(Medicine medicine, int stock){
        medicine.setLowStockAlert(stock);
        System.out.println("Low Stock level alert for medicine: " + medicine.getName() + " is updated to " + stock);
    }

    public void approveReplenishmentRequest(){

    }
}
