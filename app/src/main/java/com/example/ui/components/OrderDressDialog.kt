package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FashionItem
import com.example.data.model.PlacedOrder
import com.example.ui.theme.*
import com.example.ui.viewmodel.FashionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDressDialog(
    item: FashionItem,
    viewModel: FashionViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lang by viewModel.currentLanguage.collectAsState()
    val isUrdu = lang == "ur"
    val userProfile by viewModel.userProfile.collectAsState()

    var customerName by remember { mutableStateOf(userProfile?.name ?: "") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryCity by remember { mutableStateOf(userProfile?.city ?: "Lahore") }
    var deliveryAddress by remember { mutableStateOf("") }
    var selectedSize by remember { mutableStateOf("M (Medium)") }

    val pakCities = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Peshawar", "Quetta", "Faisalabad", "Multan", "Sialkot")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PehnoWhite,
            border = BorderStroke(1.5.dp, PehnoGreenPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isUrdu) "📦 آرڈر کنفرم کریں" else "📦 Confirm Order",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = PehnoBlack
                        )
                        Text(
                            text = if (isUrdu) "کیش آن ڈیلیوری (COD) پورے پاکستان میں" else "Cash on Delivery (COD) Pakistan",
                            fontSize = 11.sp,
                            color = PehnoGreenDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PehnoTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Item Summary Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PehnoGreenLight,
                    border = BorderStroke(1.dp, PehnoGreenPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PehnoGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = PehnoWhite)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PehnoBlack)
                            Text(item.storeSource, fontSize = 10.sp, color = PehnoTextSecondary)
                            Text("Rs. ${item.priceRs}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = PehnoGreenPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input: Customer Name
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text(if (isUrdu) "کسٹمر کا نام" else "Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input: WhatsApp Phone
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text(if (isUrdu) "فون / واٹس ایپ نمبر" else "WhatsApp / Mobile Number") },
                    placeholder = { Text("03001234567") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Select City
                Text(
                    text = if (isUrdu) "شہر منتخب کریں:" else "Select City:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PehnoBlack
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Lahore", "Karachi", "Islamabad").forEach { city ->
                        FilterChip(
                            selected = deliveryCity == city,
                            onClick = { deliveryCity = city },
                            label = { Text(city, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Delivery Address
                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text(if (isUrdu) "مکمل گھر کا پتہ (گلی، محلہ، ہاؤس نمبر)" else "Full Delivery Address") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Button
                Button(
                    onClick = {
                        if (customerName.isBlank() || customerPhone.isBlank() || deliveryAddress.isBlank()) {
                            Toast.makeText(context, if (isUrdu) "برائے مہربانی نام، فون اور پتہ درج کریں" else "Please enter Name, Phone and Address", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val orderId = "ORD-${System.currentTimeMillis() % 100000}"
                        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

                        val placedOrder = PlacedOrder(
                            id = orderId,
                            dressName = item.name,
                            sellerShop = item.storeSource,
                            priceRs = item.priceRs,
                            deliveryCity = deliveryCity,
                            customerName = customerName,
                            customerPhone = customerPhone,
                            deliveryAddress = deliveryAddress,
                            orderDate = dateStr,
                            status = if (isUrdu) "تصدیق شدہ (COD)" else "Confirmed (COD)"
                        )

                        viewModel.recordOrder(placedOrder)

                        // WhatsApp Message to Seller
                        val targetWhatsApp = item.whatsappNumber?.takeIf { it.isNotBlank() } ?: "923001234567"
                        val waMessage = buildString {
                            appendLine("🛍️ *پہنو ایپ - نیا آرڈر (New Pehno Order)*")
                            appendLine("━━━━━━━━━━━━━━━━━━━")
                            appendLine("📦 آرڈر آئی ڈی: $orderId")
                            appendLine("👗 جوڑا: ${item.name}")
                            appendLine("💰 کل رقم: Rs. ${item.priceRs} (Cash on Delivery)")
                            appendLine("━━━━━━━━━━━━━━━━━━━")
                            appendLine("👤 گاہک کا نام: $customerName")
                            appendLine("📞 رابطہ: $customerPhone")
                            appendLine("📍 شہر: $deliveryCity")
                            appendLine("🏠 پتہ: $deliveryAddress")
                            appendLine("━━━━━━━━━━━━━━━━━━━")
                            appendLine("شکریہ، برائے مہربانی جلد از جلد ڈسپیچ کریں۔")
                        }

                        val waUri = Uri.parse("https://api.whatsapp.com/send?phone=${targetWhatsApp.replace("+", "").replace("-", "")}&text=${Uri.encode(waMessage)}")
                        val waIntent = Intent(Intent.ACTION_VIEW, waUri)
                        try {
                            context.startActivity(waIntent)
                        } catch (_: Exception) {
                            Toast.makeText(context, if (isUrdu) "آرڈر محفوظ ہو گیا!" else "Order placed successfully!", Toast.LENGTH_SHORT).show()
                        }

                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PehnoGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_order_btn")
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "آرڈر تصدیق کریں (واٹس ایپ)" else "Confirm Order via WhatsApp",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
