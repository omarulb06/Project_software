package il.cshaifasweng.OCSFMediatorExample.entities;

import jakarta.persistence.*;

@Entity
public class PersonalPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @Basic(optional=false)
    public String name;
}