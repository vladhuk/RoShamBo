package com.vladhuk.roshambo.server.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class ServerSessionFactory {

    private static SessionFactory sessionFactory;

    public static SessionFactory configure(String databaseURL, String username, String password) {
        Configuration configuration = new Configuration().configure();

        configuration.setProperty("hibernate.connection.url", databaseURL);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);

        sessionFactory = configuration.buildSessionFactory();

        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new NotConfigureServerSessionFactoryException();
        }

        return sessionFactory;
    }
}
