package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.InvoiceWithItems
import com.example.data.model.ItemEntity
import com.example.ui.components.InvoiceInventorySelector
import com.example.ui.components.InvoicePreviewDialog
import com.example.ui.components.InvoiceTotalCalculationCard
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.LineBorder
import com.example.ui.theme.MintContainer
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusGood
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.CartItemDraft
import com.example.ui.viewmodel.InventoryViewModel
import com.example.utils.PdfInvoiceGenerator
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceBuilderScreen(
    viewModel: InventoryViewModel,
    onBack: () -> Unit,
    onInvoiceCreated: (InvoiceWithItems) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cartItems by viewModel.cartItems.collectAsState()
    val allInventoryItems by viewModel.allInventoryItems.collectAsState()
    val custName by viewModel.customerName.collectAsState()
    val custPhone by viewModel.customerPhone.collectAsState()
    val custAddress by viewModel.customerAddress.collectAsState()
    val overallDiscount by viewModel.overallDiscount.collectAsState()
    val freightCharges by viewModel.freightCharges.collectAsState()
    val taxPercentage by viewModel.taxPercentage.collectAsState()
    val amountPaid by viewModel.amountPaid.collectAsState()
    val paymentStatus by viewModel.paymentStatus.collectAsState()
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val invoiceNotes by viewModel.invoiceNotes.collectAsState()

    var activeTabIndex by remember { mutableIntStateOf(0) } // 0: Select Inventory, 1: Bill Items, 2: Total Calculation
    var showCustomItemDialog by remember { mutableStateOf(false) }
    var showPreviewDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val subtotal = viewModel.calculateCartSubtotal()
    val itemDiscounts = viewModel.calculateItemDiscounts()
    val grandTotal = viewModel.calculateGrandTotal()
    val totalUnits = cartItems.sumOf { it.quantity }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Professional Invoice Generator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Asad Auto Corporation · Radiators & Auto Parts",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.82f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(
                            onClick = { showPreviewDialog = true },
                            modifier = Modifier.testTag("btn_preview_invoice")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Preview",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.testTag("btn_clear_invoice_cart")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear All",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = Color.White,
                    shadowElevation = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Payable (${cartItems.size} items · $totalUnits units)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                                Text(
                                    text = "Rs " + fmt.format(grandTotal),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryGreen
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { showPreviewDialog = true },
                                    modifier = Modifier.height(46.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Preview", fontSize = 13.sp)
                                }

                                Button(
                                    onClick = {
                                        if (custName.isBlank()) {
                                            errorMessage = "Please enter customer / shop name."
                                            activeTabIndex = 1
                                            return@Button
                                        }
                                        isSaving = true
                                        scope.launch {
                                            val created = viewModel.createInvoice()
                                            isSaving = false
                                            if (created != null) {
                                                onInvoiceCreated(created)
                                            }
                                        }
                                    },
                                    enabled = !isSaving,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(46.dp)
                                        .testTag("btn_confirm_create_invoice")
                                ) {
                                    if (isSaving) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Save Invoice",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stage / Tab Switcher (Select Inventory | Bill Items | Total Calculation)
            TabRow(
                selectedTabIndex = activeTabIndex,
                containerColor = Color.White,
                contentColor = PrimaryGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTabIndex]),
                        color = PrimaryGreen,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = activeTabIndex == 0,
                    onClick = { activeTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("1. Select Inventory", fontWeight = if (activeTabIndex == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                        }
                    }
                )

                Tab(
                    selected = activeTabIndex == 1,
                    onClick = { activeTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "2. Bill Items (${cartItems.size})",
                                fontWeight = if (activeTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                )

                Tab(
                    selected = activeTabIndex == 2,
                    onClick = { activeTabIndex = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "3. Total & Pay",
                                fontWeight = if (activeTabIndex == 2) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }

            // Error notice banner
            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { errorMessage = null }, modifier = Modifier.size(20.dp)) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Dismiss", tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section: Customer Information Header (always shown for convenience)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(LineBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CUSTOMER & INVOICE DETAILS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange,
                                    letterSpacing = 1.sp
                                )

                                Surface(
                                    color = MintContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Status: $paymentStatus",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = custName,
                                onValueChange = { viewModel.customerName.value = it; errorMessage = null },
                                label = { Text("Customer / Shop Name *") },
                                placeholder = { Text("e.g. Madina Autos, Lahore") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_customer_name"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = custPhone,
                                    onValueChange = { viewModel.customerPhone.value = it },
                                    label = { Text("Phone Number") },
                                    placeholder = { Text("0300-1234567") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_customer_phone"),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = custAddress,
                                    onValueChange = { viewModel.customerAddress.value = it },
                                    label = { Text("City / Market Address") },
                                    placeholder = { Text("Badami Bagh, Lahore") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                // TAB 0: Select Items from Inventory (Directly Embedded!)
                if (activeTabIndex == 0) {
                    item {
                        InvoiceInventorySelector(
                            inventoryItems = allInventoryItems,
                            selectedCartItems = cartItems,
                            onAddItem = { item -> viewModel.addToCart(item) },
                            onIncrementItem = { itemId -> viewModel.setItemQuantity(itemId, (cartItems.firstOrNull { it.itemId == itemId }?.quantity ?: 0) + 1) },
                            onDecrementItem = { itemId -> viewModel.setItemQuantity(itemId, (cartItems.firstOrNull { it.itemId == itemId }?.quantity ?: 0) - 1) }
                        )
                    }

                    if (cartItems.isNotEmpty()) {
                        item {
                            Button(
                                onClick = { activeTabIndex = 1 },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Proceed to Bill Items (${cartItems.size} Selected) →")
                            }
                        }
                    }
                }

                // TAB 1: Review Bill Items & Adjust Rates / Discounts
                if (activeTabIndex == 1) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INVOICE ITEMS LIST (${cartItems.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 1.sp
                            )

                            Row {
                                TextButton(onClick = { activeTabIndex = 0 }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ From Inventory", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                                }

                                TextButton(onClick = { showCustomItemDialog = true }) {
                                    Text("+ Custom Item", color = AccentOrange, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (cartItems.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No items in this invoice yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Select radiators or parts from inventory to generate your professional invoice.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = { activeTabIndex = 0 },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                                    ) {
                                        Icon(imageVector = Icons.Default.Inventory2, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Browse Inventory Items")
                                    }
                                }
                            }
                        }
                    } else {
                        itemsIndexed(cartItems) { index, item ->
                            InvoiceItemRow(
                                item = item,
                                onIncrement = { viewModel.incrementCartItem(index) },
                                onDecrement = { viewModel.decrementCartItem(index) },
                                onRemove = { viewModel.removeCartItem(index) },
                                onSetDiscount = { disc -> viewModel.setItemDiscount(index, disc) },
                                onSetFinalRate = { rate -> viewModel.setItemFinalRate(index, rate) }
                            )
                        }

                        item {
                            Button(
                                onClick = { activeTabIndex = 2 },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Total Financial Calculation →")
                            }
                        }
                    }
                }

                // TAB 2: Dedicated Total Calculation View & Payment Breakdown
                if (activeTabIndex == 2) {
                    item {
                        InvoiceTotalCalculationCard(
                            itemCount = cartItems.size,
                            totalUnits = totalUnits,
                            subtotal = subtotal,
                            itemDiscounts = itemDiscounts,
                            overallDiscount = overallDiscount,
                            onOverallDiscountChange = { viewModel.overallDiscount.value = it },
                            freightCharges = freightCharges,
                            onFreightChange = { viewModel.freightCharges.value = it },
                            taxPercentage = taxPercentage,
                            onTaxPercentageChange = { viewModel.taxPercentage.value = it },
                            grandTotal = grandTotal,
                            amountPaid = amountPaid,
                            onAmountPaidChange = { viewModel.amountPaid.value = it },
                            paymentStatus = paymentStatus,
                            onPaymentStatusChange = { viewModel.paymentStatus.value = it },
                            paymentMethod = paymentMethod,
                            onPaymentMethodChange = { viewModel.paymentMethod.value = it },
                            invoiceNotes = invoiceNotes,
                            onNotesChange = { viewModel.invoiceNotes.value = it }
                        )
                    }
                }
            }
        }
    }

    // Modal: Preview Invoice Dialog
    if (showPreviewDialog) {
        InvoicePreviewDialog(
            customerName = custName,
            customerPhone = custPhone,
            customerAddress = custAddress,
            items = cartItems,
            subtotal = subtotal,
            itemDiscounts = itemDiscounts,
            overallDiscount = overallDiscount,
            freightCharges = freightCharges,
            taxPercentage = taxPercentage,
            grandTotal = grandTotal,
            amountPaid = amountPaid,
            paymentStatus = paymentStatus,
            paymentMethod = paymentMethod,
            invoiceNotes = invoiceNotes,
            onDismiss = { showPreviewDialog = false },
            onConfirmPdf = {
                showPreviewDialog = false
                scope.launch {
                    val invoiceWithItems = viewModel.createInvoice()
                    if (invoiceWithItems != null) {
                        val pdf = PdfInvoiceGenerator.generatePdf(context, invoiceWithItems)
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdf)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, "Invoice ${invoiceWithItems.invoice.invoiceNumber} - AAC Auto Parts")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Invoice PDF"))
                        onInvoiceCreated(invoiceWithItems)
                    }
                }
            }
        )
    }

    // Modal: Clear Cart Confirmation
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All Invoice Items?") },
            text = { Text("This will remove all items and reset this invoice draft.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCart()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: Add Custom Line Item
    if (showCustomItemDialog) {
        var customName by remember { mutableStateOf("") }
        var customRateText by remember { mutableStateOf("") }
        var customQtyText by remember { mutableStateOf("1") }

        AlertDialog(
            onDismissRequest = { showCustomItemDialog = false },
            title = { Text("Add Custom Line Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Item Name / Description") },
                        placeholder = { Text("e.g. Radiator Cap, Coolant 1L, Fabrication") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customRateText,
                        onValueChange = { customRateText = it },
                        label = { Text("Unit Rate (Rs)") },
                        placeholder = { Text("1500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customQtyText,
                        onValueChange = { customQtyText = it },
                        label = { Text("Quantity") },
                        placeholder = { Text("1") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rate = customRateText.toDoubleOrNull() ?: 0.0
                        val qty = customQtyText.toIntOrNull() ?: 1
                        if (customName.isNotBlank() && rate >= 0) {
                            viewModel.addCustomLineItem(customName, rate, qty)
                            showCustomItemDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Add Item")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomItemDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun InvoiceItemRow(
    item: CartItemDraft,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    onSetDiscount: (Double) -> Unit,
    onSetFinalRate: (Double) -> Unit
) {
    var showDiscountDialog by remember { mutableStateOf(false) }
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LineBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title & Remove button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    val meta = listOfNotNull(
                        item.brand.ifBlank { null },
                        if (item.mm != null && item.mm > 0) "${item.mm}mm" else null,
                        item.transmission?.ifBlank { null }
                    ).joinToString(" · ")
                    if (meta.isNotBlank()) {
                        Text(
                            text = meta,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = StatusDanger,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Discount controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Catalog: Rs ${fmt.format(item.originalRate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (item.discountPerUnit > 0) TextMuted else TextPrimary,
                            textDecoration = if (item.discountPerUnit > 0) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        )
                    }

                    // Discount pill button
                    Surface(
                        color = if (item.discountPerUnit > 0) Color(0xFFFDEEEC) else Color(0xFFF1F3F2),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Discount,
                                contentDescription = null,
                                tint = if (item.discountPerUnit > 0) StatusDanger else PrimaryGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (item.discountPerUnit > 0) {
                                    "Disc: -Rs ${fmt.format(item.discountPerUnit)} (Rate: Rs ${fmt.format(item.finalRate)})"
                                } else {
                                    "+ Give Discount"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (item.discountPerUnit > 0) StatusDanger else PrimaryGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            TextButton(
                                onClick = { showDiscountDialog = true },
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text("Edit", fontSize = 10.sp, color = PrimaryGreen)
                            }
                        }
                    }
                }

                // Quantity Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MintContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                    }

                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Line Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Line Total: Rs ${fmt.format(item.lineTotal)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreen
                )
            }
        }
    }

    // Modal: Edit Item Discount / Final Price
    if (showDiscountDialog) {
        var discInput by remember { mutableStateOf(if (item.discountPerUnit > 0) item.discountPerUnit.toLong().toString() else "") }
        var finalRateInput by remember { mutableStateOf(item.finalRate.toLong().toString()) }

        AlertDialog(
            onDismissRequest = { showDiscountDialog = false },
            title = { Text("Item Price & Discount") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Standard Catalog Rate: Rs ${fmt.format(item.originalRate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Option A: Enter Discount Amount
                    OutlinedTextField(
                        value = discInput,
                        onValueChange = {
                            discInput = it
                            val d = it.toDoubleOrNull() ?: 0.0
                            val fr = (item.originalRate - d).coerceAtLeast(0.0)
                            finalRateInput = fr.toLong().toString()
                        },
                        label = { Text("Discount per unit (Rs)") },
                        placeholder = { Text("e.g. 500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Option B: Or Enter Deal / Final Unit Price directly
                    OutlinedTextField(
                        value = finalRateInput,
                        onValueChange = {
                            finalRateInput = it
                            val fr = it.toDoubleOrNull() ?: item.originalRate
                            val d = (item.originalRate - fr).coerceAtLeast(0.0)
                            discInput = if (d > 0) d.toLong().toString() else "0"
                        },
                        label = { Text("Final Unit Rate (Rs)") },
                        placeholder = { Text("e.g. 4500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val d = discInput.toDoubleOrNull() ?: 0.0
                        onSetDiscount(d)
                        showDiscountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onSetDiscount(0.0)
                    showDiscountDialog = false
                }) {
                    Text("Reset Discount")
                }
            }
        )
    }
}
