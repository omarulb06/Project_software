package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class PersonalPreference implements Serializable {

    private String levelOfSpiciness;

    private String kindOfBread;

    // Getters and Setters

    public String getLevelOfSpiciness() {
        return levelOfSpiciness;
    }

    public void setLevelOfSpiciness(String levelOfSpiciness) {
        this.levelOfSpiciness = levelOfSpiciness;
    }

    public String getKindOfBread() {
        return kindOfBread;
    }

    public void setKindOfBread(String kindOfBread) {
        this.kindOfBread = kindOfBread;
    }
}
