package il.cshaifasweng.OCSFMediatorExample.entities;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;

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

    // Remove this field from being an entity relation
    @Transient
    private String ingredientsString;

    // Remove this field from being an embedded class
    @Transient
    private String preferencesLevelOfSpiciness;
    @Transient
    private String preferencesKindOfBread;
    @Transient
    private String preferencesString;
    // Getters and Setters
    public Dish() {}
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

    public String getIngredientsString() {
        return ingredientsString;
    }

    public void setIngredientsString(String ingredientsString) {
        this.ingredientsString = ingredientsString;
    }
    public String getPreferencesLevelOfSpiciness() {
        return preferencesLevelOfSpiciness;
    }

    public void setPreferencesLevelOfSpiciness(String preferencesLevelOfSpiciness) {
        this.preferencesLevelOfSpiciness = preferencesLevelOfSpiciness;
    }

    // Getter and Setter for preferencesKindOfBread
    public String getPreferencesKindOfBread() {
        return preferencesKindOfBread;
    }

    public void setPreferencesKindOfBread(String preferencesKindOfBread) {
        this.preferencesKindOfBread = preferencesKindOfBread;
    }
    public String getPreferencesString() {
        return getPreferencesLevelOfSpiciness() + getPreferencesKindOfBread();
    }
//    public void setPreferencesString(String preferencesString) {
//        this.preferencesString = preferencesString;
//    }



}
