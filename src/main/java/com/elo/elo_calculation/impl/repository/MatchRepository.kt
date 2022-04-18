package com.elo.elo_calculation.impl.repository;

import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.entity.Match;
import com.elo.elo_calculation.impl.projection.*
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query

@Repository
public interface MatchRepository : JpaRepository<Match, ID>, JpaSpecificationExecutor<Match> {

    @Query("SELECT TOP(1) * FROM MATCH " +
            "WHERE CALCULATED = FALSE " +
            "ORDER BY START_DT, MATCHID ",
    nativeQuery = true)
    fun findMatchToCalculate() : Match?

    @Query("SELECT * FROM MATCH WHERE MATCHID = " +
            "(SELECT NEXTMATCH FROM ( " +
            "SELECT *, LEAD(MATCHID) OVER (ORDER BY START_DT, MATCHID) AS NextMatch " +
            "FROM MATCH WHERE START_DT >= ( " +
            "SELECT TOP(1) START_DT FROM MATCH " +
            "WHERE MATCHID = ?2 " +
            ") AND (TEAM1ID = ?1 OR TEAM2ID = ?1) " +
            "ORDER BY START_DT, MATCHID " +
            ") WHERE MATCHID = ?2) ",
    nativeQuery = true)
    fun findNextMatch(teamID: ID, matchID : ID) : Match?

    @Query("SELECT * FROM MATCH WHERE MATCHID = " +
            "(SELECT PREVMATCH FROM ( " +
            "SELECT *, LAG(MATCHID) OVER (ORDER BY START_DT, MATCHID) AS PrevMatch " +
            "FROM MATCH WHERE START_DT <= ( " +
            "SELECT TOP(1) START_DT FROM MATCH " +
            "WHERE MATCHID = ?2 " +
            ") AND (TEAM1ID = ?1 OR TEAM2ID = ?1) " +
            "ORDER BY START_DT, MATCHID " +
            ") WHERE MATCHID = ?2) ",
    nativeQuery = true)
    fun findPrevMatch(teamID: ID, matchID : ID) : Match?

    @Query("SELECT u FROM Match u WHERE (u.team1ID = ?1 or u.team2ID = ?1) order by u.startDt")
    fun findAllMatchesOfTeam(teamID : ID) : List<Match>

    @Query("SELECT u FROM Match u WHERE u.startDt < ?1 and (u.team1ID = ?2 or u.team2ID = ?2) ORDER BY u.startDt")
    fun findMatchesBefore(time: String, team: ID) : List<Match>

    @Query(value = "SELECT matchID, teamID, elo, start_Dt FROM ( " +
            "SELECT *, MAX(matchID) OVER (PARTITION BY teamID) as maxMatchID FROM ( " +
            "SELECT *, MAX(start_Dt) OVER (PARTITION BY teamID) as  maxStart_Dt FROM " +
            "(SELECT u1.MATCHID, u1.ELO, u1.TEAMID, u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID " +
            "WHERE u2.START_DT <= ?1)) " +
            "WHERE start_Dt = maxStart_Dt " +
            ") WHERE matchID = maxMatchID",
    nativeQuery = true)
    fun findAllTeamsBefore(time:String) : List<LastMatchOfAllTeamsBeforeDateProjection>

    @Query(value = "SELECT matchID, teamID, elo, start_Dt FROM ( " +
            "SELECT *, MAX(matchID) OVER (PARTITION BY teamID) as maxMatchID FROM ( " +
            "SELECT *, MAX(Elo) OVER (PARTITION BY teamID) as  maxElo FROM " +
            "(SELECT u1.MATCHID, u1.ELO, u1.TEAMID, u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID" +
            ")) WHERE Elo = maxElo " +
            ") WHERE matchID = maxMatchID",
    nativeQuery = true)
    fun findMaxEloOfAllTeams() : List<MaxEloOfAllTeamsProjection>

    @Query("SELECT matchID, teamID, elo, start_Dt FROM ( " +
            "SELECT *, MAX(matchID) OVER (PARTITION BY teamID) as maxMatchID FROM ( " +
            "SELECT *, MAX(Elo) OVER (PARTITION BY teamID) as  maxElo FROM " +
            "(SELECT u1.MATCHID, u1.ELO, u1.TEAMID, u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID " +
            "WHERE u1.TEAMID = ?1 ) " +
            ") WHERE Elo = maxElo " +
            ") WHERE matchID = maxMatchID",
    nativeQuery = true)
    fun findMaxEloOfTeam(team: ID) : List<MaxEloOfTeamProjection>

    @Query(value = "SELECT teamID, Elo, Start_Dt FROM ( " +
            "SELECT u1.MATCHID, u1.ELO, u1.TEAMID, u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID " +
            "WHERE u2.START_DT < ?1 ) " +
            "WHERE Elo in (" +
            "SELECT MAX(ELO) FROM ( " +
            "SELECT u1.MATCHID, u1.ELO, u1.TEAMID, u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID " +
            "WHERE u2.START_DT < ?1 ) " +
            ")",
        nativeQuery = true)
    fun findLeaderOnCurrentDate(date: String) : List<LeaderOnCurrentDateProjection>

