package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import com.example.util.PehnoStrings
import com.example.util.PortraitProcessor
import com.example.util.FaceOverlayService

@Composable
fun ProfileSetupScreen(
    viewModel: FashionViewModel,
    isEditing: Boolean = false,
    onCompleted: () -> Unit = {}
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var name by remember { mutableStateOf(userProfile?.name ?: "Hamza") }
    var gender by remember { mutableStateOf(userProfile?.gender ?: "Male") }
    var weightKg by remember { mutableFloatStateOf(userProfile?.weightKg ?: 70f) }
    var heightFt by remember { mutableFloatStateOf(userProfile?.heightFt ?: 5.8f) }
    var selectedLang by remember { mutableStateOf(lang) }
    var photoUri by remember { mutableStateOf(userProfile?.photoUri) }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val processedPath = FaceOverlayService.processAndMapFace(
                context = context,
                rawBitmap = bitmap,
                heightFt = heightFt,
                weightKg = weightKg,
                gender = gender
            ) ?: PortraitProcessor.processAndSavePortrait(context, bitmap)
            if (processedPath != null) {
                photoUri = processedPath
                viewModel.formPhotoUri.value = processedPath
                viewModel.userNotice.value = if (selectedLang == "ur") "پورٹریٹ کامیابی سے لگ گیا!" else "Portrait captured and processed successfully!"
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            viewModel.userNotice.value = if (selectedLang == "ur") "کیمرہ کی اجازت درکار ہے" else "Camera permission required"
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUri = it.toString()
            viewModel.formPhotoUri.value = it.toString()
        }
    }

    val heightCm = (heightFt * 30.48f).toInt()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PehnoWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with language switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = PehnoStrings.t("app_name", selectedLang),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = PehnoGreenPrimary
                    )
                    Text(
                        text = PehnoStrings.t("tagline", selectedLang),
                        fontSize = 13.sp,
                        color = PehnoTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Instant Language Switcher Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PehnoGreenLight,
                    border = BorderStroke(1.5.dp, PehnoGreenPrimary),
                    modifier = Modifier.clickable {
                        selectedLang = if (selectedLang == "ur") "en" else "ur"
                        viewModel.currentLanguage.value = selectedLang
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = PehnoGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedLang == "ur") "English" else "اردو",
                            fontWeight = FontWeight.Bold,
                            color = PehnoGreenPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PehnoSurface,
                border = BorderStroke(1.dp, PehnoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isEditing) PehnoStrings.t("edit_profile", selectedLang) else PehnoStrings.t("profile_setup", selectedLang),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoBlack
                    )
                    Text(
                        text = PehnoStrings.t("profile_setup_sub", selectedLang),
                        fontSize = 12.sp,
                        color = PehnoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Name Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = PehnoStrings.t("name_label", selectedLang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PehnoBlack
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(PehnoStrings.t("name_placeholder", selectedLang), color = PehnoTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PehnoGreenPrimary,
                        unfocusedBorderColor = PehnoCardBorder,
                        focusedContainerColor = PehnoWhite,
                        unfocusedContainerColor = PehnoWhite
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Gender Selection (MAIN LOGIC FILTER)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = PehnoStrings.t("gender_label", selectedLang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PehnoBlack
                )
                Text(
                    text = if (selectedLang == "ur") "مرد منتخب کرنے سے تمام مردانہ اور عورت سے زنانہ کپڑے نظر آئیں گے"
                           else "Male shows only Men's wear, Female shows only Women's wear",
                    fontSize = 11.sp,
                    color = PehnoTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Male Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (gender == "Male") PehnoGreenLight else PehnoWhite,
                        border = BorderStroke(2.dp, if (gender == "Male") PehnoGreenPrimary else PehnoCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clickable { gender = "Male" }
                            .testTag("gender_male_btn")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = PehnoStrings.t("male_btn", selectedLang),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (gender == "Male") PehnoGreenPrimary else PehnoBlack
                            )
                        }
                    }

                    // Female Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (gender == "Female") PehnoGreenLight else PehnoWhite,
                        border = BorderStroke(2.dp, if (gender == "Female") PehnoGreenPrimary else PehnoCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clickable { gender = "Female" }
                            .testTag("gender_female_btn")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = PehnoStrings.t("female_btn", selectedLang),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (gender == "Female") PehnoGreenPrimary else PehnoBlack
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Weight Slider (40kg to 120kg)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PehnoSurface,
                border = BorderStroke(1.dp, PehnoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = PehnoStrings.t("weight_label", selectedLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PehnoBlack
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PehnoGreenPrimary)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${weightKg.toInt()} kg",
                                color = PehnoWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Slider(
                        value = weightKg,
                        onValueChange = { weightKg = it },
                        valueRange = 40f..120f,
                        colors = SliderDefaults.colors(
                            thumbColor = PehnoGreenPrimary,
                            activeTrackColor = PehnoGreenPrimary,
                            inactiveTrackColor = PehnoCardBorder
                        ),
                        modifier = Modifier.testTag("weight_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("40 kg", fontSize = 11.sp, color = PehnoTextMuted)
                        Text("80 kg", fontSize = 11.sp, color = PehnoTextMuted)
                        Text("120 kg", fontSize = 11.sp, color = PehnoTextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Height Slider (4.5ft to 6.5ft)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PehnoSurface,
                border = BorderStroke(1.dp, PehnoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = PehnoStrings.t("height_label", selectedLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PehnoBlack
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PehnoGreenPrimary)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${"%.1f".format(heightFt)} ft ($heightCm cm)",
                                color = PehnoWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Slider(
                        value = heightFt,
                        onValueChange = { heightFt = it },
                        valueRange = 4.5f..6.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = PehnoGreenPrimary,
                            activeTrackColor = PehnoGreenPrimary,
                            inactiveTrackColor = PehnoCardBorder
                        ),
                        modifier = Modifier.testTag("height_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("4.5 ft (137 cm)", fontSize = 11.sp, color = PehnoTextMuted)
                        Text("5.5 ft (168 cm)", fontSize = 11.sp, color = PehnoTextMuted)
                        Text("6.5 ft (198 cm)", fontSize = 11.sp, color = PehnoTextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. User Face Photo for Model (Apna Chehra Lagayein)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PehnoSurface,
                border = BorderStroke(1.dp, PehnoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selectedLang == "ur") "اپنا چہرہ لگائیں (Selfie / Face Photo)" else "Add Your Face for Model",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = PehnoBlack
                            )
                            Text(
                                text = if (selectedLang == "ur") "ماڈل پر آپ کا اپنا چہرہ نظر آئے گا" else "Your photo will be placed on the model's head",
                                fontSize = 11.sp,
                                color = PehnoTextSecondary
                            )
                        }

                        if (!photoUri.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, PehnoGreenPrimary, CircleShape)
                            ) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "User Face",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Capture Portrait Button
                        Button(
                            onClick = {
                                val hasCam = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasCam) {
                                    cameraLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PehnoGreenPrimary,
                                contentColor = PehnoWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.15f)
                                .height(48.dp)
                                .testTag("capture_portrait_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Capture Portrait",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedLang == "ur") "پورٹریٹ کیمرہ" else "Capture Portrait",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // From Gallery Button
                        OutlinedButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            border = BorderStroke(1.5.dp, PehnoGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PehnoGreenPrimary
                            ),
                            modifier = Modifier
                                .weight(0.95f)
                                .height(48.dp)
                                .testTag("upload_user_face_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedLang == "ur") "گیلری سے" else "From Gallery",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BIG START BUTTON (High accessibility)
            Button(
                onClick = {
                    viewModel.saveInitialProfile(
                        name = name,
                        gender = gender,
                        weightKg = weightKg,
                        heightFt = heightFt,
                        language = selectedLang,
                        photoUri = photoUri
                    )
                    onCompleted()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PehnoGreenPrimary,
                    contentColor = PehnoWhite
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("start_tryon_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = PehnoStrings.t("start_btn", selectedLang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
