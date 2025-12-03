package com.powerme.exception;

public class EmailDeliveryException extends ServiceException {

    private final String recipient;
    private final String subject;

    public EmailDeliveryException(String recipient, String subject, Throwable cause) {
        super("Echec pour envoyer l'email intitulé '" + subject + "' à " + recipient, cause);
        this.recipient = recipient;
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }
}
