package com.pay.cas.server.config;

import javax.sql.DataSource;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pay.cas.server.authentication.MyAuthenticationHandler;

/***
 *
 * DataSourceConfig.java
 *
 * @author syq
 *
 *         2018年6月28日
 */
@Configuration("MyAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class DataSourceConfig implements AuthenticationEventExecutionPlanConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(myAuthenticationHandler());
    }

    @Bean
    @ConfigurationProperties(prefix = "primary.datasource")
    public DataSource dataSource() {
        logger.warn("######### dataSource success! #########");
        return DataSourceBuilder.create().build();

    }

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        logger.warn("######### getJdbcTemplate success! #########");
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public AuthenticationHandler myAuthenticationHandler() {
        logger.warn("servicesManager:{}", servicesManager);
        final MyAuthenticationHandler handler = new MyAuthenticationHandler(
                MyAuthenticationHandler.class.getSimpleName(), servicesManager, new DefaultPrincipalFactory(), 10,
                getJdbcTemplate());
        handler.setPasswordEncoder(new BCryptPasswordEncoder());
        return handler;
    }

}
