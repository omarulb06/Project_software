package il.cshaifasweng.OCSFMediatorExample.entities;
import org.hibernate.annotations.*;
import jakarta.persistence.*;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int id;

    @NaturalId
    public String name;
}
