package com.elo.elo_calculation.impl.service

import com.elo.elo_calculation.api.service.EloService
import com.elo.elo_calculation.api.service.StatisticsService
import com.elo.elo_calculation.generated.ID
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
                if (matchBeforeTeam1.team1ID == match.team1ID){
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
                if (matchBeforeTeam2.team2ID == match.team2ID){
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
        System.out.println(teamID)
        System.out.println(date)

        val match = matchRepository.findMatchesBefore(date, teamID).lastOrNull() ?: return 500
        if (match.team1ID == teamID){
            return match.team1Elo ?: throw Exception()
        } else
        {
            return match.team2Elo ?: throw Exception()
        }
    }

    override fun ratingOfAllTeamsOnCurrentDate(date: String) : Map<ID,Int>? {
        val table = matchRepository.findAllTeamsBefore(date)
        System.out.println("DEBUG")
        return null
    }

}