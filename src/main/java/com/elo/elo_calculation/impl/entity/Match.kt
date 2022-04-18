package com.elo.elo_calculation.impl.entity

import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.generated.calendarquery.Team
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "MATCH")
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
    var calculated: Boolean = false

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

    fun calculateResult() : Pair<Double,Double>{
        var team1_W  = 0.0
        var team2_W = 0.0
        if (this.gf > this.ga){
            team1_W = 1.0
            team2_W = 0.0
        } else if (this.ga > this.gf) {
            team1_W = 0.0
            team2_W = 1.0
        } else {
            if ((this.gfp == null) || (this.gap == null)){
                team1_W = 0.5
                team2_W = 0.5
            } else {
                if (this.gfp!! > this.gap!!) {
                    team1_W = 0.75
                    team2_W = 0.5
                } else if (this.gap!! > this.gfp!!) {
                    team1_W = 0.5
                    team2_W = 0.75
                } else {
                    team1_W = 0.5
                    team2_W = 0.5
                }
            }
        }
        return team1_W to team2_W
    }
}