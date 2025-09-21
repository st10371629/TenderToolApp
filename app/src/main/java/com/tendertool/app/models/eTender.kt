package com.tendertool.app.models

import androidx.room.Entity

@Entity(tableName = "eTender")
data class eTender(
    override val tenderID: String,
    override val title: String,
    override val status: String,
    override val publishedDate: String,
    override val closingDate: String,
    override val dateAppended: String,
    override val source: String,
    override val tags: List<Tag> = emptyList(),
    override val description: String? = null,
    override val supportingDocs: List<SupportingDoc> = emptyList(),

    val tenderNumber: String?,
    val procurementMethod: String?,
    val procurementMethodDetails: String?,
    val procuringEntity: String?,
    val currency: String?,
    val value: Double?,
    val category: String?,
    val tenderer: String?,

) : BaseTender(tenderID, title, status, publishedDate, closingDate, dateAppended, source, tags, description, supportingDocs)