package com.elo.elo_calculation.impl.service

import com.elo.elo_calculation.api.service.EloService
import com.elo.elo_calculation.api.service.StatisticsService
import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.projection.PlacementAllProjection
import com.elo.elo_calculation.impl.projection.toTable
import com.elo.elo_calculation.impl.repository.*
import com.elo.elo_calculation.impl.serializable.EloId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StatisticsServiceImpl(
    private val matchRepository: MatchRepository,
    private val eloRepository: EloRepository,
    private val teamRepository: TeamRepository
) : StatisticsService {

    val DEFAULT_ELO = 500

    override fun ratingDifference(matchID: ID) : String{
        val match = eloRepository.findByMatchId(matchID)
        if (match.size != 2) throw Exception()

        val team1PrevElo = eloRepository.findPrevMatchElo(match[0].teamID,match[0].matchID)?.elo ?: DEFAULT_ELO
        val team2PrevElo = eloRepository.findPrevMatchElo(match[1].teamID,match[1].matchID)?.elo ?: DEFAULT_ELO
        val answer = "Team 1 difference = " + (match[0].elo - team1PrevElo) +
                " Team 2 difference = " + (match[1].elo - team2PrevElo)
        return answer
    }

    override fun ratingOnCurrentDate(teamID : ID, date: String) : Int{
        return eloRepository.findEloOnCurrentDate(teamID,date)?.elo ?: teamRepository.findByIdOrNull(teamID)?.let { return DEFAULT_ELO } ?: throw Exception()
    }

    override fun ratingOfAllTeamsOnCurrentDate(date: String) : String {
        return matchRepository.findAllTeamsBefore(date).toTable()
    }

    override fun ratingOfTeamForAllTime(teamID: ID): String {
        return eloRepository.findEloOfAllMatchesForTeam(teamID).toTable()
    }

    override fun maxEloOfTeam(teamID : ID) : String {
        return matchRepository.findMaxEloOfTeam(teamID).toTable()
    }

    override fun maxEloOfAllTeams() : String {
        return matchRepository.findMaxEloOfAllTeams().toTable()
    }

    override fun leaderOnCurrentDate(date: String): String {
        return matchRepository.findLeaderOnCurrentDate(date).toTable()
    }

    override fun leaderHistory(): String {
        val eloList = eloRepository.findAllSorted()
        var table =  "  <table border = '1'>" +
                "   <caption>Лидеры рейтинга за всю историю</caption>" +
                "   <tr>" +
                "    <th>MatchID</th>" +
                "    <th>TeamID</th>" +
                "    <th>Elo</th>"+
                "    <th>Date</th>"+
                "   </tr>"
        var highestElo = DEFAULT_ELO
        var highestTeam : ID = ""
        for (value in eloList){
            if (value.teamID == highestTeam){
                highestElo = value.elo
            }
            else if (value.elo > highestElo){
                highestElo = value.elo
                highestTeam = value.teamID
                table += "<tr><td>${value.matchID}</td><td>${value.teamID}</td>" +
                        "<td>${value.elo}</td><td>${matchRepository.findById(value.matchID).get().startDt}</td></tr>"
            }
        }
        return table
    }

    override fun matchHistory(teamID: ID): String {
        return matchRepository.findMatchHistory(teamID).toTable()
    }

    override fun matchHistoryAll(): String {
        return matchRepository.findMatchHistoryForAllTeams().toTable()
    }

    override fun maxRatingDifference(): String {
        return matchRepository.findMaxRatingDifference().toTable()
    }

    override fun compareDates(date1: String, date2: String, teamID: ID /* = kotlin.String */): String {
        return "Before: <br>" + matchRepository.findPlacementTeam(date1,teamID).toTable() +
                "After: <br>" + matchRepository.findPlacementTeam(date2,teamID).toTable()
    }

    override fun compareDatesAll(date1: String,date2: String): String {
        return "Before: <br>" + matchRepository.findPlacementAll(date1).toTable() +
            "After: <br>" + matchRepository.findPlacementAll(date2).toTable()
    }

}