package com.elo.elo_calculation.impl.projection

import com.elo.elo_calculation.generated.ID

interface PlacementTeamProjection {
    val placement : Int?
    val teamID : ID?
    val elo : Int?
}

fun List<PlacementTeamProjection>.toTable() : String{
    var answer = "  <table border = '1'>" +
            "   <caption>Показатели рейтинга (позиция в общем списке и значение рейтинга)" +
            " на две указанные даты для указанной команды</caption>" +
            "   <tr>" +
            "    <th>Placement</th>" +
            "    <th>TeamID</th>" +
            "    <th>Elo</th>"+
            "   </tr>"
    for (value in this){
        answer += "<tr><td>${value.placement}</td><td>${value.teamID}</td><td>${value.elo}</td></tr>"
    }
    answer += "</table>"
    return answer
}