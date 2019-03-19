package com.vladhuk.roshambo.server.dao;

public interface GenericDAO <T> {

    void save(T entity);

    void update(T entity);

    void delete(T entity);

    T findById(int id);
}
