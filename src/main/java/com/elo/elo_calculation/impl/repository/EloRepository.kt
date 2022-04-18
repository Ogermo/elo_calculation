package com.elo.elo_calculation.impl.repository

import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.entity.Elo
import com.elo.elo_calculation.impl.entity.Match
import com.elo.elo_calculation.impl.projection.EloOfAllMatchesForTeamProjection
import com.elo.elo_calculation.impl.serializable.EloId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
public interface EloRepository : JpaRepository<Elo, EloId>, JpaSpecificationExecutor<Elo> {

    @Query("SELECT u2.* FROM MATCH u1 " +
            "LEFT JOIN ELO u2 ON u1.MATCHID = u2.MATCHID " +
            "WHERE u1.MATCHID = " +
            "(SELECT PREVMATCH FROM ( " +
            "SELECT *, LAG(MATCHID) OVER (ORDER BY START_DT, MATCHID) AS PrevMatch " +
            "FROM MATCH WHERE START_DT <= ( " +
            "SELECT TOP(1) START_DT FROM MATCH " +
            "WHERE MATCHID = ?2 " +
            ") AND (TEAM1ID = ?1 OR TEAM2ID = ?1) " +
            "ORDER BY START_DT, MATCHID " +
            ") WHERE MATCHID = ?2) " +
            "AND u2.TEAMID = ?1",
        nativeQuery = true)
    fun findPrevMatchElo(teamID: ID, matchID : ID) : Elo?

    @Query("SELECT u FROM Elo u WHERE u.matchID = ?1")
    fun findByMatchId(matchID: ID) : List<Elo>

    @Query("SELECT TOP(1) u1.* FROM ELO u1 " +
            "JOIN MATCH u2 " +
            "ON u1.MATCHID = u2.MATCHID " +
            "WHERE TEAMID = ?1 AND START_DT < ?2 " +
            "ORDER BY START_DT DESC, MATCHID DESC ",
    nativeQuery = true)
    fun findEloOnCurrentDate(teamID: ID, time:String) : Elo?

    @Query("SELECT u1.*, u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 " +
            "ON u1.MATCHID = u2.MATCHID " +
            "WHERE TEAMID = ?1 " +
            "ORDER BY START_DT, MATCHID",
    nativeQuery = true)
    fun findEloOfAllMatchesForTeam(teamID: ID) : List<EloOfAllMatchesForTeamProjection>

    @Query("SELECT u1.* FROM ELO u1 " +
            "JOIN MATCH u2 " +
            "ON u1.MATCHID = u2.MATCHID " +
            "ORDER BY START_DT, MATCHID",
        nativeQuery = true)
    fun findAllSorted() : List<Elo>
}