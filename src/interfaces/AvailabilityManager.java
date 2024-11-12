package interfaces;

import models.entities.Appointment;
import models.enums.DoctorAvailability;

public interface AvailabilityManager {
    void updateAvailabilitySlot(Appointment oneAppt, DoctorAvailability changedStatus, String availFilePath);
    void viewAllAvail(String doctorId);
}
