package com.example.crm_gym.dtoConverter;

import java.util.List;

public interface Converter<D, E> {
    D convertToDto(E entity);
    E convertToEntity(D dto);
    List<D> convertModelListToDtoList(List<E> entityList);
}
