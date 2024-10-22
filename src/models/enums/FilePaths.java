package models.enums;

public enum FilePaths {
    STAFF_DATA("data/Staff_List.xlsx"),
    PATIENT_DATA("data/Patient_List.xlsx"),
    AUTH_DATA("data/Auth_Data.xlsx"),
    INV_dATA("data/Medicine_List.xlsx");

    private final String path;

    FilePaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
