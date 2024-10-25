package models.entities;

import java.time.LocalDate;

public class ReplenishmentRequest {
    private int requestId;
    private String hospitalId;
    private String requesterName;
    private String medicineName;
    private int requestedAmount;
    private String status;
    private LocalDate requestDate;

    public ReplenishmentRequest(int requestId, String hospitalId, String requesterName, String medicineName, int requestedAmount) {
        this.requestId = requestId;
        this.hospitalId = hospitalId;
        this.requesterName = requesterName;
        this.medicineName = medicineName;
        this.requestedAmount = requestedAmount;
        this.status = "pending";  // Default status
        this.requestDate = LocalDate.now();  //Set to current date
    }

    public int getRequestId() {
        return requestId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getRequestedAmount() {
        return requestedAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    @Override
    public String toString() {
        return "Request from: " + requesterName + " (" + hospitalId + "), Medicine: " + medicineName + ", Requested Amount: " + requestedAmount + ", Status: " + status + ", Date: " + requestDate;
    }
}
