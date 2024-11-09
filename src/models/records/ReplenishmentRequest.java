package models.records;

import java.time.LocalDate;

/**
 * Represents a request for replenishment of a specific medicine at a hospital.
 * This class contains details about the request, including the requester, medicine,
 * requested amount, status, and request date.
 */
public class ReplenishmentRequest {
    private int requestId;
    private String hospitalId;
    private String requesterName;
    private String medicineName;
    private int requestedAmount;
    private String status;
    private LocalDate requestDate;

    /**
     * Constructs a new ReplenishmentRequest with the given details.
     * The request status is set to "pending" by default, and the request date is set to the current date.
     *
     * @param requestId       The unique identifier for the request.
     * @param hospitalId      The hospital's unique identifier.
     * @param requesterName   The name of the requester.
     * @param medicineName    The name of the medicine to be replenished.
     * @param requestedAmount The amount of medicine requested.
     */
    public ReplenishmentRequest(int requestId, String hospitalId, String requesterName, String medicineName, int requestedAmount) {
        this.requestId = requestId;
        this.hospitalId = hospitalId;
        this.requesterName = requesterName;
        this.medicineName = medicineName;
        this.requestedAmount = requestedAmount;
        this.status = "pending";  // Default status
        this.requestDate = LocalDate.now();  //Set to current date
    }

    /**
     * Constructs a new ReplenishmentRequest with the specified details, including a custom status and request date.
     *
     * @param requestId       The unique identifier for the request.
     * @param hospitalId      The hospital's unique identifier.
     * @param requesterName   The name of the requester.
     * @param medicineName    The name of the medicine to be replenished.
     * @param requestedAmount The amount of medicine requested.
     * @param status          The current status of the request.
     * @param requestDate     The date the request was made, in the format "yyyy-MM-dd".
     */
    public ReplenishmentRequest(int requestId, String hospitalId, String requesterName,
                                String medicineName, int requestedAmount, String status, String requestDate) {
        this.requestId = requestId;
        this.hospitalId = hospitalId;
        this.requesterName = requesterName;
        this.medicineName = medicineName;
        this.requestedAmount = requestedAmount;
        this.status = status;
        this.requestDate = LocalDate.parse(requestDate);
    }

    /**
     * Gets the unique identifier for the request.
     *
     * @return The request ID.
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Gets the hospital's unique identifier.
     *
     * @return The hospital ID.
     */
    public String getHospitalId() {
        return hospitalId;
    }

    /**
     * Gets the name of the requester.
     *
     * @return The requester's name.
     */
    public String getRequesterName() {
        return requesterName;
    }

    /**
     * Gets the name of the medicine to be replenished.
     *
     * @return The medicine name.
     */
    public String getMedicineName() {
        return medicineName;
    }

    /**
     * Gets the amount of medicine requested.
     *
     * @return The requested amount.
     */
    public int getRequestedAmount() {
        return requestedAmount;
    }

    /**
     * Gets the current status of the request.
     *
     * @return The request status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the request.
     *
     * @param status The new status of the request.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the date the request was made.
     *
     * @return The request date.
     */
    public LocalDate getRequestDate() {
        return requestDate;
    }

    /**
     * Returns a string representation of the ReplenishmentRequest, including requester information,
     * medicine name, requested amount, status, and date.
     *
     * @return A string representation of the request details.
     */
    @Override
    public String toString() {
        return "Request from: " + requesterName + " (" + hospitalId + "), Medicine: " + medicineName + ", Requested Amount: " + requestedAmount + ", Status: " + status + ", Date: " + requestDate;
    }
}
