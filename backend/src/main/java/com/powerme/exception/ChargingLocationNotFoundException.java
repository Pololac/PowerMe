package com.powerme.exception;

public class ChargingLocationNotFoundException extends ServiceException {

    public ChargingLocationNotFoundException() {
        super("Station de recharge non trouvée");
    }
    
    public ChargingLocationNotFoundException(Long id) {
        super("Charging location not found with id " + id);
    }
}

