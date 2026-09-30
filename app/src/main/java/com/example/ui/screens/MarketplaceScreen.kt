package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.SellerDressEntity
import com.example.data.model.FashionCatalog
import com.example.data.model.FashionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import com.example.util.PehnoStrings

@Composable
fun MarketplaceScreen(
    viewModel: FashionViewModel,
    onNavigateToTryOn: () -> Unit,
    onOpenSellForm: () -> Unit
) {
    val context = LocalContext.current
    val lang by viewModel.currentLanguage.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val gender = userProfile?.gender ?: viewModel.formGender.collectAsState().value

    var searchQuery by remember { mutableStateOf("") }
    val sellerDresses by viewModel.sellerDresses.collectAsState()

    // Multi-store results matching gender & search
    val storeItems = remember(searchQuery, gender) {
        FashionCatalog.searchMultiStoreItems(searchQuery, gender)
    }

    // Filter seller dresses matching gender & search
    val filteredSellerDresses = remember(sellerDresses, searchQuery, gender) {
        sellerDresses.filter { sellerItem ->
            val matchGender = sellerItem.gender.equals("Both", ignoreCase = true) || sellerItem.gender.equals(gender, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                sellerItem.dressName.contains(searchQuery, ignoreCase = true) ||
                sellerItem.shopName.contains(searchQuery, ignoreCase = true) ||
                sellerItem.city.contains(searchQuery, ignoreCase = true)
            matchGender && matchQuery
        }
    }

    Scaffold(
        containerColor = PehnoWhite,
        floatingActionButton = {
            // Big Floating Button: [+ Apna Jora Becho / Sell Your Dress]
            ExtendedFloatingActionButton(
                onClick = onOpenSellForm,
                containerColor = PehnoGreenPrimary,
                contentColor = PehnoWhite,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .height(56.dp)
                    .testTag("floating_sell_btn")
            ) {
                Icon(imageVector = Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = PehnoStrings.t("sell_floating", lang),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Header Info & Commission Notice
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = PehnoStrings.t("all_pakistan_marketplace", lang),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = PehnoBlack
                    )
                    Text(
                        text = PehnoStrings.t("multi_store_sub", lang),
                        fontSize = 11.sp,
                        color = PehnoTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PehnoGreenLight,
                    border = BorderStroke(1.dp, PehnoGreenPrimary)
                ) {
                    Text(
                        text = "10% Commission",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoGreenDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BIG SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = PehnoStrings.t("search_hint", lang),
                        color = PehnoTextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = PehnoGreenPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("marketplace_search_bar"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PehnoGreenPrimary,
                    unfocusedBorderColor = PehnoCardBorder,
                    focusedContainerColor = PehnoSurface,
                    unfocusedContainerColor = PehnoSurface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Total results count banner
            val totalItemsCount = storeItems.size + filteredSellerDresses.size
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$totalItemsCount Real Dresses across Pakistan",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PehnoGreenPrimary
                )
                Text(
                    text = "Showing $gender wear",
                    fontSize = 11.sp,
                    color = PehnoTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // GRID OF REAL DRESS CARDS (Store items + Seller items)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // 1. Seller Uploaded Dresses (Local Pakistan Sellers)
                items(filteredSellerDresses, key = { "seller_${it.id}" }) { sellerItem ->
                    val syntheticItem = remember(sellerItem) {
                        FashionItem(
                            id = "seller_${sellerItem.id}",
                            name = sellerItem.dressName,
                            urduName = sellerItem.dressName,
                            gender = sellerItem.gender,
                            description = "From ${sellerItem.shopName}, ${sellerItem.city}",
                            priceRs = sellerItem.priceRs,
                            frontImageUrl = sellerItem.frontPhotoUrl,
                            backImageUrl = sellerItem.backPhotoUrl,
                            storeSource = "🇵🇰 ${sellerItem.shopName} (${sellerItem.city})",
                            whatsappNumber = sellerItem.whatsappNumber,
                            isSellerItem = true
                        )
                    }

                    SellerDressCard(
                        sellerItem = sellerItem,
                        lang = lang,
                        onTryOn = {
                            viewModel.selectedDress.value = syntheticItem
                            onNavigateToTryOn()
                        },
                        onOrderWhatsApp = {
                            val message = "Assalam-o-Alaikum! I want to order your dress from Pehno App: ${sellerItem.dressName} for Rs ${sellerItem.priceRs}"
                            val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${sellerItem.whatsappNumber}?text=${Uri.encode(message)}"))
                            context.startActivity(waIntent)
                        },
                        onDownloadLook = {
                            viewModel.exportDressPng(syntheticItem) { success, _ ->
                                viewModel.userNotice.value = if (success) {
                                    if (lang == "ur") "تصویر محفوظ ہو گئی!" else "Dress PNG saved to Pictures!"
                                } else {
                                    if (lang == "ur") "تصویر محفوظ کرنے میں خرابی" else "Failed to save image"
                                }
                            }
                        }
                    )
                }

                // 2. Multi-Store Online Dresses (Daraz, Khaadi, Sana Collection, etc.)
                items(storeItems, key = { it.id }) { item ->
                    StoreDressCard(
                        item = item,
                        lang = lang,
                        onTryOn = {
                            viewModel.selectedDress.value = item
                            onNavigateToTryOn()
                        },
                        onOrderStore = {
                            val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.darazLink))
                            context.startActivity(storeIntent)
                        },
                        onDownloadLook = {
                            viewModel.exportDressPng(item) { success, _ ->
                                viewModel.userNotice.value = if (success) {
                                    if (lang == "ur") "تصویر محفوظ ہو گئی!" else "Dress PNG saved to Pictures!"
                                } else {
                                    if (lang == "ur") "تصویر محفوظ کرنے میں خرابی" else "Failed to save image"
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StoreDressCard(
    item: FashionItem,
    lang: String,
    onTryOn: () -> Unit,
    onOrderStore: () -> Unit,
    onDownloadLook: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PehnoWhite,
        border = BorderStroke(1.dp, PehnoCardBorder),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("store_card_${item.id}")
    ) {
        Column {
            // Real Model Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                if (item.frontImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.frontImageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(item.drawableResId.takeIf { it != 0 } ?: R.drawable.model_black_shalwar_male),
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Multi-Store Badge on Photo
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PehnoBlack.copy(alpha = 0.85f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.storeSource,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Info & Buttons
            Column(modifier = Modifier.padding(10.dp)) {
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rs. ${item.priceRs}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = PehnoGreenPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Try On Button
                Button(
                    onClick = onTryOn,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PehnoGreenLight,
                        contentColor = PehnoGreenPrimary
                    ),
                    border = BorderStroke(1.dp, PehnoGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .testTag("try_dress_${item.id}")
                ) {
                    Icon(imageVector = Icons.Default.Checkroom, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = PehnoStrings.t("try_this", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Button 1: Download Final Look
                OutlinedButton(
                    onClick = onDownloadLook,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = PehnoWhite,
                        contentColor = PehnoBlack
                    ),
                    border = BorderStroke(1.dp, PehnoBlack),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .testTag("download_dress_${item.id}")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = PehnoStrings.t("download_look", lang), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Button 2: Order Now Button
                Button(
                    onClick = onOrderStore,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PehnoGreenPrimary,
                        contentColor = PehnoWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("order_dress_${item.id}")
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = PehnoStrings.t("order_now", lang), fontSize = 11.sp, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = PehnoStrings.t("commission_notice", lang),
                    fontSize = 8.sp,
                    color = PehnoGreenDark,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SellerDressCard(
    sellerItem: SellerDressEntity,
    lang: String,
    onTryOn: () -> Unit,
    onOrderWhatsApp: () -> Unit,
    onDownloadLook: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PehnoWhite,
        border = BorderStroke(1.5.dp, PehnoGreenPrimary),
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("seller_card_${sellerItem.id}")
    ) {
        Column {
            // Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                if (sellerItem.frontPhotoUrl.isNotBlank()) {
                    AsyncImage(
                        model = sellerItem.frontPhotoUrl,
                        contentDescription = sellerItem.dressName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.model_black_shalwar_male),
                        contentDescription = sellerItem.dressName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Verified Pakistan Seller Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PehnoGreenPrimary)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🇵🇰 ${sellerItem.shopName} (${sellerItem.city})",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PehnoWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Info & Buttons
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = sellerItem.dressName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PehnoBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Shop: ${sellerItem.shopName}",
                    fontSize = 10.sp,
                    color = PehnoTextSecondary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rs. ${sellerItem.priceRs}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = PehnoGreenPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Try On Button
                Button(
                    onClick = onTryOn,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PehnoGreenLight,
                        contentColor = PehnoGreenPrimary
                    ),
                    border = BorderStroke(1.dp, PehnoGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .testTag("try_seller_dress_${sellerItem.id}")
                ) {
                    Icon(imageVector = Icons.Default.Checkroom, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = PehnoStrings.t("try_this", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Button 1: Download Final Look
                OutlinedButton(
                    onClick = onDownloadLook,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = PehnoWhite,
                        contentColor = PehnoBlack
                    ),
                    border = BorderStroke(1.dp, PehnoBlack),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .testTag("download_seller_dress_${sellerItem.id}")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = PehnoStrings.t("download_look", lang), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Button 2: WhatsApp Order Button
                Button(
                    onClick = onOrderWhatsApp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PehnoWhatsApp,
                        contentColor = PehnoWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("whatsapp_seller_dress_${sellerItem.id}")
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = PehnoStrings.t("order_now", lang), fontSize = 11.sp, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = PehnoStrings.t("commission_notice", lang),
                    fontSize = 8.sp,
                    color = PehnoGreenDark,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
