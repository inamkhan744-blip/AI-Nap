package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FashionCatalog
import com.example.data.model.NearShop
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel

@Composable
fun NearMeScreen(viewModel: FashionViewModel) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val city = userProfile?.city ?: viewModel.formCity.collectAsState().value

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Barber & Salon", "Master Tailors", "Rent Shops", "Daraz Hub")

    val shops = remember(city) { FashionCatalog.nearShops }

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "📍 Near Me & Rent Shops",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = GoldLight
                            )
                            Text(
                                text = "$city: Barbers, Tailors, Sherwani & Lehenga Rentals",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(city, color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Daraz Affiliate Deal Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = BorderStroke(1.5.dp, BlueDaraz),
                    modifier = Modifier.fillMaxWidth().testTag("daraz_affiliate_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DARAZ AFFILIATE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PureWhite,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BlueDaraz)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ye Same Dress Daraz Pe", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Get Rs 4,500 direct delivery in $city (Save Rs 2,500 with promo AINAP50)",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.daraz.pk"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BlueDaraz, contentColor = PureWhite),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Buy on Daraz", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Map preview simulation
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().height(110.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = GoldLight, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Interactive Map of $city Active", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PureWhite)
                            Text("Showing 6 verified partners within 2 km radius", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
            }

            // Shop Cards
            items(shops) { shop ->
                NearShopItemCard(
                    shop = shop,
                    onBookOrContact = {
                        viewModel.userNotice.value = "Contacting ${shop.name} in $city..."
                    }
                )
            }
        }
    }
}

@Composable
fun NearShopItemCard(
    shop: NearShop,
    onBookOrContact: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkCard,
        border = BorderStroke(1.dp, DarkCardBorder),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().testTag("near_shop_${shop.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (shop.type.contains("Rent")) CrimsonAccent else GoldContainer)
                ) {
                    Icon(
                        imageVector = if (shop.type.contains("Rent")) Icons.Default.Storefront else Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = if (shop.type.contains("Rent")) PureWhite else GoldLight,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(shop.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PureWhite)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("★ ${shop.rating}", fontSize = 10.sp, color = GoldLight, fontWeight = FontWeight.Bold)
                    }
                    Text("${shop.type} • ${shop.distance} (${shop.address})", fontSize = 10.sp, color = TextMuted)
                    Text(shop.priceText, fontSize = 11.sp, color = EmeraldAccent, fontWeight = FontWeight.SemiBold)
                }
            }

            Button(
                onClick = onBookOrContact,
                colors = ButtonDefaults.buttonColors(containerColor = GoldContainer, contentColor = GoldLight),
                border = BorderStroke(1.dp, GoldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(if (shop.type.contains("Rent")) "Rent" else "Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
