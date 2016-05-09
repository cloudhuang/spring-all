package org.spring.generic.dao;

import com.google.common.base.Optional;

import java.util.List;

/**
 * @author
 */
public class GenericDao<E> {

    private Class<E> entityClass;

    public GenericDao(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public List<E> findAll() {
        // ...
        return null;
    }

    public Optional<E> persist(E toPersist) {
        // ...
        return Optional.absent();
    }

}

