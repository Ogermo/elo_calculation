package com.elo.elo_calculation.impl.service

import com.elo.elo_calculation.api.service.EloService
import com.elo.elo_calculation.api.service.StatisticsService
import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.entity.Elo
import com.elo.elo_calculation.impl.entity.Match
import com.elo.elo_calculation.impl.projection.*
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

    override fun ratingDifference(matchID: ID) : List<RatingChange>{
        //return {teamID,eloPrev,eloNow}
        val match = eloRepository.findByMatchId(matchID)
        if (match.size != 2) throw Exception()
        val team1PrevElo = eloRepository.findPrevMatchElo(match[0].teamID,match[0].matchID)?.elo ?: DEFAULT_ELO
        val team2PrevElo = eloRepository.findPrevMatchElo(match[1].teamID,match[1].matchID)?.elo ?: DEFAULT_ELO
        return listOf(RatingChange(match[0].teamID,team1PrevElo,match[0].elo),
            RatingChange(match[1].teamID,team2PrevElo,match[1].elo))
    }

    override fun ratingOnCurrentDate(teamID : ID, date: String) : List<Elo>{
        val elo = eloRepository.findEloOnCurrentDate(teamID,date) ?: return listOf(Elo("Not found",teamID,DEFAULT_ELO))
        return listOf(elo)
    }

    override fun ratingOfAllTeamsOnCurrentDate(date: String) : List<LastMatchOfAllTeamsBeforeDateProjection> {
        return matchRepository.findAllTeamsBefore(date)
    }

    override fun ratingOfTeamForAllTime(teamID: ID): List<EloOfAllMatchesForTeamProjection> {
        return eloRepository.findEloOfAllMatchesForTeam(teamID)
    }

    override fun maxEloOfTeam(teamID : ID) : List<MaxEloProjection> {
        return matchRepository.findMaxEloOfTeam(teamID)
    }

    override fun maxEloOfAllTeams() : List<MaxEloProjection> {
        return matchRepository.findMaxEloOfAllTeams()
    }

    override fun leaderOnCurrentDate(date: String): List<LeaderOnCurrentDateProjection> {
        return matchRepository.findLeaderOnCurrentDate(date)
    }

    override fun leaderHistory(): List<EloWithDate> {
        val eloList = eloRepository.findAllSorted()
        val history = mutableListOf<EloWithDate>()
        var highestElo = DEFAULT_ELO
        var highestTeam : ID = ""
        for (value in eloList){
            if (value.teamID == highestTeam){
                highestElo = value.elo
            }
            else if (value.elo > highestElo){
                highestElo = value.elo
                highestTeam = value.teamID
                history.add(EloWithDate(value.matchID,value.teamID,
                    value.elo,matchRepository.findById(value.matchID).get().startDt!!))
            }
        }
        return history
    }

    override fun matchHistory(teamID: ID): List<MatchHistoryProjection> {
        return matchRepository.findMatchHistory(teamID)
    }

    override fun matchHistoryAll(): List<MatchHistoryProjection> {
        return matchRepository.findMatchHistoryForAllTeams()
    }

    override fun maxRatingDifference(): List<MaxRatingDifferenceProjection> {
        return matchRepository.findMaxRatingDifference()
    }

    override fun compareDates(date1: String, date2: String, teamID: ID /* = kotlin.String */): List<List<PlacementProjection>> {
        return listOf(matchRepository.findPlacementTeam(date1,teamID),matchRepository.findPlacementTeam(date2,teamID))
    }

    override fun compareDatesAll(date1: String,date2: String): List<List<PlacementProjection>> {
        return listOf(matchRepository.findPlacementAll(date1),matchRepository.findPlacementAll(date2))
    }

}