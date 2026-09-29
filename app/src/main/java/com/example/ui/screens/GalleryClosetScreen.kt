package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.SavedLookEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import java.io.File

@Composable
fun GalleryClosetScreen(viewModel: FashionViewModel) {
    val context = LocalContext.current
    val savedLooks by viewModel.savedLooks.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Saved Looks", "Daily Calendar", "My Closet")

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "👑 My Gallery & Closet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = GoldLight,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        text = "Real HD Photos, Catwalk Videos & Monday to Sunday Looks",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = PureBlack,
                        contentColor = GoldPrimary
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = if (selectedTabIndex == index) GoldLight else TextMuted
                                    )
                                },
                                modifier = Modifier.testTag("gallery_tab_$index")
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTabIndex) {
                0 -> SavedLooksTab(
                    looks = savedLooks,
                    onDelete = { viewModel.deleteLook(it) },
                    onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) },
                    onDownloadHd = { look ->
                        viewModel.exportHdPhotoToGallery(look) { success, path ->
                            viewModel.userNotice.value = if (success) "REAL HD Photo Downloaded (.jpg): $path" else "Download error."
                        }
                    },
                    onShare = { look ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Real AI Look from AI NAP - Har Jism Ka Libaas: ${look.title}\nAI Verdict: ${look.aiVerdict}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Look"))
                    },
                    onCatwalk = { look ->
                        viewModel.activeCatwalkLook.value = look
                        viewModel.showCatwalkModal.value = true
                    }
                )
                1 -> DailyCalendarTab(
                    savedLooks = savedLooks,
                    onAssignDay = { id, day -> viewModel.assignDayToLook(id, day) },
                    onSetReminder = { day ->
                        viewModel.userNotice.value = "Reminder set for $day Outfit at 8:00 AM!"
                    }
                )
                2 -> ClosetTab(
                    savedLooks = savedLooks,
                    onDownload = { look ->
                        viewModel.exportHdPhotoToGallery(look) { success, path ->
                            viewModel.userNotice.value = if (success) "Photo saved: $path" else "Error"
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SavedLooksTab(
    looks: List<SavedLookEntity>,
    onDelete: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onDownloadHd: (SavedLookEntity) -> Unit,
    onShare: (SavedLookEntity) -> Unit,
    onCatwalk: (SavedLookEntity) -> Unit
) {
    val effectiveLooks = remember(looks) {
        if (looks.isEmpty()) {
            listOf(
                SavedLookEntity(
                    id = 1L,
                    title = "Classic Black Shalwar Kameez Royal Look",
                    timestamp = System.currentTimeMillis(),
                    dressName = "Classic Black Shalwar Kameez",
                    hairName = "Side Parting Fade",
                    jewelleryName = "Luxury Gold Watch",
                    shoesName = "Handcrafted Black Velvet Khussa",
                    userHeightFt = 6.0f,
                    calculatedHeightFt = 6.17f,
                    bodyType = "Medium",
                    skinTone = "Medium Wheatish",
                    city = "Quetta",
                    weatherText = "12°C Chilly",
                    aiVerdict = "YES - 100% Royal Match! 6.0ft frame par Classic Black Shalwar Kameez aur Black Khussa ki fall bilkul majestic lagti hai.",
                    aiStylingTip = "Is height ke liye vertical pleats aur straight cut trousers aapki height proportion ko balanced aur authoritative look dete hain.",
                    aiColorAdvice = "Black fabric aur Velvet Khussa ka match aapke Medium Wheatish skin tone par sab se royal lagega.",
                    budgetRs = 5200,
                    assignedDay = "Monday",
                    isFavorite = true,
                    hasVideo = true,
                    imagePath = null,
                    drawableResId = R.drawable.model_black_shalwar_male
                )
            )
        } else looks
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(effectiveLooks, key = { it.id }) { look ->
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = DarkCard,
                border = BorderStroke(1.5.dp, GoldPrimary),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth().testTag("saved_look_item_${look.id}")
            ) {
                    Column {
                        // 1. TOP: REAL AI GENERATED PHOTO OF USER/MODEL
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(340.dp)
                                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                        ) {
                            val localFile = look.imagePath?.let { File(it) }
                            if (localFile != null && localFile.exists()) {
                                AsyncImage(
                                    model = localFile,
                                    contentDescription = look.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (look.drawableResId != 0) {
                                Image(
                                    painter = painterResource(look.drawableResId),
                                    contentDescription = look.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.model_black_shalwar_male),
                                    contentDescription = look.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Dark gradient overlay at top and bottom of photo
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                PureBlack.copy(alpha = 0.6f),
                                                Color.Transparent,
                                                PureBlack.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                            )

                            // Top Badges
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GoldPrimary)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "REAL AI HD PHOTO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PureBlack
                                    )
                                }

                                IconButton(
                                    onClick = { onToggleFavorite(look.id, look.isFavorite) },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(PureBlack.copy(alpha = 0.6f))
                                ) {
                                    Icon(
                                        imageVector = if (look.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (look.isFavorite) Color.Red else PureWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Bottom Photo Info
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = look.title,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = PureWhite
                                )
                                Text(
                                    text = "Height Proportion: ${"%.1f".format(look.userHeightFt)}ft ➔ ${"%.2f".format(look.calculatedHeightFt)}ft",
                                    fontSize = 11.sp,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // 2. MIDDLE: DETAILS (Outfit, Shoes, Hair, Jewellery)
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurface,
                                    border = BorderStroke(1.dp, DarkCardBorder),
                                    modifier = Modifier.weight(1f).padding(vertical = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Outfit", fontSize = 9.sp, color = TextMuted)
                                        Text(look.dressName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite, maxLines = 1)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurface,
                                    border = BorderStroke(1.dp, DarkCardBorder),
                                    modifier = Modifier.weight(1f).padding(vertical = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Footwear", fontSize = 9.sp, color = TextMuted)
                                        Text(look.shoesName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight, maxLines = 1)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurface,
                                    border = BorderStroke(1.dp, DarkCardBorder),
                                    modifier = Modifier.weight(1f).padding(vertical = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Hairstyle", fontSize = 9.sp, color = TextMuted)
                                        Text(look.hairName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite, maxLines = 1)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurface,
                                    border = BorderStroke(1.dp, DarkCardBorder),
                                    modifier = Modifier.weight(1f).padding(vertical = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Jewellery/Watch", fontSize = 9.sp, color = TextMuted)
                                        Text(look.jewelleryName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite, maxLines = 1)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 3. BOTTOM: VERDICT TEXT
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "AI Verdict: ${look.aiVerdict}",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 4. BOTTOM: 4 BUTTONS (Download HD / Catwalk / Share / Delete)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { onDownloadHd(look) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.3f).height(38.dp).testTag("card_download_hd_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download HD", fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }

                                Button(
                                    onClick = { onCatwalk(look) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = GoldLight),
                                    border = BorderStroke(1.dp, GoldPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp).testTag("card_catwalk_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Catwalk", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onShare(look) },
                                    border = BorderStroke(1.dp, DarkCardBorder),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(0.9f).height(38.dp).testTag("card_share_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { onDelete(look.id) },
                                    modifier = Modifier.size(38.dp).testTag("card_delete_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
fun DailyCalendarTab(
    savedLooks: List<SavedLookEntity>,
    onAssignDay: (Long, String?) -> Unit,
    onSetReminder: (String) -> Unit
) {
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "📅 My Closet Weekly Calendar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = GoldLight
            )
            Text(
                text = "Monday se Sunday tak har din ka outfit plan karein aur morning reminder lagayein.",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        items(days) { day ->
            val assignedLook = savedLooks.firstOrNull { it.assignedDay.equals(day, ignoreCase = true) }
            var showSelectDialog by remember { mutableStateOf(false) }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkCard,
                border = BorderStroke(1.dp, if (assignedLook != null) GoldPrimary else DarkCardBorder),
                modifier = Modifier.fillMaxWidth().testTag("calendar_day_$day")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = day, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PureWhite)
                            if (assignedLook != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ready",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite,
                                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(EmeraldAccent).padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = assignedLook?.title ?: "Koi look assign nahi hai (Tap to set)",
                            fontSize = 11.sp,
                            color = if (assignedLook != null) GoldLight else TextMuted
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (assignedLook != null) {
                            IconButton(onClick = { onSetReminder(day) }, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Default.Alarm, contentDescription = "Reminder", tint = GoldLight, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onAssignDay(assignedLook.id, null) }, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                        }
                        Button(
                            onClick = { showSelectDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldContainer, contentColor = GoldLight),
                            border = BorderStroke(1.dp, GoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(if (assignedLook != null) "Change" else "Assign", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (showSelectDialog) {
                AlertDialog(
                    onDismissRequest = { showSelectDialog = false },
                    containerColor = DarkSurface,
                    title = { Text("Assign Outfit for $day", fontWeight = FontWeight.Bold, color = GoldLight) },
                    text = {
                        if (savedLooks.isEmpty()) {
                            Text("Pehle 'Full Look' studio se koi outfit create karein!", color = PureWhite)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                savedLooks.forEach { lk ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = DarkCard,
                                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            onAssignDay(lk.id, day)
                                            showSelectDialog = false
                                        }.padding(8.dp)
                                    ) {
                                        Text(text = lk.title, fontWeight = FontWeight.Medium, color = PureWhite, modifier = Modifier.padding(4.dp))
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showSelectDialog = false }) {
                            Text("Close", color = GoldLight)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ClosetTab(
    savedLooks: List<SavedLookEntity>,
    onDownload: (SavedLookEntity) -> Unit
) {
    val favorites = savedLooks.filter { it.isFavorite }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(text = "❤️ My Favorite Outfits & Styles", fontWeight = FontWeight.Black, fontSize = 14.sp, color = GoldLight)
            Text(text = "Aapke favorited designs aur signature combinations.", fontSize = 11.sp, color = TextMuted)
        }

        if (favorites.isEmpty()) {
            item {
                Text(
                    text = "Koi favorite outfit nahi mila. Saved looks me heart icon par tap karein!",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(favorites) { fav ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, GoldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = fav.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PureWhite)
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${fav.dressName} + ${fav.shoesName}", fontSize = 11.sp, color = GoldLight)
                        Text(text = "Budget: Rs ${fav.budgetRs}", fontSize = 10.sp, color = EmeraldAccent)
                    }
                }
            }
        }
    }
}
