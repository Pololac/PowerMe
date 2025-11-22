package com.powerme.integration.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.powerme.config.AbstractIntegrationTest;
import com.powerme.entity.User;
import com.powerme.repository.UserRepository;
import com.powerme.service.mail.MailService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@Import(com.powerme.config.MailServiceMockConfig.class)
@ActiveProfiles("test")
@Transactional
public class RegisterIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepo;

    @Autowired
    MailService mailService;

    @BeforeEach
    void initialData() {
        userRepo.save(new User("john@test.com", "12345678"));
    }

    @Test
    void registerShouldCreateNewUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/account/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "email": "james@test.com",
                                      "password": "12345678"
                                    }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Le nouvel user doit être en base
        assertTrue(userRepo.findByEmail("james@test.com").isPresent());

        //
        verify(mailService, times(1))
                .sendActivationEmail(any(User.class), any(String.class));
    }

    @Test
    void registerShouldReturnConflictWhenEmailExists() throws Exception {

    }
}
