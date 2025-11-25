package com.powerme.config;

import com.powerme.service.mail.MailService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MailServiceMockConfig {

    @Bean
    public MailService mailService() {
        return Mockito.mock(MailService.class);
    }
}
