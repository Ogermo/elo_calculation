package com.elo.elo_calculation.impl.entity

import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.generated.calendarquery.Team
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Match {
    @Id
    var matchID : ID? = null
    var roundID : Int? = null
    var tournamentID : Int? = null

    var startDt : String? = null
    var weight : Int? = null
    var gf : Int = 0
    var ga : Int = 0
    var gfp : Int? = null
    var gap : Int? = null
    var team1ID : ID? = null
    var team2ID : ID? = null
    var postMatchPenalty : Boolean = false
    var team1Elo : Int? = null
    var team2Elo : Int? = null

    constructor()

    constructor(matchID: ID, roundID : Int, tournamentID : Int, startDt : String?, gf : Int, ga: Int,
                gfp : Int?, gap: Int?,team1ID: Team?, team2ID: Team?){
        this.matchID = matchID
        this.roundID = roundID
        this.tournamentID = tournamentID
        this.startDt = startDt
        this.gf = gf
        this.ga = ga
        this.gfp = gfp
        this.gap = gap
        if (team1ID != null){
            this.team1ID = team1ID.team_id
        }
        if (team2ID != null){
            this.team2ID = team2ID.team_id
        }
    }
}