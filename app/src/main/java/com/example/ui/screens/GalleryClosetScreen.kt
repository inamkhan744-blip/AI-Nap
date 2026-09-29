package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedLookEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel

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
                        text = "Saved HD photos, Catwalk videos, and weekly Monday-Sunday planner",
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
                        viewModel.exportHdPhotoToGallery(look) { success, _ ->
                            viewModel.userNotice.value = if (success) "1080x1920 HD Photo Downloaded!" else "Download error."
                        }
                    },
                    onShare = { look ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Look created on AI NAP - Har Jism Ka Libaas: ${look.title}\nAI Verdict: ${look.aiVerdict}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Look"))
                    },
                    onCatwalk = {
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
                        viewModel.exportHdPhotoToGallery(look) { success, _ ->
                            viewModel.userNotice.value = if (success) "Photo saved!" else "Error"
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
    if (looks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Icon(imageVector = Icons.Default.CollectionsBookmark, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Abhi koi Look save nahi hua", fontWeight = FontWeight.Bold, color = PureWhite, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Home par ja kar 'CREATE MY FINAL HD LOOK' par click karein aur apna mukammal look save karein!",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(looks, key = { it.id }) { look ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth().testTag("saved_look_item_${look.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = look.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PureWhite)
                                Text(
                                    text = "Height: ${"%.1f".format(look.userHeightFt)}ft ➔ ${"%.2f".format(look.calculatedHeightFt)}ft with footwear",
                                    fontSize = 11.sp,
                                    color = GoldLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            IconButton(onClick = { onToggleFavorite(look.id, look.isFavorite) }) {
                                Icon(
                                    imageVector = if (look.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (look.isFavorite) Color.Red else TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• Outfit: ${look.dressName}\n• Shoes: ${look.shoesName}\n• Hair: ${look.hairName}\n• Jewellery: ${look.jewelleryName}",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Verdict: ${look.aiVerdict}",
                                fontSize = 10.sp,
                                color = PureWhite,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { onDownloadHd(look) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.2f).height(38.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download HD", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onCatwalk(look) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = GoldLight),
                                border = BorderStroke(1.dp, GoldPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Catwalk", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onShare(look) },
                                border = BorderStroke(1.dp, DarkCardBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(0.9f).height(38.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = { onDelete(look.id) },
                                modifier = Modifier.size(38.dp)
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
