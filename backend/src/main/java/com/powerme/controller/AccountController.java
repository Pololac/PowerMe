package com.powerme.controller;

import com.powerme.dto.SimpleMessageDto;
import com.powerme.dto.UpdatePasswordDto;
import com.powerme.dto.UserRegisterDto;
import com.powerme.entity.User;
import com.powerme.mapper.UserMapper;
import com.powerme.service.account.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST gérant la création et la gestion des comptes utilisateurs.
 *
 * <p>Expose les endpoints publics et authentifiés utilisés par le front-end pour :
 * <ul>
 *   <li>Inscrire un nouvel utilisateur et déclencher l’envoi d’un e-mail de validation</li>
 *   <li>Valider un compte utilisateur à l’aide d’un token reçu par e-mail</li>
 *   <li>Demander la réinitialisation du mot de passe en générant un lien de réinitialisation</li>
 *   <li>Mettre à jour le mot de passe d’un utilisateur connecté</li>
 * </ul>
 *
 * <p>Les endpoints d’inscription, de validation et de demande de réinitialisation sont publics,
 * tandis que la mise à jour du mot de passe requiert un utilisateur authentifié.</p>
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private final UserMapper userMapper;

    public AccountController(AccountService accountService, UserMapper userMapper) {
        this.accountService = accountService;
        this.userMapper = userMapper;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public SimpleMessageDto register(@Valid @RequestBody UserRegisterDto dto) {
        User user = userMapper.toEntity(dto);
        accountService.register(user);
        return new SimpleMessageDto(
                "Registration successful. Check your email to validate your account.");
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/activate/{token}")
    public SimpleMessageDto activate(@PathVariable String token) {
        accountService.activateAccount(token);
        return new SimpleMessageDto("Account activated.");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/password/{email}")
    public SimpleMessageDto resetPassword(@PathVariable String email) {
        accountService.resetPassword(email);
        return new SimpleMessageDto("If the email exists, a reset link has been sent.");
    }

    /**
     * User forcément connecté pour faire la demande de changement.
     */
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/password")
    public SimpleMessageDto updatePassword(@Valid @RequestBody UpdatePasswordDto dto,
            @AuthenticationPrincipal User user) {
        accountService.updatePassword(user, dto.getNewPassword());
        return new SimpleMessageDto("Password updated.");
    }
}
