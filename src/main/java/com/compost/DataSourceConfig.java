package com.compost;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String dbUrl = System.getenv("DATABASE_PUBLIC_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalStateException("DATABASE_PUBLIC_URL environment variable is not set");
        }

        URI uri = URI.create(dbUrl);
        String host = uri.getHost();
        int port = uri.getPort();
        String database = uri.getPath().replaceFirst("/", "");
        String userInfo = uri.getUserInfo();
        String username = userInfo.split(":")[0];
        String password = userInfo.split(":")[1];

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
