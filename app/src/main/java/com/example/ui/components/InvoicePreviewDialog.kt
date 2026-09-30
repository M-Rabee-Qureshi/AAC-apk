package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.LineBorder
import com.example.ui.theme.MintContainer
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.StatusGood
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.CartItemDraft
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoicePreviewDialog(
    customerName: String,
    customerPhone: String,
    customerAddress: String,
    items: List<CartItemDraft>,
    subtotal: Double,
    itemDiscounts: Double,
    overallDiscount: Double,
    freightCharges: Double,
    taxPercentage: Double,
    grandTotal: Double,
    amountPaid: Double,
    paymentStatus: String,
    paymentMethod: String,
    invoiceNotes: String,
    onDismiss: () -> Unit,
    onConfirmPdf: () -> Unit
) {
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val dateStr = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()) }
    val balanceDue = (grandTotal - amountPaid).coerceAtLeast(0.0)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF3F4F6)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Professional Invoice Preview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Realistic Paper Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Letterhead
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.aac_icon_fg_1790773865842),
                                        contentDescription = "AAC Logo",
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "ASAD AUTO CORPORATION",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = PrimaryGreen
                                    )
                                    Text(
                                        text = "AAC Auto Parts · Cooling Solutions",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "61 Madina Market, Badami Bagh, Lahore",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                            }

                            Surface(
                                color = PrimaryGreen,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "INVOICE",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LineBorder))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Customer & Bill Info Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("BILLED TO:", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text(customerName.ifBlank { "Cash Customer" }, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                if (customerPhone.isNotBlank()) {
                                    Text("Phone: $customerPhone", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                                if (customerAddress.isNotBlank()) {
                                    Text("Address: $customerAddress", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("DATE & STATUS:", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text(dateStr, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = TextPrimary)
                                Surface(
                                    color = when (paymentStatus) {
                                        "PAID" -> MintContainer
                                        "PARTIAL" -> AccentOrange.copy(alpha = 0.15f)
                                        else -> Color(0xFFFDEEEC)
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "STATUS: $paymentStatus",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when (paymentStatus) {
                                            "PAID" -> PrimaryGreen
                                            "PARTIAL" -> AccentOrange
                                            else -> Color(0xFFC0392B)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Table Header
                        Surface(
                            color = MintContainer,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#", modifier = Modifier.width(24.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                Text("Item Description", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                Text("Qty", modifier = Modifier.width(36.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                Text("Rate", modifier = Modifier.width(68.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                Text("Total", modifier = Modifier.width(74.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                            }
                        }

                        // Table Rows
                        items.forEachIndexed { idx, it ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${idx + 1}", modifier = Modifier.width(24.dp), style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(it.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    val spec = listOfNotNull(it.brand.ifBlank { null }, if (it.mm != null && it.mm > 0) "${it.mm}mm" else null).joinToString(" · ")
                                    if (spec.isNotBlank()) {
                                        Text(spec, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    }
                                }
                                Text("${it.quantity}", modifier = Modifier.width(36.dp), style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                Text("Rs ${fmt.format(it.finalRate)}", modifier = Modifier.width(68.dp), style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("Rs ${fmt.format(it.lineTotal)}", modifier = Modifier.width(74.dp), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(LineBorder))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Totals Summary Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFBFA), RoundedCornerShape(8.dp))
                                .border(1.dp, LineBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Catalog Subtotal", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("Rs ${fmt.format(subtotal)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                            if (itemDiscounts > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Item Discounts", style = MaterialTheme.typography.bodySmall, color = Color(0xFFC0392B))
                                    Text("- Rs ${fmt.format(itemDiscounts)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFFC0392B))
                                }
                            }
                            if (overallDiscount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Special Discount", style = MaterialTheme.typography.bodySmall, color = Color(0xFFC0392B))
                                    Text("- Rs ${fmt.format(overallDiscount)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFFC0392B))
                                }
                            }
                            if (freightCharges > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Freight Delivery", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    Text("+ Rs ${fmt.format(freightCharges)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LineBorder))
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("FINAL GRAND TOTAL", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = PrimaryGreen)
                                Text("Rs ${fmt.format(grandTotal)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = PrimaryGreen)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amount Received ($paymentMethod)", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                Text("Rs ${fmt.format(amountPaid)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                            if (balanceDue > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Balance Remaining", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFFC0392B))
                                    Text("Rs ${fmt.format(balanceDue)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFFC0392B))
                                }
                            }
                        }

                        if (invoiceNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Terms / Notes: $invoiceNotes",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dialog Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirmPdf,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print / Share PDF")
                    }
                }
            }
        }
    }
}
