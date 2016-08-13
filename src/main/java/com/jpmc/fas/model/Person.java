package com.jpmc.fas.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * Created by amit on 8/3/16.
 */
@Entity
public class Person extends AbstractEntity{

    private String firstName;
    private String lastName;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private City livesIn;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public City getLivesIn() {
        return livesIn;
    }

    public void setLivesIn(City livesIn) {
        this.livesIn = livesIn;
    }

    public Person(String firstName, String lastName, City livesIn) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.livesIn = livesIn;
    }

    public Person() {}
}
