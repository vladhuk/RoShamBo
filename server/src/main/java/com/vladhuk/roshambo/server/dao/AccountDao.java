package com.vladhuk.roshambo.server.dao;

import com.vladhuk.roshambo.server.model.Account;
import com.vladhuk.roshambo.server.util.ServerSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;


public class AccountDao implements GenericDao<Account> {

    private Session session;
    private Transaction transaction;

    @Override
    public void save(Account account) {
        session = ServerSessionFactory.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        session.save(account);

        transaction.commit();
        session.close();
    }

    @Override
    public void update(Account account) {
        session = ServerSessionFactory.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        session.update(account);

        transaction.commit();
        session.close();
    }

    @Override
    public void delete(Account account) {
        session = ServerSessionFactory.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        session.delete(account);

        transaction.commit();
        session.close();
    }

    @Override
    public Account findById(int id) {
        session = ServerSessionFactory.getSessionFactory().openSession();

        Account account = session.get(Account.class, id);

        session.close();

        return account;
    }

    public Account findByUsernameAndPassword(String username, String password) {
        session = ServerSessionFactory.getSessionFactory().openSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Account> criteria = cb.createQuery(Account.class);
        Root<Account> root = criteria.from(Account.class);
        criteria.where(cb.and(cb.equal(root.get("username"), username),
                              cb.equal(root.get("password"), password)));

        Query<Account> query = session.createQuery(criteria);
        Account account = query.uniqueResult();

        session.close();

        return account;
    }

    public String findUsername(String username) {
        session = ServerSessionFactory.getSessionFactory().openSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> criteria = cb.createQuery(String.class);
        Root<Account> root = criteria.from(Account.class);
        criteria.select(root.get("username"));
        criteria.where(cb.equal(root.get("username"), username));

        Query<String> query = session.createQuery(criteria);
        username = query.uniqueResult();

        session.close();

        return username;
    }

}
