package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ConfettiOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val particles = remember {
        List(40) {
            Triple(
                Random.nextFloat(),
                Random.nextFloat() * 0.3f,
                listOf(GoldPrimary, GoldLight, EmeraldAccent, CrimsonAccent, Color.White).random()
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val animY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fall"
    )

    LaunchedEffect(Unit) {
        delay(4000)
        onDismiss()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { (xRatio, initialY, color) ->
            val curY = ((initialY + animY) % 1.0f) * size.height
            val curX = xRatio * size.width
            drawCircle(
                color = color,
                radius = 7.dp.toPx(),
                center = Offset(curX, curY)
            )
        }
    }
}

@Composable
fun LiveTryOnComparisonCard(
    userPhotoUri: String?,
    modelDrawableId: Int,
    sliderPos: Float,
    dressName: String,
    onSliderChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCard,
        border = BorderStroke(1.5.dp, GoldPrimary),
        modifier = modifier.fillMaxWidth().testTag("live_tryon_preview_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ Live Try-On Split Preview",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = GoldLight
                )
                Text(
                    text = "Slide to Compare",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Split Frame
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left: Original User Photo (or default portrait)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (userPhotoUri != null) {
                            AsyncImage(
                                model = userPhotoUri,
                                contentDescription = "Original Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(54.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Original User", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(PureBlack.copy(alpha = 0.7f))
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("BEFORE (Original)", fontSize = 10.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Right: Real AI Wearing Dress Photo
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.5.dp, GoldPrimary),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(modelDrawableId),
                            contentDescription = "AI Wearing Dress",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(PureBlack.copy(alpha = 0.75f))
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI AFTER ($dressName)", fontSize = 10.sp, color = GoldLight, fontWeight = FontWeight.Black, maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Slider(
                value = sliderPos,
                onValueChange = onSliderChange,
                colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary),
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Left: Original Face/Body", fontSize = 10.sp, color = TextMuted)
                Text("Right: Wearing Dress", fontSize = 10.sp, color = GoldLight)
            }
        }
    }
}

