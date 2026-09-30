package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.LineBorder
import com.example.ui.theme.MintContainer
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.StatusGood
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.CartItemDraft
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InvoiceInventorySelector(
    inventoryItems: List<ItemEntity>,
    selectedCartItems: List<CartItemDraft>,
    onAddItem: (ItemEntity) -> Unit,
    onIncrementItem: (Long) -> Unit,
    onDecrementItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf("") }
    var selectedMm by remember { mutableStateOf<Int?>(null) }

    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    val categories = remember(inventoryItems) {
        listOf("All") + inventoryItems.map { it.category.trim() }.filter { it.isNotBlank() }.distinct()
    }

    val brands = remember(inventoryItems) {
        listOf("All") + inventoryItems.map { it.brand.trim() }.filter { it.isNotBlank() }.distinct()
    }

    val mmOptions = listOf(null, 16, 26, 32, 36, 48, 56)

    val filteredItems by remember(inventoryItems, searchQuery, selectedCategory, selectedBrand, selectedMm) {
        derivedStateOf {
            val q = searchQuery.trim().lowercase()
            inventoryItems.filter { item ->
                val matchesQuery = q.isEmpty() ||
                        item.name.lowercase().contains(q) ||
                        item.brand.lowercase().contains(q) ||
                        item.category.lowercase().contains(q) ||
                        item.specifications.lowercase().contains(q)

                val matchesCategory = selectedCategory.isEmpty() || selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
                val matchesBrand = selectedBrand.isEmpty() || selectedBrand == "All" || item.brand.equals(selectedBrand, ignoreCase = true)
                val matchesMm = selectedMm == null || item.mm == selectedMm

                matchesQuery && matchesCategory && matchesBrand && matchesMm
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(LineBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SELECT FROM INVENTORY",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    color = MintContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${filteredItems.size} items available",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("invoice_inventory_search"),
                placeholder = { Text("Search by car model, core, brand, or MM...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Chips (Horizontal Scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(8).forEach { cat ->
                    val isSelected = (cat == "All" && selectedCategory.isEmpty()) || selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = if (cat == "All" || selectedCategory == cat) "" else cat
                        },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Brand & MM quick pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Brand:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                brands.take(6).forEach { br ->
                    val isSelected = (br == "All" && selectedBrand.isEmpty()) || selectedBrand == br
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedBrand = if (br == "All" || selectedBrand == br) "" else br
                        },
                        label = { Text(br, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))
                Text("MM:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                mmOptions.forEach { mm ->
                    val label = if (mm == null) "All" else "${mm}mm"
                    val isSelected = selectedMm == mm
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedMm = if (isSelected) null else mm },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.85f),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inventory Items List (Max height container for smooth scrolling)
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching items found in inventory.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredItems.take(20).forEach { item ->
                        val inCartDraft = selectedCartItems.firstOrNull { it.itemId == item.id }
                        val quantityInInvoice = inCartDraft?.quantity ?: 0

                        InventoryPickItemRow(
                            item = item,
                            quantityInInvoice = quantityInInvoice,
                            formattedRate = fmt.format(item.rate),
                            onAdd = { onAddItem(item) },
                            onIncrement = { onIncrementItem(item.id) },
                            onDecrement = { onDecrementItem(item.id) }
                        )
                    }

                    if (filteredItems.size > 20) {
                        Text(
                            text = "Showing first 20 of ${filteredItems.size} items. Refine search to see others.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryPickItemRow(
    item: ItemEntity,
    quantityInInvoice: Int,
    formattedRate: String,
    onAdd: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (quantityInInvoice > 0) MintContainer.copy(alpha = 0.45f) else Color(0xFFFAFBFA),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (quantityInInvoice > 0) PrimaryGreen.copy(alpha = 0.5f) else LineBorder
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (item.brand.isNotBlank()) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, LineBorder)
                        ) {
                            Text(
                                text = item.brand,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }

                    if (item.category.isNotBlank()) {
                        Surface(
                            color = PrimaryGreen.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryGreen,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }

                    if (item.mm != null && item.mm > 0) {
                        Surface(
                            color = AccentOrange.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${item.mm}mm",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentOrange,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rs $formattedRate",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action / Quantity Controls
            if (quantityInInvoice == 0) {
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_add_inventory_item_${item.id}")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, PrimaryGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = "$quantityInInvoice in Bill",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
