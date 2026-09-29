package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import kotlin.random.Random

@Composable
fun ConfettiOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val particles = remember {
        List(40) {
            Triple(
                Random.nextFloat(), // x position 0..1
                Random.nextFloat() * 0.3f, // initial y
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
        kotlinx.coroutines.delay(4000)
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
        kotlinx.coroutines.delay(700)
        scanningPhase = "Detecting Body Contour & Height..."
        kotlinx.coroutines.delay(800)
        scanningPhase = "Calculating Chest & Waist Proportion..."
        kotlinx.coroutines.delay(800)
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

                // Camera viewfinder box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F0F1A))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Silhouette
                    Icon(
                        imageVector = if (isMale) Icons.Default.AccessibilityNew else Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(160.dp)
                    )

                    // Laser Line Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val y = size.height * laserY
                        drawLine(
                            color = GoldLight,
                            start = Offset(20f, y),
                            end = Offset(size.width - 20f, y),
                            strokeWidth = 4.dp.toPx()
                        )
                    }

                    // Metrics overlay
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

                // Comparison Box
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

                // Weight Slider
                Slider(
                    value = targetWeightKg,
                    onValueChange = onWeightChange,
                    valueRange = 40f..110f,
                    steps = 70,
                    colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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

                // Occasion selector
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

                // Side by side preview cards
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
fun CatwalkVideoDialog(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onDownloadVideo: () -> Unit,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "catwalk")
    val walkStep by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "step"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureBlack,
            border = BorderStroke(2.dp, GoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("catwalk_video_dialog")
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
                        text = "🎬 My AI Catwalk Video (5s)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PureWhite)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Video Screen simulation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1F1F2E), Color(0xFF0A0A12))
                            )
                        )
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Fashion Ramp Floor
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2
                        val bottomY = size.height
                        // Spotlight beams
                        drawLine(
                            color = GoldLight.copy(alpha = 0.15f),
                            start = Offset(0f, 0f),
                            end = Offset(centerX, bottomY),
                            strokeWidth = 24.dp.toPx()
                        )
                        drawLine(
                            color = GoldLight.copy(alpha = 0.15f),
                            start = Offset(size.width, 0f),
                            end = Offset(centerX, bottomY),
                            strokeWidth = 24.dp.toPx()
                        )
                    }

                    // Model walking representation
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsWalk,
                                contentDescription = null,
                                tint = PureBlack,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ramp Walk in Progress (00:0${(walkStep * 5).toInt()})",
                            color = PureWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Music: Lahore Fashion Week Beat",
                            color = GoldLight,
                            fontSize = 10.sp
                        )
                    }

                    // Watermark tag
                    Text(
                        text = "AI NAP Runway • HD",
                        fontSize = 10.sp,
                        color = GoldPrimary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDownloadVideo,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download Video for TikTok & Reels", fontWeight = FontWeight.Bold)
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

                // Visual split view
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
