package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "dishes")
public class Dish implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NaturalId
    private String name;

    private int price;

    private boolean couldBeDelivered;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "DishesIngredients")
    private List<Ingredient> ingredients;

    private PersonalPreference preferences;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isCouldBeDelivered() {
        return couldBeDelivered;
    }

    public void setCouldBeDelivered(boolean couldBeDelivered) {
        this.couldBeDelivered = couldBeDelivered;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public PersonalPreference getPreferences() {
        return preferences;
    }

    public void setPreferences(PersonalPreference preferences) {
        this.preferences = preferences;
    }
}