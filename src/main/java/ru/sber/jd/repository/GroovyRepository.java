package ru.sber.jd.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sber.jd.entity.GroovyEntity;

@Repository
public interface GroovyRepository extends CrudRepository<GroovyEntity,Integer> {
}
