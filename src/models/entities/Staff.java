package models.entities;
import models.enums.*;

public abstract class Staff extends User{
    private String staffId;
    private String name;
    private Gender gender;
    private int age;

    public Staff(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID);
        this.staffId = staffId;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    public String getStaffId() {
        return staffId;
    }
//    public void setStaffId(String staffId) {
//        this.staffId = staffId;
//    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}
