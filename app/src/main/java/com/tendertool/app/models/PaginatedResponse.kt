package com.tendertool.app.models

import com.tendertool.app.models.BaseTender

data class PaginatedResponse(
    // The list of tenders (up to 10 for the current page)
    val data: List<BaseTender>,

    // The total number of pages available for the entire filter set
    val totalPages: Int
)