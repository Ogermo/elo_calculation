package com.elo.elo_calculation.impl.service

import com.elo.elo_calculation.api.service.EloService
import com.elo.elo_calculation.api.service.StatisticsService
import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.projection.PlacementAllProjection
import com.elo.elo_calculation.impl.projection.toTable
import com.elo.elo_calculation.impl.repository.MatchRepository
import com.elo.elo_calculation.impl.repository.RoundRepository
import com.elo.elo_calculation.impl.repository.TeamRepository
import com.elo.elo_calculation.impl.repository.TournamentRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StatisticsServiceImpl(
    private val matchRepository: MatchRepository,
) : StatisticsService {


    override fun ratingDifference(matchID: ID) : String{
        System.out.println(matchID)
        val match = matchRepository.findByIdOrNull(matchID) ?: throw Exception()
        val matchBeforeTeam1 = matchRepository.findMatchesBefore(match.startDt!!,match.team1ID!!).lastOrNull()
        val eloBeforeTeam1 = run {
            if (matchBeforeTeam1 == null){
                500
            } else {
                if (matchBeforeTeam1.team1ID.equals(match.team1ID)){
                    matchBeforeTeam1.team1Elo ?: throw Exception()
                } else {
                    matchBeforeTeam1.team2Elo ?: throw Exception()
                }
            }
        }
        val matchBeforeTeam2 = matchRepository.findMatchesBefore(match.startDt!!,match.team2ID!!).lastOrNull()
        val eloBeforeTeam2 = run {
            if (matchBeforeTeam2 == null){
                500
            } else {
                if (matchBeforeTeam2.team2ID.equals(match.team2ID)){
                    matchBeforeTeam2.team2Elo ?: throw Exception()
                } else {
                    matchBeforeTeam2.team1Elo ?: throw Exception()
                }
            }
        }

        val answer = "Team 1 difference = " + (match.team1Elo!! - eloBeforeTeam1) +
                "Team 2 difference = " + (match.team2Elo!! - eloBeforeTeam2)

        return answer
    }

    override fun ratingOnCurrentDate(teamID : ID, date: String) : Int{
        val match = matchRepository.findMatchesBefore(date, teamID).lastOrNull() ?: return 500
        if (match.team1ID.equals(teamID)){
            return match.team1Elo ?: throw Exception()
        } else
        {
            return match.team2Elo ?: throw Exception()
        }
    }

    override fun ratingOfAllTeamsOnCurrentDate(date: String) : String {
        return matchRepository.findAllTeamsBefore(date).toTable()
    }

    override fun ratingOfTeamForAllTime(teamID: ID): String {
        val matches = matchRepository.findAllMatchesOfTeam(teamID)
        var answer = ""
        for (match in matches){
            if (teamID.equals(match.team1ID)){
                answer += "matchID: " + match.matchID + " Elo: " + match.team1Elo + " Date: " + match.startDt + "<br>"
            } else {
                answer += "matchID: " + match.matchID + " Elo: " + match.team2Elo + " Date: " + match.startDt + "<br>"
            }
        }
        return answer
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
        val table = matchRepository.findAll()
        table.sortBy { it.startDt }
        //TODO("ASK HOW BETTER DO IT")
        var answer = ""
        var leaderTeam : ID = ""
        var leaderElo : Int = 0
        for (match in table){
            if (match.team1Elo!! > match.team2Elo!!){
                if (match.team1Elo!! > leaderElo){
                    leaderElo = match.team1Elo!!
                    if (!match.team1ID.equals(leaderTeam)){
                        leaderTeam = match.team1ID!!
                        answer += leaderTeam + " " + leaderElo + " " + match.startDt + "<br>"
                    }
                }
            } else {
                if (match.team2Elo!! > leaderElo){
                    leaderElo = match.team2Elo!!
                    if (!match.team2ID.equals(leaderTeam)){
                        leaderTeam = match.team2ID!!
                        answer += leaderTeam + " " + leaderElo + " " + match.startDt + "<br>"
                    }
                }
            }
        }
        return answer
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