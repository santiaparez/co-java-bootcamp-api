package com.example.bootcamp.infrastructure.persistence;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableConfigurationProperties(MySqlProperties.class)
public class MySqlConfig {

    @Bean
    public ConnectionFactory connectionFactory(MySqlProperties properties) {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "mysql")
                .option(HOST, properties.host())
                .option(PORT, properties.port())
                .option(USER, properties.username())
                .option(PASSWORD, properties.password())
                .option(DATABASE, properties.database())
                .option(MAX_SIZE, 20)
                .build();
        return ConnectionFactories.get(options);
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}
