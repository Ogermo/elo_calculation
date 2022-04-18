package com.elo.elo_calculation.impl.projection

import com.elo.elo_calculation.generated.ID
import javax.persistence.Id

interface EloOfAllMatchesForTeamProjection {
    val matchID: ID
    val teamID: ID
    val elo: Int
    val start_Dt: String
}

fun List<EloOfAllMatchesForTeamProjection>.toTable() : String{
    var answer = ""
    val teamId = this.getOrNull(0)?.elo
    if (teamId == null){
        answer = "Нет данных для указанной команды <br>"
    }
    answer += "  <table border = '1'>" +
            "   <caption>Рейтинг команды ${teamId}  за всю историю</caption>" +
            "   <tr>" +
            "    <th>MatchID</th>" +
            "    <th>Elo</th>"+
            "    <th>Date</th>"+
            "   </tr>"
    for (value in this){
        answer += "<tr><td>${value.matchID}</td><td>${value.elo}</td>" +
                "<td>${value.start_Dt}</td></tr>"
    }
    answer += "</table>"
    return answer
}