package com.powerme.exception;

public class ChargingStationNotFoundException extends ServiceException {

    public ChargingStationNotFoundException() {
        super("Borne de recharge non trouvée");
    }
}
