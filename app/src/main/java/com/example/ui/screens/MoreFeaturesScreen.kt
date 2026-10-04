package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import com.example.data.model.FashionCatalog
import com.example.data.model.FashionItem
import com.example.data.model.PlacedOrder
import com.example.data.model.TailorMeasurements
import com.example.ui.components.StyleAdvisorChatContent
import com.example.ui.components.TrendsSection
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import com.example.util.TailorMeasurementCalculator

@Composable
fun MoreFeaturesScreen(
    viewModel: FashionViewModel,
    onNavigateToTryOn: () -> Unit = {}
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val isUrdu = lang == "ur"
    val wishlist by viewModel.wishlistItems.collectAsState()
    val placedOrders by viewModel.placedOrders.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var selectedSection by remember { mutableStateOf("home") }
    var pushNotifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    when (selectedSection) {
        "style_chat" -> StyleAdvisorChatContent(
            viewModel = viewModel,
            onClose = { selectedSection = "home" },
            onSelectDressToTryOn = { item ->
                viewModel.selectedDress.value = item
                onNavigateToTryOn()
            }
        )
        "trends" -> TrendsSection(
            viewModel = viewModel,
            onBack = { selectedSection = "home" },
            onTrendSelectedForAdvisor = {
                selectedSection = "style_chat"
            }
        )
        "darzi" -> SmartDarziSection(
            viewModel = viewModel,
            lang = lang,
            onBack = { selectedSection = "home" }
        )
        "advisor" -> WeatherFabricAdvisorSection(
            viewModel = viewModel,
            lang = lang,
            onBack = { selectedSection = "home" },
            onExploreDress = {
                onNavigateToTryOn()
            }
        )
        "orders" -> OrdersHistorySection(
            orders = placedOrders,
            lang = lang,
            onBack = { selectedSection = "home" }
        )
        "settings" -> SettingsSection(
            lang = lang,
            pushNotifications = pushNotifications,
            darkMode = darkMode,
            onBack = { selectedSection = "home" },
            onTogglePush = { pushNotifications = it },
            onToggleDark = { darkMode = it },
            onLanguage = { viewModel.toggleLanguage() },
            onProfile = { viewModel.showProfileEdit.value = true }
        )
        else -> {
            val catalog = FashionCatalog.searchMultiStoreItems("", viewModel.formGender.value)
            MoreHome(
                lang = lang,
                ordersCount = placedOrders.size,
                onStyleAdvisor = { selectedSection = "style_chat" },
                onTrends = { selectedSection = "trends" },
                onVirtualTryOn = { viewModel.openVirtualTryOn() },
                onDarzi = { selectedSection = "darzi" },
                onAdvisor = { selectedSection = "advisor" },
                onOrders = { selectedSection = "orders" },
                onSettings = { selectedSection = "settings" },
                onAddRecommended = { item -> viewModel.addToWishlist(item) },
                onTryDress = { item ->
                    viewModel.selectedDress.value = item
                    onNavigateToTryOn()
                },
                recommended = catalog.take(4)
            )
        }
    }
}

