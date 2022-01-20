package com.elo.elo_calculation.impl.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Team {
    @Id
    Integer team_id;
    String full_name;
}
