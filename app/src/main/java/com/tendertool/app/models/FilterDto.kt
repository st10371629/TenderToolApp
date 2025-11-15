package com.tendertool.app.models

data class FilterDto(

    val sort: String? = "Descending",

    // CLIENT FILTER FIELDS (Sent but ignored by the server):
    val search: String? = null,
    val tags: List<String>? = emptyList(),
    val dateFilter: String? = null,
    val tagFilter: List<String>? = emptyList(),
    val statusFilter: String? = null,
    val alphaSort: String? = null,
    val sources: List<String>? = emptyList()
)