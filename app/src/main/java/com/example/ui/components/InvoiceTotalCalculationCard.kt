package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.LineBorder
import com.example.ui.theme.MintContainer
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusGood
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InvoiceTotalCalculationCard(
    itemCount: Int,
    totalUnits: Int,
    subtotal: Double,
    itemDiscounts: Double,
    overallDiscount: Double,
    onOverallDiscountChange: (Double) -> Unit,
    freightCharges: Double,
    onFreightChange: (Double) -> Unit,
    taxPercentage: Double,
    onTaxPercentageChange: (Double) -> Unit,
    grandTotal: Double,
    amountPaid: Double,
    onAmountPaidChange: (Double) -> Unit,
    paymentStatus: String,
    onPaymentStatusChange: (String) -> Unit,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    invoiceNotes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    var isDiscountPercentage by remember { mutableStateOf(false) }

    var extraDiscInput by remember(overallDiscount) {
        mutableStateOf(if (overallDiscount > 0) overallDiscount.toLong().toString() else "")
    }

    var freightInput by remember(freightCharges) {
        mutableStateOf(if (freightCharges > 0) freightCharges.toLong().toString() else "")
    }

    var paidInput by remember(amountPaid) {
        mutableStateOf(if (amountPaid > 0) amountPaid.toLong().toString() else "")
    }

    val netBeforeTax = (subtotal - itemDiscounts - overallDiscount).coerceAtLeast(0.0)
    val calculatedTax = (netBeforeTax * taxPercentage / 100.0).coerceAtLeast(0.0)
    val balanceDue = (grandTotal - amountPaid).coerceAtLeast(0.0)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(LineBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TOTAL CALCULATION VIEW",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    color = MintContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$itemCount items · $totalUnits units",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gross Catalog Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catalog Gross Subtotal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
                Text(
                    text = "Rs ${fmt.format(subtotal)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Per-Item Discounts Total
            if (itemDiscounts > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Discount,
                            contentDescription = null,
                            tint = StatusDanger,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Item-Level Discounts",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StatusDanger
                        )
                    }
                    Text(
                        text = "- Rs ${fmt.format(itemDiscounts)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusDanger
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Additional Overall Discount Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = extraDiscInput,
                    onValueChange = {
                        extraDiscInput = it
                        val raw = it.toDoubleOrNull() ?: 0.0
                        if (isDiscountPercentage) {
                            val computed = (subtotal * raw / 100.0).coerceAtLeast(0.0)
                            onOverallDiscountChange(computed)
                        } else {
                            onOverallDiscountChange(raw)
                        }
                    },
                    label = { Text("Special Invoice Discount (${if (isDiscountPercentage) "%" else "Rs"})") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_overall_discount"),
                    singleLine = true
                )

                // Toggle % vs PKR
                OutlinedButton(
                    onClick = {
                        isDiscountPercentage = !isDiscountPercentage
                        extraDiscInput = ""
                        onOverallDiscountChange(0.0)
                    },
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isDiscountPercentage) "% Off" else "PKR",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Freight / Bilty Charges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = freightInput,
                    onValueChange = {
                        freightInput = it
                        val f = it.toDoubleOrNull() ?: 0.0
                        onFreightChange(f)
                    },
                    label = { Text("Freight / Bilty Delivery (Rs)") },
                    placeholder = { Text("0 (Optional)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = TextMuted)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_freight_charges"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tax / GST Options (Exempt, 5%, 18%)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sales Tax / GST Rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    if (calculatedTax > 0) {
                        Text(
                            text = "+ Rs ${fmt.format(calculatedTax)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0.0 to "0% (Exempt)", 5.0 to "5%", 18.0 to "18% GST").forEach { (rate, label) ->
                        val isSelected = taxPercentage == rate
                        FilterChip(
                            selected = isSelected,
                            onClick = { onTaxPercentageChange(rate) },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LineBorder)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // HERO GRAND TOTAL CONTAINER
            Surface(
                color = PrimaryGreen,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NET PAYABLE TOTAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rs ${fmt.format(grandTotal)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        Surface(
                            color = AccentOrange,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "PKR",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PAYMENT & SETTLEMENT DETAILS
            Text(
                text = "PAYMENT SETTLEMENT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Payment Status Chips: PAID, PARTIAL, DUE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "PAID" to "Full Paid",
                    "PARTIAL" to "Partial",
                    "DUE" to "Credit / Udhar"
                ).forEach { (statusKey, label) ->
                    val isSelected = paymentStatus == statusKey
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onPaymentStatusChange(statusKey)
                            if (statusKey == "PAID") {
                                onAmountPaidChange(grandTotal)
                                paidInput = grandTotal.toLong().toString()
                            } else if (statusKey == "DUE") {
                                onAmountPaidChange(0.0)
                                paidInput = "0"
                            }
                        },
                        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (statusKey) {
                                "PAID" -> StatusGood
                                "PARTIAL" -> AccentOrange
                                else -> StatusDanger
                            },
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Payment Method Chips: Cash, Bank, JazzCash/EasyPaisa, Credit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "CASH" to "Cash",
                    "BANK_TRANSFER" to "Bank / IBFT",
                    "JAZZCASH" to "JazzCash",
                    "CREDIT" to "Udhar"
                ).forEach { (methodKey, label) ->
                    val isSelected = paymentMethod == methodKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { onPaymentMethodChange(methodKey) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amount Paid and Quick Paid Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = paidInput,
                    onValueChange = {
                        paidInput = it
                        val p = it.toDoubleOrNull() ?: 0.0
                        onAmountPaidChange(p)
                        if (p >= grandTotal && grandTotal > 0) {
                            onPaymentStatusChange("PAID")
                        } else if (p > 0) {
                            onPaymentStatusChange("PARTIAL")
                        } else {
                            onPaymentStatusChange("DUE")
                        }
                    },
                    label = { Text("Amount Paid / Received (Rs)") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_amount_paid"),
                    singleLine = true
                )

                Button(
                    onClick = {
                        onAmountPaidChange(grandTotal)
                        paidInput = grandTotal.toLong().toString()
                        onPaymentStatusChange("PAID")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Full Paid", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Balance Remaining / Due Banner
            Surface(
                color = if (balanceDue <= 0.0) MintContainer else Color(0xFFFDEEEC),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (balanceDue <= 0.0) StatusGood.copy(alpha = 0.4f) else StatusDanger.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (balanceDue <= 0.0) Icons.Default.CheckCircle else Icons.Default.Payments,
                            contentDescription = null,
                            tint = if (balanceDue <= 0.0) StatusGood else StatusDanger,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (balanceDue <= 0.0) "Payment Status: Settled in Full" else "Remaining Balance Due:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (balanceDue <= 0.0) PrimaryGreen else StatusDanger
                        )
                    }

                    Text(
                        text = if (balanceDue <= 0.0) "Rs 0.00" else "Rs ${fmt.format(balanceDue)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (balanceDue <= 0.0) StatusGood else StatusDanger
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Invoice Notes & Terms Field
            OutlinedTextField(
                value = invoiceNotes,
                onValueChange = onNotesChange,
                label = { Text("Invoice Notes & Warranty Terms (Optional)") },
                placeholder = { Text("e.g. 6 Months Warranty. Goods once sold will not be returned without bill.") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )
        }
    }
}
