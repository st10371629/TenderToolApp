package com.tendertool.app.models

import androidx.room.Entity

@Entity(tableName = "EskomTender")
data class EskomTender
(
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
    val Reference: String?,
    val Audience: String?,
    val OfficeLocation: String?,
    val Email: String?,
    val Address: String?,
    val Province: String?,

) : BaseTender(TenderID, title, Status, PublishedDate, ClosingDate, DateAppended, Source, Tags, Description, SupportingDocs)
