package com.elo.elo_calculation.generated

import com.elo.elo_calculation.generated.calendarquery.MatchPaginator
import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.Int
import kotlin.String
import kotlin.reflect.KClass

public const val CALENDAR_QUERY: String =
    "query CalendarQuery(${'$'}page: Int, ${'$'}first: Int){\r\n  calendar(filters:{has_score:true},first: ${'$'}first, page: ${'$'}page){\r\n    data {\r\n      match_id\r\n      round_id\r\n      tournament_id\r\n      start_dt\r\n      team1{\r\n        team_id\r\n        full_name\r\n      }\r\n      team2{\r\n        team_id\r\n        full_name\r\n      }\r\n      gf\r\n      ga\r\n      gfp\r\n      gap\r\n    }\r\n  }\r\n}"

@Generated
public class CalendarQuery(
  public override val variables: CalendarQuery.Variables
) : GraphQLClientRequest<CalendarQuery.Result> {
  public override val query: String = CALENDAR_QUERY

  public override val operationName: String = "CalendarQuery"

  public override fun responseType(): KClass<CalendarQuery.Result> = CalendarQuery.Result::class

  @Generated
  public data class Variables(
    public val page: OptionalInput<Int> = OptionalInput.Undefined,
    public val first: OptionalInput<Int> = OptionalInput.Undefined
  )

  @Generated
  public data class Result(
    public val calendar: MatchPaginator? = null
  )
}
