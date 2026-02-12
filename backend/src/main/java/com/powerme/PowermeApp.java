package com.powerme;

import com.powerme.service.security.JwtProperties;
import com.powerme.service.security.RefreshCookieProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        JwtProperties.class,
        RefreshCookieProperties.class
})
public class PowermeApp {

    public static void main(String[] args) {
        SpringApplication.run(PowermeApp.class, args);
    }

}