@Composable
private fun MoreHome(
    lang: String,
    ordersCount: Int,
    onStyleAdvisor: () -> Unit,
    onTrends: () -> Unit,
    onVirtualTryOn: () -> Unit,
    onDarzi: () -> Unit,
    onAdvisor: () -> Unit,
    onOrders: () -> Unit,
    onSettings: () -> Unit,
    onAddRecommended: (FashionItem) -> Unit,
    onTryDress: (FashionItem) -> Unit,
    recommended: List<FashionItem>
) {
    val isUrdu = lang == "ur"
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PehnoWhite),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (isUrdu) "مزید سہولیات اور اسٹوڈیو" else "More Features & Studio",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = PehnoBlack
            )
            Text(
                text = if (isUrdu) "درزی کا ناپ، فیبرک مشورہ، اور آرڈر ہسٹری" else "Smart Tailor Naap, Weather Advisor & Order Tracking",
                fontSize = 12.sp,
                color = PehnoTextSecondary
            )
        }

        // Featured Hero Banner: Gemini AI Style Advisor
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStyleAdvisor() },
                shape = RoundedCornerShape(20.dp),
                color = PehnoGreenLight,
                border = BorderStroke(1.5.dp, PehnoGreenPrimary),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(PehnoGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI Style Advisor",
                            tint = PehnoWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isUrdu) "اے آئی اسٹائل ایڈوائزر" else "AI Style Advisor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoBlack
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PehnoGreenPrimary
                            ) {
                                Text(
                                    text = "Gemini AI",
                                    fontSize = 9.sp,
                                    color = PehnoWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isUrdu) "شادی، عید اور تقریب کے لیے قد اور وزن کے مطابق فیشن مشورہ" else "Personalized fashion advice for occasions & your exact body type",
                            fontSize = 11.sp,
                            color = PehnoTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = PehnoGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 4 Primary Feature Tiles
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureTile(
                        icon = Icons.Default.DesignServices,
                        title = if (isUrdu) "درزی کا ناپ" else "Smart Darzi",
                        subtitle = if (isUrdu) "مکمل ناپ سلپ (انچ)" else "Tailor Size Slip",
                        onClick = onDarzi,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureTile(
                        icon = Icons.Default.WbSunny,
                        title = if (isUrdu) "موسم اور رنگ" else "Weather & Fabric",
                        subtitle = if (isUrdu) "شہر کا موسم اور کپڑا" else "Fabric & Color Match",
                        onClick = onAdvisor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureTile(
                        icon = Icons.Default.TrendingUp,
                        title = if (isUrdu) "گوگل ٹرینڈز" else "Fashion Trends",
                        subtitle = if (isUrdu) "لائیو پاکستانی روایات" else "Grounded 2026",
                        onClick = onTrends,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureTile(
                        icon = Icons.Default.Camera,
                        title = if (isUrdu) "کیمرہ ٹرائی آن" else "Virtual Try-On",
                        subtitle = if (isUrdu) "لائیو کیمرہ فٹنگ" else "CameraX Fitting",
                        onClick = onVirtualTryOn,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureTile(
                        icon = Icons.Default.LocalShipping,
                        title = if (isUrdu) "میرے آرڈرز" else "My Orders",
                        subtitle = "$ordersCount ${if (isUrdu) "آرڈرز" else "Orders"}",
                        onClick = onOrders,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureTile(
                        icon = Icons.Default.Settings,
                        title = if (isUrdu) "سیٹنگز" else "Settings",
                        subtitle = if (isUrdu) "ترجیحات اور زبان" else "Preferences",
                        onClick = onSettings,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recommended items
        item {
            Text(
                text = if (isUrdu) "آپ کے لیے منتخب شدہ جوڑے" else "Handpicked Suits for You",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PehnoBlack
            )
        }

        items(recommended, key = { it.id }) { item ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PehnoCardBorder),
                color = PehnoSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PehnoBlack)
                        Text("Rs. ${item.priceRs} • ${item.storeSource}", fontSize = 11.sp, color = PehnoGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { onTryDress(item) }) {
                        Text(if (isUrdu) "پہنو" else "Try On", fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { onAddRecommended(item) }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Add to wishlist", tint = PehnoGreenPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = PehnoGreenLight,
        border = BorderStroke(1.2.dp, PehnoGreenPrimary)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = PehnoGreenPrimary, modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Bold, color = PehnoBlack, fontSize = 13.sp)
            Text(subtitle, fontSize = 11.sp, color = PehnoTextSecondary)
        }
    }
}

@Composable
private fun SmartDarziSection(
    viewModel: FashionViewModel,
    lang: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isUrdu = lang == "ur"
    val profile by viewModel.userProfile.collectAsState()
    val heightFt = profile?.heightFt ?: viewModel.formHeightFt.value
    val weightKg = profile?.weightKg ?: viewModel.formWeightKg.value
    val gender = profile?.gender ?: viewModel.formGender.value

    val measurements = remember(heightFt, weightKg, gender) {
        TailorMeasurementCalculator.calculate(heightFt, weightKg, gender)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PehnoWhite)
    ) {
        SectionHeader(if (isUrdu) "درزی کا ناپ سلپ (Smart Darzi)" else "Smart Tailor Naap Slip", onBack)

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PehnoGreenLight,
                    border = BorderStroke(1.dp, PehnoGreenPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isUrdu) "تجویز کردہ سائز (Standard Size)" else "Recommended Size",
                                fontSize = 12.sp,
                                color = PehnoTextSecondary
                            )
                            Text(
                                text = measurements.standardSize,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoGreenDark
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PehnoGreenPrimary
                        ) {
                            Text(
                                text = "$heightFt ft • ${weightKg.toInt()} kg",
                                color = PehnoWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Measurement rows
            item {
                Text(
                    text = if (isUrdu) "سلائی کی پیمائش (انچ میں)" else "Tailoring Measurements (Inches)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = PehnoBlack
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PehnoSurface),
                    border = BorderStroke(1.dp, PehnoCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NaapRow(if (isUrdu) "قمیض / کُرتی لمبائی" else "Kameez Length", "${measurements.kameezLengthInches}\"")
                        HorizontalDivider(color = PehnoCardBorder)
                        NaapRow(if (isUrdu) "تیرہ / شولڈر" else "Shoulder (Teera)", "${measurements.shoulderInches}\"")
                        HorizontalDivider(color = PehnoCardBorder)
                        NaapRow(if (isUrdu) "چھاتی / چیسٹ" else "Chest (Seena)", "${measurements.chestInches}\"")
                        HorizontalDivider(color = PehnoCardBorder)
                        NaapRow(if (isUrdu) "کمر" else "Waist (Kamar)", "${measurements.waistInches}\"")
                        HorizontalDivider(color = PehnoCardBorder)
                        NaapRow(if (isUrdu) "دامن گھیرا" else "Daman Ghera", "${measurements.damanInches}\"")
                        HorizontalDivider(color = PehnoCardBorder)
                        NaapRow(if (isUrdu) "آستین لمبائی" else "Sleeve Length", "${measurements.sleeveLengthInches}\"")
                        if (measurements.collarInches > 0) {
                            HorizontalDivider(color = PehnoCardBorder)
                            NaapRow(if (isUrdu) "بین / کالر" else "Collar / Ban", "${measurements.collarInches}\"")
                        }
                        HorizontalDivider(color = PehnoCardBorder)
                        NaapRow(if (isUrdu) "شلوار / ٹراؤزر لمبائی" else "Trouser / Shalwar Length", "${measurements.trouserLengthInches}\"")
                        HorizontalDivider(color = PehnoCardBorder)
                        NaapRow(if (isUrdu) "پانچہ" else "Pauncha", "${measurements.paunchaInches}\"")
                    }
                }
            }

            // WhatsApp Button
            item {
                Button(
                    onClick = {
                        val slipText = TailorMeasurementCalculator.generateDarziSlipUrdu(
                            customerName = profile?.name ?: "Customer",
                            m = measurements
                        )
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, slipText)
                            type = "text/plain"
                        }
                        try {
                            context.startActivity(Intent.createChooser(sendIntent, "Share Darzi Naap Slip"))
                        } catch (_: Exception) {
                            Toast.makeText(context, "Could not share slip", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PehnoGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "درزی کو واٹس ایپ پر ناپ بھیجیں" else "Send Naap Slip to Tailor via WhatsApp",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun NaapRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp, color = PehnoBlack)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = PehnoGreenPrimary)
    }
}

@Composable
private fun WeatherFabricAdvisorSection(
    viewModel: FashionViewModel,
    lang: String,
    onBack: () -> Unit,
    onExploreDress: () -> Unit
) {
    val isUrdu = lang == "ur"
    val profile by viewModel.userProfile.collectAsState()
    val city = profile?.city ?: "Lahore"
    val skinTone = profile?.skinTone ?: "Wheatish"
    val weather = FashionCatalog.getWeatherForCity(city)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PehnoWhite)
    ) {
        SectionHeader(if (isUrdu) "موسم اور رنگ مشورہ" else "Fabric & Color Advisor", onBack)

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Weather Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PehnoGreenLight,
                    border = BorderStroke(1.dp, PehnoGreenPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PehnoGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Thermostat, contentDescription = null, tint = PehnoWhite)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${weather.city} • ${weather.tempC}°C",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoBlack
                            )
                            Text(
                                text = weather.condition,
                                fontSize = 12.sp,
                                color = PehnoGreenDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Fabric Advice
            item {
                Text(
                    text = if (isUrdu) "فیبرک کا مشورہ (Fabric Recommendation)" else "Recommended Fabric",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = PehnoBlack
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PehnoSurface),
                    border = BorderStroke(1.dp, PehnoCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = weather.wardrobeAdvice,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = PehnoBlack
                        )
                    }
                }
            }

            // Skin Tone Palette
            item {
                Text(
                    text = if (isUrdu) "آپ کی رنگت کے مطابق بہترین رنگ" else "Best Color Contrasts ($skinTone Skin)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = PehnoBlack
                )
            }

            item {
                val recommendedColors = when (skinTone) {
                    "Fair" -> listOf("Royal Emerald Green", "Ruby Maroon", "Navy Blue", "Deep Wine")
                    "Wheatish" -> listOf("Monochrome Jet Black", "Mustard Gold", "Teal Blue", "Olive Green")
                    else -> listOf("Ivory Cream", "Pastel Pink", "Sky Blue", "Rich Gold")
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PehnoSurface,
                    border = BorderStroke(1.dp, PehnoCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recommendedColors.forEach { colName ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(PehnoGreenPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(colName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PehnoBlack)
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onExploreDress,
                    colors = ButtonDefaults.buttonColors(containerColor = PehnoGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text(if (isUrdu) "اس مشورے پر جوڑا آزمائیں" else "Try Suits Matching this Advice", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OrdersHistorySection(
    orders: List<PlacedOrder>,
    lang: String,
    onBack: () -> Unit
) {
    val isUrdu = lang == "ur"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PehnoWhite)
    ) {
        SectionHeader(if (isUrdu) "میرے آرڈرز (Order History)" else "My Orders", onBack)

        if (orders.isEmpty()) {
            EmptySection(
                Icons.Default.LocalShipping,
                if (isUrdu) "ابھی تک کوئی آرڈر نہیں دیا گیا" else "No orders placed yet"
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, PehnoCardBorder),
                        color = PehnoSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.id,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PehnoTextSecondary
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PehnoGreenLight
                                ) {
                                    Text(
                                        text = order.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PehnoGreenDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = order.dressName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoBlack
                            )
                            Text(
                                text = "Rs. ${order.priceRs} (COD) • ${order.deliveryCity}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PehnoGreenPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = order.orderDate,
                                fontSize = 10.sp,
                                color = PehnoTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    lang: String,
    pushNotifications: Boolean,
    darkMode: Boolean,
    onBack: () -> Unit,
    onTogglePush: (Boolean) -> Unit,
    onToggleDark: (Boolean) -> Unit,
    onLanguage: () -> Unit,
    onProfile: () -> Unit
) {
    val context = LocalContext.current
    val isUrdu = lang == "ur"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PehnoWhite)
    ) {
        SectionHeader(if (isUrdu) "سیٹنگز" else "Settings", onBack)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SettingRow(
                    Icons.Default.Person,
                    if (isUrdu) "پروفائل تبدیل کریں" else "Edit Profile",
                    if (isUrdu) "قد، وزن اور تصویر" else "Height, weight and photo",
                    onProfile
                )
            }
            item {
                SettingRow(
                    Icons.Default.Language,
                    if (isUrdu) "زبان" else "Language",
                    if (isUrdu) "اردو / English" else "Urdu / English",
                    onLanguage
                )
            }
            item {
                SwitchRow(
                    Icons.Default.Notifications,
                    if (isUrdu) "پش اطلاعات" else "Push Notifications",
                    pushNotifications,
                    onTogglePush
                )
            }
            item {
                SwitchRow(
                    Icons.Default.DarkMode,
                    if (isUrdu) "ڈارک موڈ" else "Dark Mode",
                    darkMode,
                    onToggleDark
                )
            }
            item {
                SettingRow(
                    Icons.Default.SupportAgent,
                    if (isUrdu) "کسٹمر سپورٹ (واٹس ایپ)" else "WhatsApp Support",
                    "+92 300 1234567",
                    {
                        val waUri = Uri.parse("https://api.whatsapp.com/send?phone=923001234567&text=Assalam-o-Alaikum%20Pehno%20Support")
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, waUri))
                        } catch (_: Exception) {}
                    }
                )
            }
            item {
                SettingRow(
                    Icons.AutoMirrored.Filled.HelpOutline,
                    if (isUrdu) "مدد اور سپورٹ" else "Help & About",
                    "Pehno v1.0 • All Pakistan",
                    {}
                )
            }
            item {
                SettingRow(
                    Icons.Default.PrivacyTip,
                    if (isUrdu) "رازداری" else "Privacy",
                    if (isUrdu) "آپ کا ڈیٹا ڈیوائس پر محفوظ ہے" else "Your profile is stored on this device",
                    {}
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = PehnoBlack)
    }
}

@Composable
private fun EmptySection(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, Modifier.size(64.dp), tint = PehnoCardBorder)
        Spacer(Modifier.height(12.dp))
        Text(text, color = PehnoTextSecondary)
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = PehnoSurface
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = PehnoGreenPrimary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = PehnoBlack)
                Text(subtitle, fontSize = 11.sp, color = PehnoTextSecondary)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = PehnoTextSecondary)
        }
    }
}

@Composable
private fun SwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = PehnoSurface
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = PehnoGreenPrimary)
            Spacer(Modifier.width(12.dp))
            Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold, color = PehnoBlack)
            Switch(checked, onCheckedChange)
        }
    }
}
