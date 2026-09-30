package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.InvoiceWithItems
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoiceRepository(private val database: AppDatabase) {

    fun getAllInvoices(): Flow<List<InvoiceWithItems>> = database.invoiceDao().getAllInvoices()

    fun getInvoiceById(id: Long): Flow<InvoiceWithItems?> = database.invoiceDao().getInvoiceById(id)

    suspend fun createInvoice(invoice: InvoiceEntity, items: List<InvoiceItemEntity>): Long {
        return database.invoiceDao().insertFullInvoice(invoice, items)
    }

    suspend fun deleteInvoice(id: Long) {
        database.invoiceDao().deleteInvoiceById(id)
    }

    suspend fun generateNextInvoiceNumber(): String {
        val last = database.invoiceDao().getLastInvoiceNumber()
        val count = database.invoiceDao().getInvoiceCount()
        val nextSeq = count + 1001
        return if (last != null && last.startsWith("AAC-")) {
            val numPart = last.removePrefix("AAC-").toIntOrNull()
            if (numPart != null) "AAC-${numPart + 1}" else "AAC-$nextSeq"
        } else {
            "AAC-$nextSeq"
        }
    }
}
