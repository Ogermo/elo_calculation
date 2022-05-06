package com.elo.elo_calculation.api.controller

import com.elo.elo_calculation.api.service.StatisticsService
import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.entity.Elo
import com.elo.elo_calculation.impl.projection.*
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/elo")
class StatisticsController(private val statisticsService: StatisticsService) {

    @GetMapping("/stats")
    fun statistics(@RequestParam date : String) : List<LastMatchOfAllTeamsBeforeDateProjection> {
        return statisticsService.ratingOfAllTeamsOnCurrentDate(date)
    }

    @GetMapping("/diff")
    fun diff(@RequestParam matchID : ID) : List<RatingChange>{
        return statisticsService.ratingDifference(matchID)
    }

    @GetMapping("/cur")
    fun currentRating(@RequestParam teamID : ID, @RequestParam date: String) : List<Elo>{
        return statisticsService.ratingOnCurrentDate(teamID,date)
    }

    @GetMapping("/max")
    fun maxRating(@RequestParam(required = false) teamID : ID?) : List<MaxEloProjection>{
        if(teamID == null) {
            return statisticsService.maxEloOfAllTeams()
        }
        else {
            return statisticsService.maxEloOfTeam(teamID)
        }
    }

    @GetMapping("/rating")
    fun historyRating(@RequestParam teamID : ID) : List<EloOfAllMatchesForTeamProjection> {
        return statisticsService.ratingOfTeamForAllTime(teamID)
    }

    @GetMapping("/top")
    fun topRating(date : String) : List<LeaderOnCurrentDateProjection> {
        return statisticsService.leaderOnCurrentDate(date)
    }

    @GetMapping("/history")
    fun leaderHistory() : List<EloWithDate>{
        return statisticsService.leaderHistory()
    }


    @GetMapping("/matchStats")
    fun matchHistory(@RequestParam(required = false) teamID: ID?) : List<MatchHistoryProjection> {
        if(teamID == null){
            return statisticsService.matchHistoryAll()
        } else {
            return statisticsService.matchHistory(teamID)
        }
    }

    @GetMapping("/maxDiff")
    fun maxDifference() : List<MaxRatingDifferenceProjection> {
        return statisticsService.maxRatingDifference()
    }

    @GetMapping("/comparison")
    fun compareDates(@RequestParam date1:String, @RequestParam date2 : String, @RequestParam(required = false) teamID: ID?) : List<List<PlacementProjection>> {
        if (teamID == null){
            return statisticsService.compareDatesAll(date1,date2)
        } else {
            return statisticsService.compareDates(date1, date2, teamID)
        }
    }
}