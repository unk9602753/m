package com.epam.esm.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.epam.esm")
@PropertySource("classpath:prod-connection.properties")
@PropertySource("classpath:dev-connection.properties")
public class DaoConfig {
    private Environment environment;

    @Autowired
    public DaoConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Profile("dev")
    public HikariConfig hikariConfig() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(environment.getProperty("mysql.url"));
        hikariConfig.setUsername(environment.getProperty("mysql.username"));
        hikariConfig.setPassword(environment.getProperty("mysql.pw"));
        hikariConfig.setDriverClassName(environment.getProperty("mysql.cn"));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(environment.getProperty("mysql.ps")));
        return hikariConfig;
    }

    @Bean
    @Profile("dev")
    public DataSource dataSource(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    @Profile("prod")
    public DataSource embeddedDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(environment.getProperty("h2.url"));
        dataSource.setUsername(environment.getProperty("h2.username"));
        dataSource.setPassword(environment.getProperty("h2.pw"));
        dataSource.setDriverClassName(environment.getProperty("h2.cn"));
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
