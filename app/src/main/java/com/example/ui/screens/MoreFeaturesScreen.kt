package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FashionCatalog
import com.example.data.model.FashionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel

@Composable
fun MoreFeaturesScreen(viewModel: FashionViewModel) {
    val lang by viewModel.currentLanguage.collectAsState()
    val wishlist by viewModel.wishlistItems.collectAsState()
    var selectedSection by remember { mutableStateOf("home") }
    var pushNotifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    when (selectedSection) {
        "wishlist" -> WishlistSection(
            items = wishlist,
            lang = lang,
            onBack = { selectedSection = "home" },
            onRemove = { item -> viewModel.removeFromWishlist(item.id) },
            onTryOn = { item -> viewModel.selectedDress.value = item; selectedSection = "home" }
        )
        "settings" -> SettingsSection(
            lang = lang,
            pushNotifications = pushNotifications,
            darkMode = darkMode,
            onBack = { selectedSection = "home" },
            onTogglePush = { pushNotifications = it },
            onToggleDark = { darkMode = it },
            onLanguage = { viewModel.toggleLanguage() },
            onProfile = { viewModel.showProfileEdit.value = true }
        )
        else -> {
            val catalog = FashionCatalog.searchMultiStoreItems("", viewModel.formGender.value)
            MoreHome(
                lang = lang,
                wishlistCount = wishlist.size,
                onWishlist = { selectedSection = "wishlist" },
                onSettings = { selectedSection = "settings" },
                onAddRecommended = { item ->
                    viewModel.addToWishlist(item)
                },
                recommended = catalog.take(6)
            )
        }
    }
}

@Composable
private fun MoreHome(lang: String, wishlistCount: Int, onWishlist: () -> Unit, onSettings: () -> Unit, onAddRecommended: (FashionItem) -> Unit, recommended: List<FashionItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PehnoWhite),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(if (lang == "ur") "مزید سہولیات" else "More Features", fontSize = 24.sp, fontWeight = FontWeight.Black, color = PehnoBlack)
            Text(if (lang == "ur") "اپنی پسند محفوظ کریں اور ایپ کو اپنی مرضی کے مطابق بنائیں" else "Save your favourites and personalize the app", fontSize = 12.sp, color = PehnoTextSecondary)
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureTile(Icons.Default.Favorite, if (lang == "ur") "وشلسٹ" else "Wishlist", "$wishlistCount items", onWishlist, Modifier.weight(1f))
                FeatureTile(Icons.Default.Settings, if (lang == "ur") "سیٹنگز" else "Settings", if (lang == "ur") "ترجیحات" else "Preferences", onSettings, Modifier.weight(1f))
            }
        }
        item { Text(if (lang == "ur") "آپ کے لیے تجویز کردہ" else "Recommended for You", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PehnoBlack) }
        items(recommended, key = { it.id }) { item ->
            Surface(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, PehnoCardBorder), color = PehnoSurface) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PehnoBlack)
                        Text("Rs. ${item.priceRs} • ${item.storeSource}", fontSize = 11.sp, color = PehnoTextSecondary)
                    }
                    IconButton(onClick = { onAddRecommended(item) }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Add to wishlist", tint = PehnoGreenPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureTile(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit, modifier: Modifier) {
    Surface(modifier = modifier.clickable { onClick() }, shape = RoundedCornerShape(16.dp), color = PehnoGreenLight, border = BorderStroke(1.dp, PehnoGreenPrimary)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = PehnoGreenPrimary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Bold, color = PehnoBlack)
            Text(subtitle, fontSize = 11.sp, color = PehnoTextSecondary)
        }
    }
}

@Composable
private fun WishlistSection(items: List<FashionItem>, lang: String, onBack: () -> Unit, onRemove: (FashionItem) -> Unit, onTryOn: (FashionItem) -> Unit) {
    Column(Modifier.fillMaxSize().background(PehnoWhite)) {
        SectionHeader(if (lang == "ur") "میری وشلسٹ" else "My Wishlist", onBack)
        if (items.isEmpty()) {
            EmptySection(Icons.Default.FavoriteBorder, if (lang == "ur") "وشلسٹ خالی ہے" else "Your wishlist is empty")
        } else LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items, key = { it.id }) { item ->
                Surface(shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, PehnoCardBorder), color = PehnoSurface) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text(item.name, fontWeight = FontWeight.Bold, color = PehnoBlack); Text("Rs. ${item.priceRs}", color = PehnoGreenPrimary, fontSize = 12.sp) }
                        TextButton(onClick = { onTryOn(item) }) { Text(if (lang == "ur") "پہن کر دیکھیں" else "Try on") }
                        IconButton(onClick = { onRemove(item) }) { Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = Color.Red) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(lang: String, pushNotifications: Boolean, darkMode: Boolean, onBack: () -> Unit, onTogglePush: (Boolean) -> Unit, onToggleDark: (Boolean) -> Unit, onLanguage: () -> Unit, onProfile: () -> Unit) {
    Column(Modifier.fillMaxSize().background(PehnoWhite)) {
        SectionHeader(if (lang == "ur") "سیٹنگز" else "Settings", onBack)
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { SettingRow(Icons.Default.Person, if (lang == "ur") "پروفائل تبدیل کریں" else "Edit Profile", if (lang == "ur") "قد، وزن اور تصویر" else "Height, weight and photo", onProfile) }
            item { SettingRow(Icons.Default.Language, if (lang == "ur") "زبان" else "Language", if (lang == "ur") "اردو / English" else "Urdu / English", onLanguage) }
            item { SwitchRow(Icons.Default.Notifications, if (lang == "ur") "پش اطلاعات" else "Push Notifications", pushNotifications, onTogglePush) }
            item { SwitchRow(Icons.Default.DarkMode, if (lang == "ur") "ڈارک موڈ" else "Dark Mode", darkMode, onToggleDark) }
            item { SettingRow(Icons.AutoMirrored.Filled.HelpOutline, if (lang == "ur") "مدد اور سپورٹ" else "Help & Support", "Pehno v1.0", {}) }
            item { SettingRow(Icons.Default.PrivacyTip, if (lang == "ur") "رازداری" else "Privacy", if (lang == "ur") "آپ کا ڈیٹا ڈیوائس پر محفوظ ہے" else "Your profile is stored on this device", {}) }
        }
    }
}

@Composable private fun SectionHeader(title: String, onBack: () -> Unit) { Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }; Text(title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = PehnoBlack) } }
@Composable private fun EmptySection(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) { Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Icon(icon, null, Modifier.size(64.dp), tint = PehnoCardBorder); Spacer(Modifier.height(12.dp)); Text(text, color = PehnoTextSecondary) } }
@Composable private fun SettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) { Surface(Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(12.dp), color = PehnoSurface) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = PehnoGreenPrimary); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold, color = PehnoBlack); Text(subtitle, fontSize = 11.sp, color = PehnoTextSecondary) }; Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = PehnoTextSecondary) } } }
@Composable private fun SwitchRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) { Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = PehnoSurface) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = PehnoGreenPrimary); Spacer(Modifier.width(12.dp)); Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold, color = PehnoBlack); Switch(checked, onCheckedChange) } } }
