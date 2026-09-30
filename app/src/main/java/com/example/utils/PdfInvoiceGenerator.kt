package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import com.example.data.model.InvoiceWithItems
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfInvoiceGenerator {

    private const val PAGE_WIDTH = 595 // A4 standard pt width
    private const val PAGE_HEIGHT = 842 // A4 standard pt height

    fun formatMoney(amount: Double): String {
        val format = NumberFormat.getNumberInstance(Locale.US)
        format.maximumFractionDigits = 0
        return "Rs " + format.format(amount)
    }

    fun generatePdf(context: Context, invoiceWithItems: InvoiceWithItems): File {
        val invoice = invoiceWithItems.invoice
        val items = invoiceWithItems.items

        val pdfDoc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDoc.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Palette
        val colorPrimary = Color.rgb(23, 63, 53) // #173F35 Forest Green
        val colorAccent = Color.rgb(217, 122, 53) // #D97A35 Warm Amber
        val colorTint = Color.rgb(246, 248, 246)
        val colorLine = Color.rgb(228, 233, 229)
        val colorText = Color.rgb(29, 43, 39)
        val colorMuted = Color.rgb(113, 128, 121)
        val colorDiscount = Color.rgb(185, 80, 76)

        // Header Band
        paint.color = colorPrimary
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 105f, paint)

        paint.color = colorAccent
        canvas.drawRect(0f, 105f, PAGE_WIDTH.toFloat(), 110f, paint)

        var textStartX = 36f

        // Draw official AAC Logo on white badge if available
        try {
            val logoBitmap = android.graphics.BitmapFactory.decodeResource(
                context.resources,
                com.example.R.drawable.aac_logo_1790773852854
            )
            if (logoBitmap != null) {
                val logoCardW = 76f
                val logoCardH = 62f
                val cardY = 22f

                // White card background for logo
                paint.color = Color.WHITE
                paint.style = Paint.Style.FILL
                canvas.drawRoundRect(36f, cardY, 36f + logoCardW, cardY + logoCardH, 6f, 6f, paint)

                // Draw bitmap centered in card
                val destRect = android.graphics.RectF(38f, cardY + 2f, 36f + logoCardW - 2f, cardY + logoCardH - 2f)
                canvas.drawBitmap(logoBitmap, null, destRect, paint)

                textStartX = 36f + logoCardW + 14f
            }
        } catch (_: Exception) {
            textStartX = 36f
        }

        // Header Title
        paint.color = Color.WHITE
        paint.isFakeBoldText = true
        paint.textSize = 17f
        canvas.drawText("ASAD AUTO CORPORATION", textStartX, 44f, paint)

        paint.textSize = 9f
        paint.isFakeBoldText = false
        canvas.drawText("AAC Auto Parts  |  Radiators, Tractor Parts & Cooling Systems", textStartX, 62f, paint)
        canvas.drawText("Phone: 0301 6181234  |  61 Madina Market, Badami Bagh, Lahore", textStartX, 78f, paint)

        // Invoice Label on top right
        paint.isFakeBoldText = true
        paint.textSize = 24f
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("INVOICE", (PAGE_WIDTH - 36).toFloat(), 55f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Customer Bill To Card
        var y = 135f
        val boxWidth = (PAGE_WIDTH - 72 - 16) / 2f
        val boxHeight = 76f

        // Left box: Customer details
        paint.color = colorTint
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(36f, y, 36f + boxWidth, y + boxHeight, 8f, 8f, paint)

        paint.color = colorAccent
        canvas.drawRect(36f, y, 40f, y + boxHeight, paint)

        paint.color = colorAccent
        paint.textSize = 9f
        paint.isFakeBoldText = true
        canvas.drawText("BILL TO", 50f, y + 18f, paint)

        paint.color = colorText
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText(invoice.customerName, 50f, y + 36f, paint)

        paint.color = colorMuted
        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Phone: ${invoice.customerPhone}", 50f, y + 52f, paint)
        if (invoice.customerAddress.isNotBlank()) {
            canvas.drawText("Address: ${invoice.customerAddress}", 50f, y + 67f, paint)
        }

        // Right box: Invoice meta
        val rightBoxX = 36f + boxWidth + 16f
        paint.color = colorTint
        canvas.drawRoundRect(rightBoxX, y, rightBoxX + boxWidth, y + boxHeight, 8f, 8f, paint)

        paint.color = colorPrimary
        canvas.drawRect(rightBoxX, y, rightBoxX + 4f, y + boxHeight, paint)

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date(invoice.dateMillis))

        paint.textSize = 9.5f
        paint.color = colorMuted
        canvas.drawText("Invoice No:", rightBoxX + 14f, y + 22f, paint)
        paint.color = colorPrimary
        paint.isFakeBoldText = true
        canvas.drawText(invoice.invoiceNumber, rightBoxX + 85f, y + 22f, paint)

        paint.color = colorMuted
        paint.isFakeBoldText = false
        canvas.drawText("Date:", rightBoxX + 14f, y + 42f, paint)
        paint.color = colorText
        canvas.drawText(dateStr, rightBoxX + 85f, y + 42f, paint)

        paint.color = colorMuted
        canvas.drawText("Status:", rightBoxX + 14f, y + 62f, paint)
        paint.color = if (invoice.status == "PAID") Color.rgb(46, 139, 103) else colorAccent
        paint.isFakeBoldText = true
        canvas.drawText(invoice.status, rightBoxX + 85f, y + 62f, paint)

        // Table Header
        y = 230f
        paint.color = colorPrimary
        canvas.drawRect(36f, y, (PAGE_WIDTH - 36).toFloat(), y + 24f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9.5f
        paint.isFakeBoldText = true

        val colNo = 46f
        val colItem = 76f
        val colRate = 320f
        val colDisc = 390f
        val colQty = 450f
        val colTotal = (PAGE_WIDTH - 46).toFloat()

        canvas.drawText("#", colNo, y + 16f, paint)
        canvas.drawText("ITEM DESCRIPTION", colItem, y + 16f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("RATE", colRate, y + 16f, paint)
        canvas.drawText("DISC.", colDisc, y + 16f, paint)
        canvas.drawText("QTY", colQty, y + 16f, paint)
        canvas.drawText("AMOUNT", colTotal, y + 16f, paint)
        paint.textAlign = Paint.Align.LEFT

        y += 24f

        // Table Rows
        items.forEachIndexed { index, item ->
            val rowHeight = 28f
            if (index % 2 == 1) {
                paint.color = colorTint
                canvas.drawRect(36f, y, (PAGE_WIDTH - 36).toFloat(), y + rowHeight, paint)
            }

            paint.color = colorMuted
            paint.textSize = 9f
            paint.isFakeBoldText = false
            canvas.drawText((index + 1).toString(), colNo, y + 17f, paint)

            paint.color = colorText
            paint.isFakeBoldText = true
            paint.textSize = 9.5f

            // Shorten name if too long
            var displayName = item.name
            if (displayName.length > 36) {
                displayName = displayName.substring(0, 33) + "..."
            }
            canvas.drawText(displayName, colItem, y + 14f, paint)

            // Sub text: Brand & specs
            val subText = listOfNotNull(
                item.brand.ifBlank { null },
                if (item.mm != null && item.mm > 0) "${item.mm}mm" else null,
                item.transmission?.ifBlank { null }
            ).joinToString(" · ")

            if (subText.isNotBlank()) {
                paint.color = colorMuted
                paint.isFakeBoldText = false
                paint.textSize = 7.5f
                canvas.drawText(subText, colItem, y + 24f, paint)
            }

            paint.textAlign = Paint.Align.RIGHT
            paint.textSize = 9f
            paint.isFakeBoldText = false
            paint.color = colorText
            canvas.drawText(formatMoney(item.originalRate), colRate, y + 17f, paint)

            if (item.discountPerUnit > 0) {
                paint.color = colorDiscount
                canvas.drawText("-${formatMoney(item.discountPerUnit)}", colDisc, y + 17f, paint)
            } else {
                paint.color = colorMuted
                canvas.drawText("—", colDisc, y + 17f, paint)
            }

            paint.color = colorText
            canvas.drawText(item.quantity.toString(), colQty, y + 17f, paint)

            paint.isFakeBoldText = true
            canvas.drawText(formatMoney(item.lineTotal), colTotal, y + 17f, paint)
            paint.textAlign = Paint.Align.LEFT

            y += rowHeight
        }

        // Table bottom border
        paint.color = colorLine
        paint.strokeWidth = 1f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(36f, y, (PAGE_WIDTH - 36).toFloat(), y, paint)
        paint.style = Paint.Style.FILL

        // Totals Card on Bottom Right
        y += 18f
        val totBoxW = 210f
        val totBoxX = (PAGE_WIDTH - 36 - totBoxW).toFloat()

        paint.textSize = 9.5f
        paint.color = colorMuted
        paint.isFakeBoldText = false
        canvas.drawText("Subtotal", totBoxX, y + 10f, paint)
        paint.textAlign = Paint.Align.RIGHT
        paint.color = colorText
        paint.isFakeBoldText = true
        canvas.drawText(formatMoney(invoice.subtotal), (PAGE_WIDTH - 36).toFloat(), y + 10f, paint)
        paint.textAlign = Paint.Align.LEFT

        if (invoice.itemDiscountsTotal > 0) {
            y += 18f
            paint.color = colorDiscount
            paint.isFakeBoldText = false
            canvas.drawText("Item Discounts", totBoxX, y + 10f, paint)
            paint.textAlign = Paint.Align.RIGHT
            paint.isFakeBoldText = true
            canvas.drawText("- ${formatMoney(invoice.itemDiscountsTotal)}", (PAGE_WIDTH - 36).toFloat(), y + 10f, paint)
            paint.textAlign = Paint.Align.LEFT
        }

        if (invoice.overallDiscount > 0) {
            y += 18f
            paint.color = colorDiscount
            paint.isFakeBoldText = false
            canvas.drawText("Extra Discount", totBoxX, y + 10f, paint)
            paint.textAlign = Paint.Align.RIGHT
            paint.isFakeBoldText = true
            canvas.drawText("- ${formatMoney(invoice.overallDiscount)}", (PAGE_WIDTH - 36).toFloat(), y + 10f, paint)
            paint.textAlign = Paint.Align.LEFT
        }

        // Grand Total Badge
        y += 24f
        paint.color = colorPrimary
        canvas.drawRoundRect(totBoxX - 8f, y - 4f, (PAGE_WIDTH - 36).toFloat(), y + 28f, 6f, 6f, paint)

        paint.color = Color.WHITE
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText("TOTAL", totBoxX + 6f, y + 17f, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 13f
        canvas.drawText(formatMoney(invoice.grandTotal), (PAGE_WIDTH - 46).toFloat(), y + 17f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Signature Section
        val sigY = PAGE_HEIGHT - 90f
        paint.color = colorMuted
        paint.strokeWidth = 0.8f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(36f, sigY, 190f, sigY, paint)
        paint.style = Paint.Style.FILL
        paint.textSize = 9f
        paint.isFakeBoldText = false
        canvas.drawText("Authorised Signature", 36f, sigY + 14f, paint)

        // Bottom Footer Banner
        paint.color = colorPrimary
        canvas.drawRect(0f, (PAGE_HEIGHT - 38).toFloat(), PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), paint)

        paint.color = colorAccent
        canvas.drawRect(0f, (PAGE_HEIGHT - 41).toFloat(), PAGE_WIDTH.toFloat(), (PAGE_HEIGHT - 38).toFloat(), paint)

        paint.color = Color.WHITE
        paint.textSize = 9.5f
        paint.isFakeBoldText = true
        canvas.drawText("Thank you for your business!", 36f, (PAGE_HEIGHT - 16).toFloat(), paint)

        paint.textSize = 8.5f
        paint.isFakeBoldText = false
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("61 Madina Market, Badami Bagh, Lahore · Page 1 of 1", (PAGE_WIDTH - 36).toFloat(), (PAGE_HEIGHT - 16).toFloat(), paint)

        pdfDoc.finishPage(page)

        val cleanNumber = invoice.invoiceNumber.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        val file = File(context.cacheDir, "Invoice_${cleanNumber}.pdf")
        val fos = FileOutputStream(file)
        pdfDoc.writeTo(fos)
        fos.close()
        pdfDoc.close()

        return file
    }
}
