package com.elo.elo_calculation.impl.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "TOURNAMENT")
class Tournament {
    @Id
    var tournamentID : Int? = null
    var weight : Int = 30 //default value

    constructor()

    constructor(tournamentID: Int){
        this.tournamentID = tournamentID
    }
}