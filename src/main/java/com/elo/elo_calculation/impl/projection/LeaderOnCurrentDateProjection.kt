package com.elo.elo_calculation.impl.projection

import com.elo.elo_calculation.generated.ID

interface LeaderOnCurrentDateProjection {
    val teamID : ID?
    val elo : Int?
    val start_Dt : String?
}

fun List<LeaderOnCurrentDateProjection>.toTable() : String{
    var answer = "  <table border = '1'>" +
            "   <caption>Лидер рейтинга на указанную дату</caption>" +
            "   <tr>" +
            "    <th>TeamID</th>" +
            "    <th>Elo</th>"+
            "    <th>Date</th>"+
            "   </tr>"
    for (value in this){
        answer += "<tr><td>${value.teamID}</td><td>${value.elo}</td>" +
                "<td>${value.start_Dt}</td></tr>"
    }
    answer += "</table>"
    return answer
}