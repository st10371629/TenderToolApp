package com.tendertool.app.models

import androidx.room.Entity

@Entity(tableName = "eTender")
data class eTender(
    override val TenderID: String,
    override val title: String,
    override val Status: String,
    override val PublishedDate: String,
    override val ClosingDate: String,
    override val DateAppended: String,
    override val Source: String,
    override val Tags: List<Tag> = emptyList(),
    override val Description: String? = null,
    override val SupportingDocs: List<SupportingDoc> = emptyList(),

    val TenderNumber: String?,
    val ProcurementMethod: String?,
    val ProcurementMethodDetails: String?,
    val ProcuringEntity: String?,
    val Currency: String?,
    val Value: Double?,
    val Category: String?,
    val Tenderer: String?,

) : BaseTender(TenderID, title, Status, PublishedDate, ClosingDate, DateAppended, Source, Tags, Description, SupportingDocs)