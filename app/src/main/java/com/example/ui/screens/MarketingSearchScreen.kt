package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ItemEntity
import com.example.ui.components.AddEditItemDialog
import com.example.ui.components.CartFloatingBar
import com.example.ui.components.ItemCard
import com.example.ui.components.ItemDetailDialog
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.LineBorder
import com.example.ui.theme.MintContainer
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketingSearchScreen(
    viewModel: InventoryViewModel,
    onOpenInvoiceBuilder: () -> Unit,
    onOpenInvoicesHistory: () -> Unit,
    onOpenCategoriesBrands: () -> Unit,
    onOpenSpreadsheetManager: () -> Unit,
    onOpenSupabaseSettings: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val brands by viewModel.brands.collectAsState()
    val specs by viewModel.specs.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedBrand by viewModel.selectedBrand.collectAsState()
    val selectedMm by viewModel.selectedMm.collectAsState()

    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()

    var itemDetailTarget by remember { mutableStateOf<ItemEntity?>(null) }
    var itemEditTarget by remember { mutableStateOf<ItemEntity?>(null) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    var topMenuExpanded by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showRestoreDefaultDialog by remember { mutableStateOf(false) }
    var deleteAllPin by remember { mutableStateOf("") }
    var deleteAllError by remember { mutableStateOf<String?>(null) }
    var restorePin by remember { mutableStateOf("") }
    var restoreError by remember { mutableStateOf<String?>(null) }
    var itemToDelete by remember { mutableStateOf<ItemEntity?>(null) }

    val totalCartCount = cartItems.sumOf { it.quantity }
    val cartTotal = viewModel.calculateGrandTotal()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(36.dp)
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.aac_icon_fg_1790773865842),
                            contentDescription = "AAC Auto Parts",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                        )
                    }
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Asad Auto Corporation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "AAC Auto Parts · Master Search",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenSpreadsheetManager,
                        modifier = Modifier.testTag("btn_open_excel")
                    ) {
                        Icon(imageVector = Icons.Default.TableChart, contentDescription = "Excel", tint = Color.White)
                    }
                    IconButton(
                        onClick = onOpenInvoicesHistory,
                        modifier = Modifier.testTag("btn_open_invoices")
                    ) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = "Invoices", tint = Color.White)
                    }
                    Box {
                        IconButton(
                            onClick = { topMenuExpanded = true },
                            modifier = Modifier.testTag("btn_top_menu")
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                        }

                        DropdownMenu(
                            expanded = topMenuExpanded,
                            onDismissRequest = { topMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Generate New Invoice", fontWeight = FontWeight.Bold) },
                                onClick = {
                                    topMenuExpanded = false
                                    onOpenInvoiceBuilder()
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = PrimaryGreen)
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Categories & Brands") },
                                onClick = {
                                    topMenuExpanded = false
                                    onOpenCategoriesBrands()
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = PrimaryGreen)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Excel / CSV Tools") },
                                onClick = {
                                    topMenuExpanded = false
                                    onOpenSpreadsheetManager()
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.TableChart, contentDescription = null, tint = PrimaryGreen)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Supabase Cloud Sync") },
                                onClick = {
                                    topMenuExpanded = false
                                    onOpenSupabaseSettings()
                                },
                                leadingIcon = {
                                    if (isSyncing) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = PrimaryGreen)
                                    }
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Restore Default Catalogue") },
                                onClick = {
                                    topMenuExpanded = false
                                    restorePin = ""
                                    restoreError = null
                                    showRestoreDefaultDialog = true
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = PrimaryGreen)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete All Items", color = StatusDanger, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    topMenuExpanded = false
                                    deleteAllPin = ""
                                    deleteAllError = null
                                    showDeleteAllDialog = true
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = StatusDanger)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PrimaryGreen)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemDialog = true },
                containerColor = AccentOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = if (totalCartCount > 0) 70.dp else 0.dp)
                    .testTag("fab_add_item")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        bottomBar = {
            CartFloatingBar(
                itemCount = totalCartCount,
                totalAmount = cartTotal,
                onReviewInvoice = onOpenInvoiceBuilder
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6F7F5))
        ) {
            // Search Input Header
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search model, brand, MM, rate, engine...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = PrimaryGreen)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_query")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Categories Horizontal Filter Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = selectedCategory.isEmpty(),
                            onClick = { viewModel.selectCategory("") },
                            label = { Text("All Categories") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = Color.White
                            )
                        )

                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat.name,
                                onClick = { viewModel.selectCategory(cat.name) },
                                label = { Text(cat.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        // Customize categories/brands button
                        IconButton(
                            onClick = onOpenCategoriesBrands,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = "Manage", tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Brands & MM Horizontal Filter Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Brand:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )

                        FilterChip(
                            selected = selectedBrand.isEmpty(),
                            onClick = { viewModel.selectBrand("") },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentOrange,
                                selectedLabelColor = Color.White
                            )
                        )

                        brands.forEach { b ->
                            FilterChip(
                                selected = selectedBrand == b.name,
                                onClick = { viewModel.selectBrand(b.name) },
                                label = { Text(b.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentOrange,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Core MM:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )

                        listOf(16, 26, 32, 36, 48).forEach { mmVal ->
                            FilterChip(
                                selected = selectedMm == mmVal,
                                onClick = { viewModel.selectMm(mmVal) },
                                label = { Text("${mmVal}mm") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF1D5A7A),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Results Counter & Clear Filter banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${items.size} ${if (items.size == 1) "item" else "items"} available",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                if (searchQuery.isNotBlank() || selectedCategory.isNotBlank() || selectedBrand.isNotBlank() || selectedMm != null) {
                    TextButton(onClick = { viewModel.clearFilters() }) {
                        Text("Reset Filters", color = AccentOrange, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Items List
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.FilterAlt, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching items found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try clearing filters, searching for model name (e.g. Corolla, Swift, Cultus) or add a new item.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.clearFilters() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                        ) {
                            Text("Clear Filters")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        val qtyInCart = cartItems.firstOrNull { it.itemId == item.id }?.quantity ?: 0
                        val cartIndex = cartItems.indexOfFirst { it.itemId == item.id }

                        ItemCard(
                            item = item,
                            cartQuantity = qtyInCart,
                            onAddToCart = { viewModel.addToCart(item) },
                            onIncrementCart = {
                                if (cartIndex >= 0) viewModel.incrementCartItem(cartIndex)
                            },
                            onDecrementCart = {
                                if (cartIndex >= 0) viewModel.decrementCartItem(cartIndex)
                            },
                            onEditItem = { itemEditTarget = item },
                            onDeleteItem = { itemToDelete = item },
                            onClick = { itemDetailTarget = item }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog
    itemDetailTarget?.let { item ->
        ItemDetailDialog(
            item = item,
            onDismiss = { itemDetailTarget = null },
            onAddToCart = { viewModel.addToCart(item) }
        )
    }

    // Add / Edit Dialog
    if (showAddItemDialog || itemEditTarget != null) {
        AddEditItemDialog(
            itemToEdit = itemEditTarget,
            categories = categories,
            brands = brands,
            specs = specs,
            onDismiss = {
                showAddItemDialog = false
                itemEditTarget = null
            },
            onSave = { savedItem ->
                viewModel.saveItem(savedItem)
                showAddItemDialog = false
                itemEditTarget = null
            },
            onAddNewCategory = { viewModel.addCategory(it) },
            onAddNewBrand = { viewModel.addBrand(it) }
        )
    }

    // Dialog: Delete All Items (PIN: 110116)
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteAllDialog = false
                deleteAllPin = ""
                deleteAllError = null
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = StatusDanger,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text("Delete All Items?", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Are you sure you want to delete ALL items from the inventory? This action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Security PIN required (Default PIN: 110116):",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                    OutlinedTextField(
                        value = deleteAllPin,
                        onValueChange = {
                            deleteAllPin = it
                            deleteAllError = null
                        },
                        label = { Text("Enter 6-digit PIN") },
                        placeholder = { Text("110116") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        isError = deleteAllError != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_delete_all_pin_search")
                    )
                    if (deleteAllError != null) {
                        Text(
                            text = deleteAllError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllItems(deleteAllPin) { success, msg ->
                            if (success) {
                                showDeleteAllDialog = false
                                deleteAllPin = ""
                                deleteAllError = null
                            } else {
                                deleteAllError = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger),
                    modifier = Modifier.testTag("btn_confirm_delete_all")
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteAllDialog = false
                    deleteAllPin = ""
                    deleteAllError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Restore Default Catalogue
    if (showRestoreDefaultDialog) {
        AlertDialog(
            onDismissRequest = {
                showRestoreDefaultDialog = false
                restorePin = ""
                restoreError = null
            },
            title = {
                Text("Restore Default Radiators?", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "This will reload the ~80 default radiator and heater models. Enter security PIN (default: 110116):",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = restorePin,
                        onValueChange = {
                            restorePin = it
                            restoreError = null
                        },
                        label = { Text("Enter 6-digit PIN") },
                        placeholder = { Text("110116") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        isError = restoreError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (restoreError != null) {
                        Text(
                            text = restoreError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restoreDefaultCatalogue(restorePin) { success, msg ->
                            if (success) {
                                showRestoreDefaultDialog = false
                                restorePin = ""
                                restoreError = null
                            } else {
                                restoreError = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRestoreDefaultDialog = false
                    restorePin = ""
                    restoreError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Delete Single Item
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Item?") },
            text = {
                Text("Remove '${item.name}' from your catalogue?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteItem(item)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
