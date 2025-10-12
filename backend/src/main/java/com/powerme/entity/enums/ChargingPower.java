package com.powerme.entity.enums;

/**
 * Puissances standard pour bornes de recharge électrique (en kW).
 */
public enum ChargingPower {
    // Puissances pour Type 2S et Type 2 (AC - Courant Alternatif)
    POWER_3_7(3.7, "3,7 kW - Prise domestique renforcée"),
    POWER_7_4(7.4, "7,4 kW - Wallbox standard"),
    POWER_11(11.0, "11 kW - Wallbox triphasée"),
    POWER_22(22.0, "22 kW - Borne publique AC"),

    // Puissances pour CCS et CHAdeMO (DC - Courant Continu)
    POWER_50(50.0, "50 kW - Charge rapide DC"),
    POWER_100(100.0, "100 kW - Charge ultra-rapide"),
    POWER_150(150.0, "150 kW - Superchargeur"),
    POWER_350(350.0, "350 kW - Hyperchargeur");

    private final double kilowatts;
    private final String displayName;

    ChargingPower(double kilowatts, String displayName) {
        this.kilowatts = kilowatts;
        this.displayName = displayName;
    }

    public double getKilowatts() {
        return kilowatts;
    }

    public String getDisplayName() {
        return displayName;
    }
}
