package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.OrderDressDialog
import com.example.ui.components.StyleAdvisorModalDialog
import com.example.ui.components.VirtualTryOnDialog
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.ModelTryOnScreen
import com.example.ui.screens.MoreFeaturesScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.SellDressScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import com.example.util.PehnoStrings

enum class PehnoMainTab {
    TRY_ON,
    MARKETPLACE,
    WISHLIST,
    SELL_DRESS,
    MORE
}

class MainActivity : ComponentActivity() {

    private val viewModel: FashionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val lang by viewModel.currentLanguage.collectAsState()
            val userProfile by viewModel.userProfile.collectAsState()
            val userNotice by viewModel.userNotice.collectAsState()
            val showProfileEdit by viewModel.showProfileEdit.collectAsState()
            val wishlistItems by viewModel.wishlistItems.collectAsState()
            val showOrderDialog by viewModel.showOrderDialog.collectAsState()
            val activeOrderDress by viewModel.activeOrderDress.collectAsState()
            val showStyleAdvisorModal by viewModel.showStyleAdvisorModal.collectAsState()
            val showVirtualTryOnModal by viewModel.showVirtualTryOnModal.collectAsState()

            var currentTab by remember { mutableStateOf(PehnoMainTab.TRY_ON) }
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(userNotice) {
                userNotice?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.userNotice.value = null
                }
            }

            val isSetupComplete = userProfile?.isSetupComplete ?: false

            FitLookStudioTheme {
                if (!isSetupComplete || showProfileEdit) {
                    ProfileSetupScreen(
                        viewModel = viewModel,
                        isEditing = isSetupComplete,
                        onCompleted = {
                            viewModel.showProfileEdit.value = false
                        }
                    )
                } else {
                    if (currentTab != PehnoMainTab.TRY_ON) {
                        BackHandler {
                            currentTab = PehnoMainTab.TRY_ON
                        }
                    }

                    Scaffold(
                        containerColor = PehnoWhite,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            Surface(
                                color = PehnoWhite,
                                border = BorderStroke(1.dp, PehnoCardBorder),
                                shadowElevation = 3.dp,
                                modifier = Modifier.fillMaxWidth().statusBarsPadding()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // App Brand & Tagline
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { currentTab = PehnoMainTab.TRY_ON }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(PehnoGreenPrimary)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Checkroom,
                                                contentDescription = "Pehno",
                                                tint = PehnoWhite,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = PehnoStrings.t("app_name", lang),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 20.sp,
                                                color = PehnoGreenPrimary
                                            )
                                            Text(
                                                text = PehnoStrings.t("tagline", lang),
                                                fontSize = 10.sp,
                                                color = PehnoTextSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Action buttons: AI Stylist + Language toggle + Profile Edit icon
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // AI Style Advisor Button
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = PehnoGreenPrimary,
                                            modifier = Modifier
                                                .clickable { viewModel.openStyleAdvisor() }
                                                .testTag("top_bar_ai_stylist_btn")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = "AI Stylist",
                                                    tint = PehnoWhite,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (lang == "ur") "AI مشورہ" else "AI Stylist",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = PehnoWhite
                                                )
                                            }
                                        }

                                        // Language Switcher (Urdu <-> English)
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = PehnoGreenLight,
                                            border = BorderStroke(1.dp, PehnoGreenPrimary),
                                            modifier = Modifier.clickable { viewModel.toggleLanguage() }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Language,
                                                    contentDescription = null,
                                                    tint = PehnoGreenPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (lang == "ur") "English" else "اردو",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = PehnoGreenPrimary
                                                )
                                            }
                                        }

                                        // Profile Edit Icon
                                        IconButton(
                                            onClick = { viewModel.showProfileEdit.value = true },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(PehnoSurface)
                                                .border(1.dp, PehnoCardBorder, CircleShape)
                                                .testTag("edit_profile_icon_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Edit Profile",
                                                tint = PehnoBlack,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = PehnoWhite,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("pehno_bottom_nav")
                            ) {
                                // 1. Model Try-On Tab
                                NavigationBarItem(
                                    selected = currentTab == PehnoMainTab.TRY_ON,
                                    onClick = { currentTab = PehnoMainTab.TRY_ON },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Checkroom,
                                            contentDescription = "Model Try-On",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = PehnoStrings.t("tab_tryon", lang),
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == PehnoMainTab.TRY_ON) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PehnoGreenPrimary,
                                        selectedTextColor = PehnoGreenPrimary,
                                        unselectedIconColor = PehnoTextSecondary,
                                        unselectedTextColor = PehnoTextSecondary,
                                        indicatorColor = PehnoGreenLight
                                    ),
                                    modifier = Modifier.testTag("tab_nav_tryon")
                                )

                                // 2. Marketplace Tab
                                NavigationBarItem(
                                    selected = currentTab == PehnoMainTab.MARKETPLACE,
                                    onClick = { currentTab = PehnoMainTab.MARKETPLACE },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Storefront,
                                            contentDescription = "Marketplace",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = PehnoStrings.t("tab_marketplace", lang),
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == PehnoMainTab.MARKETPLACE) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PehnoGreenPrimary,
                                        selectedTextColor = PehnoGreenPrimary,
                                        unselectedIconColor = PehnoTextSecondary,
                                        unselectedTextColor = PehnoTextSecondary,
                                        indicatorColor = PehnoGreenLight
                                    ),
                                    modifier = Modifier.testTag("tab_nav_marketplace")
                                )

                                // 3. Wishlist Tab (With Badge)
                                NavigationBarItem(
                                    selected = currentTab == PehnoMainTab.WISHLIST,
                                    onClick = { currentTab = PehnoMainTab.WISHLIST },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (wishlistItems.isNotEmpty()) {
                                                    Badge(containerColor = PehnoGreenPrimary, contentColor = PehnoWhite) {
                                                        Text("${wishlistItems.size}", fontSize = 9.sp)
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = "Wishlist",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = PehnoStrings.t("tab_wishlist", lang),
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == PehnoMainTab.WISHLIST) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PehnoGreenPrimary,
                                        selectedTextColor = PehnoGreenPrimary,
                                        unselectedIconColor = PehnoTextSecondary,
                                        unselectedTextColor = PehnoTextSecondary,
                                        indicatorColor = PehnoGreenLight
                                    ),
                                    modifier = Modifier.testTag("tab_nav_wishlist")
                                )

                                // 4. Sell Dress Tab
                                NavigationBarItem(
                                    selected = currentTab == PehnoMainTab.SELL_DRESS,
                                    onClick = { currentTab = PehnoMainTab.SELL_DRESS },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.AddBusiness,
                                            contentDescription = "Sell Dress",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = PehnoStrings.t("tab_sell", lang),
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == PehnoMainTab.SELL_DRESS) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PehnoGreenPrimary,
                                        selectedTextColor = PehnoGreenPrimary,
                                        unselectedIconColor = PehnoTextSecondary,
                                        unselectedTextColor = PehnoTextSecondary,
                                        indicatorColor = PehnoGreenLight
                                    ),
                                    modifier = Modifier.testTag("tab_nav_sell")
                                )

                                // 5. More Features Tab
                                NavigationBarItem(
                                    selected = currentTab == PehnoMainTab.MORE,
                                    onClick = { currentTab = PehnoMainTab.MORE },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = "More Features",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = PehnoStrings.t("tab_more", lang),
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == PehnoMainTab.MORE) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PehnoGreenPrimary,
                                        selectedTextColor = PehnoGreenPrimary,
                                        unselectedIconColor = PehnoTextSecondary,
                                        unselectedTextColor = PehnoTextSecondary,
                                        indicatorColor = PehnoGreenLight
                                    ),
                                    modifier = Modifier.testTag("tab_nav_more")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                PehnoMainTab.TRY_ON -> ModelTryOnScreen(
                                    viewModel = viewModel,
                                    onNavigateToMarketplace = { currentTab = PehnoMainTab.MARKETPLACE }
                                )
                                PehnoMainTab.MARKETPLACE -> MarketplaceScreen(
                                    viewModel = viewModel,
                                    onNavigateToTryOn = { currentTab = PehnoMainTab.TRY_ON },
                                    onOpenSellForm = { currentTab = PehnoMainTab.SELL_DRESS }
                                )
                                PehnoMainTab.WISHLIST -> WishlistScreen(
                                    viewModel = viewModel,
                                    onNavigateToMarketplace = { currentTab = PehnoMainTab.MARKETPLACE },
                                    onNavigateToTryOn = { currentTab = PehnoMainTab.TRY_ON }
                                )
                                PehnoMainTab.SELL_DRESS -> SellDressScreen(
                                    viewModel = viewModel,
                                    onDressPublished = { currentTab = PehnoMainTab.MARKETPLACE }
                                )
                                PehnoMainTab.MORE -> MoreFeaturesScreen(
                                    viewModel = viewModel,
                                    onNavigateToTryOn = { currentTab = PehnoMainTab.TRY_ON }
                                )
                            }

                            // Order Dialog Overlay
                            if (showOrderDialog && activeOrderDress != null) {
                                OrderDressDialog(
                                    item = activeOrderDress!!,
                                    viewModel = viewModel,
                                    onDismiss = { viewModel.showOrderDialog.value = false }
                                )
                            }

                            // Style Advisor Modal Overlay
                            if (showStyleAdvisorModal) {
                                StyleAdvisorModalDialog(
                                    viewModel = viewModel,
                                    onDismiss = { viewModel.closeStyleAdvisor() },
                                    onSelectDressToTryOn = {
                                        currentTab = PehnoMainTab.TRY_ON
                                    }
                                )
                            }

                            // CameraX Virtual Try-On Dialog Overlay
                            if (showVirtualTryOnModal) {
                                VirtualTryOnDialog(
                                    viewModel = viewModel,
                                    onDismiss = { viewModel.closeVirtualTryOn() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
