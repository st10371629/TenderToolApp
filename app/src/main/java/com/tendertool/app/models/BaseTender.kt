package com.tendertool.app.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BaseTender")
open class BaseTender
    (
    @PrimaryKey
    open val TenderID: String,
    open val title: String,
    open val Status: String,
    open val PublishedDate: String,
    open val ClosingDate: String,
    open val DateAppended: String,
    open val Source: String,
    open val Tags: List<Tag> = emptyList(),
    open val Description: String? = null,
    open val SupportingDocs: List<SupportingDoc> = emptyList(),
)