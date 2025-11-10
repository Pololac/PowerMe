package com.powerme.service.mail;

import com.powerme.entity.User;

public interface MailService {

    void sendActivationEmail(User user, String token);

    void sendResetPasswordEmail(User user, String token);
}

