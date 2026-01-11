package com.powerme.enums;

/**
 * Puissances standard pour bornes de recharge électrique (en kW).
 */
public enum ChargingPower {
    // Puissances pour Type 2S et Type 2 (AC - Courant Alternatif)
    AC_3_7(3.7),
    AC_7_4(7.4),
    AC_11(11.0),
    AC_22(22.0),

    // Puissances pour CCS et CHAdeMO (DC - Courant Continu)
    DC_50(50.0),
    DC_100(100.0),
    DC_150(150.0),
    DC_350(350.0);

    private final double kilowatts;

    ChargingPower(double kilowatts) {
        this.kilowatts = kilowatts;
    }

    public double getKilowatts() {
        return kilowatts;
    }

    public boolean isCompatibleWith(SocketType socket) {
        return switch (socket) {
            case TYPE_2, TYPE_2S -> this.name().startsWith("AC_");
            case CCS, CHADEMO -> this.name().startsWith("DC_");
        };
    }
}
