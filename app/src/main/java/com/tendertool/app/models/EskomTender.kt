package com.tendertool.app.models

import androidx.room.Entity

@Entity(tableName = "EskomTender")
data class EskomTender
(
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
    val reference: String?,
    val audience: String?,
    val officeLocation: String?,
    val email: String?,
    val address: String?,
    val province: String?,

) : BaseTender(tenderID, title, status, publishedDate, closingDate, dateAppended, source, tags, description, supportingDocs)
