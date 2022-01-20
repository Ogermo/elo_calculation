package com.elo.elo_calculation.impl.service;
import com.elo.elo_calculation.api.service.EloService
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import graphql.servlet.internal.GraphQLRequest
import kotlinx.coroutines.runBlocking
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Service
class EloServiceImpl : EloService {
    val httpClient: HttpClient = HttpClient.create()
    val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient.wiretap(true))
    val webClientBuilder = WebClient.builder()
        .clientConnector(connector)
        .defaultHeader("Api-Key", "N4yRsKzw9rKmE42kPk8QWvPg376cGJypQHkEU3VCuPHVbQxQQeQMnmXCUW9pJEdB")

    val client = GraphQLWebClient(
        url = "https://api.joinsport.io/graphql",
        builder = webClientBuilder
    )



    override fun addMatch() : String{
        val string = "    tournaments{\n" +
                "      data{\n" +
                "        tournament_id\n" +
                "        full_name\n" +
                "      }"
        val request : GraphQLRequest = GraphQLRequest()
        request.query = "    tournaments{\n" +
                "      data{\n" +
                "        tournament_id\n" +
                "        full_name\n" +
                "      }"

        runBlocking{
//            val response = client.execute(ss)
//            System.out.println(response.data)
        }

        return "Hello world!"
    }
}
