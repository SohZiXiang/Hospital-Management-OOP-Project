package models.entities;
import models.enums.*;

/**
 * Abstract class representing a staff member in the system.
 * This class extends the User class and includes additional
 * attributes specific to staff, such as staff ID and age.
 */
public abstract class Staff extends User{
    private String staffId;
    private int age;

    /**
     * Constructs a Staff object with the specified details.
     *
     * @param hospitalID the hospital ID associated with the staff member
     * @param staffId the unique staff ID for the staff member
     * @param name the name of the staff member
     * @param gender the gender of the staff member
     * @param age the age of the staff member
     */
    public Staff(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID = staffId, name , gender);
        this.staffId = staffId;
        this.age = age;
    }

    /**
     * Returns the staff ID of the staff member.
     *
     * @return the staff ID
     */
    public String getStaffId() {
        return staffId;
    }


    /**
     * Sets the staff ID of the staff member.
     *
     * @param staffId the new staff ID to set
     */
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }


    /**
     * Returns the age of the staff member.
     *
     * @return the age of the staff member
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of the staff member.
     *
     * @param age the new age to set
     */
    public void setAge(int age) {
        this.age = age;
    }
}
