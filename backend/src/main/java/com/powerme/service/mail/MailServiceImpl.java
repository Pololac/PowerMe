package com.powerme.service.mail;

import com.powerme.entity.User;
import com.powerme.exception.EmailDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendActivationEmail(User user, String token) {
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        String message = """
                Pour activer votre compte, cliquez sur <a href="%s">ce lien</a>
                """
                .formatted(serverUrl + "/api/account/validate/" + token);
        sendBaseMail(user.getEmail(), message, "PowerMe - Activation du compte");
    }

    @Override
    public void sendResetPasswordEmail(User user, String token) {
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        String message = """
                Pour réinitialiser votre mot de passe, cliquez sur <a href="%s">ce lien</a>
                """
                .formatted(serverUrl + "/reset-password.html?token=" + token);
        sendBaseMail(user.getEmail(), message, "PowerMe - Réinitialisation du mot de passe");
    }


    private void sendBaseMail(String to, String message, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(to);
            helper.setFrom("admin@powerme.fr");
            helper.setSubject(subject);

            helper.setText(message, true); // Temporaire, email à remplacer par un JWT
            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            throw new EmailDeliveryException(to, subject, e);
        }
    }

}

