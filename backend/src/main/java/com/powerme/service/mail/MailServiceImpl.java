package com.powerme.service.mail;

import com.powerme.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendActivationEmail(User user, String token) {
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        String message = """
                To validate your account click on <a href="%s">this link</a>
                """
                .formatted(serverUrl + "/api/account/validate/" + token);
        sendBaseMail(user.getEmail(), message, "Spring Holiday Email Validation");
    }

    @Override
    public void sendResetPasswordEmail(User user, String token) {
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        String message = """
                To reset your password click on <a href="%s">this link</a>
                """
                .formatted(serverUrl + "/reset-password.html?token=" + token);
        sendBaseMail(user.getEmail(), message, "Spring Holiday Reset Password");
    }


    private void sendBaseMail(String to, String message, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(to);
            helper.setFrom("springholiday@human-booster.fr");
            helper.setSubject(subject);

            helper.setText(message, true); //Temporaire, email à remplacer par un JWT
            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Unable to send mail", e);
        }
    }

}

