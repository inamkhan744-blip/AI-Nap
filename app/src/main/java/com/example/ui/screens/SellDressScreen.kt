package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import com.example.util.PehnoStrings

@Composable
fun SellDressScreen(
    viewModel: FashionViewModel,
    onDressPublished: () -> Unit = {}
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var shopName by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("Lahore") }
    var dressName by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("4500") }
    var whatsappNumber by remember { mutableStateOf("03001234567") }
    var dressGender by remember { mutableStateOf(userProfile?.gender ?: "Male") }

    var frontPhotoUri by remember {
        mutableStateOf(
            if (dressGender == "Female")
                "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80"
            else
                "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?auto=format&fit=crop&w=800&q=80"
        )
    }
    var backPhotoUri by remember {
        mutableStateOf(
            if (dressGender == "Female")
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80"
            else
                "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80"
        )
    }

    // Photo pickers
    val frontPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { frontPhotoUri = it.toString() }
    }

    val backPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { backPhotoUri = it.toString() }
    }

    val cities = listOf("Lahore", "Karachi", "Quetta", "Islamabad", "Peshawar", "Multan", "Faisalabad", "Rawalpindi")
    var cityExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PehnoWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PehnoGreenLight)
                ) {
                    Icon(imageVector = Icons.Default.AddBusiness, contentDescription = null, tint = PehnoGreenPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = PehnoStrings.t("sell_title", lang),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PehnoBlack
                    )
                    Text(
                        text = PehnoStrings.t("sell_sub", lang),
                        fontSize = 11.sp,
                        color = PehnoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Shop Name
            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text(PehnoStrings.t("shop_name", lang)) },
                placeholder = { Text("e.g. Sana Bridal Studio / Al-Rehman Kurta") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PehnoGreenPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_shop_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Dress Title
            OutlinedTextField(
                value = dressName,
                onValueChange = { dressName = it },
                label = { Text(PehnoStrings.t("dress_name", lang)) },
                placeholder = { Text("e.g. Black Velvet Shalwar Kameez / Party Kurti") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PehnoGreenPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_dress_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. City Selection & Gender
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // City dropdown
                Box(modifier = Modifier.weight(1.2f)) {
                    OutlinedTextField(
                        value = selectedCity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(PehnoStrings.t("city_label", lang)) },
                        trailingIcon = {
                            IconButton(onClick = { cityExpanded = !cityExpanded }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select City")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PehnoGreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { cityExpanded = true }
                    )
                    DropdownMenu(
                        expanded = cityExpanded,
                        onDismissRequest = { cityExpanded = false }
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city) },
                                onClick = {
                                    selectedCity = city
                                    cityExpanded = false
                                }
                            )
                        }
                    }
                }

                // Gender Toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PehnoSurface,
                    border = BorderStroke(1.dp, PehnoCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable {
                            dressGender = if (dressGender == "Male") "Female" else "Male"
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (dressGender == "Male") "👨 Male Wear" else "👩 Female Wear",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PehnoGreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Price & WhatsApp Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(PehnoStrings.t("price_label", lang)) },
                    placeholder = { Text("4500") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PehnoGreenPrimary),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = whatsappNumber,
                    onValueChange = { whatsappNumber = it },
                    label = { Text(PehnoStrings.t("whatsapp_label", lang)) },
                    placeholder = { Text("03001234567") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PehnoGreenPrimary),
                    modifier = Modifier.weight(1.3f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Dress Front Photo Upload
            Text(
                text = PehnoStrings.t("front_photo", lang),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = PehnoBlack,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PehnoSurface,
                border = BorderStroke(1.dp, PehnoCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable { frontPickerLauncher.launch("image/*") }
            ) {
                if (frontPhotoUri.isNotBlank()) {
                    AsyncImage(
                        model = frontPhotoUri,
                        contentDescription = "Front Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = PehnoGreenPrimary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Tap to upload front dress photo", fontSize = 12.sp, color = PehnoTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Dress Back Photo Upload
            Text(
                text = PehnoStrings.t("back_photo", lang),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = PehnoBlack,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PehnoSurface,
                border = BorderStroke(1.dp, PehnoCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable { backPickerLauncher.launch("image/*") }
            ) {
                if (!backPhotoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = backPhotoUri,
                        contentDescription = "Back Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.FlipCameraAndroid, contentDescription = null, tint = PehnoGreenPrimary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Tap to upload back view of dress", fontSize = 12.sp, color = PehnoTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 7. BIG SUBMIT BUTTON
            Button(
                onClick = {
                    val price = priceText.toIntOrNull() ?: 4500
                    viewModel.publishSellerDress(
                        shopName = shopName.ifBlank { "Pakistan Boutique" },
                        city = selectedCity,
                        dressName = dressName.ifBlank { "Traditional Pakistani Suit" },
                        frontUrl = frontPhotoUri,
                        backUrl = backPhotoUri,
                        priceRs = price,
                        whatsappNumber = whatsappNumber.ifBlank { "03001234567" },
                        gender = dressGender
                    )
                    onDressPublished()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PehnoGreenPrimary,
                    contentColor = PehnoWhite
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("submit_seller_dress_btn")
            ) {
                Icon(imageVector = Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = PehnoStrings.t("submit_dress", lang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Notice: 10% commission on confirmed orders goes to Pehno App",
                fontSize = 10.sp,
                color = PehnoTextMuted
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
