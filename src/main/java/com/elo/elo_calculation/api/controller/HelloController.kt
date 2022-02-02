package com.elo.elo_calculation.api.controller

import com.elo.elo_calculation.api.service.EloService
import com.elo.elo_calculation.api.service.StatisticsService
import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.repository.MatchRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(private val eloService: EloService,
    private val statisticsService: StatisticsService) {

    @GetMapping("/download")
    fun download(): String? {
        return eloService.downloadFromServer()
    }

    @GetMapping("/recalculate")
    fun recalculate(): String? {
        return eloService.recalculateElo()
    }

    @GetMapping("/matches")
    fun matches() : String? {
        return  eloService.showMatches()
    }

    @GetMapping("/rounds")
    fun rounds() : String? {
        return eloService.showRounds()
    }

    @GetMapping("/teams")
    fun teams() : String? {
        return eloService.showTeams()
    }

    @GetMapping("/tournaments")
    fun tournaments() : String? {
        return  eloService.showTournaments()
    }

    @GetMapping("/stats")
    fun statistics(@RequestParam date : String) : Map<ID,Int>? {
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
}