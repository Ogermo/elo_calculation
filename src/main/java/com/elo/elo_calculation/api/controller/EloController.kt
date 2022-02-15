package com.elo.elo_calculation.api.controller

import com.elo.elo_calculation.api.service.EloService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EloController(private val eloService: EloService) {

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

}