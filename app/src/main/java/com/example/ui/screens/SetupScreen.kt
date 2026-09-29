package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.BodyScannerDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FashionViewModel

@Composable
fun OnboardingCrownScreen(onStartClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PureBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Crown Logo with glowing gold aura
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(GoldDark, GoldPrimary, GoldLight))
                        )
                        .padding(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(PureBlack)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond, // Crown sparkle
                            contentDescription = "Crown",
                            tint = GoldPrimary,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "AI NAP",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldLight,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "HAR JISM KA LIBAAS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "“Kapda Kharidne Se Pehle,\nAI Me Pehen Ke Dekho”",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PureWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Pakistan's #1 Virtual Fitting, Height Proportion, & Wedding Couture Studio.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = PureBlack
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_start_button")
                ) {
                    Text(
                        text = "Apna Look Shuru Karo (Start)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "No measuring tape needed • 100% Free Virtual Trial",
                    fontSize = 11.sp,
                    color = TextSubtle
                )
            }
        }
    }
}

@Composable
fun SetupScreen(
    viewModel: FashionViewModel,
    isEditing: Boolean = false,
    onCompleted: () -> Unit = {}
) {
    val gender by viewModel.formGender.collectAsState()
    val heightFt by viewModel.formHeightFt.collectAsState()
    val weightKg by viewModel.formWeightKg.collectAsState()
    val bodyType by viewModel.formBodyType.collectAsState()
    val skinTone by viewModel.formSkinTone.collectAsState()
    val city by viewModel.formCity.collectAsState()
    val photoUri by viewModel.formPhotoUri.collectAsState()
    val chest by viewModel.formChestInches.collectAsState()
    val waist by viewModel.formWaistInches.collectAsState()
    val showScanner by viewModel.showScannerModal.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setPhotoUri(uri.toString())
        }
    }

    val heightCm = (heightFt * 30.48f).toInt()
    val scrollState = rememberScrollState()

    if (showScanner) {
        BodyScannerDialog(
            heightFt = heightFt,
            weightKg = weightKg,
            isMale = gender == "Male",
            onDismiss = { viewModel.showScannerModal.value = false },
            onCompleteScan = { viewModel.simulateBodyScan() }
        )
    }

    Scaffold(
        containerColor = PureBlack,
        bottomBar = {
            Surface(
                color = DarkSurface,
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.saveBodyProfile(onCompleted) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = PureBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("mera_body_save_karo_btn")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Mera Body Profile Update Karo" else "Mera Body Save Karo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "AI Body Measurement Setup",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = GoldLight
                    )
                    Text(
                        text = "Apni accurate measurement set karein",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                // AI Body Scanner Button
                Button(
                    onClick = { viewModel.showScannerModal.value = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldContainer,
                        contentColor = GoldLight
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("ai_body_scanner_btn")
                ) {
                    Icon(imageVector = Icons.Default.CenterFocusWeak, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Scanner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Gender Selection
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "1. Gender (جنس)", fontWeight = FontWeight.Bold, color = PureWhite)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Male" to Icons.Default.Male, "Female" to Icons.Default.Female).forEach { (opt, icon) ->
                            val isSelected = gender.equals(opt, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GoldPrimary else DarkSurface,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { viewModel.setGender(opt) }
                                    .testTag("gender_${opt.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = icon, contentDescription = opt, tint = if (isSelected) PureBlack else TextMuted)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(opt, fontWeight = FontWeight.Bold, color = if (isSelected) PureBlack else PureWhite)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Height Slider (4.0 ft to 7.0 ft)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "2. Height (قد)", fontWeight = FontWeight.Bold, color = PureWhite)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldPrimary)
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${"%.1f".format(heightFt)} ft ($heightCm cm)",
                                color = PureBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = heightFt,
                        onValueChange = { viewModel.setHeightFt(it) },
                        valueRange = 4.0f..7.0f,
                        steps = 30,
                        colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("height_slider")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("4.0 ft (122 cm)", fontSize = 10.sp, color = TextSubtle)
                        Text("5.6 ft", fontSize = 10.sp, color = GoldLight)
                        Text("7.0 ft (213 cm)", fontSize = 10.sp, color = TextSubtle)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Weight Slider (35kg to 130kg)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "3. Weight (وزن)", fontWeight = FontWeight.Bold, color = PureWhite)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldAccent)
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${weightKg.toInt()} kg",
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = weightKg,
                        onValueChange = { viewModel.setWeightKg(it) },
                        valueRange = 35f..130f,
                        steps = 95,
                        colors = SliderDefaults.colors(thumbColor = EmeraldAccent, activeTrackColor = EmeraldAccent),
                        modifier = Modifier.fillMaxWidth().testTag("weight_slider")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("35 kg", fontSize = 10.sp, color = TextSubtle)
                        Text("70 kg", fontSize = 10.sp, color = TextMuted)
                        Text("130 kg", fontSize = 10.sp, color = TextSubtle)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Body Type & Scanner Measurements
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "4. Body Type", fontWeight = FontWeight.Bold, color = PureWhite)
                        Text("Chest: ${chest.toInt()}\" • Waist: ${waist.toInt()}\"", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Slim", "Medium", "Chubby", "Muscular").forEach { type ->
                            val isSelected = bodyType.equals(type, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setBodyType(type) },
                                label = { Text(type, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = PureBlack
                                ),
                                modifier = Modifier.weight(1f).testTag("body_type_$type")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Skin Tone & City
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "5. Skin Tone (رنگت)", fontWeight = FontWeight.Bold, color = PureWhite)
                    Spacer(modifier = Modifier.height(8.dp))

                    val tones = listOf(
                        "Fair" to Color(0xFFF9E4D4),
                        "Medium Wheatish" to Color(0xFFD8A878),
                        "Dark" to Color(0xFF8D5524)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tones.forEach { (label, color) ->
                            val isSelected = skinTone.equals(label, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GoldContainer else DarkSurface,
                                border = BorderStroke(1.5.dp, if (isSelected) GoldPrimary else Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setSkinTone(label) }
                                    .padding(vertical = 4.dp)
                                    .testTag("skin_tone_${label.take(4).lowercase()}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(label, fontSize = 10.sp, color = PureWhite, textAlign = TextAlign.Center, maxLines = 1)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // City selector
                    Text(text = "City (Weather & Delivery):", fontWeight = FontWeight.Bold, color = PureWhite)
                    Spacer(modifier = Modifier.height(8.dp))

                    val cities = listOf("Quetta", "Lahore", "Karachi", "Islamabad", "Peshawar")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        cities.forEach { c ->
                            val isSelected = city.equals(c, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setCity(c) },
                                label = { Text(c, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = PureBlack
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Photo Try-On
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "User",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.5.dp, GoldPrimary, RoundedCornerShape(10.dp))
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurface)
                        ) {
                            Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, tint = GoldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Photo Try-On (Optional)", fontWeight = FontWeight.Bold, color = PureWhite)
                        Text(
                            text = if (photoUri != null) "Photo uploaded for AI dressing!" else "Upload photo for realistic face try-on.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    TextButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Text(if (photoUri != null) "Change" else "Upload", fontWeight = FontWeight.Bold, color = GoldLight)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
