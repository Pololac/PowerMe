package com.powerme;

import com.powerme.config.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;


@SuppressWarnings("SqlNoDataSourceInspection")
@SpringBootTest
class PowermeApplicationIT extends AbstractIntegrationTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Vérifie que le contexte Spring démarre correctement
    @Test
    void contextLoads() {
    }

    // Vérifie que la connexion à la database fonctionne (Docker lancé)
    @Test
    void databaseConnectionWorks() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertThat(result).isEqualTo(1);
    }

    // Vérifie que l'extension PostGIS est disponible
    @Test
    void postgisExtensionIsAvailable() {
        String version = jdbcTemplate.queryForObject(
                "SELECT PostGIS_Version()",
                String.class
        );
        assertThat(version).isNotNull();
        assertThat(version).contains("3.5");
    }
}
