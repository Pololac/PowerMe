package com.powerme.exception;

public class ChargingStationNotFoundException extends ServiceException {

    public ChargingStationNotFoundException() {
        super("Charging station not found");
    }
}
