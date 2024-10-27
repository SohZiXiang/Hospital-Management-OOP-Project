package models.enums;

public enum FilePaths {
    STAFF_DATA("data/Staff_List.xlsx"),
    PATIENT_DATA("data/Patient_List.xlsx"),
    AUTH_DATA("data/Auth_Data.xlsx"),
    INV_DATA("data/Medicine_List.xlsx"),
    REPLENISH_REQ_DATA("data/ReplenishmentRequests.xlsx"),
    APPT_DATA("data/Appointment_List.xlsx"),
    DOCAVAIL_DATA("data/Availability_List.xlsx"),
    ACTIVITY_LOG("data/Activity_Logs.xlsx");

    private final String path;

    FilePaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
