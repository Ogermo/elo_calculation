package com.elo.elo_calculation.impl.projection

import com.elo.elo_calculation.generated.ID

interface MaxRatingDifferenceProjection {
    val matchID : ID?
    val teamID : ID?
    val elo : Int?
    val previous_elo : Int?
    val start_Dt : String?
    val diff : Int?
}

fun List<MaxRatingDifferenceProjection>.toTable() : String{
    var answer = "  <table border = '1'>" +
            "   <caption>Максимальные изменения рейтинга за всю историю </caption>" +
            "   <tr>" +
            "    <th>matchID</th>" +
            "    <th>TeamID</th>" +
            "    <th>Elo</th>"+
            "    <th>Previous Elo</th>"+
            "    <th>Date</th>"+
            "    <th>Difference</th>"+
            "   </tr>"
    for (value in this){
        answer += "<tr><td>${value.matchID}</td><td>${value.teamID}</td><td>${value.elo}</td>" +
                "<td>${value.previous_elo}</td><td>${value.start_Dt}</td><td>${value.diff}</td></tr>"
    }
    answer += "</table>"
    return answer
}
