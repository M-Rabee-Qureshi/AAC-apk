package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BrandEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ItemEntity
import com.example.data.model.SpecDefinitionEntity
import com.example.ui.theme.MintContainer
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ItemDetailDialog(
    item: ItemEntity,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit
) {
    val clipboard: ClipboardManager = LocalClipboardManager.current
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Item Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rate Banner (Yellow Automotive Hero)
                Surface(
                    color = com.example.ui.theme.BrandYellowContainer,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.BrandYellow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Catalog Sale Rate",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = com.example.ui.theme.OnBrandYellowContainer
                        )
                        Text(
                            text = "Rs " + fmt.format(item.rate),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = com.example.ui.theme.DarkSlate
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SPECIFICATIONS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                SpecRow(label = "Brand", value = item.brand)
                SpecRow(label = "Category", value = item.category)
                if (item.mm != null && item.mm > 0) {
                    SpecRow(label = "Core Thickness", value = "${item.mm} mm")
                }
                if (!item.transmission.isNullOrBlank()) {
                    SpecRow(label = "Transmission", value = item.transmission)
                }
                if (item.specifications.isNotBlank()) {
                    SpecRow(label = "Other Specs", value = item.specifications)
                }
                if (item.notes.isNotBlank()) {
                    SpecRow(label = "Notes", value = item.notes)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val info = "${item.name}\nBrand: ${item.brand} | Rate: Rs ${fmt.format(item.rate)}\nSpecs: ${item.mm ?: ""}mm ${item.transmission ?: ""} ${item.specifications}"
                            clipboard.setText(AnnotatedString(info))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Info")
                    }

                    Button(
                        onClick = {
                            onAddToCart()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.example.ui.theme.BrandYellow,
                            contentColor = com.example.ui.theme.DarkSlate
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemDialog(
    itemToEdit: ItemEntity? = null,
    categories: List<CategoryEntity>,
    brands: List<BrandEntity>,
    specs: List<SpecDefinitionEntity>,
    onDismiss: () -> Unit,
    onSave: (ItemEntity) -> Unit,
    onAddNewCategory: (String) -> Unit,
    onAddNewBrand: (String) -> Unit
) {
    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(itemToEdit?.category ?: categories.firstOrNull()?.name ?: "Radiators") }
    var selectedBrand by remember { mutableStateOf(itemToEdit?.brand ?: brands.firstOrNull()?.name ?: "AutoCool") }
    var rateText by remember { mutableStateOf(if (itemToEdit != null && itemToEdit.rate > 0) itemToEdit.rate.toLong().toString() else "") }
    var mmText by remember { mutableStateOf(itemToEdit?.mm?.toString() ?: "") }
    var transmission by remember { mutableStateOf(itemToEdit?.transmission ?: "MT") }
    var specifications by remember { mutableStateOf(itemToEdit?.specifications ?: "") }
    var notes by remember { mutableStateOf(itemToEdit?.notes ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }

    var showNewCatDialog by remember { mutableStateOf(false) }
    var showNewBrandDialog by remember { mutableStateOf(false) }
    var newCatName by remember { mutableStateOf("") }
    var newBrandName by remember { mutableStateOf("") }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (itemToEdit == null) "Add New Item" else "Edit Item",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Item Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMsg = null },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g. Radiator Toyota Corolla 2009-13 MT") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_item_name"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        selectedCategory = cat.name
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(onClick = { showNewCatDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "New Category", tint = PrimaryGreen)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Brand Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExposedDropdownMenuBox(
                        expanded = brandExpanded,
                        onExpandedChange = { brandExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedBrand,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Brand / Make") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = brandExpanded,
                            onDismissRequest = { brandExpanded = false }
                        ) {
                            brands.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b.name) },
                                    onClick = {
                                        selectedBrand = b.name
                                        brandExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(onClick = { showNewBrandDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "New Brand", tint = PrimaryGreen)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rate & MM Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it; errorMsg = null },
                        label = { Text("Rate (PKR) *") },
                        placeholder = { Text("9500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_item_rate"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = mmText,
                        onValueChange = { mmText = it },
                        label = { Text("Core MM") },
                        placeholder = { Text("16 or 26") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.7f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Transmission row chips
                Text(text = "Transmission", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MT", "AT", "Universal", "None").forEach { option ->
                        val isSelected = transmission.equals(option, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PrimaryGreen else Color(0xFFF1F3F2),
                            modifier = Modifier.weight(1f)
                        ) {
                            TextButton(
                                onClick = { transmission = if (option == "None") "" else option }
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Additional specifications
                OutlinedTextField(
                    value = specifications,
                    onValueChange = { specifications = it },
                    label = { Text("Custom Specifications") },
                    placeholder = { Text("e.g. Aluminum, 1300cc, Double Pipe") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Internal Notes") },
                    placeholder = { Text("e.g. Best seller, Rack #4") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Dialog Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMsg = "Item name is required."
                                return@Button
                            }
                            val rate = rateText.toDoubleOrNull()
                            if (rate == null || rate < 0) {
                                errorMsg = "Please enter a valid rate."
                                return@Button
                            }
                            val mm = mmText.toIntOrNull()

                            val result = itemToEdit?.copy(
                                name = name.trim(),
                                category = selectedCategory,
                                brand = selectedBrand,
                                rate = rate,
                                mm = mm,
                                transmission = transmission.ifBlank { null },
                                specifications = specifications.trim(),
                                notes = notes.trim(),
                                updatedAt = System.currentTimeMillis()
                            ) ?: ItemEntity(
                                name = name.trim(),
                                category = selectedCategory,
                                brand = selectedBrand,
                                rate = rate,
                                mm = mm,
                                transmission = transmission.ifBlank { null },
                                specifications = specifications.trim(),
                                notes = notes.trim()
                            )

                            onSave(result)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        modifier = Modifier.testTag("btn_save_item")
                    ) {
                        Text(if (itemToEdit == null) "Add Item" else "Save Changes")
                    }
                }
            }
        }
    }

    // Sub-dialog: New Category
    if (showNewCatDialog) {
        AlertDialog(
            onDismissRequest = { showNewCatDialog = false },
            title = { Text("Add Category") },
            text = {
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Category Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newCatName.isNotBlank()) {
                        onAddNewCategory(newCatName.trim())
                        selectedCategory = newCatName.trim()
                        newCatName = ""
                        showNewCatDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showNewCatDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Sub-dialog: New Brand
    if (showNewBrandDialog) {
        AlertDialog(
            onDismissRequest = { showNewBrandDialog = false },
            title = { Text("Add Brand") },
            text = {
                OutlinedTextField(
                    value = newBrandName,
                    onValueChange = { newBrandName = it },
                    label = { Text("Brand Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newBrandName.isNotBlank()) {
                        onAddNewBrand(newBrandName.trim())
                        selectedBrand = newBrandName.trim()
                        newBrandName = ""
                        showNewBrandDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showNewBrandDialog = false }) { Text("Cancel") }
            }
        )
    }
}
