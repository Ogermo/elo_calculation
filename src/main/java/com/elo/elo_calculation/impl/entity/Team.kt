package com.elo.elo_calculation.impl.entity;

import com.elo.elo_calculation.generated.ID
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
class Team {
    @Id
    var teamId : ID? = null
    var fullName : String? = null

    constructor()

    constructor(teamId: ID, fullName : String){
        this.teamId = teamId
        this.fullName = fullName
    }
}
