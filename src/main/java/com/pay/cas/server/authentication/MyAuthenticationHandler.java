package com.pay.cas.server.authentication;

import java.security.GeneralSecurityException;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/***
 *
 * MyAuthenticationHandler.java
 *
 * @author syq
 *
 *         2018年6月29日
 */
public class MyAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    private static final Logger logger = LoggerFactory.getLogger(MyAuthenticationHandler.class);

    private static final String SQL = "SELECT password FROM prom_admin WHERE  phone_no = ?";

    private JdbcTemplate jdbcTemplate;

    public MyAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
            Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    public MyAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
            Integer order, JdbcTemplate jdbcTemplate) {
        super(name, servicesManager, principalFactory, order);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential,
            String originalPassword) throws GeneralSecurityException, PreventedException {

        logger.debug("jdbc:{}", jdbcTemplate);
        final String username = credential.getUsername();
        final String password = credential.getPassword();

        try {
            final Map<String, Object> dbFields = jdbcTemplate.queryForMap(SQL, username);
            final String dbPassword = (String) dbFields.get("password");
            if (StringUtils.isBlank(originalPassword)) {
                throw new FailedLoginException("Password is blank.");
            }

            if (StringUtils.isNotBlank(originalPassword) && !matches(originalPassword, dbPassword)) {
                throw new FailedLoginException("Password does not match value on record.");
            }

        } catch (final IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with SQL query");
            }
            throw new FailedLoginException("Multiple records found for " + username);
        } catch (final DataAccessException e) {
            throw new PreventedException("SQL exception while executing query for " + username, e);
        }
        return createHandlerResult(credential, principalFactory.createPrincipal(username), null);
    }

}
