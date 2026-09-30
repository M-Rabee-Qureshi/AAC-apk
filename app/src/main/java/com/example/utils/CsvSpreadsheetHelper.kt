package com.example.utils

import android.content.Context
import com.example.data.model.ItemEntity
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader

object CsvSpreadsheetHelper {

    fun generateTemplateCsv(): String {
        return buildString {
            appendLine("Name,Category,Brand,Rate,MM,Transmission,Specifications,Notes")
            appendLine("\"Radiator Toyota Corolla 2009-13 MT\",Radiators,IMPORTED,9130,16,MT,\"Core 16mm, Aluminum\",\"High quality imported\"")
            appendLine("\"Radiator Suzuki Cultus New\",Radiators,AutoCool,5200,26,MT,\"Core 26mm\",\"Fast moving item\"")
            appendLine("\"Radiator Honda City 2024 A/T\",Radiators,Koolex,17500,16,AT,\"Core 16mm\",\"New model\"")
            appendLine("\"Heater Core Santro\",Heaters,AutoCool,4500,16,Universal,\"Fits Santro 1998-2004\",\"Copper tubes\"")
        }
    }

    fun exportItemsToCsv(items: List<ItemEntity>): String {
        return buildString {
            appendLine("Name,Category,Brand,Rate,MM,Transmission,Specifications,Notes")
            for (item in items) {
                append(escapeCsv(item.name)).append(",")
                append(escapeCsv(item.category)).append(",")
                append(escapeCsv(item.brand)).append(",")
                append(item.rate).append(",")
                append(item.mm ?: "").append(",")
                append(escapeCsv(item.transmission ?: "")).append(",")
                append(escapeCsv(item.specifications)).append(",")
                appendLine(escapeCsv(item.notes))
            }
        }
    }

    fun parseStream(inputStream: InputStream): List<ItemEntity> {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = mutableListOf<String>()
        var line = reader.readLine()
        while (line != null) {
            if (line.isNotBlank()) {
                lines.add(line)
            }
            line = reader.readLine()
        }
        return parseLines(lines)
    }

    fun parseText(text: String): List<ItemEntity> {
        val lines = text.lines().filter { it.isNotBlank() }
        return parseLines(lines)
    }

    private fun parseLines(lines: List<String>): List<ItemEntity> {
        if (lines.isEmpty()) return emptyList()

        // Check if tab-separated or comma-separated
        val headerTokens = tokenizeLine(lines[0])
        val colMap = mutableMapOf<String, Int>()
        for ((index, token) in headerTokens.withIndex()) {
            val clean = token.trim().lowercase()
            when {
                clean in listOf("name", "item", "product", "description", "product / description") -> colMap["name"] = index
                clean in listOf("category", "source", "type", "cat") -> colMap["category"] = index
                clean in listOf("brand", "make", "mfg") -> colMap["brand"] = index
                clean in listOf("rate", "sale rate", "price", "mrp", "cost", "sale_rate") -> colMap["rate"] = index
                clean in listOf("mm", "core mm", "thickness", "size") -> colMap["mm"] = index
                clean in listOf("transmission", "trans", "gear", "m/t", "a/t") -> colMap["transmission"] = index
                clean in listOf("specifications", "spec", "specs", "details") -> colMap["specifications"] = index
                clean in listOf("notes", "note", "remark", "remarks") -> colMap["notes"] = index
            }
        }

        // If no header matches, assume default order: Name, Category, Brand, Rate, MM, Trans...
        val hasHeaders = colMap.containsKey("name") || colMap.containsKey("rate")
        val startIndex = if (hasHeaders) 1 else 0

        val nameIdx = colMap["name"] ?: 0
        val catIdx = colMap["category"] ?: 1
        val brandIdx = colMap["brand"] ?: 2
        val rateIdx = colMap["rate"] ?: 3
        val mmIdx = colMap["mm"] ?: 4
        val transIdx = colMap["transmission"] ?: 5
        val specIdx = colMap["specifications"] ?: 6
        val notesIdx = colMap["notes"] ?: 7

        val result = mutableListOf<ItemEntity>()

        for (i in startIndex until lines.size) {
            val tokens = tokenizeLine(lines[i])
            if (tokens.isEmpty()) continue

            val name = tokens.getOrNull(nameIdx)?.trim() ?: ""
            if (name.isBlank()) continue

            val category = tokens.getOrNull(catIdx)?.trim()?.ifBlank { "Radiators" } ?: "Radiators"
            val brand = tokens.getOrNull(brandIdx)?.trim()?.ifBlank { "AutoCool" } ?: "AutoCool"
            val rateRaw = tokens.getOrNull(rateIdx)?.trim()?.replace("Rs", "", ignoreCase = true)?.replace(",", "") ?: "0"
            val rate = rateRaw.toDoubleOrNull() ?: 0.0

            val mmRaw = tokens.getOrNull(mmIdx)?.trim()?.filter { it.isDigit() }
            val mm = mmRaw?.toIntOrNull()

            var trans = tokens.getOrNull(transIdx)?.trim()?.ifBlank { null }
            if (trans == null) {
                // Infer transmission from name if present
                val upperName = name.uppercase()
                trans = when {
                    "A/T" in upperName || " AT " in upperName || upperName.endsWith(" AT") || "AUTO" in upperName -> "AT"
                    "M/T" in upperName || " MT " in upperName || upperName.endsWith(" MT") || "MANUAL" in upperName -> "MT"
                    else -> null
                }
            }

            val specs = tokens.getOrNull(specIdx)?.trim() ?: ""
            val notes = tokens.getOrNull(notesIdx)?.trim() ?: ""

            result.add(
                ItemEntity(
                    name = name,
                    category = category,
                    brand = brand,
                    rate = rate,
                    mm = mm,
                    transmission = trans,
                    specifications = specs,
                    notes = notes
                )
            )
        }

        return result
    }

    private fun tokenizeLine(line: String): List<String> {
        val delimiter = if (line.contains('\t')) '\t' else ','
        val tokens = mutableListOf<String>()
        val sb = java.lang.StringBuilder()
        var inQuotes = false

        for (ch in line) {
            when {
                ch == '\"' -> inQuotes = !inQuotes
                ch == delimiter && !inQuotes -> {
                    tokens.add(sb.toString().trim())
                    sb.setLength(0)
                }
                else -> sb.append(ch)
            }
        }
        tokens.add(sb.toString().trim())
        return tokens
    }

    private fun escapeCsv(value: String): String {
        if (value.contains(',') || value.contains('\"') || value.contains('\n')) {
            return "\"" + value.replace("\"", "\"\"") + "\""
        }
        return value
    }

    fun writeTemplateFile(context: Context): File {
        val file = File(context.cacheDir, "AAC_Item_Template.csv")
        file.writeText(generateTemplateCsv())
        return file
    }

    fun writeExportFile(context: Context, items: List<ItemEntity>): File {
        val file = File(context.cacheDir, "AAC_Inventory_Export_${System.currentTimeMillis()}.csv")
        file.writeText(exportItemsToCsv(items))
        return file
    }
}
