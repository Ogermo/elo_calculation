package com.elo.elo_calculation.api.service

import com.elo.elo_calculation.generated.ID

interface StatisticsService {
    fun ratingDifference(matchID: ID) : String
    fun ratingOnCurrentDate(teamID : ID, date: String) : Int
    fun ratingOfAllTeamsOnCurrentDate(date: String) : Map<ID,Int>?
}