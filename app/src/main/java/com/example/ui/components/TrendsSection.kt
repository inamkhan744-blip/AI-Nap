package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FashionTrendItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel

@Composable
fun TrendsSection(
    viewModel: FashionViewModel,
    onBack: (() -> Unit)? = null,
    onTrendSelectedForAdvisor: ((FashionTrendItem) -> Unit)? = null
) {
    val context = LocalContext.current
    val lang by viewModel.currentLanguage.collectAsState()
    val isUrdu = lang == "ur"
    val trendsState by viewModel.fashionTrendsState.collectAsState()

    val categories = listOf(
        "All" to if (isUrdu) "تمام ٹرینڈز" else "All Trends",
        "Bridal & Wedding" to if (isUrdu) "برائیڈل و شادی" else "Bridal & Wedding",
        "Festive Lawn" to if (isUrdu) "عید و لان" else "Festive Lawn",
        "Men's Formal" to if (isUrdu) "مردانہ فیشن" else "Men's Formal",
        "Colors & Fabrics" to if (isUrdu) "رنگ اور فیبرک" else "Colors & Fabrics"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PehnoWhite)
    ) {
        // --- Header ---
        Surface(
            color = PehnoGreenLight,
            border = BorderStroke(1.dp, PehnoGreenPrimary.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) {
                        IconButton(onClick = onBack, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PehnoBlack)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PehnoGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Trends",
                            tint = PehnoWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isUrdu) "پاکستان فیشن ٹرینڈز" else "Pakistan Fashion Trends",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoBlack
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PehnoGreenPrimary
                            ) {
                                Text(
                                    text = "Google Grounded",
                                    fontSize = 9.sp,
                                    color = PehnoWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isUrdu) "گوگل سرچ پر مبنی تازہ ترین روایات اور ڈیزائنز" else "Live updates grounded via Google Search",
                            fontSize = 11.sp,
                            color = PehnoTextSecondary
                        )
                    }
                }

                // Refresh Button
                IconButton(
                    onClick = { viewModel.fetchFashionTrends(category = trendsState.selectedCategory, forceRefresh = true) },
                    enabled = !trendsState.isLoading,
                    modifier = Modifier.size(36.dp).testTag("refresh_trends_btn")
                ) {
                    if (trendsState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = PehnoGreenPrimary)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PehnoGreenPrimary)
                    }
                }
            }
        }

        // --- Grounding Intelligence Status Banner ---
        Surface(
            color = PehnoSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = PehnoGreenPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isUrdu) "لائیو سرچ استفسارات:" else "Live Google Queries:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PehnoTextSecondary
                        )
                    }
                    if (trendsState.lastUpdated.isNotBlank()) {
                        Text(
                            text = "Updated: ${trendsState.lastUpdated}",
                            fontSize = 9.sp,
                            color = PehnoTextSecondary
                        )
                    }
                }

                if (trendsState.searchQueries.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(trendsState.searchQueries) { q ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PehnoGreenLight,
                                border = BorderStroke(0.6.dp, PehnoGreenPrimary.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "🔍 $q",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PehnoGreenDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Category Filters ---
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(categories) { (key, label) ->
                val isSelected = trendsState.selectedCategory == key
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.fetchFashionTrends(category = key, forceRefresh = false) },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PehnoGreenPrimary,
                        selectedLabelColor = PehnoWhite,
                        containerColor = PehnoSurface,
                        labelColor = PehnoBlack
                    )
                )
            }
        }

        HorizontalDivider(color = PehnoCardBorder, thickness = 0.8.dp)

        // --- Trends List ---
        if (trendsState.isLoading && trendsState.trends.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PehnoGreenPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isUrdu) "گوگل سرچ سے تازہ ترین ٹرینڈز حاصل کیے جا رہے ہیں..." else "Fetching latest Pakistani fashion trends via Google Search...",
                        fontSize = 12.sp,
                        color = PehnoTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(trendsState.trends, key = { it.id }) { trend ->
                    TrendCard(
                        trend = trend,
                        isUrdu = isUrdu,
                        onAskAdvisor = {
                            viewModel.askAdvisorAboutTrend(trend)
                            onTrendSelectedForAdvisor?.invoke(trend)
                        },
                        onOpenSource = { url ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendCard(
    trend: FashionTrendItem,
    isUrdu: Boolean,
    onAskAdvisor: () -> Unit,
    onOpenSource: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PehnoWhite,
        border = BorderStroke(1.2.dp, PehnoCardBorder),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category & Season Tag Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PehnoGreenLight
                ) {
                    Text(
                        text = trend.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoGreenDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7) // Light amber
                ) {
                    Text(
                        text = "🔥 ${trend.seasonTag}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trend Title
            Text(
                text = if (isUrdu) trend.urduTitle else trend.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = PehnoBlack
            )
            if (isUrdu) {
                Text(
                    text = trend.title,
                    fontSize = 11.sp,
                    color = PehnoTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = trend.summary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Key Elements Tags
            if (trend.keyElements.isNotEmpty()) {
                Text(
                    text = if (isUrdu) "نمایاں پہلو (Key Highlights):" else "Key Highlights:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PehnoTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(trend.keyElements) { elem ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PehnoSurface,
                            border = BorderStroke(0.6.dp, PehnoCardBorder)
                        ) {
                            Text(
                                text = "✨ $elem",
                                fontSize = 10.sp,
                                color = PehnoBlack,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Trending Colors Row
            if (trend.trendingColors.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isUrdu) "رنگ:" else "Trending Colors:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoTextSecondary
                    )
                    trend.trendingColors.forEach { colorName ->
                        val chipColor = getTrendDisplayColor(colorName)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = chipColor.copy(alpha = 0.15f),
                            border = BorderStroke(0.8.dp, chipColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(chipColor)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(colorName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PehnoBlack)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Grounding Citations / Sources
            if (trend.sources.isNotEmpty()) {
                HorizontalDivider(color = PehnoCardBorder, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🌐 Grounded Sources:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoTextSecondary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f).padding(start = 6.dp)
                    ) {
                        items(trend.sources.take(3)) { source ->
                            Row(
                                modifier = Modifier.clickable { onOpenSource(source.url) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = source.title.take(24) + "...",
                                    fontSize = 9.sp,
                                    color = PehnoGreenPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = PehnoGreenPrimary, modifier = Modifier.size(10.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Action Button: Ask Style Advisor about this trend!
            Button(
                onClick = onAskAdvisor,
                colors = ButtonDefaults.buttonColors(containerColor = PehnoGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isUrdu) "اس ٹرینڈ پر اپنا ناپ و مشورہ لیں" else "Style This Trend For My Body Type",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getTrendDisplayColor(colorName: String): Color {
    val lower = colorName.lowercase()
    return when {
        lower.contains("pink") -> Color(0xFFF472B6)
        lower.contains("mint") || lower.contains("sage") -> Color(0xFF34D399)
        lower.contains("blue") -> Color(0xFF60A5FA)
        lower.contains("black") -> Color(0xFF1F2937)
        lower.contains("navy") -> Color(0xFF1E3A8A)
        lower.contains("gold") || lower.contains("yellow") -> Color(0xFFD97706)
        lower.contains("maroon") || lower.contains("crimson") -> Color(0xFF991B1B)
        lower.contains("emerald") || lower.contains("green") -> Color(0xFF047857)
        lower.contains("ivory") || lower.contains("white") -> Color(0xFF6B7280)
        lower.contains("grey") || lower.contains("gray") -> Color(0xFF4B5563)
        lower.contains("purple") || lower.contains("plum") || lower.contains("lilac") -> Color(0xFF8B5CF6)
        else -> PehnoGreenPrimary
    }
}