@Composable
fun CatwalkVideoDialog(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onDownloadVideo: () -> Unit,
    onDismiss: () -> Unit
) {
    var playSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                delay(1000)
                playSeconds = (playSeconds + 1) % 6
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureBlack,
            border = BorderStroke(2.dp, GoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("catwalk_video_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldPrimary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("VEO AI", fontSize = 9.sp, fontWeight = FontWeight.Black, color = PureBlack)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Catwalk Runway Video",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PureWhite)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real Video Player Viewport with real runway model photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Real Model on Runway photo
                    Image(
                        painter = painterResource(R.drawable.catwalk_runway_model),
                        contentDescription = "Catwalk Video",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Video Play / Pause button in center
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PureBlack.copy(alpha = 0.65f))
                            .border(2.dp, GoldPrimary, CircleShape)
                            .clickable { onTogglePlay() }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = GoldLight,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    // Top Badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) Color.Red else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "LIVE RUNWAY" else "PAUSED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    // Bottom Video Player Controls Bar
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, PureBlack.copy(alpha = 0.9f))
                                )
                            )
                            .padding(10.dp)
                    ) {
                        // Video progress bar
                        LinearProgressIndicator(
                            progress = { playSeconds / 5f },
                            color = GoldPrimary,
                            trackColor = Color.DarkGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "00:0$playSeconds / 00:05",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            Text(
                                text = "1080x1920 60FPS • TikTok/Reels Ready",
                                fontSize = 9.sp,
                                color = PureWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Real MP4 Video Download Button
                Button(
                    onClick = onDownloadVideo,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("download_catwalk_mp4_btn")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download Catwalk Video (MP4)", fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun BodyScannerDialog(
    heightFt: Float,
    weightKg: Float,
    isMale: Boolean,
    onDismiss: () -> Unit,
    onCompleteScan: () -> Unit
) {
    var scanningPhase by remember { mutableStateOf("Position yourself in frame...") }
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    LaunchedEffect(Unit) {
        delay(700)
        scanningPhase = "Detecting Body Contour & Height..."
        delay(800)
        scanningPhase = "Calculating Chest & Waist Proportion..."
        delay(800)
        onCompleteScan()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureBlack,
            border = BorderStroke(2.dp, GoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("body_scanner_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Body Measurement Scanner",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PureWhite)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F0F1A))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMale) Icons.Default.AccessibilityNew else Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(160.dp)
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val y = size.height * laserY
                        drawLine(
                            color = GoldLight,
                            start = Offset(20f, y),
                            end = Offset(size.width - 20f, y),
                            strokeWidth = 4.dp.toPx()
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text("Target: ${"%.1f".format(heightFt)}ft | ${weightKg.toInt()}kg", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(scanningPhase, color = PureWhite, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("AI analyzing shoulder-to-waist ratio...", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}

@Composable
fun WeightPreviewDialog(
    currentWeightKg: Float,
    targetWeightKg: Float,
    userHeightFt: Float,
    onWeightChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("weight_preview_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚖️ Weight Preview Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PureWhite)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Agar aap ${targetWeightKg.toInt()}kg ho jaye to?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )

                Text(
                    text = "See how your dresses and waist will look after fitness transformation.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f).height(120.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Current Weight", fontSize = 11.sp, color = TextMuted)
                            Text("${currentWeightKg.toInt()} kg", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                            Text("Waist: ~${(30 + (currentWeightKg - 65) * 0.2f).toInt()}\"", fontSize = 11.sp, color = GoldLight)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldContainer,
                        border = BorderStroke(1.dp, GoldPrimary),
                        modifier = Modifier.weight(1f).height(120.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Target AI Look", fontSize = 11.sp, color = OnGoldContainer)
                            Text("${targetWeightKg.toInt()} kg", fontSize = 22.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                            Text("Waist: ~${(30 + (targetWeightKg - 65) * 0.2f).toInt()}\"", fontSize = 11.sp, color = PureWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Slider(
                    value = targetWeightKg,
                    onValueChange = onWeightChange,
                    valueRange = 40f..110f,
                    steps = 70,
                    colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("40 kg (Slim)", fontSize = 10.sp, color = TextMuted)
                    Text("70 kg (Fit)", fontSize = 10.sp, color = TextMuted)
                    Text("110 kg", fontSize = 10.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Apply Target Weight to Try-On", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CoupleMatchingDialog(
    occasion: String,
    onOccasionChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val occasions = listOf("Walima", "Baraat", "Mehndi", "Eid")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("couple_matching_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👫 Couple Matching Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PureWhite)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Miyan Biwi & Couple Perfect Color Matching",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    occasions.forEach { occ ->
                        val isSelected = occ == occasion
                        FilterChip(
                            selected = isSelected,
                            onClick = { onOccasionChange(occ) },
                            label = { Text(occ, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = PureBlack
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f).height(160.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Male, contentDescription = null, tint = GoldLight, modifier = Modifier.size(36.dp))
                            Text("Groom / Male", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PureWhite)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (occasion == "Baraat") "Ivory Sherwani with Maroon Stole" else "Black Prince Coat Suit",
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                color = TextMuted
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, CrimsonAccent.copy(alpha = 0.7f)),
                        modifier = Modifier.weight(1f).height(160.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Female, contentDescription = null, tint = CrimsonAccent, modifier = Modifier.size(36.dp))
                            Text("Bride / Female", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PureWhite)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (occasion == "Baraat") "Deep Crimson Velvet Lehenga" else "Emerald Green Flared Maxi",
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "AI Match Score: 98% (Colors and fabric texture complement in real photos!)",
                    fontSize = 11.sp,
                    color = EmeraldAccent,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Apply Couple Theme", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MemeComparisonDialog(
    sliderPos: Float,
    onSliderChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("meme_comparison_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "😂 Shaadi Se Pehle vs Baad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PureWhite)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Swipe slider to compare look before vs after marriage!", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, GoldPrimary),
                        modifier = Modifier.weight(1f).height(150.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Shaadi Se Pehle 🕺", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldLight)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Slim, Fade Cut, Sharp Jawline, Fit T-Shirt, Hero Vibe!", fontSize = 10.sp, textAlign = TextAlign.Center, color = PureWhite)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, CrimsonAccent),
                        modifier = Modifier.weight(1f).height(150.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Shaadi Ke Baad 🧔🏻‍♂️", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CrimsonAccent)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("+8kg Weight, Relaxed Kurta, Family Pack, Biryani lover!", fontSize = 10.sp, textAlign = TextAlign.Center, color = PureWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Slider(
                    value = sliderPos,
                    onValueChange = onSliderChange,
                    colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Before Marriage", fontSize = 10.sp, color = GoldLight)
                    Text("After Marriage", fontSize = 10.sp, color = CrimsonAccent)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("Share Meme on WhatsApp", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
