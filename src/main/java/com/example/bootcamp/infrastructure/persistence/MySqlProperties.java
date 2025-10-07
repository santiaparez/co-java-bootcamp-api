package com.example.bootcamp.infrastructure.persistence;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "app.datasource")
public record MySqlProperties(
        String host,
        int port,
        String database,
        String username,
        String password
) {

    public MySqlProperties {
        Assert.hasText(host, "MySQL host must not be empty");
        Assert.isTrue(port > 0, "MySQL port must be greater than 0");
        Assert.hasText(database, "MySQL database must not be empty");
        Assert.hasText(username, "MySQL username must not be empty");
        Assert.hasText(password, "MySQL password must not be empty");
    }
}
