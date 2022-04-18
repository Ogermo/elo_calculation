package com.elo.elo_calculation.impl.serializable

import com.elo.elo_calculation.generated.ID
import java.io.Serializable

class EloId(val matchID: ID = "", val teamID: ID = "") : Serializable