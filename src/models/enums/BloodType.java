package models.enums;

/**
 * Enum representing the different blood types.
 * Each blood type is associated with a display value that represents it in a human-readable format.
 */
public enum BloodType {
    /**
     * Blood type A positive.
     */
    A_POSITIVE("A+"),

    /**
     * Blood type A negative.
     */
    A_NEGATIVE("A-"),

    /**
     * Blood type B positive.
     */
    B_POSITIVE("B+"),

    /**
     * Blood type B negative.
     */
    B_NEGATIVE("B-"),

    /**
     * Blood type AB positive.
     */
    AB_POSITIVE("AB+"),

    /**
     * Blood type AB negative.
     */
    AB_NEGATIVE("AB-"),

    /**
     * Blood type O positive.
     */
    O_POSITIVE("O+"),

    /**
     * Blood type O negative.
     */
    O_NEGATIVE("O-");

    private final String displayValue;

    /**
     * Constructor for BloodType enum.
     *
     * @param displayValue The display value representing the blood type.
     */
    BloodType(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * Gets the display value of the blood type.
     *
     * @return The display value of the blood type.
     */
    public String getDisplayValue() {
        return displayValue;
    }


    /**
     * Returns the corresponding BloodType enum constant for the given string value.
     *
     * @param value The string representation of the blood type.
     * @return The BloodType enum constant that matches the given value.
     * @throws IllegalArgumentException If the value does not match any known blood type.
     */
    public static BloodType fromString(String value) {
        for (BloodType bloodType : BloodType.values()) {
            if (bloodType.displayValue.equalsIgnoreCase(value)) {
                return bloodType;
            }
        }
        throw new IllegalArgumentException("Unknown blood type: " + value);
    }
}