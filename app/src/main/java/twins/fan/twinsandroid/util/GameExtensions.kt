package twins.fan.twinsandroid.util

import android.graphics.Color
import twins.fan.twinsandroid.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun String.toFormattedDate(): StringBuilder{
    val result = StringBuilder()

    for(i in indices step 2){
        result.append(substring(i, minOf(i+2, length)))
        if(i + 2 < length){
            result.append(".")
        }
    }

    val localDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyMMdd"))
    val dayOfWeekKorean = localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ko-KR"))

    result.append("(${dayOfWeekKorean.substring(0,1)})")

    return result
}

fun scoreLocate(isLGHome: Boolean, lgScore: List<String>, versusScore: List<String>): String {
    val visitScore = if(isLGHome) versusScore[versusScore.size-4] else lgScore[lgScore.size-4]
    val homeScore = if(isLGHome) lgScore[lgScore.size-4] else versusScore[versusScore.size-4]

    return homeScore+"_"+visitScore
}

fun checkLogo(versusTeam:String, isLGHome:Boolean): List<Int> {
    var imgPath = 0
    val visitLogo:Int
    val homeLogo:Int
    when(versusTeam){
        "삼성" -> imgPath = R.raw.lions
        "키움" -> imgPath = R.raw.heroes
        "롯데" -> imgPath = R.raw.giants
        "두산" -> imgPath = R.raw.bears
        "KT"   -> imgPath = R.raw.wiz
        "SSG"  -> imgPath = R.raw.landers
        "한화" -> imgPath = R.raw.eagles
        "기아" -> imgPath = R.raw.tigers
        "NC"   -> imgPath = R.raw.dinos
    }
    when(isLGHome){
        true -> {
            visitLogo = imgPath
            homeLogo = R.raw.twins
        }
        else -> {
            visitLogo = R.raw.twins
            homeLogo = imgPath
        }
    }

    return listOf(homeLogo, visitLogo)
}

fun pitchResult(winner:String, loser:String, homeScore:Int, visitScore:Int): List<List<Any>> {
    val winColor = "#204B9B"
    val loseColor = "#B32653"
    val homeResult = listOf(
        when {
            homeScore > visitScore -> "승: $winner"
            homeScore < visitScore -> "패: $loser"
            else -> "무"
        },
        when {
            homeScore > visitScore -> Color.parseColor(winColor)
            homeScore < visitScore -> Color.parseColor(loseColor)
            else -> Color.BLACK
        }
    )
    val visitResult = listOf(
        when {
            visitScore > homeScore -> "승: $winner"
            visitScore < homeScore -> "패: $loser"
            else -> "무"
        },
        when {
            visitScore > homeScore -> Color.parseColor(winColor)
            visitScore < homeScore -> Color.parseColor(loseColor)
            else -> Color.BLACK
        }
    )
    return listOf(homeResult, visitResult)
}