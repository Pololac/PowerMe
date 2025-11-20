package com.powerme.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${app.debug:false}")
    private boolean debugMode;

    // 401 - Unauthorized
    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidToken(InvalidTokenException ex) {

        var pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Invalid Token");
        pd.setDetail(ex.getMessage());

        logger.warn("Invalid token: {}", ex.getMessage());
        return pd;
    }

    // 401 - Unauthorized

    /**
     * Gère les erreurs d'authentification de Spring Security (email inexistant ou mot de passe
     * incorrect).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException e) {
        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Wrong email or password. Please try again."
        );
        pd.setTitle("Invalid Credentials");

        logger.warn("Failed login attempt: Bad credentials");
        return pd;
    }

    // 403 - Forbidden

    /**
     * Gère les comptes non activés (Spring Security).
     */
    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabled(DisabledException e) {
        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Your account is not yet activated. Please check your email for the activation link."
        );
        pd.setTitle("Account Not Activated");

        logger.warn("Login attempt on non-activated account");
        return pd;
    }

    // 403 - Forbidden

    /**
     * Gère les comptes bloqués = supprimés (Spring Security).
     */
    @ExceptionHandler(LockedException.class)
    public ProblemDetail handleLocked(LockedException e) {

        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "This account has been deleted. Please contact the administrator for assistance."
        );
        pd.setTitle("Account Deleted");

        logger.warn("Login attempt on deleted account");
        return pd;
    }

    // 403 - Forbidden pour tous les autres cas
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ProblemDetail handleUnauthorizedAccess(UnauthorizedAccessException ex) {

        var pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Access Denied");
        pd.setDetail(ex.getMessage());

        logger.warn("Unauthorized access attempt: {}", ex.getMessage());
        return pd;
    }

    // 409 - Conflict
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {

        var pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("User Already Exists");
        pd.setDetail(ex.getMessage());

        logger.info("User already exists: {}", ex.getMessage());
        return pd;
    }

    // 404 - Not Found (ressources introuvables)
    @ExceptionHandler({
            UserNotFoundException.class,
            ChargingStationNotFoundException.class,
            BookingNotFoundException.class
    })
    public ProblemDetail handleNotFound(ServiceException ex) {
        var pd = ProblemDetail.forStatus(
                HttpStatus.NOT_FOUND);
        pd.setTitle("Resource Not Found");
        pd.setDetail(ex.getMessage());

        logger.info("Resource not found: {}", ex.getMessage());
        return pd;
    }

    // 400 - Bad Request (données invalides)

    /**
     * Validation métier (par exemple, date réservation antérieure à date actuelle).
     */
    @ExceptionHandler({ValidationException.class})
    public ProblemDetail handleBadRequest(ServiceException ex) {

        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        pd.setTitle("Validation Error");

        logger.info("Business validation error: {}", ex.getMessage());
        return pd;
    }

    /**
     * Gère toutes les autres ServicesException (qui n'ont pas d'Handler spécifique).
     */
    @ExceptionHandler(ServiceException.class)
    public ProblemDetail handleServiceException(ServiceException ex) {

        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        pd.setTitle("Business Error");  // ✅ Titre générique

        logger.warn("Service exception: {}", ex.getMessage());
        return pd;
    }

    /**
     * Validation errors (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {

        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Invalid request");

        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation Error");
        pd.setDetail(msg);

        logger.warn("Validation error: {}", msg);
        return pd;
    }

    /**
     * JPA validation constraints.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraint(ConstraintViolationException ex) {

        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Constraint Violation");
        pd.setDetail(ex.getMessage());

        logger.warn("Constraint violation: {}", ex.getMessage());
        return pd;
    }

    /**
     * Problème d'accès à la BDD.
     */
    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDataAccess(DataAccessException ex) {

        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service temporarily unavailable. Please try again later."
        );
        pd.setTitle("Database Error");

        logger.error("Database access error: {}", ex.getMessage(), ex);
        return pd;
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ProblemDetail handleEmailDelivery(EmailDeliveryException e,
            HttpServletRequest request) {
        // Choix du statut selon la cause (exemple basique)
        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "We could not deliver the requested email. Please try again later."
        );
        pd.setTitle("Email Delivery Failed");
        pd.setProperty("recipient", e.getRecipient());
        pd.setProperty("subject", e.getSubject());

        // ⚠️ Log complet en backend (pour debug)
        logger.error("Failed to send email to {} with subject '{}'",
                e.getRecipient(),
                e.getSubject(),
                e
        );

        return pd;
    }

    /**
     * Exception levée par JPA quand une contrainte de base de données est violée.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {

        String detail = "Data integrity violation";

        // Détecte les cas spécifiques sans exposer la BDD
        String message = ex.getMessage().toLowerCase();

        if (message.contains("uk_user_email") || message.contains("email")) {
            detail = "Email already exists";
        } else if (message.contains("fk_charging_location_owner")) {
            detail = "Cannot delete: this user has active charging location";

        } else if (message.contains("not-null") || message.contains("null")) {
            // NOT NULL constraint
            detail = "Required field is missing";
        }

        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                detail
        );
        pd.setTitle("Data Integrity Error");

        // ⚠️ Log complet en backend (pour debug)
        logger.warn("Data integrity violation: {}", ex.getMessage());

        return pd;
    }

    /**
     * Exception levée quand JSON envoyé mal formaté.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMalformedJson(HttpMessageNotReadableException ex) {

        String detail = "Invalid request format";

        // Détecte les erreurs courantes
        String message = ex.getMessage().toLowerCase();

        if (message.contains("localdatetime") || message.contains("date")) {
            detail = "Invalid date format. Expected: yyyy-MM-ddTHH:mm:ss";
        } else if (message.contains("number") || message.contains("integer")) {
            detail = "Invalid number format";
        } else if (message.contains("boolean")) {
            detail = "Invalid boolean value. Expected: true or false";
        }

        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                detail
        );
        pd.setTitle("Invalid Request Format");

        logger.info("Malformed JSON: {}", ex.getMessage());

        return pd;
    }

    // 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Unexpected Error");

        if (debugMode) {
            pd.setDetail(ex.getMessage());
            logger.error("Unhandled exception in DEBUG mode", ex);
        } else {
            pd.setDetail("An unexpected error occurred. Please try again later.");
            logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        }

        return pd;
    }
}
