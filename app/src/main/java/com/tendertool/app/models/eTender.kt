package com.tendertool.app.models

data class eTender(
    var tenderID_val: String,
    var title_val: String,
    var status_val: String,
    var publishedDate_val: String,
    var closingDate_val: String,
    var dateAppended_val: String,
    var source_val: String,
    var tags_val: List<Tag> = emptyList(),
    var description_val: String? = null,
    var supportingDocs_val: List<SupportingDoc> = emptyList(),

    val tenderNumber: String?,
    val procurementMethod: String?,
    val procurementMethodDetails: String?,
    val procuringEntity: String?,
    val currency: String?,
    val value: Double?,
    val category: String?,
    val tenderer: String?,

) : BaseTender(tenderID_val, title_val, status_val, publishedDate_val, closingDate_val,
    dateAppended_val, source_val, description_val, tags_val, supportingDocs_val)