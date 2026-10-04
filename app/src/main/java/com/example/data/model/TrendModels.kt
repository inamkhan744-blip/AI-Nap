package com.example.data.model

import java.util.UUID

data class GroundingSource(
    val title: String,
    val url: String
)

data class FashionTrendItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val urduTitle: String,
    val category: String, // e.g. "Bridal & Wedding", "Lawn & Casual", "Men's Formal", "Color & Fabrics"
    val summary: String,
    val keyElements: List<String>,
    val trendingColors: List<String>,
    val seasonTag: String = "2026 Trend",
    val sources: List<GroundingSource> = emptyList()
)

data class TrendFetchResult(
    val trends: List<FashionTrendItem>,
    val searchQueries: List<String> = emptyList(),
    val isGrounded: Boolean = true
)

data class TrendsFeedState(
    val isLoading: Boolean = false,
    val trends: List<FashionTrendItem> = emptyList(),
    val searchQueries: List<String> = emptyList(),
    val selectedCategory: String = "All",
    val lastUpdated: String = "",
    val error: String? = null
)
