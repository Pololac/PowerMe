package com.powerme.unit;

import com.powerme.repository.UserRepository;
import com.powerme.service.account.AccountServiceImpl;
import com.powerme.service.mail.MailService;
import com.powerme.service.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Test
    void registerShouldPersistIfUserNotExists() {

    }

    @Test
    void registerShouldThrowExceptionWhenEmailAlreadyExists() {

    }

    @Test
    void registerShouldHashPasswordBeforeSaving() {

    }

    @Test
    void registerShouldSendActivationEmail() {

    }

    @Test
    void registerShouldNotSendEmailIfSaveFails() {

    }
}
