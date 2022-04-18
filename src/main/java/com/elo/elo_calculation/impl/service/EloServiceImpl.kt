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
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.*

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

                    if (!tournamentRepository.existsById(match.tournament_id)){
                        tournamentRepository.save(Tournament(match.tournament_id))
                    }
                    if (!roundRepository.existsById(match.round_id)){
                        roundRepository.save(Round(match.round_id))
                    }
                    if (match.team1 != null){
                        if (!teamRepository.existsById(match.team1.team_id)){
                            teamRepository.save(Team(match.team1.team_id,match.team1.full_name))
                        }
                    }
                    if (match.team2 != null){
                        if (!teamRepository.existsById(match.team2.team_id)){
                            teamRepository.save(Team(match.team2.team_id,match.team2.full_name))
                        }
                    }

                    matchRepository.save(Match(
                        match.match_id,
                        match.round_id,
                        match.tournament_id,
                        match.start_dt,
                        match.gf!!,
                        match.ga!!,
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

    override fun recalculateElo() : String{
        val matchList = matchRepository.findAll()
        matchList.sortBy { it.startDt }
        val teamMap = mutableMapOf<ID,Int>()
        for (match in matchList){
            var K : Int = tournamentRepository.findById(match.tournamentID).get().weight
            roundRepository.findById(match.roundID).get().weight?.let {K = it}
            match.weight?.let{K = it}
            val result = match.calculateResult()
            //gf = team1 ; ga = team2

            teamMap.putIfAbsent(match.team1ID!!,DEFAULT_ELO)
            teamMap.putIfAbsent(match.team2ID!!,DEFAULT_ELO)

            val team1_We = 1.0 / (Math.pow(10.0,-(teamMap[match.team1ID]!! - teamMap[match.team2ID]!!)/600.0) + 1.0)
            val team2_We = 1.0 / (Math.pow(10.0,-(teamMap[match.team2ID]!! - teamMap[match.team1ID]!!)/600.0) + 1.0)

            var eloDouble = K * (result.first - team1_We)
            var eloDr : Int = 0
            if (eloDouble > 0){
                eloDr = Math.ceil(eloDouble).toInt()
            } else {
                eloDr = Math.floor(eloDouble).toInt()
            }
            teamMap.put(match.team1ID!!,teamMap[match.team1ID]!! + eloDr)

            eloDouble = K * (result.second - team2_We)
            if (eloDouble > 0){
                eloDr = Math.ceil(eloDouble).toInt()
            } else {
                eloDr = Math.floor(eloDouble).toInt()
            }
            teamMap.put(match.team2ID!!,teamMap[match.team2ID]!! + eloDr)

            matchRepository.save(match)
            eloRepository.save(Elo(match.matchID!!,match.team1ID!!,teamMap[match.team1ID]!!))
            eloRepository.save(Elo(match.matchID!!,match.team2ID!!,teamMap[match.team2ID]!!))
        }
        return "Done!"
    }

    override fun calculateElo(): String {
        while (true){
            val match = matchRepository.findMatchToCalculate()?: return "Done"
            calculateMatch(match)
        }
    }

    override fun calculateMatch(match: Match) {

        var K : Int = tournamentRepository.findById(match.tournamentID).get().weight
        roundRepository.findById(match.roundID).get().weight?.let {K = it}
        match.weight?.let{K = it}

        val result = match.calculateResult()

        var team1Elo = eloRepository.findPrevMatchElo(match.team1ID!!,match.matchID!!)?.elo ?: DEFAULT_ELO
        var team2Elo = eloRepository.findPrevMatchElo(match.team2ID!!,match.matchID!!)?.elo ?: DEFAULT_ELO

        val team1_We = 1.0 / (Math.pow(10.0,-(team1Elo - team2Elo)/600.0) + 1.0)
        val team2_We = 1.0 / (Math.pow(10.0,-(team2Elo - team1Elo)/600.0) + 1.0)

        var eloDouble = K * (result.first - team1_We)
        var eloDr : Int = 0
        if (eloDouble > 0){
            eloDr = Math.ceil(eloDouble).toInt()
        } else {
            eloDr = Math.floor(eloDouble).toInt()
        }
        team1Elo += eloDr

        eloDouble = K * (result.second - team2_We)
        if (eloDouble > 0){
            eloDr = Math.ceil(eloDouble).toInt()
        } else {
            eloDr = Math.floor(eloDouble).toInt()
        }
        team2Elo += eloDr


        //save calculated Elo
        eloRepository.save(Elo(match.matchID!!,match.team1ID!!,team1Elo))
        eloRepository.save(Elo(match.matchID!!,match.team2ID!!,team2Elo))


        //Mark next matches as needed to be calculated
        val matchTeam1 = matchRepository.findNextMatch(match.team1ID!!,match.matchID!!)
        val matchTeam2 = matchRepository.findNextMatch(match.team2ID!!,match.matchID!!)

        if (matchTeam1 != null){
            matchTeam1.calculated = false
            matchRepository.save(matchTeam1)
        }
        if (matchTeam2 != null){
            matchTeam2.calculated = false
            matchRepository.save(matchTeam2)
        }

        //Mark this match as done
        match.calculated = true
        matchRepository.save(match)

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
