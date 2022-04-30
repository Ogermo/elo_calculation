package com.elo.elo_calculation.impl.projection

import com.elo.elo_calculation.generated.ID

interface MaxEloProjection {
    val matchID : ID?
    val teamID : ID?
    val elo : Int?
    val start_Dt : String?
}

fun List<MaxEloProjection>.toTable() : String{
    var answer = "  <table border = '1'>" +
            "   <caption>Максимальный рейтинг указанной команды (значение и дата)</caption>" +
            "   <tr>" +
            "    <th>matchID</th>" +
            "    <th>TeamID</th>" +
            "    <th>Elo</th>"+
            "    <th>Date</th>"+
            "   </tr>"
    for (value in this){
        answer += "<tr><td>${value.matchID}</td><td>${value.teamID}</td><td>${value.elo}</td>" +
                "<td>${value.start_Dt}</td></tr>"
    }
    answer += "</table>"
    return answer
}