package com.elo.elo_calculation.impl.repository;

import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.entity.Match;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import javax.persistence.Entity
import javax.persistence.EntityManager
import javax.persistence.SqlResultSetMapping

@Repository
public interface MatchRepository : JpaRepository<Match, ID>, JpaSpecificationExecutor<Match> {

    @Query("SELECT u FROM Match u WHERE u.startDt < ?1 and (u.team1ID = ?2 or u.team2ID = ?2) ORDER BY u.startDt")
    fun findMatchesBefore(time: String, team: ID) : List<Match>

    @Query(value = "SELECT Elo, teamID, MAX(startDt) AS startDt" +
            " FROM (SELECT u.team1Elo as Elo, u.team1ID as teamID, u.startDt as startDt FROM Match u WHERE startDt <= ?1 " +
            "UNION " +
            "SELECT u.team1Elo as Elo, u.team2Elo as teamID, u.startDt as startDt FROM Match u WHERE startDt <= ?1)" +
            "GROUP BY teamID",
    nativeQuery = true)
    fun findAllTeamsBefore(time:String) : List<Any>?

//    @Query(value = "SELECT Elo, teamID, MAX(startDt) AS startDt" +
//            " FROM (SELECT u.team1Elo as Elo, u.team1ID as teamID, u.startDt as startDt FROM Match u WHERE startDt <= ?1 " +
//            "UNION " +
//            "SELECT u.team1Elo as Elo, u.team2Elo as teamID, u.startDt as startDt FROM Match u WHERE startDt <= ?1)" +
//            "GROUP BY teamID",
//        nativeQuery = true)
}
