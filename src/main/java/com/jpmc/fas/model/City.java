package com.jpmc.fas.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

/**
 * Created by amit on 8/3/16.
 */
@Entity
public class City extends AbstractEntity{
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "livesIn")
    private Set<Person> residents;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City(String name) {
        this.name = name;
    }

    public City() {}

    public Set<Person> getResidents() {
        return residents;
    }

    public void setResidents(Set<Person> residents) {
        this.residents = residents;
    }
}
