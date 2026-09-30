package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.InvoiceWithItems
import com.example.data.repository.InvoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class InvoiceViewModel(
    private val invoiceRepo: InvoiceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("ALL") // ALL, PAID, PENDING
    val statusFilter = _statusFilter.asStateFlow()

    val invoices: StateFlow<List<InvoiceWithItems>> = combine(
        invoiceRepo.getAllInvoices(),
        _searchQuery,
        _statusFilter
    ) { list, query, status ->
        val q = query.trim().lowercase()
        list.filter { item ->
            val matchesQuery = q.isEmpty() ||
                item.invoice.invoiceNumber.lowercase().contains(q) ||
                item.invoice.customerName.lowercase().contains(q) ||
                item.invoice.customerPhone.lowercase().contains(q)
            val matchesStatus = status == "ALL" || item.invoice.status.equals(status, ignoreCase = true)
            matchesQuery && matchesStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String) {
        _statusFilter.value = status
    }

    fun deleteInvoice(id: Long) {
        viewModelScope.launch {
            invoiceRepo.deleteInvoice(id)
        }
    }

    fun buildWhatsAppText(invoiceWithItems: InvoiceWithItems): String {
        val inv = invoiceWithItems.invoice
        val fmt = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }
        return buildString {
            appendLine("*ASAD AUTO CORPORATION*")
            appendLine("AAC Auto Parts - Badami Bagh, Lahore")
            appendLine("------------------------------")
            appendLine("*Invoice:* ${inv.invoiceNumber}")
            appendLine("*Customer:* ${inv.customerName}")
            if (inv.customerPhone.isNotBlank()) appendLine("*Phone:* ${inv.customerPhone}")
            appendLine("------------------------------")
            invoiceWithItems.items.forEachIndexed { i, item ->
                appendLine("${i + 1}. *${item.name}*")
                val discText = if (item.discountPerUnit > 0) " (Disc: Rs ${fmt.format(item.discountPerUnit)})" else ""
                appendLine("   Rate: Rs ${fmt.format(item.finalRate)}$discText x ${item.quantity} = *Rs ${fmt.format(item.lineTotal)}*")
            }
            appendLine("------------------------------")
            appendLine("*Subtotal:* Rs ${fmt.format(inv.subtotal)}")
            if (inv.itemDiscountsTotal > 0) {
                appendLine("*Item Discounts:* -Rs ${fmt.format(inv.itemDiscountsTotal)}")
            }
            if (inv.overallDiscount > 0) {
                appendLine("*Extra Discount:* -Rs ${fmt.format(inv.overallDiscount)}")
            }
            appendLine("*GRAND TOTAL: Rs ${fmt.format(inv.grandTotal)}*")
            appendLine("------------------------------")
            appendLine("Thank you for your business!")
        }
    }

    companion object {
        fun provideFactory(invoiceRepo: InvoiceRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return InvoiceViewModel(invoiceRepo) as T
                }
            }
    }
}
