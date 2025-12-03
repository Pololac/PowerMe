package com.powerme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(

    /**
     * Token de réinitialisation (optionnel si utilisateur déjà connecté).
     * Présent uniquement pour le flux "forgot password".
     */
    String token,

    @NotBlank
    @Size(min = 8, message = "Password should have at least 8 characters")
    // PROD : au moins 1 min, 1 maj, 1 chiffre, 1 caractère spécial et > 8 caractères
    // @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    String newPassword
) {}

