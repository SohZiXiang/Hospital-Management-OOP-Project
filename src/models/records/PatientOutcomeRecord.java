package models.records;

public class PatientOutcomeRecord {
    private String appointmentId;
    private String serviceType;
    private String consultationNote;
    private String medicine;
    private String medicineStatus;
    private String outcomeStatus;

    public PatientOutcomeRecord(String appointmentId, String serviceType, String consultationNote,
                                String medicine, String medicineStatus, String outcomeStatus) {
        this.appointmentId = appointmentId;
        this.serviceType = serviceType;
        this.consultationNote = consultationNote;
        this.medicine = medicine;
        this.medicineStatus = medicineStatus;
        this.outcomeStatus = outcomeStatus;
    }

    // Getters
    public String getAppointmentId() {
        return appointmentId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getConsultationNote() {
        return consultationNote;
    }

    public String getMedicine() {
        return medicine;
    }

    public String getMedicineStatus() {
        return medicineStatus;
    }

    public String getOutcomeStatus() {
        return outcomeStatus;
    }

    // Setters
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setConsultationNote(String consultationNote) {
        this.consultationNote = consultationNote;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public void setMedicineStatus(String medicineStatus) {
        this.medicineStatus = medicineStatus;
    }

    public void setOutcomeStatus(String outcomeStatus) {
        this.outcomeStatus = outcomeStatus;
    }
}
