package com.powerme.exception;

public class ChargingStationNotFoundException extends ServiceException {

    public ChargingStationNotFoundException() {
        super("Borne de recharge non trouvée");
    }

    public ChargingStationNotFoundException(Long id) {
        super("Borne de recharge non trouvée avec l'id " + id);
    }
}
