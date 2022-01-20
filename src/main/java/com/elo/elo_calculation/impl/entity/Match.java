package com.elo.elo_calculation.impl.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Match {
    @Id
    Integer match_id;
    Integer gf;
    Integer ga;
    Integer team1_id;
    Integer team2_id;
    String start_dt;
}
