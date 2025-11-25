package com.powerme;

import com.powerme.service.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class PowermeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PowermeApplication.class, args);
    }

}
