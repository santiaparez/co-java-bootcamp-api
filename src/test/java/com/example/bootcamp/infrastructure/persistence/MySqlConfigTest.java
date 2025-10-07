package com.example.bootcamp.infrastructure.persistence;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MySqlConfigTest {

    private final MySqlConfig config = new MySqlConfig();

    @Test
    void buildsConnectionFactoryFromProperties() {
        MySqlProperties properties = new MySqlProperties("localhost", 3306, "bootcamp", "user", "secret");

        ConnectionFactory factory = config.connectionFactory(properties);

        assertNotNull(factory);
    }

    @Test
    void exposesTemplateBean() {
        MySqlProperties properties = new MySqlProperties("localhost", 3306, "bootcamp", "user", "secret");
        ConnectionFactory factory = config.connectionFactory(properties);

        assertNotNull(config.r2dbcEntityTemplate(factory));
    }
}
