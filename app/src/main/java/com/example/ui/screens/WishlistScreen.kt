package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FashionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import com.example.util.PehnoStrings

@Composable
fun WishlistScreen(
    viewModel: FashionViewModel,
    onNavigateToMarketplace: () -> Unit = {}
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(PehnoWhite)) {
        if (wishlistItems.isEmpty()) {
            // Empty Wishlist State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = PehnoCardBorder
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (lang == "ur") "آپ کی وشلسٹ خالی ہے" else "Your Wishlist is Empty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PehnoBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (lang == "ur") "مارکیٹ پلیس میں جائیں اور اپنی پسندیدہ چیزیں شامل کریں" else "Visit Marketplace to add your favorite items",
                    fontSize = 14.sp,
                    color = PehnoTextSecondary
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToMarketplace,
                    colors = ButtonDefaults.buttonColors(containerColor = PehnoGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == "ur") "مارکیٹ پلیس دیکھیں" else "Browse Marketplace",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Wishlist Grid
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    color = PehnoGreenLight,
                    border = BorderStroke(1.dp, PehnoGreenPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (lang == "ur") "میری وشلسٹ" else "My Wishlist",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoGreenDark
                            )
                            Text(
                                text = "${wishlistItems.size} ${if (lang == "ur") "اشیاء" else "items"}",
                                fontSize = 12.sp,
                                color = PehnoTextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(wishlistItems) { item ->
                        WishlistItemCard(
                            item = item,
                            lang = lang,
                            onRemove = { viewModel.removeFromWishlist(item.id) },
                            onSelect = { viewModel.selectItemLayer(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistItemCard(
    item: FashionItem,
    lang: String,
    onRemove: () -> Unit,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, PehnoCardBorder),
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(PehnoSurface),
                contentAlignment = Alignment.Center
            ) {
                if (!item.frontImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.frontImageUrl,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = PehnoCardBorder
                    )
                }

                // Remove Heart Button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove",
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Info
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PehnoBlack,
                    maxLines = 1
                )
                Text(
                    text = item.urduName,
                    fontSize = 10.sp,
                    color = PehnoTextSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Rs. ${item.priceRs}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = PehnoGreenPrimary
                )
                Text(
                    text = item.storeSource,
                    fontSize = 9.sp,
                    color = PehnoTextSecondary
                )
            }
        }
    }
}