    @Query(value = "SELECT teamID as teamID, COUNT(matchID) as amount ,SUM(team_Goal) as goals_given, SUM(opponent_goal) as goals_received, " +
            "COUNT(CASE WHEN team_Goal > opponent_Goal THEN 1 " +
            "WHEN team_penalty IS NOT NULL AND opponent_penalty IS NOT NULL AND team_penalty > opponent_penalty THEN 1 END) as win, " +
            "COUNT(CASE WHEN opponent_Goal > team_Goal THEN 1 " +
            "WHEN team_penalty IS NOT NULL AND opponent_penalty IS NOT NULL AND opponent_penalty > team_penalty THEN 1 END) as loss, " +
            "COUNT(CASE WHEN team_penalty IS NOT NULL AND opponent_penalty IS NOT NULL AND opponent_penalty = team_penalty THEN 1 " +
            "WHEN team_penalty IS NULL AND opponent_penalty IS NULL AND team_Goal = opponent_Goal THEN 1 END) as draw " +
            "FROM ( SELECT u1.MATCHID, u1.ELO, u1.TEAMID, " +
            "CASE WHEN u1.TEAMID = u2.TEAM1ID THEN u2.ga ELSE u2.gf END AS TEAM_GOAL, " +
            "CASE WHEN u1.TEAMID = u2.TEAM1ID THEN u2.gf ELSE u2.ga END AS OPPONENT_GOAL, " +
            "CASE WHEN u1.TEAMID = u2.TEAM1ID THEN u2.gap ELSE u2.gfp END AS TEAM_PENALTY, " +
            "CASE WHEN u1.TEAMID = u2.TEAM1ID THEN u2.gfp ELSE u2.gap END AS OPPONENT_PENALTY, " +
            "u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID) " +
            "GROUP BY teamID ",
    nativeQuery = true)
    fun findMatchHistoryForAllTeams() : List<MatchHistoryForAllTeamsProjection>

    @Query(value = "SELECT teamID as teamID, COUNT(matchID) as amount ,SUM(team_Goal) as goals_given, SUM(opponent_goal) as goals_received, " +
            "COUNT(CASE WHEN team_Goal > opponent_Goal THEN 1 " +
            "WHEN team_penalty IS NOT NULL AND opponent_penalty IS NOT NULL AND team_penalty > opponent_penalty THEN 1 END) as win, " +
            "COUNT(CASE WHEN opponent_Goal > team_Goal THEN 1 " +
            "WHEN team_penalty IS NOT NULL AND opponent_penalty IS NOT NULL AND opponent_penalty > team_penalty THEN 1 END) as loss, " +
            "COUNT(CASE WHEN team_penalty IS NOT NULL AND opponent_penalty IS NOT NULL AND opponent_penalty = team_penalty THEN 1 " +
            "WHEN team_penalty IS NULL AND opponent_penalty IS NULL AND team_Goal = opponent_Goal THEN 1 END) as draw " +
            "FROM ( SELECT u1.MATCHID, u1.ELO, u1.TEAMID, " +
            "CASE WHEN u1.TEAMID = u2.TEAM1ID THEN u2.ga ELSE u2.gf END AS TEAM_GOAL, " +
            "CASE WHEN u1.TEAMID = u2.TEAM1ID THEN u2.gf ELSE u2.ga END AS OPPONENT_GOAL, " +
            "CASE WHEN u1.TEAMID = u2.TEAM1ID THEN u2.gap ELSE u2.gfp END AS TEAM_PENALTY, " +
            "CASE WHEN u1.TEAMID = u2.TEAM1ID THEN u2.gfp ELSE u2.gap END AS OPPONENT_PENALTY, " +
            "u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID " +
            "WHERE u1.TEAMID = ?1 ) " +
            "GROUP BY teamID ",
        nativeQuery = true)
    fun findMatchHistory(teamID: ID) : List<MatchHistoryProjection>

    @Query(value = "SELECT TOP(1) *, Elo - previous_Elo as diff FROM( " +
            "SELECT matchID, teamID, Elo, ISNULL(LAG(Elo) " +
            "OVER (PARTITION BY teamID ORDER BY start_Dt, matchID),500) AS previous_elo ,  start_Dt  FROM ( " +
            "SELECT u1.MATCHID, u1.ELO, u1.TEAMID, u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID " +
            "ORDER BY START_DT, MATCHID) " +
            ") ORDER BY ABS(Elo - previous_Elo) DESC",
    nativeQuery = true)
    fun findMaxRatingDifference() : List<MaxRatingDifferenceProjection>

    @Query(value = "SELECT ROW_NUMBER() OVER(ORDER BY (Elo) DESC) AS placement, teamID, elo FROM ( " +
            "SELECT *, MAX(matchID) OVER (PARTITION BY teamID) as maxMatchID FROM ( " +
            "SELECT *, MAX(start_Dt) OVER (PARTITION BY teamID) as  maxStart_Dt FROM " +
            "(SELECT u1.MATCHID, u1.ELO, u1.TEAMID, u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID " +
            "WHERE u2.START_DT <= ?1) " +
            ") " +
            "WHERE start_Dt = maxStart_Dt " +
            ") WHERE matchID = maxMatchID " +
            "ORDER BY elo DESC",
    nativeQuery = true)
    fun findPlacementAll(date: String) : List<PlacementAllProjection>

    @Query(value = "SELECT * FROM( " +
            "SELECT ROW_NUMBER() OVER(ORDER BY (Elo) DESC) AS placement, teamID, elo FROM ( " +
            "SELECT *, MAX(matchID) OVER (PARTITION BY teamID) as maxMatchID FROM ( " +
            "SELECT *, MAX(start_Dt) OVER (PARTITION BY teamID) as  maxStart_Dt FROM " +
            "(SELECT u1.MATCHID, u1.ELO, u1.TEAMID, u2.START_DT FROM ELO u1 " +
            "JOIN MATCH u2 ON u1.MATCHID = u2.MATCHID " +
            "WHERE START_DT <= ?1 ) " +
            ") WHERE start_Dt = maxStart_Dt " +
            ") WHERE matchID = maxMatchID " +
            "ORDER BY elo DESC " +
            ") WHERE teamID = ?2",
        nativeQuery = true)
    fun findPlacementTeam(date: String , teamID: ID) : List<PlacementTeamProjection>
}
