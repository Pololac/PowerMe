package com.powerme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordDto {

    // Optionnel : présent si reset par email, absent si user connecté
    private String token;

    @NotBlank
    @Size(min = 8, message = "Password should have at least 8 characters")
    // PROD : au moins 1 min, 1 maj, 1 chiffre, 1 caractère spécial et > 8 caractères
    // @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    private String newPassword;

    public ResetPasswordDto() {
    }

    public ResetPasswordDto(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

