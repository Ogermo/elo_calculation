package com.elo.elo_calculation.api.service

import com.elo.elo_calculation.generated.ID

interface StatisticsService {
    fun ratingDifference(matchID: ID) : String
    fun ratingOnCurrentDate(teamID : ID, date: String) : Int
    fun ratingOfAllTeamsOnCurrentDate(date: String) : String
    fun ratingOfTeamForAllTime(teamID : ID) : String
    fun maxEloOfAllTeams() : String
    fun maxEloOfTeam(teamID : ID) : String
    fun leaderOnCurrentDate(date: String) : String
    fun leaderHistory() : String
    fun matchHistoryAll() : String
    fun matchHistory(teamID: ID) : String
    fun maxRatingDifference() : String
    fun compareDates(date1 : String, date2 : String, teamID: ID /* = kotlin.String */) : String
    fun compareDatesAll(date1 : String, date2 : String) : String
}