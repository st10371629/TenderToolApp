package com.tendertool.app.models

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore

@Entity(tableName = "BaseTender")
open class BaseTender
    (
    @PrimaryKey
    open var tenderID: String,
    open var title: String,
    open var status: String,
    open var publishedDate: String,
    open var closingDate: String,
    open var dateAppended: String,
    open var source: String,
    open var description: String?,

    @Ignore
    open var tags: List<Tag> = emptyList(),
    @Ignore
    open var supportingDocs: List<SupportingDoc> = emptyList(),
) {
    // No-arg constructor required by Room and subclasses
    constructor() : this("", "", "", "", "", "", "", null)
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