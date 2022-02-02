package com.elo.elo_calculation.api.service;

import com.elo.elo_calculation.impl.entity.Match;

interface EloService {
    fun downloadFromServer() : String
    fun recalculateElo() : String
    fun showTournaments() : String
    fun showRounds() : String
    fun showTeams(): String
    fun showMatches(): String
}
