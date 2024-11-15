package models.enums;

/**
 * The PrescriptionStatus enum represents the status of a prescribed medication in the system.
 */
public enum PrescriptionStatus {
    /**
     * The prescription is yet to be dispensed.
     */
    PENDING,

    /**
     * The prescription has been successfully dispensed to the patient.
     */
    DISPENSED,

    /**
     * No prescription is associated or no action is required.
     */
    NIL
}
