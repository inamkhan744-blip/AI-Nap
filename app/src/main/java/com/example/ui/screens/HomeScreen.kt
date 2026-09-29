package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FashionCatalog
import com.example.data.model.FashionCategory
import com.example.ui.components.AdMobBannerCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FashionViewModel

@Composable
fun HomeScreen(
    viewModel: FashionViewModel,
    onNavigateToCategory: (FashionCategory) -> Unit,
    onOpenEditProfile: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isWeddingMode by viewModel.isWeddingStudioActive.collectAsState()

    val height = userProfile?.heightFt ?: 5.6f
    val bodyType = userProfile?.bodyType ?: "Medium"
    val gender = userProfile?.gender ?: "Male"
    val city = userProfile?.city ?: "Quetta"
    val isPremium = userProfile?.isPremium ?: false

    val weather = remember(city) { FashionCatalog.getWeatherForCity(city) }

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            Surface(
                color = DarkSurface,
                tonalElevation = 6.dp,
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
                    // Left: User Avatar & Height & Weather
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onOpenEditProfile() }
                            .testTag("top_user_profile_pill")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(GoldPrimary, GoldLight)))
                        ) {
                            Icon(
                                imageVector = if (gender == "Female") Icons.Default.Face4 else Icons.Default.Face,
                                contentDescription = "User",
                                tint = PureBlack,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$city ${weather.tempC}°C",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• You: ${"%.1f".format(height)}ft $bodyType",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PureWhite
                                )
                            }
                            Text(
                                text = "AI NAP Studio • Tap to edit body",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Right: VIP Club & Baji Voice
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                viewModel.playBajiVoice("Assalam o Alaikum! Me aapki AI stylist Baji hoon. Aaj $city me mausam ${weather.tempC} degree hai. Chalein aapke liye best look banatay hain!")
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(UrduVoiceAccent)
                                .testTag("baji_voice_top_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Baji Voice",
                                tint = PureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { viewModel.showPremiumDialog.value = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                                .testTag("vip_top_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "VIP",
                                tint = PureBlack,
                                modifier = Modifier.size(20.dp)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Weather & City Styling Alert Card
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GoldContainer)
                        ) {
                            Icon(imageVector = Icons.Default.Cloud, contentDescription = null, tint = GoldLight)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Weather Styling Today ($city):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = GoldLight
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

            // Hero Banner (Myntra/Daraz animation & visual look)
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, GoldPrimary),
                    modifier = Modifier.fillMaxWidth().testTag("hero_fashion_banner")
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        Image(
                            painter = painterResource(
                                if (isWeddingMode) R.drawable.wedding_studio_banner else R.drawable.hero_fashion_banner
                            ),
                            contentDescription = "Fashion Studio",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, PureBlack.copy(alpha = 0.85f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "✨ HAR JISM KA LIBAAS • AI NAP",
                                color = GoldLight,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Dress according to your exact ${"%.1f".format(height)}ft height & body type",
                                color = PureWhite,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // ✨ MAIN GOLD BUTTON - CREATE MY FINAL LOOK
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Transparent,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.FINAL_LOOK) }
                        .testTag("create_final_look_banner_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GoldDark, GoldPrimary, GoldLight)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(PureBlack)
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldLight)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "CREATE MY FINAL HD LOOK",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = PureBlack
                                    )
                                    Text(
                                        text = "Dress + Hair + Shoes + Jewellery Proportions",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2C2200)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = PureBlack,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // GRID OF 8 MAIN MODULES
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "8 AI Styling Modules (ماڈیولز)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = GoldLight
                    )
                    Text(
                        text = "Viral Super App",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Render 8 Module Cards in 2-column grid
            item {
                val modules = listOf(
                    ModuleCardData(
                        id = 1,
                        title = "1. Dress Try-On",
                        urdu = "روزانہ اور شادی کے کپڑے",
                        desc = "Suit Score: 95% for ${"%.1f".format(height)}ft",
                        icon = Icons.Default.Checkroom,
                        color = GoldPrimary,
                        onClick = {
                            viewModel.selectedCategory.value = FashionCategory.DRESS_TRY_ON
                            viewModel.navigateTo(AppScreen.TRY_ON)
                        }
                    ),
                    ModuleCardData(
                        id = 2,
                        title = "2. Hair & Beard",
                        urdu = "ہئیر + داڑھی اسٹوڈیو",
                        desc = "Before/After + Baji Voice Advice",
                        icon = Icons.Default.Face,
                        color = Color(0xFF0E8A5E),
                        onClick = {
                            viewModel.selectedCategory.value = FashionCategory.HAIR_BEARD
                            viewModel.navigateTo(AppScreen.TRY_ON)
                        }
                    ),
                    ModuleCardData(
                        id = 3,
                        title = "3. Jewellery & Watch",
                        urdu = "جیولری اور لگژری گھڑی",
                        desc = "Kundan, Tops, Tikka, Rings",
                        icon = Icons.Default.Diamond,
                        color = Color(0xFFF1C40F),
                        onClick = {
                            viewModel.selectedCategory.value = FashionCategory.JEWELLERY
                            viewModel.navigateTo(AppScreen.TRY_ON)
                        }
                    ),
                    ModuleCardData(
                        id = 4,
                        title = "4. Shoes & Khussa",
                        urdu = "ہائٹ بوسٹ جوتے",
                        desc = "Heels se aap 5.8ft lagenge!",
                        icon = Icons.Default.IceSkating,
                        color = Color(0xFFE67E22),
                        onClick = {
                            viewModel.selectedCategory.value = FashionCategory.SHOES
                            viewModel.navigateTo(AppScreen.TRY_ON)
                        }
                    ),
                    ModuleCardData(
                        id = 5,
                        title = "5. Mehndi Designer",
                        urdu = "مہندی ڈیزائنر",
                        desc = "Front/Back/Feet Kashee Designs",
                        icon = Icons.Default.Brush,
                        color = CrimsonAccent,
                        onClick = {
                            viewModel.selectedCategory.value = FashionCategory.MEHNDI
                            viewModel.navigateTo(AppScreen.TRY_ON)
                        }
                    ),
                    ModuleCardData(
                        id = 6,
                        title = "6. Weight Preview",
                        urdu = "وزن کم کرنے کے بعد",
                        desc = "Agar aap 70kg ho jaye to?",
                        icon = Icons.Default.Scale,
                        color = Color(0xFF3498DB),
                        onClick = { viewModel.showWeightPreviewModal.value = true }
                    ),
                    ModuleCardData(
                        id = 7,
                        title = "7. Couple Matching",
                        urdu = "میاں بیوی میچنگ لک",
                        desc = "Walima/Baraat Perfect Palette",
                        icon = Icons.Default.Favorite,
                        color = Color(0xFFE91E63),
                        onClick = { viewModel.showCoupleModal.value = true }
                    ),
                    ModuleCardData(
                        id = 8,
                        title = "8. AI Catwalk Video",
                        urdu = "فیشن کیٹ واک ویڈیو",
                        desc = "5s Runway Walk for TikTok",
                        icon = Icons.Default.Movie,
                        color = GoldLight,
                        onClick = { viewModel.showCatwalkModal.value = true }
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in modules.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ModuleCardView(data = modules[i], modifier = Modifier.weight(1f))
                            if (i + 1 < modules.size) {
                                ModuleCardView(data = modules[i + 1], modifier = Modifier.weight(1f))
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Viral Meme Feature: Shaadi Se Pehle vs Baad
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showMemeModal.value = true }
                        .testTag("meme_comparison_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "😂", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Shaadi Se Pehle vs Shaadi Ke Baad",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = PureWhite
                                )
                                Text(
                                    text = "Swipe slider for viral look comparison meme",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // AdMob Banner
            item {
                AdMobBannerCard(
                    isPremium = isPremium,
                    onUpgradeClick = { viewModel.showPremiumDialog.value = true }
                )
            }
        }
    }
}

data class ModuleCardData(
    val id: Int,
    val title: String,
    val urdu: String,
    val desc: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun ModuleCardView(data: ModuleCardData, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkCard,
        border = BorderStroke(1.dp, DarkCardBorder),
        shadowElevation = 4.dp,
        modifier = modifier
            .height(130.dp)
            .clickable { data.onClick() }
            .testTag("module_card_${data.id}")
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(data.color.copy(alpha = 0.2f))
                    .border(1.dp, data.color.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = data.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = data.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PureWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = data.urdu,
                    fontSize = 10.sp,
                    color = GoldLight,
                    maxLines = 1
                )
                Text(
                    text = data.desc,
                    fontSize = 9.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
