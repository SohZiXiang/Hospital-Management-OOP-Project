package models.entities;

import models.enums.Gender;

public class Administrator extends Staff{
    public Administrator(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
    }

    @Override
    public String getRole() {
        return "administrator";
    }
}
