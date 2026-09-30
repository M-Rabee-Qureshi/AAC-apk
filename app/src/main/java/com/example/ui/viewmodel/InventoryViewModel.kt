package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.DefaultData
import com.example.data.model.BrandEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.InvoiceWithItems
import com.example.data.model.ItemEntity
import com.example.data.model.SpecDefinitionEntity
import com.example.data.remote.SupabaseConfig
import com.example.data.remote.SupabaseSyncService
import com.example.data.repository.InventoryRepository
import com.example.data.repository.InvoiceRepository
import com.example.utils.CsvSpreadsheetHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

data class CartItemDraft(
    val itemId: Long? = null,
    val name: String,
    val category: String = "",
    val brand: String = "",
    val mm: Int? = null,
    val transmission: String? = null,
    val originalRate: Double,
    val discountPerUnit: Double = 0.0,
    val quantity: Int = 1
) {
    val finalRate: Double get() = (originalRate - discountPerUnit).coerceAtLeast(0.0)
    val lineTotal: Double get() = finalRate * quantity
    val totalDiscount: Double get() = discountPerUnit * quantity
}

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModel(
    private val inventoryRepo: InventoryRepository,
    private val invoiceRepo: InvoiceRepository,
    val supabaseConfig: SupabaseConfig,
    val supabaseSyncService: SupabaseSyncService
) : ViewModel() {

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedBrand = MutableStateFlow("")
    val selectedBrand = _selectedBrand.asStateFlow()

    private val _selectedMm = MutableStateFlow<Int?>(null)
    val selectedMm = _selectedMm.asStateFlow()

    // Filtered Items Flow
    val items: StateFlow<List<ItemEntity>> = combine(
        _searchQuery,
        _selectedCategory,
        _selectedBrand,
        _selectedMm
    ) { q, cat, brand, mm ->
        FilterParams(q, cat, brand, mm)
    }.flatMapLatest { p ->
        inventoryRepo.filterItems(p.query, p.category, p.brand, p.mm)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categories: StateFlow<List<CategoryEntity>> = inventoryRepo.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val brands: StateFlow<List<BrandEntity>> = inventoryRepo.getAllBrands()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val specs: StateFlow<List<SpecDefinitionEntity>> = inventoryRepo.getAllSpecs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInventoryItems: StateFlow<List<ItemEntity>> = inventoryRepo.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart / Active Invoice Items
    private val _cartItems = MutableStateFlow<List<CartItemDraft>>(emptyList())
    val cartItems: StateFlow<List<CartItemDraft>> = _cartItems.asStateFlow()

    // Customer Information for Invoice
    var customerName = MutableStateFlow("")
    var customerPhone = MutableStateFlow("")
    var customerAddress = MutableStateFlow("")
    var overallDiscount = MutableStateFlow(0.0)
    var freightCharges = MutableStateFlow(0.0)
    var taxPercentage = MutableStateFlow(0.0)
    var amountPaid = MutableStateFlow(0.0)
    var paymentStatus = MutableStateFlow("PAID") // PAID, PARTIAL, DUE
    var paymentMethod = MutableStateFlow("CASH") // CASH, BANK_TRANSFER, JAZZCASH, CREDIT
    var invoiceNotes = MutableStateFlow("")

    // Sync State
    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage = _syncMessage.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    // Filter mutations
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun selectCategory(cat: String) { _selectedCategory.value = if (_selectedCategory.value == cat) "" else cat }
    fun selectBrand(brand: String) { _selectedBrand.value = if (_selectedBrand.value == brand) "" else brand }
    fun selectMm(mm: Int?) { _selectedMm.value = if (_selectedMm.value == mm) null else mm }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = ""
        _selectedBrand.value = ""
        _selectedMm.value = null
    }

    // Cart operations
    fun addToCart(item: ItemEntity) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.itemId == item.id }
        if (existingIndex >= 0) {
            val old = current[existingIndex]
            current[existingIndex] = old.copy(quantity = old.quantity + 1)
        } else {
            current.add(
                CartItemDraft(
                    itemId = item.id,
                    name = item.name,
                    category = item.category,
                    brand = item.brand,
                    mm = item.mm,
                    transmission = item.transmission,
                    originalRate = item.rate,
                    discountPerUnit = 0.0,
                    quantity = 1
                )
            )
        }
        _cartItems.value = current
    }

    fun incrementCartItem(index: Int) {
        val current = _cartItems.value.toMutableList()
        if (index in current.indices) {
            val it = current[index]
            current[index] = it.copy(quantity = it.quantity + 1)
            _cartItems.value = current
        }
    }

    fun decrementCartItem(index: Int) {
        val current = _cartItems.value.toMutableList()
        if (index in current.indices) {
            val it = current[index]
            if (it.quantity > 1) {
                current[index] = it.copy(quantity = it.quantity - 1)
            } else {
                current.removeAt(index)
            }
            _cartItems.value = current
        }
    }

    fun removeCartItem(index: Int) {
        val current = _cartItems.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _cartItems.value = current
        }
    }

    fun setItemDiscount(index: Int, discountPerUnit: Double) {
        val current = _cartItems.value.toMutableList()
        if (index in current.indices) {
            val it = current[index]
            current[index] = it.copy(discountPerUnit = discountPerUnit.coerceAtLeast(0.0))
            _cartItems.value = current
        }
    }

    fun setItemFinalRate(index: Int, finalRate: Double) {
        val current = _cartItems.value.toMutableList()
        if (index in current.indices) {
            val it = current[index]
            val disc = (it.originalRate - finalRate).coerceAtLeast(0.0)
            current[index] = it.copy(discountPerUnit = disc)
            _cartItems.value = current
        }
    }

    fun addCustomLineItem(name: String, rate: Double, quantity: Int, discount: Double = 0.0) {
        val current = _cartItems.value.toMutableList()
        current.add(
            CartItemDraft(
                itemId = null,
                name = name.trim(),
                originalRate = rate,
                discountPerUnit = discount,
                quantity = quantity
            )
        )
        _cartItems.value = current
    }

    fun removeItemById(itemId: Long) {
        val current = _cartItems.value.toMutableList()
        current.removeAll { it.itemId == itemId }
        _cartItems.value = current
    }

    fun setItemQuantity(itemId: Long, qty: Int) {
        if (qty <= 0) {
            removeItemById(itemId)
            return
        }
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.itemId == itemId }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = qty)
            _cartItems.value = current
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        customerName.value = ""
        customerPhone.value = ""
        customerAddress.value = ""
        overallDiscount.value = 0.0
        freightCharges.value = 0.0
        taxPercentage.value = 0.0
        amountPaid.value = 0.0
        paymentStatus.value = "PAID"
        paymentMethod.value = "CASH"
        invoiceNotes.value = ""
    }

    fun calculateCartSubtotal(): Double = _cartItems.value.sumOf { it.originalRate * it.quantity }
    fun calculateItemDiscounts(): Double = _cartItems.value.sumOf { it.totalDiscount }
    fun calculateNetBeforeTax(): Double {
        val sub = calculateCartSubtotal()
        val itemDisc = calculateItemDiscounts()
        val extraDisc = overallDiscount.value
        return (sub - itemDisc - extraDisc).coerceAtLeast(0.0)
    }
    fun calculateTaxAmount(): Double {
        val net = calculateNetBeforeTax()
        return (net * taxPercentage.value / 100.0).coerceAtLeast(0.0)
    }
    fun calculateGrandTotal(): Double {
        val net = calculateNetBeforeTax()
        val tax = calculateTaxAmount()
        val freight = freightCharges.value
        return (net + tax + freight).coerceAtLeast(0.0)
    }
    fun calculateBalanceDue(): Double {
        val grand = calculateGrandTotal()
        val paid = amountPaid.value
        return (grand - paid).coerceAtLeast(0.0)
    }

    // Save and Create Invoice
    suspend fun createInvoice(
        statusOverride: String? = null,
        notesOverride: String? = null
    ): InvoiceWithItems? {
        val items = _cartItems.value
        if (items.isEmpty()) return null
        val custName = customerName.value.trim().ifBlank { "Cash Customer" }
        val custPhone = customerPhone.value.trim()
        val custAddress = customerAddress.value.trim()

        val subtotal = calculateCartSubtotal()
        val itemDiscounts = calculateItemDiscounts()
        val extraDiscount = overallDiscount.value
        val grandTotal = calculateGrandTotal()
        val invoiceNumber = invoiceRepo.generateNextInvoiceNumber()

        val finalStatus = statusOverride ?: paymentStatus.value
        val finalNotes = notesOverride ?: invoiceNotes.value

        val invoice = InvoiceEntity(
            invoiceNumber = invoiceNumber,
            customerName = custName,
            customerPhone = custPhone,
            customerAddress = custAddress,
            dateMillis = System.currentTimeMillis(),
            subtotal = subtotal,
            itemDiscountsTotal = itemDiscounts,
            overallDiscount = extraDiscount,
            grandTotal = grandTotal,
            status = finalStatus,
            notes = finalNotes
        )

        val invoiceItems = items.map {
            InvoiceItemEntity(
                invoiceId = 0,
                itemId = it.itemId,
                name = it.name,
                category = it.category,
                brand = it.brand,
                mm = it.mm,
                transmission = it.transmission,
                originalRate = it.originalRate,
                discountPerUnit = it.discountPerUnit,
                finalRate = it.finalRate,
                quantity = it.quantity,
                lineTotal = it.lineTotal
            )
        }

        val invoiceId = invoiceRepo.createInvoice(invoice, invoiceItems)
        val createdInvoice = invoice.copy(id = invoiceId)
        val createdWithItems = InvoiceWithItems(
            invoice = createdInvoice,
            items = invoiceItems.map { it.copy(invoiceId = invoiceId) }
        )

        clearCart()

        // Background auto sync to Supabase if configured
        if (supabaseConfig.isConfigured() && supabaseConfig.autoSyncEnabled) {
            viewModelScope.launch {
                supabaseSyncService.syncAll()
            }
        }

        return createdWithItems
    }

    // CRUD Item
    fun saveItem(item: ItemEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            if (item.id == 0L) {
                inventoryRepo.insertItem(item)
            } else {
                inventoryRepo.updateItem(item)
            }
            onDone()
        }
    }

    fun deleteItem(item: ItemEntity) {
        viewModelScope.launch {
            inventoryRepo.deleteItem(item)
        }
    }

    fun deleteAllItems(pin: String, onResult: (Boolean, String?) -> Unit) {
        if (pin.trim() != DEFAULT_SECURITY_PIN) {
            onResult(false, "Incorrect PIN code. Please enter the correct PIN (default: $DEFAULT_SECURITY_PIN).")
            return
        }
        viewModelScope.launch {
            inventoryRepo.deleteAllItems()
            clearCart()
            onResult(true, "All items have been successfully deleted from inventory.")
        }
    }

    fun restoreDefaultCatalogue(pin: String, onResult: (Boolean, String?) -> Unit) {
        if (pin.trim() != DEFAULT_SECURITY_PIN) {
            onResult(false, "Incorrect PIN code. Please enter the correct PIN (default: $DEFAULT_SECURITY_PIN).")
            return
        }
        viewModelScope.launch {
            inventoryRepo.restoreDefaultItems()
            onResult(true, "Default catalogue restored successfully.")
        }
    }

    // Categories, Brands, Specs
    fun addCategory(name: String) {
        viewModelScope.launch {
            inventoryRepo.addCategory(name)
        }
    }

    fun deleteCategory(cat: CategoryEntity) {
        viewModelScope.launch {
            inventoryRepo.deleteCategory(cat)
        }
    }

    fun addBrand(name: String) {
        viewModelScope.launch {
            inventoryRepo.addBrand(name)
        }
    }

    fun deleteBrand(brand: BrandEntity) {
        viewModelScope.launch {
            inventoryRepo.deleteBrand(brand)
        }
    }

    fun addSpec(name: String, defaultValue: String = "") {
        viewModelScope.launch {
            inventoryRepo.addSpec(name, defaultValue)
        }
    }

    fun deleteSpec(spec: SpecDefinitionEntity) {
        viewModelScope.launch {
            inventoryRepo.deleteSpec(spec)
        }
    }

    // Excel / CSV Import & Export
    fun importSpreadsheet(inputStream: InputStream, onResult: (Int, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val parsed = CsvSpreadsheetHelper.parseStream(inputStream)
                if (parsed.isEmpty()) {
                    onResult(0, "No valid items found in spreadsheet.")
                    return@launch
                }
                inventoryRepo.bulkInsertItems(parsed)

                // Also ensure new categories and brands from spreadsheet get added
                val newCats = parsed.map { it.category }.distinct()
                newCats.forEach { inventoryRepo.addCategory(it) }
                val newBrands = parsed.map { it.brand }.distinct()
                newBrands.forEach { inventoryRepo.addBrand(it) }

                onResult(parsed.size, null)
            } catch (e: Exception) {
                onResult(0, e.localizedMessage)
            }
        }
    }

    suspend fun exportInventory(context: Context): File {
        val allItems = inventoryRepo.getAllItems()
        val list = items.value.ifEmpty { DefaultData.defaultItems }
        return CsvSpreadsheetHelper.writeExportFile(context, list)
    }

    // Supabase Sync
    fun syncWithSupabase() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Connecting to Supabase..."
            val result = supabaseSyncService.syncAll()
            _isSyncing.value = false
            _syncMessage.value = result.getOrElse { it.localizedMessage ?: "Sync error" }
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    private data class FilterParams(
        val query: String,
        val category: String,
        val brand: String,
        val mm: Int?
    )

    companion object {
        const val DEFAULT_SECURITY_PIN = "110116"

        fun provideFactory(
            inventoryRepo: InventoryRepository,
            invoiceRepo: InvoiceRepository,
            supabaseConfig: SupabaseConfig,
            supabaseSyncService: SupabaseSyncService
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return InventoryViewModel(inventoryRepo, invoiceRepo, supabaseConfig, supabaseSyncService) as T
            }
        }
    }
}
