package com.jpmc.fas;

import com.jpmc.fas.model.City;
import com.jpmc.fas.model.Person;
import com.jpmc.fas.repository.PersonRepository;
import com.jpmc.fas.test.support.TestJpaApplicationConfig2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by amit on 8/7/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestJpaApplicationConfig2.class)
public class JobSpecParserTests2 {

    @Autowired
    EntityManager entityManager;

    @Autowired
    PersonRepository personRepository;

    @Test
    public void shouldBeAbleToAddEntitiesAndFetchThemViaEm(){

        Assert.assertNotNull(entityManager);
        Assert.assertNotNull(personRepository);

        List<Person> people = Arrays.asList(
                new Person("Amit", "Tripathy", new City("Piscataway")),
                new Person("Amam", "Sharma", new City("Monroe")),
                new Person("Bhavin", "Patel", new City("Monroe"))
        );
        personRepository.save(people);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> criteriaQuery =  criteriaBuilder.createTupleQuery();

        Root<City> fromCities = criteriaQuery.from(City.class);
        Join<City, Person> fromCitiesToPerson = fromCities.join("residents");

        Path<String> cityName = fromCities.get("name");
        Path<Long> personId = fromCitiesToPerson.get("id");

        criteriaQuery.multiselect(cityName, criteriaBuilder.count(personId));
        criteriaQuery.where(criteriaBuilder.equal(cityName,""));
        criteriaQuery.groupBy(cityName);

        List<Tuple> tuples = entityManager.createQuery(criteriaQuery).getResultList();

        Assert.assertNotNull(tuples);
    }


}
