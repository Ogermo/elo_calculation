package com.elo.elo_calculation.generated

import com.elo.elo_calculation.generated.tournamentquery.MatchPaginator
import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.String
import kotlin.reflect.KClass

public const val TOURNAMENT_QUERY: String =
    "query TournamentQuery{\r\n    calendar{\r\n        data{\r\n            match_id\r\n            round_id\r\n            start_dt\r\n            gf\r\n            ga\r\n        }\r\n    }\r\n}"

@Generated
public class TournamentQuery : GraphQLClientRequest<TournamentQuery.Result> {
  public override val query: String = TOURNAMENT_QUERY

  public override val operationName: String = "TournamentQuery"

  public override fun responseType(): KClass<TournamentQuery.Result> = TournamentQuery.Result::class

  @Generated
  public data class Result(
    public val calendar: MatchPaginator? = null
  )
}
