package com.example.crm_gym.services;

import com.example.crm_gym.dao.BaseDAO;

import java.util.Optional;

public abstract class BaseService<T> {

    protected final BaseDAO<T> repository;

    public BaseService(BaseDAO<T> repository) {
        this.repository = repository;
    }

    public Optional<T> findEntityById(Long id) {
        return repository.findById(id);
    }
}
