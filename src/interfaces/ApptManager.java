package interfaces;

import models.entities.Appointment;
import models.enums.DoctorAvailability;

import java.time.LocalDate;
import java.util.Optional;

public interface ApptManager {
    void cancelAppt(Appointment oneAppt);
    Optional<Appointment> findAppt(LocalDate desiredDate, String startTime);
}
