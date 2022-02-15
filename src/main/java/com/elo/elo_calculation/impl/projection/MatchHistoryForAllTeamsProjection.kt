package com.elo.elo_calculation.impl.projection

import com.elo.elo_calculation.generated.ID

interface MatchHistoryForAllTeamsProjection {
    val teamID : ID?
    val amount : Int
    val goals_given : Int
    val goals_received : Int
    val win : Int
    val loss : Int
    val draw : Int
}

fun List<MatchHistoryForAllTeamsProjection>.toTable() : String{
    var answer = "  <table border = '1'>" +
            "   <caption>Статистика обработанных матчей " +
            "(количество, победы/ничьи/поражения, забитые пропущенные мячи) всех команд</caption>" +
            "   <tr>" +
            "    <th>teamID</th>" +
            "    <th>Match amount</th>" +
            "    <th>Goals given</th>"+
            "    <th>Goals received</th>"+
            "    <th>Wins</th>"+
            "    <th>Loss</th>"+
            "    <th>Draw</th>"+
            "   </tr>"
    for (value in this){
        answer += "<tr><td>${value.teamID}</td><td>${value.amount}</td><td>${value.goals_given}</td>" +
                "<td>${value.goals_received}</td><td>${value.win}</td><td>${value.loss}</td>" +
                "<td>${value.draw}</td></tr>"
    }
    answer += "</table>"
    return answer
}