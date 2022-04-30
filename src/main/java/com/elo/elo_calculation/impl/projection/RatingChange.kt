package com.elo.elo_calculation.impl.projection

import com.elo.elo_calculation.generated.ID

data class RatingChange(
    val teamID: ID,
    val eloPrev : Int,
    val eloNow : Int
)
