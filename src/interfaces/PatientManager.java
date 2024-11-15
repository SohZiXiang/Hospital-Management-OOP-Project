package interfaces;

import models.entities.Patient;

/**
 * The PatientManager interface defines methods for managing patient data,
 * including retrieving patient lists, displaying patient records, and updating medical records.
 */
public interface PatientManager {
    /**
     * Retrieves and loads the list of patients.
     */
    void getPatientList();

    /**
     * Updates the patient data by reloading the patient list.
     */
    void updateData();

    /**
     * Resets the patient data, clearing the current patient list.
     */
    void resetData();

    /**
     * Displays all patient records, showing detailed information for each patient.
     */
    void showAllPatientsRecords();

    /**
     * Filters and displays the details of a specific patient based on their ID.
     *
     * @param patientID The ID of the patient to filter and display.
     */
    void filterPatients(String patientID);

    /**
     * Checks whether a patient with the specified ID is valid (exists in the list).
     *
     * @param patientID The ID of the patient to check.
     * @return The Patient object if found, or null if not found.
     */
    Patient checkWhetherPatientValid(String patientID);

    /**
     * Updates the medical record of a specific patient, adding new diagnosis and treatment information.
     *
     * @param patient       The Patient object whose record is to be updated.
     * @param newDiagnosis  The new diagnosis to add to the patient's record.
     * @param newTreatment  The new treatment to add to the patient's record.
     */
    void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment);
}
