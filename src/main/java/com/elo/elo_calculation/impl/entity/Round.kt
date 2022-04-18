package com.elo.elo_calculation.impl.entity;

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "ROUND")
class Round {
    @Id
    var roundID : Int? = null
    var weight : Int? = null

    constructor()

    constructor(roundID: Int){
        this.roundID = roundID
    }
}