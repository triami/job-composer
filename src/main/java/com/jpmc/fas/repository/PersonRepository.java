package com.jpmc.fas.repository;

import com.jpmc.fas.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by amit on 8/3/16.
 */
@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    List<Person> findByLastName(String lastName);

}
