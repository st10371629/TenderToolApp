package com.tendertool.app.src

import android.util.Log
import java.time.Instant
import com.tendertool.app.models.BaseTender
import com.tendertool.app.src.DateUtil.toSimpleDate
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object Analyticals {

    data class ClosingSoonResult(val closingSoon : Int, val notClosingSoon : Int)

    //calculate tenders in watchlist with closing dates that fall within the 7 day window
    fun calculateClosingSoon(watchlist: List<BaseTender>, daysWindow: Long = 7L) : ClosingSoonResult {
        val today = LocalDate.now(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        var closingSoon = 0
        var validTotal = 0

        Log.d("AnalyticsDebug", "Starting calculation. Watchlist size: ${watchlist.size}")

        watchlist.forEachIndexed { index, t ->
            val raw = t.closingDate.toSimpleDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
            Log.d("AnalyticsDebug", "Tender[$index]: raw closingDate = $raw")

            val closingDate: LocalDate? = try
            {
                //try ISO date
                LocalDate.parse(raw, formatter)
            }
            catch(e: Exception)
            {
                Log.e("AnalyticsDebug", "Failed ISO parse for tender[$index]: ${e.message}")
                try
                {
                    Instant.ofEpochMilli(raw.toLong()).atZone(ZoneId.systemDefault()).toLocalDate()
                }
                catch (ex: Exception)
                {
                    Log.e("AnalyticsDebug", "Failed epoch parse for tender[$index]: ${ex.message}")
                    null
                }
            }

            //increment values
            closingDate?.let { date ->
                validTotal++
                val daysUntil = ChronoUnit.DAYS.between(today, date)
                Log.d("AnalyticsDebug", "Tender[$index]: closing in $daysUntil days")
                if(daysUntil in 0..daysWindow) closingSoon++
            } ?: Log.w("AnalyticsDebug", "Tender[$index] has invalid closing date")
        }

        val notClosingSoon = (validTotal - closingSoon).coerceAtLeast(0)
        Log.d(
            "AnalyticsDebug",
            "Calculation complete: closingSoon=$closingSoon, notClosingSoon=$notClosingSoon"
        )
        return ClosingSoonResult(closingSoon, notClosingSoon)
    }
}