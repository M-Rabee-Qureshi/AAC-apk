package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val subtotal: Double = 0.0,
    val itemDiscountsTotal: Double = 0.0,
    val overallDiscount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val status: String = "PAID", // PAID, PENDING
    val notes: String = "",
    val isSynced: Boolean = false
)

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceId: Long,
    val itemId: Long? = null,
    val name: String,
    val category: String = "",
    val brand: String = "",
    val mm: Int? = null,
    val transmission: String? = null,
    val originalRate: Double,
    val discountPerUnit: Double = 0.0,
    val finalRate: Double,
    val quantity: Int = 1,
    val lineTotal: Double
)

data class InvoiceWithItems(
    @Embedded val invoice: InvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<InvoiceItemEntity>
)
