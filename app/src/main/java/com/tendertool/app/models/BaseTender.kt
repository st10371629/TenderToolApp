package com.tendertool.app.models

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Entity(tableName = "BaseTender")
open class BaseTender
    (
    @PrimaryKey
    open val tenderID: String,
    open val title: String,
    open val status: String,
    open val publishedDate: String,
    open val closingDate: String,
    open val dateAppended: String,
    open val source: String,
    open val tags: List<Tag> = emptyList(),
    open val description: String? = null,
    open val supportingDocs: List<SupportingDoc> = emptyList(),
) {

    /**Checks if the tender is closing within X days from today.*/
    fun isClosingWithinDays(days: Int): Boolean {
        return try {
            // Parse ISO 8601 date-time
            val closingDateTime = java.time.LocalDateTime.parse(closingDate)
            val closing = closingDateTime.toLocalDate() // get only the date part
            val today = java.time.LocalDate.now()
            val diff = java.time.temporal.ChronoUnit.DAYS.between(today, closing)
            diff in 0..days
        } catch (e: Exception) {
            Log.e("BaseTender", "Invalid date: $closingDate for tender $title", e)
            false
        }
    }
}