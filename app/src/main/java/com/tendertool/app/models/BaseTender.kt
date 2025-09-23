package com.tendertool.app.models

import androidx.room.Entity
import androidx.room.PrimaryKey

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
)