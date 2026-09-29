package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FashionCatalog
import com.example.data.model.FashionCategory
import com.example.data.model.FashionItem
import com.example.ui.components.HeightBoostCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel

@Composable
fun TryOnScreen(viewModel: FashionViewModel) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val gender = userProfile?.gender ?: viewModel.formGender.collectAsState().value
    val height = userProfile?.heightFt ?: viewModel.formHeightFt.collectAsState().value
    val bodyType = userProfile?.bodyType ?: viewModel.formBodyType.collectAsState().value

    val items = remember(selectedCategory, gender) {
        FashionCatalog.getItemsForCategory(selectedCategory, gender)
    }

    val selectedShoes by viewModel.selectedShoes.collectAsState()

    // Mehndi specific state
    var mehndiPlacement by remember { mutableStateOf("Front Hand") }
    var mehndiPrompt by remember { mutableStateOf("peacock bridal kashee") }

    // Hair Before/After Slider state
    var hairSliderPos by remember { mutableStateOf(0.5f) }

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                    Text(
                        text = "AI NAP Virtual Try-On",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = GoldLight,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        text = "Real-time AI dressing for ${"%.1f".format(height)}ft | $bodyType body",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Category Tabs
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filterCats = listOf(
                            FashionCategory.DRESS_TRY_ON,
                            FashionCategory.HAIR_BEARD,
                            FashionCategory.JEWELLERY,
                            FashionCategory.SHOES,
                            FashionCategory.MEHNDI
                        )
                        items(filterCats) { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectedCategory.value = cat },
                                label = {
                                    Text(
                                        cat.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = PureBlack
                                ),
                                modifier = Modifier.testTag("tryon_cat_${cat.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Category 4: Shoes Height Boost Visualizer
            if (selectedCategory == FashionCategory.SHOES) {
                item {
                    val activeShoe = selectedShoes ?: items.firstOrNull()
                    HeightBoostCard(
                        baseHeightFt = height,
                        shoesName = activeShoe?.name ?: "Footwear",
                        boostInches = activeShoe?.heightBoostInches ?: 2.0f
                    )
                }
            }

            // Category 2: Hair & Beard Before/After Slider + Baji Voice Suggestion
            if (selectedCategory == FashionCategory.HAIR_BEARD) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("hair_before_after_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💇 Hair Before vs After Transformation",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GoldLight
                                )
                                IconButton(
                                    onClick = {
                                        viewModel.playBajiVoice("Aapki height ${"%.1f".format(height)}ft hai. Short fade ya high bun aapke head profile ko elevate karega aur aapko 2 inch lamba dikhayega!")
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(UrduVoiceAccent)
                                ) {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Voice", tint = PureWhite, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "“Aapki height kam hai, ye hair style aapko lamba dikhayega.”",
                                fontSize = 12.sp,
                                color = EmeraldAccent,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Interactive Before/After Split preview
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkSurface,
                                    modifier = Modifier.weight(1f).height(100.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("BEFORE 😶", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                        Text("Flat unstyled hair", fontSize = 10.sp, color = TextSubtle)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GoldContainer,
                                    border = BorderStroke(1.dp, GoldPrimary),
                                    modifier = Modifier.weight(1f).height(100.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("AI AFTER ✨", fontSize = 11.sp, fontWeight = FontWeight.Black, color = GoldLight)
                                        Text("+1.5\" Height Illusion", fontSize = 10.sp, color = PureWhite)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Slider(
                                value = hairSliderPos,
                                onValueChange = { hairSliderPos = it },
                                colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Category 5: Mehndi Designer prompt & 4 designs generator
            if (selectedCategory == FashionCategory.MEHNDI) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, CrimsonAccent.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🎨 AI Mehndi Designer",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Hand selection: Front / Back / Feet
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Front Hand", "Back Hand", "Feet").forEach { p ->
                                    val isSel = mehndiPlacement == p
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { mehndiPlacement = p },
                                        label = { Text(p, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CrimsonAccent,
                                            selectedLabelColor = PureWhite
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = mehndiPrompt,
                                onValueChange = { mehndiPrompt = it },
                                label = { Text("Prompt (e.g. peacock bridal)", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = DarkCardBorder,
                                    focusedLabelColor = GoldLight,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = PureWhite,
                                    unfocusedTextColor = PureWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // 4 generated designs preview
                            Text("Generated 4 Designs:", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Design 1", "Design 2", "Design 3", "Design 4").forEach { d ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = DarkSurface,
                                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                                        modifier = Modifier.weight(1f).height(65.dp).clickable {
                                            viewModel.userNotice.value = "$d ($mehndiPrompt) Downloaded!"
                                        }
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(imageVector = Icons.Default.Brush, contentDescription = null, tint = GoldLight, modifier = Modifier.size(18.dp))
                                            Text(d, fontSize = 9.sp, color = PureWhite)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Item Cards for Current Category
            items(items) { item ->
                NapCatalogItemCard(
                    item = item,
                    userHeight = height,
                    onCheckSuitability = { viewModel.checkItemSuitability(item) },
                    onSelectForFinalLook = { viewModel.selectForFinalLook(item) }
                )
            }
        }
    }
}

@Composable
fun NapCatalogItemCard(
    item: FashionItem,
    userHeight: Float,
    onCheckSuitability: () -> Unit,
    onSelectForFinalLook: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkCard,
        border = BorderStroke(1.dp, DarkCardBorder),
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("nap_item_${item.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = item.urduName,
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Suitability Match Badge (Suit Score: 95%)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldAccent)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Suit: ${item.suitabilityScore}%",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Height tip box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.heightTip,
                        fontSize = 11.sp,
                        color = PureWhite,
                        lineHeight = 15.sp
                    )
                }
            }

            // Height lift indicator
            if (item.heightBoostInches > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${item.heightBoostInches}\" lift! Heels se aap ${"%.2f".format(userHeight + (item.heightBoostInches / 12f))}ft lagenge",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price & Rent tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Buy: Rs ${item.priceRs} • Rent: Rs ${item.rentPerDayRs}/day",
                    fontSize = 11.sp,
                    color = GoldLight,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.tag,
                    fontSize = 10.sp,
                    color = TextSubtle
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCheckSuitability,
                    border = BorderStroke(1.dp, GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f).height(40.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Will it suit ${"%.1f".format(userHeight)}ft?", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                }

                Button(
                    onClick = onSelectForFinalLook,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Select Look", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
