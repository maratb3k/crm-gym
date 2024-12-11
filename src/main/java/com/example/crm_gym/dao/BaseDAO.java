package com.example.crm_gym.dao;

import java.util.Optional;

public interface BaseDAO<T> {
    Optional<T> findById(Long id);
    Optional<T> save(T entity);
}
