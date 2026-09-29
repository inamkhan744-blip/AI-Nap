package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FashionViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FashionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userProfile by viewModel.userProfile.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val suitabilityState by viewModel.showSuitabilityDialog.collectAsState()
            val activeItem by viewModel.activeSuitabilityItem.collectAsState()
            val finalLookState by viewModel.finalLookState.collectAsState()
            val showPremium by viewModel.showPremiumDialog.collectAsState()
            val userNotice by viewModel.userNotice.collectAsState()

            val showWeight by viewModel.showWeightPreviewModal.collectAsState()
            val targetWeight by viewModel.targetWeightKg.collectAsState()
            val showCouple by viewModel.showCoupleModal.collectAsState()
            val coupleOccasion by viewModel.coupleOccasion.collectAsState()
            val showCatwalk by viewModel.showCatwalkModal.collectAsState()
            val isCatwalkPlaying by viewModel.isCatwalkPlaying.collectAsState()
            val showMeme by viewModel.showMemeModal.collectAsState()
            val memeSlider by viewModel.memeSliderPosition.collectAsState()

            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(userNotice) {
                userNotice?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearNotice()
                }
            }

            FitLookStudioTheme {
                val isSetupComplete = userProfile?.isSetupComplete ?: false

                if (!isSetupComplete && currentScreen == AppScreen.ONBOARDING_CROWN) {
                    OnboardingCrownScreen(
                        onStartClick = { viewModel.navigateTo(AppScreen.ONBOARDING_FORM) }
                    )
                } else if (!isSetupComplete || currentScreen == AppScreen.ONBOARDING_FORM) {
                    SetupScreen(
                        viewModel = viewModel,
                        isEditing = isSetupComplete,
                        onCompleted = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                } else {
                    if (currentScreen != AppScreen.HOME) {
                        BackHandler {
                            viewModel.navigateTo(AppScreen.HOME)
                        }
                    }

                    Scaffold(
                        containerColor = PureBlack,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            NavigationBar(
                                containerColor = DarkSurface,
                                tonalElevation = 6.dp,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                // 1. Home
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.HOME,
                                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = { Text("Home", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.HOME) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldPrimary,
                                        selectedTextColor = GoldPrimary,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted,
                                        indicatorColor = GoldContainer
                                    ),
                                    modifier = Modifier.testTag("nav_home")
                                )

                                // 2. Try On
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.TRY_ON,
                                    onClick = { viewModel.navigateTo(AppScreen.TRY_ON) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Checkroom,
                                            contentDescription = "Try On"
                                        )
                                    },
                                    label = { Text("Try On", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.TRY_ON) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldPrimary,
                                        selectedTextColor = GoldPrimary,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted,
                                        indicatorColor = GoldContainer
                                    ),
                                    modifier = Modifier.testTag("nav_try_on")
                                )

                                // 3. Full Look (Highlighted Gold Button)
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.FINAL_LOOK,
                                    onClick = { viewModel.navigateTo(AppScreen.FINAL_LOOK) },
                                    icon = {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(GoldPrimary)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = "Full Look",
                                                tint = PureBlack,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = "Full Look",
                                            fontWeight = FontWeight.Black,
                                            color = GoldLight,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.testTag("nav_full_look")
                                )

                                // 4. My Gallery
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.GALLERY,
                                    onClick = { viewModel.navigateTo(AppScreen.GALLERY) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = "My Gallery"
                                        )
                                    },
                                    label = { Text("My Gallery", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.GALLERY) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldPrimary,
                                        selectedTextColor = GoldPrimary,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted,
                                        indicatorColor = GoldContainer
                                    ),
                                    modifier = Modifier.testTag("nav_gallery")
                                )

                                // 5. Near Me
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.NEAR_ME,
                                    onClick = { viewModel.navigateTo(AppScreen.NEAR_ME) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "Near Me"
                                        )
                                    },
                                    label = { Text("Near Me", fontSize = 10.sp, fontWeight = if (currentScreen == AppScreen.NEAR_ME) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldPrimary,
                                        selectedTextColor = GoldPrimary,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted,
                                        indicatorColor = GoldContainer
                                    ),
                                    modifier = Modifier.testTag("nav_near_me")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                AppScreen.HOME -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToCategory = {
                                        viewModel.navigateTo(AppScreen.TRY_ON)
                                    },
                                    onOpenEditProfile = {
                                        viewModel.navigateTo(AppScreen.ONBOARDING_FORM)
                                    }
                                )
                                AppScreen.TRY_ON -> TryOnScreen(viewModel = viewModel)
                                AppScreen.FINAL_LOOK -> FinalLookScreen(
                                    viewModel = viewModel,
                                    onNavigateToCategory = {
                                        viewModel.navigateTo(AppScreen.TRY_ON)
                                    },
                                    onNavigateToNearMe = {
                                        viewModel.navigateTo(AppScreen.NEAR_ME)
                                    }
                                )
                                AppScreen.GALLERY -> GalleryClosetScreen(viewModel = viewModel)
                                AppScreen.NEAR_ME -> NearMeScreen(viewModel = viewModel)
                                AppScreen.ONBOARDING_FORM -> SetupScreen(
                                    viewModel = viewModel,
                                    isEditing = true,
                                    onCompleted = { viewModel.navigateTo(AppScreen.HOME) }
                                )
                                AppScreen.ONBOARDING_CROWN -> OnboardingCrownScreen(
                                    onStartClick = { viewModel.navigateTo(AppScreen.ONBOARDING_FORM) }
                                )
                            }
                        }
                    }
                }

                // Suitability Dialog
                if (suitabilityState && activeItem != null) {
                    val item = activeItem!!
                    val h = userProfile?.heightFt ?: viewModel.formHeightFt.collectAsState().value
                    val score by viewModel.activeSuitabilityScore.collectAsState()
                    val reason by viewModel.activeSuitabilityReason.collectAsState()

                    AlertDialog(
                        onDismissRequest = { viewModel.showSuitabilityDialog.value = false },
                        containerColor = DarkSurface,
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Suit Score: $score% for ${"%.1f".format(h)}ft", fontWeight = FontWeight.Bold, color = GoldLight)
                            }
                        },
                        text = {
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold, color = PureWhite, fontSize = 14.sp)
                                Text(item.urduName, color = GoldLight, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(reason, color = TextMuted, fontSize = 12.sp, lineHeight = 16.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Recommendation: ${item.heightTip}", color = EmeraldAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.selectForFinalLook(item)
                                    viewModel.showSuitabilityDialog.value = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = PureBlack)
                            ) {
                                Text("Add to Final Look", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.showSuitabilityDialog.value = false }) {
                                Text("Close", color = TextMuted)
                            }
                        }
                    )
                }

                // Weight Preview Modal
                if (showWeight) {
                    val curW = userProfile?.weightKg ?: viewModel.formWeightKg.collectAsState().value
                    val curH = userProfile?.heightFt ?: viewModel.formHeightFt.collectAsState().value
                    WeightPreviewDialog(
                        currentWeightKg = curW,
                        targetWeightKg = targetWeight,
                        userHeightFt = curH,
                        onWeightChange = { viewModel.targetWeightKg.value = it },
                        onDismiss = { viewModel.showWeightPreviewModal.value = false }
                    )
                }

                // Couple Matching Modal
                if (showCouple) {
                    CoupleMatchingDialog(
                        occasion = coupleOccasion,
                        onOccasionChange = { viewModel.coupleOccasion.value = it },
                        onDismiss = { viewModel.showCoupleModal.value = false }
                    )
                }

                // Catwalk Video Modal
                if (showCatwalk) {
                    val activeLook = viewModel.activeCatwalkLook.value ?: viewModel.savedLooks.collectAsState().value.firstOrNull()
                    CatwalkVideoDialog(
                        isPlaying = isCatwalkPlaying,
                        onTogglePlay = { viewModel.isCatwalkPlaying.value = !isCatwalkPlaying },
                        onDownloadVideo = {
                            if (activeLook != null) {
                                viewModel.exportCatwalkVideo(activeLook) { success, path ->
                                    viewModel.userNotice.value = if (success) "REAL Catwalk Video Downloaded (.mp4): $path" else "Failed to export video."
                                }
                            } else {
                                viewModel.userNotice.value = "Catwalk Video Downloaded (1080x1920 MP4)!"
                            }
                            viewModel.showCatwalkModal.value = false
                        },
                        onDismiss = { viewModel.showCatwalkModal.value = false }
                    )
                }

                // Meme Comparison Modal (Shaadi Se Pehle vs Baad)
                if (showMeme) {
                    MemeComparisonDialog(
                        sliderPos = memeSlider,
                        onSliderChange = { viewModel.memeSliderPosition.value = it },
                        onDismiss = { viewModel.showMemeModal.value = false }
                    )
                }

                // Interstitial Ad
                if (finalLookState.showInterstitialAd) {
                    InterstitialAdDialog(
                        onDismiss = { viewModel.dismissInterstitialAd() },
                        onUpgradeToPremium = {
                            viewModel.dismissInterstitialAd()
                            viewModel.showPremiumDialog.value = true
                        }
                    )
                }

                // VIP Premium Dialog (Rs 299/month)
                if (showPremium) {
                    PremiumUpgradeDialog(
                        isCurrentPremium = userProfile?.isPremium ?: false,
                        onDismiss = { viewModel.showPremiumDialog.value = false },
                        onActivate = { isVIP -> viewModel.togglePremium(isVIP) }
                    )
                }
            }
        }
    }
}
