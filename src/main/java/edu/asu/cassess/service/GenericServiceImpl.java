package edu.asu.cassess.service;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class GenericServiceImpl<T, D, ID extends Serializable> implements GenericService<T, D, ID> {

    protected Class<T> entityClass;
    protected Class<D> dtoClass;
    @Autowired
    private JpaRepository<T, ID> repository;
    @Autowired
    private DozerBeanMapper mapper;

    @SuppressWarnings("unchecked")
    public GenericServiceImpl() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
        this.dtoClass = (Class<D>) genericSuperclass.getActualTypeArguments()[1];
    }

    public D findOne(ID id) {
        return mapper.map(repository.findOne(id), dtoClass);
    }

    public List<D> findAll() {
        List<D> result = new ArrayList<D>();
        for (T t : repository.findAll()) {
            result.add(mapper.map(t, dtoClass));
        }
        return result;
    }

    public void save(D dto) {
        repository.saveAndFlush(mapper.map(dto, entityClass));
    }

}
