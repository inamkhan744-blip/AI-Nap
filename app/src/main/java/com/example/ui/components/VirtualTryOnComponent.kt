package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.FashionCatalog
import com.example.data.model.FashionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import com.example.util.ClothingOverlayEngine
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun VirtualTryOnDialog(
    viewModel: FashionViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            VirtualTryOnScreen(
                viewModel = viewModel,
                onClose = onDismiss
            )
        }
    }
}

@Composable
fun VirtualTryOnScreen(
    viewModel: FashionViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lang by viewModel.currentLanguage.collectAsState()
    val isUrdu = lang == "ur"
    val userProfile by viewModel.userProfile.collectAsState()
    val gender = userProfile?.gender ?: viewModel.formGender.collectAsState().value
    val heightFt = userProfile?.heightFt ?: viewModel.formHeightFt.collectAsState().value
    val weightKg = userProfile?.weightKg ?: viewModel.formWeightKg.collectAsState().value

    // Available dresses to try on
    val availableDresses = remember(gender) {
        FashionCatalog.searchMultiStoreItems("", gender)
    }

    var selectedDress by remember {
        mutableStateOf(viewModel.selectedDress.value ?: availableDresses.firstOrNull() ?: FashionCatalog.items.first())
    }

    // Permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(
                context,
                if (isUrdu) "کیمرہ کی اجازت درکار ہے" else "Camera permission required for Virtual Try-On",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // CameraX and image manipulation states
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var compositeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Manipulation Controls
    var garmentScale by remember { mutableFloatStateOf(1.0f) }
    var garmentOffsetY by remember { mutableFloatStateOf(0.0f) }
    var garmentOffsetX by remember { mutableFloatStateOf(0.0f) }
    var garmentAlpha by remember { mutableFloatStateOf(1.0f) }
    var splitSliderPos by remember { mutableFloatStateOf(1.0f) } // 1.0 = full composite, 0.0 = full raw
    var showAdjustControls by remember { mutableStateOf(false) }

    // Recompute composite when parameters change
    LaunchedEffect(capturedBitmap, selectedDress, garmentScale, garmentOffsetY, garmentOffsetX, garmentAlpha) {
        val captured = capturedBitmap ?: return@LaunchedEffect
        val dressDrawable = selectedDress.drawableResId.takeIf { it != 0 } ?: R.drawable.model_black_shalwar_male
        val garmentBmp = BitmapFactory.decodeResource(context.resources, dressDrawable) ?: return@LaunchedEffect

        val composite = ClothingOverlayEngine.compositeGarmentOnPhoto(
            userPhoto = captured,
            garmentBitmap = garmentBmp,
            scaleFactor = garmentScale,
            offsetXPct = garmentOffsetX,
            offsetYPct = garmentOffsetY,
            alpha = garmentAlpha,
            gender = gender,
            heightFt = heightFt,
            weightKg = weightKg
        )
        compositeBitmap = composite
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (!hasCameraPermission) {
            // Permission Request Card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = PehnoGreenPrimary,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isUrdu) "کیمرہ کی اجازت درکار ہے" else "Camera Permission Required",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isUrdu) "لائیو ورچوئل ٹرائی آن کیلئے کیمرہ آن کریں" else "Enable camera to capture your photo and overlay digital designer clothes in real-time.",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = PehnoGreenPrimary)
                ) {
                    Text(if (isUrdu) "کیمرہ آن کریں" else "Allow Camera Access")
                }
            }
        } else if (capturedBitmap == null) {
            // -------------------------------------------------------------
            // 1. LIVE CAMERAX PREVIEW MODE
            // -------------------------------------------------------------
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = capture

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build()

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                capture
                            )
                        } catch (_: Exception) {}
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = capture

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build()

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                capture
                            )
                        } catch (_: Exception) {}
                    }, ContextCompat.getMainExecutor(context))
                }
            )

            // Live Digital Clothing Silhouette Guide Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 140.dp, top = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                // Semi-transparent clothing guide to help user align their body
                val previewDrawable = selectedDress.drawableResId.takeIf { it != 0 } ?: R.drawable.model_black_shalwar_male
                Image(
                    painter = painterResource(previewDrawable),
                    contentDescription = "Garment Silhouette Guide",
                    modifier = Modifier
                        .fillMaxHeight(0.72f)
                        .padding(horizontal = 40.dp),
                    alpha = 0.45f,
                    contentScale = ContentScale.Fit
                )
            }

            // Top Camera Bar (Flip Camera, Close)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.size(44.dp).clickable { onClose() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUrdu) "لائیو کیمرہ ٹرائی آن" else "Live Camera Try-On",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.size(44.dp).clickable {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            CameraSelector.LENS_FACING_BACK
                        } else {
                            CameraSelector.LENS_FACING_FRONT
                        }
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip Camera", tint = Color.White)
                    }
                }
            }

            // Bottom Carousel & Shutter Button
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(bottom = 24.dp, top = 12.dp)
            ) {
                // Garment Selector Carousel
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(availableDresses) { item ->
                        val isSelected = item.id == selectedDress.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PehnoGreenPrimary else Color.DarkGray,
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) Color.White else Color.Gray),
                            modifier = Modifier
                                .width(90.dp)
                                .height(80.dp)
                                .clickable { selectedDress = item }
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = item.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 2
                                )
                                Text(
                                    text = "Rs. ${item.priceRs}",
                                    fontSize = 9.sp,
                                    color = PehnoGreenLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Center Shutter Capture Button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(4.dp, PehnoGreenPrimary),
                        modifier = Modifier
                            .size(72.dp)
                            .clickable {
                                val capture = imageCapture ?: return@clickable
                                capture.takePicture(
                                    cameraExecutor,
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(image: ImageProxy) {
                                            val buffer = image.planes[0].buffer
                                            val bytes = ByteArray(buffer.remaining())
                                            buffer.get(bytes)
                                            val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                            val rotation = image.imageInfo.rotationDegrees

                                            // Rotate bitmap to match screen orientation
                                            val matrix = Matrix().apply {
                                                postRotate(rotation.toFloat())
                                                if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                                                    postScale(-1f, 1f) // Mirror selfie
                                                }
                                            }
                                            val orientedBitmap = Bitmap.createBitmap(
                                                rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true
                                            )
                                            image.close()

                                            ContextCompat.getMainExecutor(context).execute {
                                                capturedBitmap = orientedBitmap
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            ContextCompat.getMainExecutor(context).execute {
                                                Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                            .testTag("camerax_shutter_btn")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Camera, contentDescription = "Capture", tint = PehnoGreenPrimary, modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
        } else {
            // -------------------------------------------------------------
            // 2. CAPTURED PHOTO & DIGITAL CLOTHING OVERLAY MANIPULATION MODE
            // -------------------------------------------------------------
            val displayBitmap = remember(capturedBitmap, compositeBitmap, splitSliderPos) {
                val raw = capturedBitmap ?: return@remember null
                val comp = compositeBitmap ?: return@remember raw
                if (splitSliderPos >= 0.98f) {
                    comp
                } else if (splitSliderPos <= 0.02f) {
                    raw
                } else {
                    ClothingOverlayEngine.renderBeforeAfterSplit(raw, comp, splitSliderPos)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (displayBitmap != null) {
                    Image(
                        bitmap = displayBitmap.asImageBitmap(),
                        contentDescription = "Virtual Try-On Result",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Top Controls: Retake & Sliders Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.clickable {
                            capturedBitmap = null
                            compositeBitmap = null
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retake", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUrdu) "دوبارہ تصویر لیں" else "Retake", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (showAdjustControls) PehnoGreenPrimary else Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.clickable { showAdjustControls = !showAdjustControls }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Adjust", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUrdu) "فٹنگ ایڈجسٹ کریں" else "Adjust Fitting", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Fine-Tuning Slider Controls Overlay
                AnimatedVisibility(
                    visible = showAdjustControls,
                    enter = androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 90.dp, start = 16.dp, end = 16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, PehnoGreenPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Scale Slider
                            Text("Garment Scale / سائز: ${(garmentScale * 100).toInt()}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Slider(
                                value = garmentScale,
                                onValueChange = { garmentScale = it },
                                valueRange = 0.75f..1.35f,
                                colors = SliderDefaults.colors(thumbColor = PehnoGreenPrimary, activeTrackColor = PehnoGreenPrimary)
                            )

                            // Vertical Position Slider
                            Text("Shoulder Alignment / اونچائی: ${(garmentOffsetY * 100).toInt()}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Slider(
                                value = garmentOffsetY,
                                onValueChange = { garmentOffsetY = it },
                                valueRange = -0.15f..0.20f,
                                colors = SliderDefaults.colors(thumbColor = PehnoGreenPrimary, activeTrackColor = PehnoGreenPrimary)
                            )

                            // Before vs After Split
                            Text("Before vs After Split / لائیو موازنہ: ${(splitSliderPos * 100).toInt()}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Slider(
                                value = splitSliderPos,
                                onValueChange = { splitSliderPos = it },
                                valueRange = 0.0f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = PehnoGreenPrimary)
                            )
                        }
                    }
                }

                // Bottom Action Bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.88f))
                        .padding(16.dp)
                ) {
                    // Dress Title Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(selectedDress.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Rs. ${selectedDress.priceRs} • ${selectedDress.storeSource}", color = PehnoGreenLight, fontSize = 11.sp)
                        }

                        // Try Another Outfit Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.DarkGray,
                            modifier = Modifier.clickable {
                                val nextIdx = (availableDresses.indexOfFirst { it.id == selectedDress.id } + 1) % availableDresses.size
                                selectedDress = availableDresses[nextIdx]
                            }
                        ) {
                            Text("Next Outfit ➔", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Save to Gallery Button
                        Button(
                            onClick = {
                                val finalBitmap = compositeBitmap ?: capturedBitmap ?: return@Button
                                val path = ClothingOverlayEngine.saveCompositeBitmap(context, finalBitmap)
                                if (path != null) {
                                    Toast.makeText(context, if (isUrdu) "تصویر گیلری میں محفوظ ہو گئی!" else "Try-On photo saved to Pictures!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PehnoGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(46.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isUrdu) "محفوظ کریں" else "Save Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Set as Avatar / Model Portrait
                        Button(
                            onClick = {
                                val finalBitmap = compositeBitmap ?: capturedBitmap ?: return@Button
                                viewModel.processAndSetCameraPortrait(context, finalBitmap)
                                Toast.makeText(context, if (isUrdu) "3D ماڈل پر لاگو کر دیا گیا!" else "Applied to 3D Try-On Model!", Toast.LENGTH_SHORT).show()
                                onClose()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PehnoWhite, contentColor = PehnoBlack),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(46.dp)
                        ) {
                            Icon(Icons.Default.Checkroom, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isUrdu) "ماڈل پر لگائیں" else "Apply Look", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
