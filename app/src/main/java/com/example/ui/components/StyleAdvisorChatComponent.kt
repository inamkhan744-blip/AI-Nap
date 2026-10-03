package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ChatMessage
import com.example.data.model.FashionItem
import com.example.data.model.MessageSender
import com.example.data.model.StyleAdvisorPresets
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StyleAdvisorModalDialog(
    viewModel: FashionViewModel,
    onDismiss: () -> Unit,
    onSelectDressToTryOn: ((FashionItem) -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = PehnoWhite,
            border = BorderStroke(1.5.dp, PehnoGreenPrimary),
            shadowElevation = 8.dp
        ) {
            StyleAdvisorChatContent(
                viewModel = viewModel,
                onClose = onDismiss,
                onSelectDressToTryOn = { item ->
                    viewModel.selectedDress.value = item
                    onDismiss()
                    onSelectDressToTryOn?.invoke(item)
                }
            )
        }
    }
}

@Composable
fun StyleAdvisorChatContent(
    viewModel: FashionViewModel,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    onSelectDressToTryOn: ((FashionItem) -> Unit)? = null
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val isUrdu = lang == "ur"
    val messages by viewModel.styleAdvisorMessages.collectAsState()
    val isLoading by viewModel.isAdvisorLoading.collectAsState()
    val currentOccasion by viewModel.selectedAdvisorOccasion.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val gender = userProfile?.gender ?: viewModel.formGender.collectAsState().value
    val heightFt = userProfile?.heightFt ?: viewModel.formHeightFt.collectAsState().value
    val weightKg = userProfile?.weightKg ?: viewModel.formWeightKg.collectAsState().value
    val bodyType = userProfile?.bodyType ?: viewModel.formBodyType.collectAsState().value
    val skinTone = userProfile?.skinTone ?: viewModel.formSkinTone.collectAsState().value

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll on new message
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PehnoWhite)
    ) {
        // --- Header ---
        Surface(
            color = PehnoGreenLight,
            border = BorderStroke(1.dp, PehnoGreenPrimary.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PehnoGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = PehnoWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isUrdu) "پہنو اے آئی اسٹائل ایڈوائزر" else "Pehno Style Advisor",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = PehnoGreenDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PehnoGreenPrimary
                            ) {
                                Text(
                                    text = "Gemini AI",
                                    fontSize = 9.sp,
                                    color = PehnoWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isUrdu) "آپ کے قد، وزن اور موقع کے مطابق فیشن مشورہ" else "Personalized for your body type & occasion",
                            fontSize = 11.sp,
                            color = PehnoTextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.clearAdvisorChat() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Clear Chat",
                            tint = PehnoTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (onClose != null) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = PehnoBlack,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- User Stats Bar ---
        Surface(
            color = PehnoSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isUrdu) "آپ کا پروفائل:" else "Your Profile:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PehnoTextSecondary
                )
                ProfileChip(text = "$gender • ${"%.1f".format(heightFt)}ft")
                ProfileChip(text = "${weightKg.toInt()}kg • $bodyType")
                ProfileChip(text = skinTone)
            }
        }

        // --- Occasions Bar ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = if (isUrdu) "موقع منتخب کریں (Occasion):" else "Select Occasion:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PehnoBlack,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(StyleAdvisorPresets.occasions) { occ ->
                    val isSelected = currentOccasion == occ.label("en") || currentOccasion == occ.englishName
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedAdvisorOccasion.value = occ.englishName
                            viewModel.sendAdvisorMessage(
                                prompt = if (isUrdu) "اس موقع (${occ.urduName}) کے لیے میرے قد اور جسم کے لحاظ سے بہترین لباس اور رنگ کیا ہوگا؟" else "What is the best outfit and color palette for ${occ.englishName} for my frame?",
                                occasionOverride = occ.englishName
                            )
                        },
                        label = {
                            Text(
                                text = occ.label(lang),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PehnoGreenPrimary,
                            selectedLabelColor = PehnoWhite,
                            containerColor = PehnoGreenLight,
                            labelColor = PehnoBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) PehnoGreenPrimary else PehnoCardBorder
                        )
                    )
                }
            }
        }

        HorizontalDivider(color = PehnoCardBorder, thickness = 0.8.dp)

        // --- Chat Messages List ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(
                    message = msg,
                    lang = lang,
                    onTryOn = onSelectDressToTryOn
                )
            }

            // Typing Indicator
            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 6.dp, top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PehnoGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PehnoGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PehnoSurface,
                            border = BorderStroke(1.dp, PehnoCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = PehnoGreenPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isUrdu) "اسٹائل ایڈوائزر سوچ رہا ہے..." else "Advisor is crafting your advice...",
                                    fontSize = 11.sp,
                                    color = PehnoTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Quick Prompts Suggestions ---
        val suggestions = remember(gender, lang) {
            StyleAdvisorPresets.getSuggestions(gender, lang)
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(suggestions) { suggestion ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PehnoSurface,
                    border = BorderStroke(1.dp, PehnoCardBorder),
                    modifier = Modifier.clickable {
                        viewModel.sendAdvisorMessage(suggestion)
                    }
                ) {
                    Text(
                        text = suggestion,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = PehnoBlack,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // --- Input Bar ---
        Surface(
            color = PehnoWhite,
            border = BorderStroke(1.dp, PehnoCardBorder),
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (isUrdu) "فیشن مشورہ پوچھیں (مثلاً: ولیمے پر کیا پہنوں؟)" else "Ask fashion advice (e.g. Walima look for 5.8ft?)...",
                            fontSize = 12.sp,
                            color = PehnoTextSecondary
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("advisor_chat_input"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PehnoGreenPrimary,
                        unfocusedBorderColor = PehnoCardBorder
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendAdvisorMessage(inputText)
                                inputText = ""
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendAdvisorMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isLoading) PehnoGreenPrimary else PehnoCardBorder)
                        .testTag("advisor_chat_send_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = PehnoWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileChip(text: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = PehnoGreenLight,
        border = BorderStroke(0.8.dp, PehnoGreenPrimary.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = PehnoGreenDark,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    lang: String,
    onTryOn: ((FashionItem) -> Unit)? = null
) {
    val isUser = message.sender == MessageSender.USER
    val isUrdu = lang == "ur"
    val timeFormatted = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PehnoGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = PehnoGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) PehnoGreenPrimary else PehnoSurface,
                border = if (isUser) null else BorderStroke(1.dp, PehnoCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (message.occasion != null && isUser) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = message.occasion,
                                fontSize = 9.sp,
                                color = PehnoWhite,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = if (isUser) PehnoWhite else PehnoBlack
                    )

                    // Suggested Outfits from catalog
                    if (message.suggestedItems.isNotEmpty() && !isUser) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = PehnoCardBorder)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isUrdu) "👗 ماڈل پر پہن کر دیکھیں:" else "👗 Recommended Outfits to Try On:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PehnoGreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        message.suggestedItems.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PehnoWhite,
                                border = BorderStroke(1.dp, PehnoGreenPrimary.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { onTryOn?.invoke(item) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PehnoBlack)
                                        Text("Rs. ${item.priceRs} • ${item.storeSource}", fontSize = 9.sp, color = PehnoGreenPrimary)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PehnoGreenLight
                                    ) {
                                        Text(
                                            text = if (isUrdu) "ٹرائی آن" else "Try On",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PehnoGreenDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = timeFormatted,
                fontSize = 9.sp,
                color = PehnoTextSecondary,
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PehnoSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = PehnoGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
