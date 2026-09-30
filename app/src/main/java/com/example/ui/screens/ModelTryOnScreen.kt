package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.FashionCatalog
import com.example.data.model.FashionItem
import com.example.data.model.PehnoCategory
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import com.example.util.PehnoStrings
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelTryOnScreen(
    viewModel: FashionViewModel,
    onNavigateToMarketplace: () -> Unit = {}
) {
    val context = LocalContext.current
    val lang by viewModel.currentLanguage.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val gender = userProfile?.gender ?: viewModel.formGender.collectAsState().value
    val weightKg = userProfile?.weightKg ?: viewModel.formWeightKg.collectAsState().value
    val heightFt = userProfile?.heightFt ?: viewModel.formHeightFt.collectAsState().value
    val userPhotoUri = userProfile?.photoUri ?: viewModel.formPhotoUri.collectAsState().value

    // Face Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateUserPhotoUri(it.toString()) }
    }

    var showFaceOptionsSheet by remember { mutableStateOf(false) }

    // Camera Launcher for Portrait Capture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.processAndSetCameraPortrait(context, bitmap)
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            viewModel.userNotice.value = if (lang == "ur") "کیمرہ کی اجازت درکار ہے" else "Camera permission required to capture portrait"
        }
    }

    val launchCameraForPortrait = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            cameraLauncher.launch(null)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Layers
    val dress by viewModel.selectedDress.collectAsState()
    val gala by viewModel.selectedGala.collectAsState()
    val cap by viewModel.selectedCap.collectAsState()
    val daman by viewModel.selectedDaman.collectAsState()
    val mehndi by viewModel.selectedMehndi.collectAsState()
    val hair by viewModel.selectedHair.collectAsState()
    val jewellery by viewModel.selectedJewellery.collectAsState()
    val shoes by viewModel.selectedShoes.collectAsState()

    // 360 & View state
    var isBackView by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    // Active Category Bottom Sheet
    var selectedCategoryForSheet by remember { mutableStateOf<PehnoCategory?>(null) }
    val sheetItems = remember(selectedCategoryForSheet, gender) {
        selectedCategoryForSheet?.let { cat ->
            FashionCatalog.getItemsForPehnoCategory(cat, gender)
        } ?: emptyList()
    }

    // Dynamic Weight & Height Body Auto-Scaling
    val widthScale = (0.76f + ((weightKg - 40f) / 80f) * 0.50f).coerceIn(0.74f, 1.35f)
    val heightScale = (heightFt / 5.5f).coerceIn(0.85f, 1.25f)
    val modelBoxHeight = (430 * heightScale).dp
    val shoeBoost = shoes?.heightBoostInches ?: 0f
    val effectiveHeight = heightFt + (shoeBoost / 12f)

    val bodyTypeLabel = when {
        weightKg < 55 -> if (lang == "ur") "دبلا پتلا (Slim Fit)" else "Slim Fit"
        weightKg < 75 -> if (lang == "ur") "متوازن (Medium / Athletic)" else "Medium Athletic"
        weightKg < 95 -> if (lang == "ur") "بھاری جسم (Chubby / Broad)" else "Chubby / Broad"
        else -> if (lang == "ur") "ہیوی سائز (Plus Size / Heavy)" else "Heavy Plus Size"
    }

    Box(modifier = Modifier.fillMaxSize().background(PehnoWhite)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for bottom category bar
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Commission & User Face Action
            Surface(
                color = PehnoGreenLight,
                border = BorderStroke(1.dp, PehnoGreenPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = PehnoGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = PehnoStrings.t("commission_notice", lang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PehnoGreenDark
                            )
                        }
                        Text(
                            text = "جسم: ${weightKg.toInt()}kg ($bodyTypeLabel) • قد: ${"%.1f".format(heightFt)}ft ${if (shoeBoost > 0) "(+${shoeBoost.toInt()}\" جوتا)" else ""}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = PehnoTextSecondary
                        )
                    }

                    // Face Upload Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PehnoWhite,
                        border = BorderStroke(1.dp, PehnoGreenPrimary),
                        modifier = Modifier.clickable {
                            if (userPhotoUri.isNullOrBlank()) {
                                launchCameraForPortrait()
                            } else {
                                showFaceOptionsSheet = true
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (userPhotoUri.isNullOrBlank()) Icons.Default.CameraAlt else Icons.Default.Face,
                                contentDescription = "Face",
                                tint = PehnoGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (userPhotoUri.isNullOrBlank()) {
                                    if (lang == "ur") "پورٹریٹ لیں" else "Capture Portrait"
                                } else {
                                    if (lang == "ur") "چہرہ تبدیل کریں" else "Change Face"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PehnoGreenPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. CENTER 3D/2D HUMAN MODEL VIEWPORT (With Dynamic Scale & Superimposed Face)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(modelBoxHeight)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(PehnoSurface)
                    .border(2.dp, PehnoGreenPrimary, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Interactive 3D Rotation transform
                val effectiveRotation = if (isBackView) 180f else rotationAngle
                val isBackFacing = (effectiveRotation in 90f..270f)

                // Real Dress Image (Front or Back)
                val activeImageUrl = if (isBackFacing) {
                    dress?.backImageUrl ?: dress?.frontImageUrl
                } else {
                    dress?.frontImageUrl
                }

                val fallbackRes = if (gender == "Female") {
                    if (dress?.name?.contains("Lehenga", true) == true) R.drawable.model_bridal_lehenga_female
                    else R.drawable.model_emerald_kurti_female
                } else {
                    if (dress?.name?.contains("Sherwani", true) == true) R.drawable.model_sherwani_male
                    else R.drawable.model_black_shalwar_male
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width((280 * widthScale).dp)
                        .graphicsLayer {
                            rotationY = rotationAngle
                            cameraDistance = 12 * density
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Layer 1: Real dress photo of model
                    if (!activeImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = activeImageUrl,
                            contentDescription = dress?.name ?: "Model Dress",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                        )
                    } else {
                        Image(
                            painter = painterResource(fallbackRes),
                            contentDescription = "Model Dress",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                        )
                    }

                    // Layer 2: USER'S REAL FACE SUPERIMPOSED ON MODEL (When facing front)
                    if (!isBackFacing) {
                        if (!userPhotoUri.isNullOrBlank()) {
                            // User's real face framed accurately on the head/face region
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 24.dp)
                                    .size(width = 88.dp, height = 106.dp)
                                    .clip(RoundedCornerShape(48.dp))
                                    .background(PehnoSurface)
                                    .border(2.5.dp, PehnoGreenPrimary, RoundedCornerShape(48.dp))
                                    .clickable { showFaceOptionsSheet = true }
                            ) {
                                AsyncImage(
                                    model = userPhotoUri,
                                    contentDescription = "User Face on Model",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Corner indicator badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .background(PehnoBlack.copy(alpha = 0.7f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (lang == "ur") "آپ کا چہرہ" else "You",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PehnoWhite
                                    )
                                }
                            }
                        } else {
                            // Prompt to tap and capture portrait on model
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = PehnoBlack.copy(alpha = 0.72f),
                                border = BorderStroke(1.5.dp, PehnoGreenPrimary),
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 34.dp)
                                    .clickable { launchCameraForPortrait() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Capture Portrait",
                                        tint = PehnoGreenLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (lang == "ur") "پورٹریٹ لیں (Capture Portrait)" else "Capture Portrait",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PehnoWhite
                                    )
                                }
                            }
                        }
                    }

                    // Layer 3: Subtle gradient overlay for contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.25f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.65f)
                                    )
                                )
                            )
                    )

                    // Layer badges overlay on model
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = dress?.name ?: "Pehno Jora",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = PehnoWhite
                        )
                        Text(
                            text = "${dress?.storeSource ?: "All Pakistan Store"} • Rs. ${dress?.priceRs ?: 4500}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PehnoGreenLight
                        )
                    }
                }

                // Front / Back View Indicator Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PehnoBlack.copy(alpha = 0.75f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isBackFacing) Color.Yellow else PehnoGreenAccent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBackFacing) PehnoStrings.t("back_view", lang) else PehnoStrings.t("front_view", lang),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PehnoWhite
                        )
                    }
                }

                // 360 Angle Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PehnoGreenPrimary)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${rotationAngle.toInt()}° 3D",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = PehnoWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dedicated Face & 3D Model Overlay Action Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PehnoSurface,
                border = BorderStroke(1.dp, PehnoCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = PehnoGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = PehnoStrings.t("face_overlay_title", lang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PehnoBlack
                            )
                        }

                        if (!userPhotoUri.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PehnoGreenLight,
                                border = BorderStroke(1.dp, PehnoGreenPrimary.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = if (lang == "ur") "چہرہ ماڈل پر فعال ہے" else "Face Active on 3D Model",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PehnoGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Button 1: Capture Portrait (Camera)
                        Button(
                            onClick = { launchCameraForPortrait() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PehnoGreenPrimary,
                                contentColor = PehnoWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.25f)
                                .height(46.dp)
                                .testTag("capture_portrait_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Capture Portrait",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == "ur") "پورٹریٹ کیمرہ" else "Capture Portrait",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Button 2: Gallery Picker
                        OutlinedButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            border = BorderStroke(1.5.dp, PehnoGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PehnoGreenPrimary
                            ),
                            modifier = Modifier
                                .weight(0.95f)
                                .height(46.dp)
                                .testTag("upload_user_face_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == "ur") "گیلری سے" else "From Gallery",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. FRONT & BACK BUTTONS + 360° GHUMAO SLIDER
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PehnoSurface,
                border = BorderStroke(1.dp, PehnoCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Front / Back Big Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Front View Button
                        Button(
                            onClick = {
                                isBackView = false
                                rotationAngle = 0f
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isBackView && rotationAngle == 0f) PehnoGreenPrimary else PehnoWhite,
                                contentColor = if (!isBackView && rotationAngle == 0f) PehnoWhite else PehnoBlack
                            ),
                            border = BorderStroke(1.5.dp, if (!isBackView && rotationAngle == 0f) PehnoGreenPrimary else PehnoCardBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_front_view")
                        ) {
                            Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = PehnoStrings.t("front_view", lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Back View Button
                        Button(
                            onClick = {
                                isBackView = true
                                rotationAngle = 180f
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBackView || rotationAngle == 180f) PehnoGreenPrimary else PehnoWhite,
                                contentColor = if (isBackView || rotationAngle == 180f) PehnoWhite else PehnoBlack
                            ),
                            border = BorderStroke(1.5.dp, if (isBackView || rotationAngle == 180f) PehnoGreenPrimary else PehnoCardBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_back_view")
                        ) {
                            Icon(imageVector = Icons.Default.FlipCameraAndroid, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = PehnoStrings.t("back_view", lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 360° Ghumao Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.RotateRight, contentDescription = null, tint = PehnoGreenPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = PehnoStrings.t("rotate_360", lang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PehnoBlack
                            )
                        }
                        Text(
                            text = PehnoStrings.t("drag_to_rotate", lang),
                            fontSize = 11.sp,
                            color = PehnoTextSecondary
                        )
                    }

                    Slider(
                        value = rotationAngle,
                        onValueChange = {
                            rotationAngle = it
                            isBackView = (it in 90f..270f)
                        },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = PehnoGreenPrimary,
                            activeTrackColor = PehnoGreenPrimary,
                            inactiveTrackColor = PehnoCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("rotate_360_slider")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. STYLE ADVISOR & VERDICT: KIA ACHA LAGTA HAI / KIA ACHA NAHI LAGTA HAI
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PehnoWhite,
                border = BorderStroke(1.5.dp, PehnoGreenPrimary),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header with Score & Voice Speaker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (lang == "ur") "سٹائل تجزیہ: کیا اچھا لگتا ہے اور کیا نہیں" else "Style Evaluation: What Suits You & What Doesn't",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoBlack
                            )
                            Text(
                                text = "Aap ki body (${weightKg.toInt()}kg, ${"%.1f".format(heightFt)}ft) ke mutabiq",
                                fontSize = 11.sp,
                                color = PehnoTextSecondary
                            )
                        }

                        // Match score badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PehnoGreenPrimary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "97% Match",
                                color = PehnoWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Green box: KIA ACHA LAGTA HAI (What looks great)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PehnoGreenLight,
                        border = BorderStroke(1.dp, PehnoGreenPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PehnoGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == "ur") "✅ کیا اچھا لگتا ہے (What Looks Great):" else "✅ What Looks Great on You:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PehnoGreenDark
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• ڈریس فال: ${dress?.name ?: "Jora"} کا vertical cut aap ke ${"%.1f".format(heightFt)}ft qad par shaandar balance deta hai.\n" +
                                       "• جوتا لفٹ: ${shoes?.name ?: "Joota"} se aap ko +${shoeBoost.toInt()}\" ka natural lift mil raha hai (${"%.2f".format(effectiveHeight)}ft look).\n" +
                                       "• چہرہ و ہیئر: ${hair?.name ?: "Hairstyle"} aap ke collar ke sath royal posture banata hai.",
                                fontSize = 11.sp,
                                color = PehnoBlack,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Yellow/Orange box: KIA ACHA NAHI LAGTA / EHTIYAAT (What to improve)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PehnoSurface,
                        border = BorderStroke(1.dp, Color(0xFFEAB308)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == "ur") "⚠️ کیا اچھا نہیں لگتا / احتیاط کریں:" else "⚠️ What to Avoid / Fashion Tips:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            val adviceText = if (weightKg > 82) {
                                "• وزن کے مطابق: ${weightKg.toInt()}kg par broad horizontal prints se bachein; straight dark falls aap ko mazeed smart dikhayenge.\n" +
                                "• فٹنگ: Bohat tight fitting ke bajaye relaxed regular drape chunein."
                            } else if (heightFt < 5.4f) {
                                "• قد کے مطابق: Floor-length heavy flares ke bajaye shorter waistcoats aur 2\" heel footwear choose karein.\n" +
                                "• گلا: Stand ban collar gardan ko lamba frame deta hai."
                            } else {
                                "• متوازن جسم: Oversized baggy fitting se bachein taake tailored cuts aap ki height aur chest line ko ubhar sakein."
                            }
                            Text(
                                text = adviceText,
                                fontSize = 11.sp,
                                color = PehnoBlack,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Voice Stylist Button (Baji voice advice)
                    Button(
                        onClick = {
                            val speechText = if (lang == "ur") {
                                "Zabardast! Aap ke ${"%.1f".format(heightFt)}ft qad aur ${weightKg.toInt()}kg par yeh ${dress?.name} bohot shaandar jach raha hai. Footwear se aap ko ${shoeBoost.toInt()} inch ka height boost mil raha hai."
                            } else {
                                "Great look! On your ${"%.1f".format(heightFt)}ft height and ${weightKg.toInt()}kg body, this ${dress?.name} fits perfectly with a ${shoeBoost.toInt()} inch shoe lift."
                            }
                            viewModel.voiceStylist.speak(speechText)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PehnoGreenLight,
                            contentColor = PehnoGreenPrimary
                        ),
                        border = BorderStroke(1.dp, PehnoGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == "ur") "🔊 باجی کی زبانی مشورہ سنیں (Listen)" else "🔊 Listen to Stylist Advice",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. LAYER SUMMARY PILLS (What is currently on model)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PehnoSurface,
                border = BorderStroke(1.dp, PehnoCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = PehnoStrings.t("layer_by_layer", lang),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        dress?.let {
                            item {
                                LayerAppliedChip(title = it.name, subtitle = "Jora", onRemove = {})
                            }
                        }
                        gala?.let {
                            item {
                                LayerAppliedChip(title = it.name, subtitle = "Gala", onRemove = { viewModel.selectedGala.value = null })
                            }
                        }
                        cap?.let {
                            item {
                                LayerAppliedChip(title = it.name, subtitle = "Topi", onRemove = { viewModel.selectedCap.value = null })
                            }
                        }
                        daman?.let {
                            item {
                                LayerAppliedChip(title = it.name, subtitle = "Daman", onRemove = { viewModel.selectedDaman.value = null })
                            }
                        }
                        mehndi?.let {
                            item {
                                LayerAppliedChip(title = it.name, subtitle = "Mehndi", onRemove = { viewModel.selectedMehndi.value = null })
                            }
                        }
                        hair?.let {
                            item {
                                LayerAppliedChip(title = it.name, subtitle = "Hair", onRemove = {})
                            }
                        }
                        jewellery?.let {
                            item {
                                LayerAppliedChip(title = it.name, subtitle = "Jewellery", onRemove = { viewModel.selectedJewellery.value = null })
                            }
                        }
                        shoes?.let {
                            item {
                                LayerAppliedChip(title = it.name, subtitle = "+${it.heightBoostInches}\" Shoes", onRemove = {})
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. ACTION BUTTONS: [TASVEER SAVE KARO] + [ORDER KARO]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Download Final Look
                Button(
                    onClick = {
                        viewModel.exportPehnoModelLookPng { success, path ->
                            viewModel.userNotice.value = if (success) {
                                if (lang == "ur") "تصویر محفوظ ہو گئی: Pehno PNG!" else "Final Look Image saved as PNG to Pictures!"
                            } else {
                                if (lang == "ur") "تصویر محفوظ کرنے میں خرابی" else "Failed to save image"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PehnoBlack,
                        contentColor = PehnoWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("download_look_btn")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = PehnoStrings.t("download_look", lang),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Button 2: Order Now
                Button(
                    onClick = {
                        dress?.let { currentDress ->
                            if (!currentDress.whatsappNumber.isNullOrBlank()) {
                                // Local seller -> WhatsApp order
                                val message = "Assalam-o-Alaikum! I want to order your dress from Pehno App: ${currentDress.name} for Rs ${currentDress.priceRs}"
                                val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${currentDress.whatsappNumber}?text=${Uri.encode(message)}"))
                                context.startActivity(waIntent)
                            } else {
                                // Store dress -> Affiliate order
                                val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentDress.darazLink))
                                context.startActivity(storeIntent)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PehnoGreenPrimary,
                        contentColor = PehnoWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("order_now_btn")
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = PehnoStrings.t("order_now", lang),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 5. BOTTOM BAR: BIG CATEGORY ICONS
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = PehnoWhite,
            border = BorderStroke(1.dp, PehnoCardBorder),
            shadowElevation = 8.dp
        ) {
            val categories = if (gender == "Female") {
                listOf(
                    PehnoCategory.DRESS to Icons.Default.Checkroom,
                    PehnoCategory.GALA_DESIGN to Icons.Default.AutoFixHigh,
                    PehnoCategory.DAMAN_DESIGN to Icons.Default.BorderBottom,
                    PehnoCategory.MEHNDI to Icons.Default.Brush,
                    PehnoCategory.HAIRSTYLE to Icons.Default.Face,
                    PehnoCategory.JEWELLERY to Icons.Default.Diamond,
                    PehnoCategory.SHOES to Icons.Default.IceSkating
                )
            } else {
                listOf(
                    PehnoCategory.DRESS to Icons.Default.Checkroom,
                    PehnoCategory.GALA_COLLAR to Icons.Default.AutoFixHigh,
                    PehnoCategory.TOPI_CAP to Icons.Default.Face,
                    PehnoCategory.HAIRSTYLE to Icons.Default.ContentCut,
                    PehnoCategory.SHOES to Icons.Default.IceSkating
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { (cat, icon) ->
                    val isSelected = (selectedCategoryForSheet == cat)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) PehnoGreenPrimary else PehnoSurface,
                        border = BorderStroke(1.dp, if (isSelected) PehnoGreenPrimary else PehnoCardBorder),
                        modifier = Modifier
                            .clickable {
                                selectedCategoryForSheet = if (isSelected) null else cat
                            }
                            .testTag("cat_btn_${cat.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = cat.english,
                                tint = if (isSelected) PehnoWhite else PehnoGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (lang == "ur") cat.urdu else cat.english,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PehnoWhite else PehnoBlack
                            )
                        }
                    }
                }
            }
        }

        // 6. BOTTOM SHEET FOR 10 REAL PHOTO OPTIONS
        selectedCategoryForSheet?.let { cat ->
            ModalBottomSheet(
                onDismissRequest = { selectedCategoryForSheet = null },
                containerColor = PehnoWhite,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (lang == "ur") "${cat.urdu} منتخب کریں (10 آپشنز)" else "Select ${cat.english} (10 Options)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoBlack
                            )
                            Text(
                                text = if (lang == "ur") "کسی بھی آئٹم پر کلک کر کے ماڈل پر لگائیں" else "Tap an item to layer it directly on your model",
                                fontSize = 11.sp,
                                color = PehnoTextSecondary
                            )
                        }
                        IconButton(onClick = { selectedCategoryForSheet = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                    ) {
                        items(sheetItems) { item ->
                            CategoryItemRealPhotoCard(
                                item = item,
                                isSelected = (dress?.id == item.id || gala?.id == item.id || cap?.id == item.id || daman?.id == item.id || mehndi?.id == item.id || hair?.id == item.id || jewellery?.id == item.id || shoes?.id == item.id),
                                onSelect = {
                                    viewModel.selectItemLayer(item)
                                    selectedCategoryForSheet = null
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // 7. BOTTOM SHEET FOR FACE PORTRAIT OPTIONS
        if (showFaceOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFaceOptionsSheet = false },
                containerColor = PehnoWhite,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (lang == "ur") "ماڈل کے چہرے کا انتخاب" else "Model Face & Portrait",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoBlack
                            )
                            Text(
                                text = if (lang == "ur") "کیمرہ سے پورٹریٹ لیں یا گیلری سے لگائیں" else "Capture portrait using camera or choose from gallery",
                                fontSize = 12.sp,
                                color = PehnoTextSecondary
                            )
                        }
                        IconButton(onClick = { showFaceOptionsSheet = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Option 1: Capture Portrait (Camera)
                    Button(
                        onClick = {
                            showFaceOptionsSheet = false
                            launchCameraForPortrait()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PehnoGreenPrimary,
                            contentColor = PehnoWhite
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("sheet_capture_portrait_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (lang == "ur") "کیمرہ سے پورٹریٹ لیں (Capture Portrait)" else "Capture Portrait with Camera",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Gallery Picker
                    OutlinedButton(
                        onClick = {
                            showFaceOptionsSheet = false
                            photoPickerLauncher.launch("image/*")
                        },
                        border = BorderStroke(1.5.dp, PehnoGreenPrimary),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PehnoGreenPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("sheet_gallery_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Gallery",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (lang == "ur") "گیلری سے تصویر منتخب کریں" else "Choose from Photo Gallery",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!userPhotoUri.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Option 3: Remove face
                        OutlinedButton(
                            onClick = {
                                showFaceOptionsSheet = false
                                viewModel.updateUserPhotoUri("")
                            },
                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("sheet_remove_face_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == "ur") "چہرہ ہٹائیں (Remove Face)" else "Remove Face from Model",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun LayerAppliedChip(
    title: String,
    subtitle: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = PehnoGreenLight,
        border = BorderStroke(1.dp, PehnoGreenPrimary)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(subtitle, fontSize = 9.sp, color = PehnoGreenDark, fontWeight = FontWeight.Bold)
                Text(title, fontSize = 11.sp, color = PehnoBlack, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = PehnoGreenPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun CategoryItemRealPhotoCard(
    item: FashionItem,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = PehnoWhite,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) PehnoGreenPrimary else PehnoCardBorder),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("item_card_${item.id}")
    ) {
        Column {
            // Real photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            ) {
                if (item.frontImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.frontImageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (item.drawableResId != 0) {
                    Image(
                        painter = painterResource(item.drawableResId),
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.model_black_shalwar_male),
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(PehnoGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = PehnoWhite, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Info
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PehnoBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.urduName,
                    fontSize = 10.sp,
                    color = PehnoGreenDark,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rs. ${item.priceRs}",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = PehnoGreenPrimary
                    )
                    Text(
                        text = if (isSelected) "Active" else "Tap to add",
                        fontSize = 9.sp,
                        color = if (isSelected) PehnoGreenPrimary else PehnoTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
