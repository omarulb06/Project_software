package il.cshaifasweng.OCSFMediatorExample.entities;
// probably use several maps:
// map dish  id to name and price,
// map dish id to ingredients
// map dish id to personal preferences

import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import java.util.List;

@Entity
@Table(name = "dishes")
public class Dish {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int id;

    @NaturalId
    public String name;

    public int price;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="DishesIngredients")
    public List<Ingredient> ingredients;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="DishesPersonalPreferences")
    public List<PersonalPreference> preferences;
}