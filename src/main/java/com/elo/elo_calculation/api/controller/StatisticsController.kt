package com.elo.elo_calculation.api.controller

import com.elo.elo_calculation.api.service.StatisticsService
import com.elo.elo_calculation.generated.ID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class StatisticsController(private val statisticsService: StatisticsService) {

    @GetMapping("/stats")
    fun statistics(@RequestParam date : String) : String {
        return statisticsService.ratingOfAllTeamsOnCurrentDate(date)
    }

    @GetMapping("/diff")
    fun diff(@RequestParam matchID : ID) : String{
        return statisticsService.ratingDifference(matchID)
    }

    @GetMapping("/cur")
    fun currentRating(@RequestParam teamID : ID, @RequestParam date: String) : Int{
        return statisticsService.ratingOnCurrentDate(teamID,date)
    }

    @GetMapping("/max")
    fun maxRating(@RequestParam(required = false) teamID : ID?) : String{
        if(teamID == null){
            return statisticsService.maxEloOfAllTeams()
        } else {
            return statisticsService.maxEloOfTeam(teamID)
        }
    }

    @GetMapping("/history")
    fun historyRating(@RequestParam teamID : ID) : String{
        return statisticsService.ratingOfTeamForAllTime(teamID)
    }

    @GetMapping("/top")
    fun topRating(@RequestParam(required = false) date : String?) : String{
        if (date == null){
            return statisticsService.leaderHistory()
        } else
        {
            return statisticsService.leaderOnCurrentDate(date)
        }
    }

    @GetMapping("/matchStats")
    fun matchHistory(@RequestParam(required = false) teamID: ID?) : String {
        if(teamID == null){
            return statisticsService.matchHistoryAll()
        } else {
            return statisticsService.matchHistory(teamID)
        }
    }

    @GetMapping("/maxDiff")
    fun maxDifference() : String {
        return statisticsService.maxRatingDifference()
    }

    @GetMapping("/comparison")
    fun compareDates(@RequestParam date1:String, @RequestParam date2 : String, @RequestParam(required = false) teamID: ID?) : String{
        if (teamID == null){
            return statisticsService.compareDatesAll(date1,date2)
        } else {
            return statisticsService.compareDates(date1, date2, teamID)
        }
    }
}