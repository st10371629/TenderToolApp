package com.tendertool.app.src

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtil {

    fun String.toSimpleDate(): String {
        return try {
            LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME).toLocalDate().toString()
        } catch (e: Exception) {
            try {
                LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate().toString()
            } catch (_: Exception) {
                this
            }
        }
    }
}