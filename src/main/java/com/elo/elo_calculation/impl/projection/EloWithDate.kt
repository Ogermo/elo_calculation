package com.elo.elo_calculation.impl.projection

import com.elo.elo_calculation.generated.ID

data class EloWithDate(    val matchID: ID,
                           val teamID: ID,
                           val elo: Int,
                           val start_Dt: String)
