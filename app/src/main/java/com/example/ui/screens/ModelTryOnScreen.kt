package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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

    // Weight & Height model auto-scaling
    val widthScale = (0.90f + (weightKg - 40f) / 160f).coerceIn(0.85f, 1.25f)
    val shoeBoost = shoes?.heightBoostInches ?: 0f
    val effectiveHeight = heightFt + (shoeBoost / 12f)

    Box(modifier = Modifier.fillMaxSize().background(PehnoWhite)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for bottom category bar
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar Commission & Quick Order / Download Banner
            Surface(
                color = PehnoGreenLight,
                border = BorderStroke(1.dp, PehnoGreenPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = PehnoGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = PehnoStrings.t("commission_notice", lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PehnoGreenDark
                        )
                    }

                    Text(
                        text = "${"%.1f".format(effectiveHeight)}ft (+${shoeBoost.toInt()}\")",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. CENTER 3D/2D HUMAN MODEL VIEWPORT
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
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

                    // Layer 2: Top subtle gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.35f),
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
                            text = dress?.storeSource ?: "All Pakistan Store",
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
                            Icon(imageVector = Icons.Default.RotateRight, contentDescription = null, tint = PehnoGreenPrimary, modifier = Modifier.size(20.dp))
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

            // 3. LAYER SUMMARY PILLS (What is currently on model)
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
