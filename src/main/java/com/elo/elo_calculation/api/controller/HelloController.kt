package com.elo.elo_calculation.api.controller

import com.elo.elo_calculation.api.service.EloService
import com.elo.elo_calculation.impl.repository.MatchRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(private val eloService: EloService) {

    @GetMapping("/")
    fun index(): String? {
        return eloService.addMatch()
    }
}