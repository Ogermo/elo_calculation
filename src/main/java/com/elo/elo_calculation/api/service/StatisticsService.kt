package com.elo.elo_calculation.api.service

import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.entity.Elo
import com.elo.elo_calculation.impl.projection.*

interface StatisticsService {
    fun ratingDifference(matchID: ID) : List<RatingChange>
    fun ratingOnCurrentDate(teamID : ID, date: String) : List<Elo>
    fun ratingOfAllTeamsOnCurrentDate(date: String) : List<LastMatchOfAllTeamsBeforeDateProjection>
    fun ratingOfTeamForAllTime(teamID : ID) : List<EloOfAllMatchesForTeamProjection>
    fun maxEloOfAllTeams() : List<MaxEloProjection>
    fun maxEloOfTeam(teamID : ID) : List<MaxEloProjection>
    fun leaderOnCurrentDate(date: String) : List<LeaderOnCurrentDateProjection>
    fun leaderHistory() : List<EloWithDate>
    fun matchHistoryAll() : List<MatchHistoryProjection>
    fun matchHistory(teamID: ID) : List<MatchHistoryProjection>
    fun maxRatingDifference() : List<MaxRatingDifferenceProjection>
    fun compareDates(date1 : String, date2 : String, teamID: ID) : List<List<PlacementProjection>>
    fun compareDatesAll(date1 : String, date2 : String) : List<List<PlacementProjection>>
}