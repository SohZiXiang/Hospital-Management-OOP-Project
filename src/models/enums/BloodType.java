package models.enums;

public enum BloodType {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String displayValue;

    BloodType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    //get BloodType from string input
    public static BloodType fromString(String value) {
        for (BloodType bloodType : BloodType.values()) {
            if (bloodType.displayValue.equalsIgnoreCase(value)) {
                return bloodType;
            }
        }
        throw new IllegalArgumentException("Unknown blood type: " + value);
    }
}