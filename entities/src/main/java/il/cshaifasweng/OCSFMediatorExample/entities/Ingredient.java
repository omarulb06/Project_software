package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;

import java.io.Serializable;

@Entity
public class Ingredient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NaturalId
    private String name;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}