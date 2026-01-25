package com.powerme.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions pour l'application PowerMe. Intercepte et formate toutes les
 * exceptions levées par les contrôleurs.
 * <p>
 * Conventions : - Messages utilisateur : FRANÇAIS - Logs développeur : ANGLAIS
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${app.debug:false}")
    private boolean debugMode;

    // ========== 401 - UNAUTHORIZED ==========

    /**
     * Gère les tokens JWT invalides ou expirés.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidToken(InvalidTokenException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Token Invalide");
        pd.setDetail(ex.getMessage());

        logger.warn("Invalid token: {}", ex.getMessage());
        return pd;
    }

    /**
     * Gère les erreurs d'authentification Spring Security (email inexistant ou mot de passe
     * incorrect).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Email ou mot de passe incorrect. Veuillez réessayer."
        );
        pd.setTitle("Identifiants invalides");

        logger.warn("Failed login attempt: Bad credentials");
        return pd;
    }

    // ========== 403 - FORBIDDEN ==========

    /**
     * Gère les comptes non activés (Spring Security).
     */
    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabled(DisabledException ex) {
        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Votre compte n'est pas encore activé. Veuillez vérifier votre email pour le lien d'activation."
        );
        pd.setTitle("Compte non activé");

        logger.warn("Login attempt on non-activated account");
        return pd;
    }

    /**
     * Gère les comptes bloqués (supprimés logiquement).
     */
    @ExceptionHandler(LockedException.class)
    public ProblemDetail handleLocked(LockedException ex) {
        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Ce compte a été supprimé. Veuillez contacter l'administrateur pour obtenir de l'aide."
        );
        pd.setTitle("Compte supprimé");

        logger.warn("Login attempt on deleted account");
        return pd;
    }

    /**
     * Gère les accès non autorisés (authentifié mais pas les droits).
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ProblemDetail handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Accès refusé");
        pd.setDetail(ex.getMessage());

        logger.warn("Access denied: {}", ex.getMessage());
        return pd;
    }

    // ========== 404 - NOT FOUND ==========

    /**
     * Gère les ressources introuvables (utilisateur, borne, réservation).
     */
    @ExceptionHandler({
            UserNotFoundException.class,
            ChargingLocationNotFoundException.class,
            ChargingStationNotFoundException.class,
            BookingNotFoundException.class
    })
    public ProblemDetail handleNotFound(ServiceException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Ressource non trouvée");
        pd.setDetail(ex.getMessage());

        logger.info("Resource not found: {}", ex.getMessage());
        return pd;
    }

    /**
     * Gère les cas où une entité demandée n'existe pas. Étend RuntimeException.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Ressource non trouvée");
        pd.setDetail(ex.getMessage());

        logger.info("Entity not found: {}", ex.getMessage());
        return pd;
    }

    // ========== 409 - CONFLICT ==========

    /**
     * Gère les tentatives de création d'un utilisateur avec un email déjà existant.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Utilisateur déjà existant");
        pd.setDetail(ex.getMessage());

        logger.info("User already exists: {}", ex.getMessage());
        return pd;
    }

    /**
     * Gère les tentatives de réservation sur des slots déjà réservées.
     */
    @ExceptionHandler(BookingConflictException.class)
    public ProblemDetail handleBookingConflict(BookingConflictException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflit de réservation");
        pd.setDetail(ex.getMessage());
        pd.setProperty("slots", ex.getDetails());

        logger.info("Booking conflict: {}", ex.getMessage());
        return pd;
    }


    /**
     * Gère les violations de contraintes de base de données (clés uniques, clés étrangères).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        String detail = "Violation d'intégrité des données";

        // Détecte les cas spécifiques sans exposer la structure de la BDD
        String message = ex.getMessage().toLowerCase();

        if (message.contains("uk_user_email") || message.contains("email")) {
            detail = "Cet email est déjà utilisé";
        } else if (message.contains("fk_charging_location_owner")) {
            detail = "Impossible de supprimer : cet utilisateur possède des lieux de recharge actifs";
        } else if (message.contains("not-null") || message.contains("null")) {
            detail = "Un champ obligatoire est manquant";
        }

        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, detail);
        pd.setTitle("Erreur d'intégrité des données");

        logger.warn("Data integrity violation: {}", ex.getMessage());
        return pd;
    }

    // ========== 400 - BAD REQUEST ==========

    /**
     * Gère les erreurs de validation métier (ex: date de réservation antérieure à la date
     * actuelle).
     */
    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Erreur de validation");
        pd.setDetail(ex.getMessage());

        logger.info("Validation error: {}", ex.getMessage());
        return pd;
    }

    /**
     * Gère les erreurs dues à des requêtes invalides côté client.
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ProblemDetail handleBadRequest(RuntimeException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Requête invalide");
        pd.setDetail(ex.getMessage());

        logger.warn("Bad request: {}", ex.getMessage());
        return pd;
    }

    /**
     * Gère les erreurs de validation des données entrantes (@Valid sur les DTOs).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage()
                                : "Valeur invalide",
                        (existing, replacement) -> existing + "; " + replacement
                ));

        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Erreur de validation");
        pd.setDetail("Plusieurs erreurs de validation sont survenues");
        pd.setProperty("errors", errors);

        logger.info("Validation errors: {}", errors);
        return pd;
    }

    /**
     * Gère les violations de contraintes JPA (ex: @NotNull, @Size).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Violation de contrainte");
        pd.setDetail(ex.getMessage());

        logger.warn("Constraint violation: {}", ex.getMessage());
        return pd;
    }

    /**
     * Gère les JSON mal formatés (ex: date invalide, nombre attendu mais string reçue).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMalformedJson(HttpMessageNotReadableException ex) {
        String detail = "Format de requête invalide";

        // Détecte les erreurs courantes
        String message = ex.getMessage().toLowerCase();

        if (message.contains("localdatetime") || message.contains("date")) {
            detail = "Format de date invalide. Format attendu : yyyy-MM-ddTHH:mm:ss";
        } else if (message.contains("number") || message.contains("integer")) {
            detail = "Format de nombre invalide";
        } else if (message.contains("boolean")) {
            detail = "Valeur booléenne invalide. Attendu : true ou false";
        }

        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setTitle("Format de requête invalide");

        logger.info("Malformed JSON: {}", ex.getMessage());
        return pd;
    }

    /**
     * Gère toutes les autres ServiceException (exceptions métier non spécifiques).
     */
    @ExceptionHandler(ServiceException.class)
    public ProblemDetail handleServiceException(ServiceException ex) {
        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        pd.setTitle("Erreur métier");

        logger.info("Business error: {}", ex.getMessage());
        return pd;
    }

    // ========== 503 - SERVICE UNAVAILABLE ==========

    /**
     * Gère les problèmes d'accès à la base de données.
     */
    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDataAccess(DataAccessException ex) {
        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service temporairement indisponible. Veuillez réessayer plus tard."
        );
        pd.setTitle("Erreur de base de données");

        logger.error("Database access error: {}", ex.getMessage(), ex);
        return pd;
    }

    /**
     * Gère les échecs d'envoi d'email.
     */
    @ExceptionHandler(EmailDeliveryException.class)
    public ProblemDetail handleEmailDelivery(EmailDeliveryException ex,
            HttpServletRequest request) {
        var pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Impossible d'envoyer l'email demandé. Veuillez réessayer plus tard."
        );
        pd.setTitle("Échec d'envoi d'email");
        pd.setProperty("recipient", ex.getRecipient());
        pd.setProperty("subject", ex.getSubject());

        logger.error("Failed to send email to {} with subject '{}'",
                ex.getRecipient(), ex.getSubject(), ex);
        return pd;
    }

    // ========== 500 - INTERNAL SERVER ERROR ==========

    /**
     * Gère toutes les exceptions non prévues. En mode debug, expose le message d'erreur. En
     * production, retourne un message générique.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Erreur inattendue");

        if (debugMode) {
            pd.setDetail(ex.getMessage());
            logger.error("Unhandled exception in DEBUG mode", ex);
        } else {
            pd.setDetail("Une erreur inattendue s'est produite. Veuillez réessayer plus tard.");
            logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        }

        return pd;
    }
}
