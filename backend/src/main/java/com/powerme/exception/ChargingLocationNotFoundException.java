package com.powerme.exception;

public class ChargingLocationNotFoundException extends ServiceException {

    public ChargingLocationNotFoundException() {
        super("Station de recharge non trouvée");
    }
    
    public ChargingLocationNotFoundException(Long id) {
        super("Station de recharge non trouvée avec l'id " + id);
    }
}

