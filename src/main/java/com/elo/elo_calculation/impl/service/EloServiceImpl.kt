package com.elo.elo_calculation.impl.service;
import com.elo.elo_calculation.api.service.EloService
import com.elo.elo_calculation.generated.CalendarQuery
import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.entity.*
import com.elo.elo_calculation.impl.repository.*
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import kotlinx.coroutines.runBlocking
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.lang.Math.ceil
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.withSign

@Service
class EloServiceImpl(
    private val matchRepository: MatchRepository,
    private val eloRepository: EloRepository,
    private val roundRepository: RoundRepository,
    private val teamRepository: TeamRepository,
    private val tournamentRepository: TournamentRepository
) : EloService {
    val httpClient: HttpClient = HttpClient.create()
    val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient.wiretap(true))
    val webClientBuilder = WebClient.builder()
        .clientConnector(connector)
        .defaultHeader("Api-Key", "N4yRsKzw9rKmE42kPk8QWvPg376cGJypQHkEU3VCuPHVbQxQQeQMnmXCUW9pJEdB")

    val client = GraphQLWebClient(
        url = "https://api.joinsport.io/graphql",
        builder = webClientBuilder
    )

    val DEFAULT_ELO = 500

    override fun downloadFromServer() : String{
        var page = 0
        while(true){
            val variables = CalendarQuery.Variables(first = OptionalInput.Defined(500),page = OptionalInput.Defined(page))
            val request = CalendarQuery(variables)
            var response : GraphQLClientResponse<CalendarQuery.Result>
            runBlocking{
                response = client.execute(request)
                for (match in response.data!!.calendar!!.data){
                    //Check if match data is correct and complete
                    if ((match.team1 == null) || (match.team2 == null) || (match.ga == null) || (match.gf == null)){
                        continue
                    }

                    if (!tournamentRepository.existsById(match.tournament_id)){
                        tournamentRepository.save(Tournament(match.tournament_id))
                    }
                    if (!roundRepository.existsById(match.round_id)){
                        roundRepository.save(Round(match.round_id))
                    }
                    if (!teamRepository.existsById(match.team1.team_id)){
                        teamRepository.save(Team(match.team1.team_id,match.team1.full_name))
                    }
                    if (!teamRepository.existsById(match.team2.team_id)){
                        teamRepository.save(Team(match.team2.team_id,match.team2.full_name))
                    }
                    //check match
                    val oldMatch = matchRepository.findByIdOrNull(match.match_id)
                    if (oldMatch != null)
                    {
                        //check if any of important values were changed
                        //startDt,gf,ga,gfp,gap
                        if ((oldMatch.startDt != match.start_dt) ||
                            (oldMatch.gf != match.gf) || (oldMatch.ga != match.ga) ||
                            (oldMatch.gfp != match.gfp) || (oldMatch.gap != match.gap))
                        {
                            oldMatch.gf = match.gf
                            oldMatch.ga = match.ga
                            oldMatch.gfp = match.gfp
                            oldMatch.gap = match.gap
                            oldMatch.startDt = match.start_dt
                            oldMatch.calculated = false
                            matchRepository.save(oldMatch)
                        }
                        continue
                    }

                    matchRepository.save(Match(
                        match.match_id,
                        match.round_id,
                        match.tournament_id,
                        match.start_dt,
                        match.gf,
                        match.ga,
                        match.gfp,
                        match.gap,
                        match.team1,
                        match.team2
                    ))
                }
            }
            if (response.data!!.calendar!!.data.isEmpty()){
                break
            }
            page++
        }
        return "Done!"
    }

    override fun calculateElo(): String {
        while (true){
            val match = matchRepository.findMatchToCalculate()?: return "Done"
            calculateMatch(match)
        }
    }

    private fun calculateMatch(match: Match) {

        var K : Int = tournamentRepository.findById(match.tournamentID).get().weight
        roundRepository.findById(match.roundID).get().weight?.let {K = it}
        match.weight?.let{K = it}

        val result = match.calculateResult()

        val team1Elo = eloRepository.findPrevMatchElo(match.team1ID!!,match.matchID!!)?.elo ?: DEFAULT_ELO
        val team2Elo = eloRepository.findPrevMatchElo(match.team2ID!!,match.matchID!!)?.elo ?: DEFAULT_ELO

        //calculate elo for first team
        calculateEloForTeam(matchID = match.matchID!!,teamID = match.team1ID!!, elo = team1Elo,
            diff = team1Elo - team2Elo, K = K, W = result.first)

        //calculate elo for second team
        calculateEloForTeam(matchID = match.matchID!!,teamID = match.team2ID!!, elo = team2Elo,
            diff = team2Elo - team1Elo, K = K, W = result.second)


        //Mark this match as done
        match.calculated = true
        matchRepository.save(match)

    }

    private fun calculateEloForTeam(matchID: ID,teamID:ID, elo: Int, diff: Int,  K:Int,W:Double){

        val teamWe = 1.0 / (Math.pow(10.0,-(diff)/600.0) + 1.0)
        val eloDouble = K * (W - teamWe)
        val eloRounded = ceil(eloDouble.absoluteValue).withSign(eloDouble).toInt()

        eloRepository.save(Elo(matchID,teamID,elo + eloRounded))

        val nextMatch = matchRepository.findNextMatch(teamID,matchID)
        if (nextMatch != null){
            nextMatch.calculated = false
            matchRepository.save(nextMatch)
        }
    }

    override fun showMatches(): String {
        var answer : String = ""
        val list = matchRepository.findAll()
        list.sortBy { it.startDt }
        for (index in list){
            answer += "<br>" + index.matchID + " " + index.startDt +
                    " team1: " + index.team1ID + ":" +
                    " team2: " + index.team2ID + ":"
        }
        return answer
    }

    override fun showTeams(): String {
        var answer : String = ""
        for (index in teamRepository.findAll()){
            answer += "<br>" + index.teamId + " " + index.fullName
        }
        return answer      }

    override fun showRounds(): String {
        var answer : String = ""
        for (index in roundRepository.findAll()){
            answer += "<br>" + index.roundID + " " + index.weight
        }
        return answer
    }

    override fun showTournaments(): String {
        var answer : String = ""
        for (index in tournamentRepository.findAll()){
            answer += "<br>" + index.tournamentID + " " + index.weight
        }
        return answer
    }

}
