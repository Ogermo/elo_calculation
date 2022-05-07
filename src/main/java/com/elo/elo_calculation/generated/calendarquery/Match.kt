package com.elo.elo_calculation.generated.calendarquery

import com.elo.elo_calculation.generated.DateTime
import com.elo.elo_calculation.generated.ID
import com.expediagroup.graphql.client.Generated
import kotlin.Int

@Generated
public data class Match(
  public val match_id: ID,
  public val round_id: Int,
  public val tournament_id: Int,
  public val start_dt: DateTime? = null,
  public val team1: Team? = null,
  public val team2: Team? = null,
  public val gf: Int? = null,
  public val ga: Int? = null,
  public val gfp: Int? = null,
  public val gap: Int? = null
)
