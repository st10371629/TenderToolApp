package com.tendertool.app.src

import java.time.Instant
import com.tendertool.app.models.BaseTender
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

        watchlist.forEach { t ->
            val raw = t.closingDate.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: return@forEach
            val closingDate: LocalDate? = try
            {
                //try ISO date
                LocalDate.parse(raw, formatter)
            }
            catch(e: Exception)
            {
                try
                {
                    Instant.ofEpochMilli(raw.toLong()).atZone(ZoneId.systemDefault()).toLocalDate()
                }
                catch (_: Exception)
                {
                    null
                }
            }

            //increment values
            closingDate?.let { date ->
                validTotal++
                val daysUntil = ChronoUnit.DAYS.between(today, date)
                if(daysUntil in 0..daysWindow) closingSoon++
            }
        }

        val notClosingSoon = (validTotal - closingSoon).coerceAtLeast(0)
        return ClosingSoonResult(closingSoon, notClosingSoon)
    }
}