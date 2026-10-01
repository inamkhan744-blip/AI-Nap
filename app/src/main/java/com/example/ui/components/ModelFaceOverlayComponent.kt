package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ui.theme.PehnoBlack
import com.example.ui.theme.PehnoGreenLight
import com.example.ui.theme.PehnoGreenPrimary
import com.example.ui.theme.PehnoSurface
import com.example.ui.theme.PehnoWhite
import com.example.util.FaceOverlayService

/**
 * Composable component that uses the device camera to capture a user's portrait image,
 * processes it via FaceOverlayService, and overlays it onto the 3D model head,
 * ensuring dynamic scaling and alignment based on the user's height and weight settings.
 */
@Composable
fun ModelFaceOverlayComponent(
    userPhotoUri: String?,
    heightFt: Float,
    weightKg: Float,
    gender: String,
    isBackFacing: Boolean,
    onPhotoCaptured: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFaceClicked: () -> Unit = {}
) {
    val context = LocalContext.current

    // Dynamically calculate head dimensions and vertical offset based on user profile's height and weight
    val alignment = remember(heightFt, weightKg, gender) {
        FaceOverlayService.calculateHeadAlignment(heightFt, weightKg, gender)
    }

    // Camera Launcher for capturing portrait
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
            )
            if (processedPath != null) {
                onPhotoCaptured(processedPath)
            } else {
                Toast.makeText(context, "Could not process portrait", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Permission launcher for Camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission required to capture portrait", Toast.LENGTH_SHORT).show()
        }
    }

    val launchCamera = {
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

    // When facing front on 3D rotation, show face overlay or camera capture prompt
    if (!isBackFacing) {
        if (!userPhotoUri.isNullOrBlank()) {
            Box(
                modifier = modifier
                    .padding(top = alignment.topPaddingDp)
                    .size(width = alignment.headWidthDp, height = alignment.headHeightDp)
                    .clip(RoundedCornerShape(alignment.cornerRadiusDp))
                    .background(PehnoSurface)
                    .border(alignment.borderWidthDp, PehnoGreenPrimary, RoundedCornerShape(alignment.cornerRadiusDp))
                    .clickable { onFaceClicked() }
                    .testTag("model_face_overlay")
            ) {
                AsyncImage(
                    model = userPhotoUri,
                    contentDescription = "User Face on 3D Model",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtitle indicator badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(PehnoBlack.copy(alpha = 0.72f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "آپ کا چہرہ / You",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoWhite
                    )
                }
            }
        } else {
            // Interactive camera capture trigger on head location
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PehnoBlack.copy(alpha = 0.75f),
                border = BorderStroke(1.5.dp, PehnoGreenPrimary),
                modifier = modifier
                    .padding(top = alignment.topPaddingDp + 10.dp)
                    .clickable { launchCamera() }
                    .testTag("model_face_prompt_capture")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                        text = "پورٹریٹ لیں (Capture Portrait)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoWhite
                    )
                }
            }
        }
    }
}

/**
 * Dedicated button component to capture user portrait from camera with image processing
 * and instant mapping onto the 3D model head.
 */
@Composable
fun CapturePortraitButton(
    heightFt: Float,
    weightKg: Float,
    gender: String,
    onPhotoCaptured: (String) -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Capture Portrait"
) {
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
            )
            if (processedPath != null) {
                onPhotoCaptured(processedPath)
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission required to capture portrait", Toast.LENGTH_SHORT).show()
        }
    }

    Button(
        onClick = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) {
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
        modifier = modifier.testTag("capture_portrait_btn")
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Capture Portrait",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
