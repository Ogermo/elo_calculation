package com.elo.elo_calculation.impl.entity

import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.serializable.EloId
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass


@Entity
@IdClass(EloId::class)
class Elo {
    @Id
    val matchID: ID
    @Id
    val teamID: ID
    var elo: Int = 0

    constructor(){
        this.matchID = ""
        this.teamID = ""
    }

    constructor(matchId: ID, teamId: ID, elo: Int){
        this.matchID = matchId
        this.teamID = teamId
        this.elo = elo
    }
}