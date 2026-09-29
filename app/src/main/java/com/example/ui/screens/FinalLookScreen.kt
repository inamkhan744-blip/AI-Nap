package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.SavedLookEntity
import com.example.data.model.FashionCatalog
import com.example.data.model.FashionCategory
import com.example.data.model.FashionItem
import com.example.ui.components.ConfettiOverlay
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FashionViewModel

@Composable
fun FinalLookScreen(
    viewModel: FashionViewModel,
    onNavigateToCategory: (FashionCategory) -> Unit,
    onNavigateToNearMe: () -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val finalLookState by viewModel.finalLookState.collectAsState()

    val dress by viewModel.selectedDress.collectAsState()
    val shoes by viewModel.selectedShoes.collectAsState()
    val hair by viewModel.selectedHair.collectAsState()
    val jewellery by viewModel.selectedJewellery.collectAsState()
    val mehndi by viewModel.selectedMehndi.collectAsState()

    val height = userProfile?.heightFt ?: viewModel.formHeightFt.collectAsState().value
    val bodyType = userProfile?.bodyType ?: viewModel.formBodyType.collectAsState().value
    val skinTone = userProfile?.skinTone ?: viewModel.formSkinTone.collectAsState().value
    val city = userProfile?.city ?: viewModel.formCity.collectAsState().value
    val photoUri = userProfile?.photoUri
    val isPremium = userProfile?.isPremium ?: false

    val budgetLimit by viewModel.filterBudgetRs.collectAsState()
    val shoeBoost = shoes?.heightBoostInches ?: 0f
    val calculatedHeight = height + (shoeBoost / 12f)
    val weather = remember(city) { FashionCatalog.getWeatherForCity(city) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = PureBlack,
            topBar = {
                Surface(
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "👑 Apna Mukammal Look Banao",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = GoldLight
                            )
                            Text(
                                text = "AI Fit & Height Couture • 1080x1920 HD Proportions",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        if (isPremium) {
                            Surface(shape = RoundedCornerShape(8.dp), color = GoldPrimary) {
                                Text(
                                    text = "VIP ACTIVE",
                                    color = PureBlack,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Height Based AI Recommendation Box
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = GoldContainer,
                        border = BorderStroke(1.dp, GoldPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("height_recommendation_box")
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Straighten, contentDescription = null, tint = GoldLight, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Height Based AI Recommendation (${"%.1f".format(height)}ft):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = GoldLight
                                )
                                Text(
                                    text = "Aap ${"%.1f".format(height)}ft ho, vertical stripes aur heel aapko lamba dikhayega. ${if (skinTone.contains("Fair")) "Yellow avoid karo, Emerald & White best hai" else "Dark shades avoid karo, Cream & Mustard best hai"} for your $skinTone skin.",
                                    fontSize = 11.sp,
                                    color = PureWhite,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Weather Suggestion Box
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("weather_suggestion_box")
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Weather Suggestion ($city ${weather.tempC}°C):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EmeraldAccent
                                )
                                Text(
                                    text = weather.wardrobeAdvice,
                                    fontSize = 11.sp,
                                    color = PureWhite,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Budget Filter Slider (Rs 2,000 to Rs 20,000)
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Budget Filter:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PureWhite)
                                Text("Under Rs ${budgetLimit}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = GoldLight)
                            }
                            Slider(
                                value = budgetLimit.toFloat(),
                                onValueChange = { viewModel.filterBudgetRs.value = it.toInt() },
                                valueRange = 2000f..20000f,
                                steps = 18,
                                colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Rs 2,000", fontSize = 9.sp, color = TextSubtle)
                                Text("Rs 10,000", fontSize = 9.sp, color = TextSubtle)
                                Text("Rs 20,000", fontSize = 9.sp, color = TextSubtle)
                            }
                        }
                    }
                }

                // 4-5 Slots Summary
                item {
                    Text(
                        text = "Selected Items for Full Look:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GoldLight
                    )
                }

                item {
                    SlotItemCard(
                        title = "1. Dress / Outfit",
                        item = dress,
                        icon = Icons.Default.Checkroom,
                        onSelect = {
                            viewModel.selectedCategory.value = FashionCategory.DRESS_TRY_ON
                            onNavigateToCategory(FashionCategory.DRESS_TRY_ON)
                        }
                    )
                }

                item {
                    SlotItemCard(
                        title = "2. Shoes & Footwear",
                        item = shoes,
                        icon = Icons.Default.IceSkating,
                        boostText = if (shoeBoost > 0) "+${shoeBoost}\" height lift" else null,
                        onSelect = {
                            viewModel.selectedCategory.value = FashionCategory.SHOES
                            onNavigateToCategory(FashionCategory.SHOES)
                        }
                    )
                }

                item {
                    SlotItemCard(
                        title = "3. Hair & Beard",
                        item = hair,
                        icon = Icons.Default.Face,
                        onSelect = {
                            viewModel.selectedCategory.value = FashionCategory.HAIR_BEARD
                            onNavigateToCategory(FashionCategory.HAIR_BEARD)
                        }
                    )
                }

                item {
                    SlotItemCard(
                        title = "4. Jewellery & Watch",
                        item = jewellery,
                        icon = Icons.Default.Watch,
                        onSelect = {
                            viewModel.selectedCategory.value = FashionCategory.JEWELLERY
                            onNavigateToCategory(FashionCategory.JEWELLERY)
                        }
                    )
                }

                if (userProfile?.gender == "Female") {
                    item {
                        SlotItemCard(
                            title = "5. Mehndi Art",
                            item = mehndi,
                            icon = Icons.Default.Brush,
                            onSelect = {
                                viewModel.selectedCategory.value = FashionCategory.MEHNDI
                                onNavigateToCategory(FashionCategory.MEHNDI)
                            }
                        )
                    }
                }

                // GLOWING GOLD BUTTON - CREATE MY FINAL HD LOOK
                item {
                    Button(
                        onClick = { viewModel.createFinalLook() },
                        enabled = !finalLookState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = PureBlack
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .testTag("create_final_hd_look_button")
                    ) {
                        if (finalLookState.isLoading) {
                            CircularProgressIndicator(color = PureBlack, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(finalLookState.progressStep, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PureBlack)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CREATE MY FINAL HD LOOK",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // GENERATED HD RESULT
                finalLookState.generatedLook?.let { look ->
                    item {
                        FullLookResultCard(
                            look = look,
                            isPremium = isPremium,
                            onDownloadHd = {
                                viewModel.exportHdPhotoToGallery(look) { success, _ ->
                                    viewModel.userNotice.value = if (success) "1080x1920 HD Look saved to Gallery!" else "Error downloading."
                                }
                            },
                            onDownloadVideo = {
                                viewModel.showCatwalkModal.value = true
                            },
                            onShare = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Look created on AI NAP - Har Jism Ka Libaas!\nLook: ${look.title}\nHeight: ${"%.1f".format(look.userHeightFt)}ft ➔ ${"%.2f".format(look.calculatedHeightFt)}ft\n${look.aiVerdict}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Look"))
                            },
                            onBuyDaraz = {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.daraz.pk"))
                                context.startActivity(browserIntent)
                            },
                            onRentShop = onNavigateToNearMe
                        )
                    }
                }
            }
        }

        // Confetti burst on creation
        if (finalLookState.showConfetti) {
            ConfettiOverlay(onDismiss = { viewModel.dismissConfetti() })
        }
    }
}

@Composable
fun SlotItemCard(
    title: String,
    item: FashionItem?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    boostText: String? = null,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkCard,
        border = BorderStroke(1.dp, if (item != null) GoldPrimary.copy(alpha = 0.8f) else DarkCardBorder),
        modifier = Modifier.fillMaxWidth().clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (item != null) GoldContainer else DarkSurface)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = if (item != null) GoldLight else TextMuted, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 10.sp, color = TextMuted)
                Text(item?.name ?: "Tap to choose", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PureWhite, maxLines = 1)
                if (boostText != null) {
                    Text(boostText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldAccent)
                }
            }
            TextButton(onClick = onSelect) {
                Text(if (item != null) "Change" else "Select", fontWeight = FontWeight.Bold, color = GoldLight, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun FullLookResultCard(
    look: SavedLookEntity,
    isPremium: Boolean,
    onDownloadHd: () -> Unit,
    onDownloadVideo: () -> Unit,
    onShare: () -> Unit,
    onBuyDaraz: () -> Unit,
    onRentShop: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(2.dp, GoldPrimary),
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth().testTag("final_look_result_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldPrimary)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("✨ 1080x1920 HD LOOK GENERATED", color = PureBlack, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
                Text(
                    text = if (isPremium) "VIP (No Watermark)" else "Free (Watermarked)",
                    fontSize = 11.sp,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Realistic Preview Graphic Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkCard,
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().height(230.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text("You will look like this in real:", fontWeight = FontWeight.Bold, color = GoldLight, fontSize = 13.sp)
                        Text(
                            text = "Height Proportion: ${"%.1f".format(look.userHeightFt)}ft ➔ ${"%.2f".format(look.calculatedHeightFt)}ft with footwear lift",
                            color = PureWhite,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DarkSurface).padding(6.dp)) {
                                Text(look.dressName, fontSize = 10.sp, color = PureWhite)
                            }
                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DarkSurface).padding(6.dp)) {
                                Text(look.shoesName, fontSize = 10.sp, color = PureWhite)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DarkSurface).padding(6.dp)) {
                                Text(look.hairName, fontSize = 10.sp, color = PureWhite)
                            }
                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(DarkSurface).padding(6.dp)) {
                                Text(look.jewelleryName, fontSize = 10.sp, color = PureWhite)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Total Estimated Look Budget: Rs ${look.budgetRs}", fontSize = 11.sp, color = EmeraldAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Verdict
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("AI Verdict & Proportion Tip:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GoldLight)
                    Text(look.aiVerdict, fontSize = 11.sp, color = PureWhite, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(look.aiStylingTip, fontSize = 11.sp, color = EmeraldAccent)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5 Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. DOWNLOAD HD PHOTO
                Button(
                    onClick = onDownloadHd,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("download_hd_btn")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("DOWNLOAD HD PHOTO (1080x1920)", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // 2. DOWNLOAD VIDEO
                    Button(
                        onClick = onDownloadVideo,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard, contentColor = GoldLight),
                        border = BorderStroke(1.dp, GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("catwalk_video_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Catwalk Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // 3. SHARE ON WHATSAPP/INSTAGRAM
                    Button(
                        onClick = onShare,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent, contentColor = PureWhite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("share_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Look", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // 4. BUY THIS LOOK ON DARAZ (Affiliate)
                    Button(
                        onClick = onBuyDaraz,
                        colors = ButtonDefaults.buttonColors(containerColor = BlueDaraz, contentColor = PureWhite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("buy_daraz_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Buy on Daraz", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // 5. RENT THIS LOOK
                    Button(
                        onClick = onRentShop,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard, contentColor = PureWhite),
                        border = BorderStroke(1.dp, DarkCardBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("rent_look_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rent Near Me", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
