package models.entities;
import models.enums.*;

public abstract class Staff extends User{
    private String staffId;
    private int age;

    public Staff(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID = staffId, name , gender);
        this.staffId = staffId;
        this.age = age;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}
