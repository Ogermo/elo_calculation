package com.elo.elo_calculation.impl.entity;

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Round {
    @Id
    var roundID : Int? = null
    var weight : Int? = null

    constructor()

    constructor(roundID: Int){
        this.roundID = roundID
    }
}