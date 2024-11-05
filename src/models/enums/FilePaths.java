package models.enums;

/**
 * Enum representing file paths for various data files used in the application.
 */
public enum FilePaths {
    /**
     * File path for the staff data.
     */
    STAFF_DATA("data/Staff_List.xlsx"),

    /**
     * File path for the patient data.
     */
    PATIENT_DATA("data/Patient_List.xlsx"),

    /**
     * File path for the authentication data.
     */
    AUTH_DATA("data/Auth_Data.xlsx"),

    /**
     * File path for the inventory data.
     */
    INV_DATA("data/Medicine_List.xlsx"),

    /**
     * File path for the replenishment requests data.
     */
    REPLENISH_REQ_DATA("data/ReplenishmentRequests.xlsx"),

    /**
     * File path for the appointment data.
     */
    APPT_DATA("data/Appointment_List.xlsx"),

    /**
     * File path for the doctor availability data.
     */
    DOCAVAIL_DATA("data/Availability_List.xlsx"),

    /**
     * File path for the appointment outcome records.
     */
    APPTOUTCOME("data/Outcome_Record.xlsx"),

    /**
     * File path for the activity logs.
     */
    ACTIVITY_LOG("data/Activity_Logs.xlsx");

    private final String path;

    FilePaths(String path) {
        this.path = path;
    }


    /**
     * Returns the file path associated with the enum constant.
     *
     * @return the file path as a String.
     */
    public String getPath() {
        return path;
    }
}
