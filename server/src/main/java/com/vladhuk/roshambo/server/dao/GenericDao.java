package com.vladhuk.roshambo.server.dao;

public interface GenericDao<T> {
    void save(T entity);
    void update(T entity);
    void delete(T entity);
    T findById(int id);
}
