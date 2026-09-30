package com.example.data.remote

import com.example.data.local.AppDatabase
import com.example.data.model.BrandEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseSyncService(
    private val config: SupabaseConfig,
    private val database: AppDatabase
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
        if (!config.isConfigured()) {
            return@withContext Result.failure(Exception("Supabase URL and Anon Key are required."))
        }
        try {
            val url = "${config.supabaseUrl}/rest/v1/items?select=id&limit=1"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", config.anonKey)
                .addHeader("Authorization", "Bearer ${config.anonKey}")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success("Connection successful! Connected to Supabase.")
                } else {
                    val body = response.body?.string() ?: ""
                    Result.failure(Exception("HTTP ${response.code}: $body"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun syncAll(): Result<String> = withContext(Dispatchers.IO) {
        if (!config.isConfigured()) {
            return@withContext Result.failure(Exception("Supabase is not configured."))
        }

        try {
            var itemsUploaded = 0
            var itemsDownloaded = 0

            // 1. Sync Categories to Supabase
            val categoriesJson = JSONArray()
            database.categoryDao().getAllCategories()
            val localCategories = database.categoryDao().getCount()
            // Pull cloud categories
            val catReq = Request.Builder()
                .url("${config.supabaseUrl}/rest/v1/categories?select=*")
                .addHeader("apikey", config.anonKey)
                .addHeader("Authorization", "Bearer ${config.anonKey}")
                .get()
                .build()

            try {
                client.newCall(catReq).execute().use { res ->
                    if (res.isSuccessful) {
                        val body = res.body?.string()
                        if (!body.isNullOrBlank()) {
                            val arr = JSONArray(body)
                            val list = mutableListOf<CategoryEntity>()
                            for (i in 0 until arr.length()) {
                                val obj = arr.getJSONObject(i)
                                val name = obj.optString("name")
                                if (name.isNotBlank()) {
                                    list.add(CategoryEntity(name = name))
                                }
                            }
                            if (list.isNotEmpty()) {
                                database.categoryDao().insertAll(list)
                            }
                        }
                    }
                }
            } catch (_: Exception) { }

            // 2. Upload unsynced local items to Supabase
            val unsyncedItems = database.itemDao().getUnsyncedItems()
            if (unsyncedItems.isNotEmpty()) {
                val batchArr = JSONArray()
                for (item in unsyncedItems) {
                    val obj = JSONObject().apply {
                        put("name", item.name)
                        put("category", item.category)
                        put("brand", item.brand)
                        put("rate", item.rate)
                        if (item.mm != null) put("mm", item.mm)
                        if (item.transmission != null) put("transmission", item.transmission)
                        put("specifications", item.specifications)
                        put("notes", item.notes)
                    }
                    batchArr.put(obj)
                }

                val pushReq = Request.Builder()
                    .url("${config.supabaseUrl}/rest/v1/items")
                    .addHeader("apikey", config.anonKey)
                    .addHeader("Authorization", "Bearer ${config.anonKey}")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "resolution=merge-duplicates")
                    .post(batchArr.toString().toRequestBody(jsonMediaType))
                    .build()

                client.newCall(pushReq).execute().use { res ->
                    if (res.isSuccessful) {
                        database.itemDao().markSynced(unsyncedItems.map { it.id })
                        itemsUploaded = unsyncedItems.size
                    }
                }
            }

            // 3. Pull latest items from Supabase
            val pullReq = Request.Builder()
                .url("${config.supabaseUrl}/rest/v1/items?select=*&order=name.asc")
                .addHeader("apikey", config.anonKey)
                .addHeader("Authorization", "Bearer ${config.anonKey}")
                .get()
                .build()

            client.newCall(pullReq).execute().use { res ->
                if (res.isSuccessful) {
                    val body = res.body?.string()
                    if (!body.isNullOrBlank()) {
                        val arr = JSONArray(body)
                        val fetchedItems = mutableListOf<ItemEntity>()
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            val name = obj.optString("name")
                            if (name.isBlank()) continue
                            val cat = obj.optString("category", "Radiators")
                            val brand = obj.optString("brand", "AutoCool")
                            val rate = obj.optDouble("rate", 0.0)
                            val mm = if (obj.has("mm") && !obj.isNull("mm")) obj.getInt("mm") else null
                            val trans = if (obj.has("transmission") && !obj.isNull("transmission")) obj.getString("transmission") else null
                            val specs = obj.optString("specifications", "")
                            val notes = obj.optString("notes", "")

                            fetchedItems.add(
                                ItemEntity(
                                    name = name,
                                    category = cat,
                                    brand = brand,
                                    rate = rate,
                                    mm = mm,
                                    transmission = trans,
                                    specifications = specs,
                                    notes = notes,
                                    isSynced = true
                                )
                            )
                        }
                        if (fetchedItems.isNotEmpty()) {
                            database.itemDao().insertAll(fetchedItems)
                            itemsDownloaded = fetchedItems.size
                        }
                    }
                }
            }

            // 4. Sync Unsynced Invoices
            val unsyncedInvoices = database.invoiceDao().getUnsyncedInvoices()
            if (unsyncedInvoices.isNotEmpty()) {
                val invoicesArr = JSONArray()
                val itemsArr = JSONArray()

                for (inv in unsyncedInvoices) {
                    val i = inv.invoice
                    val obj = JSONObject().apply {
                        put("invoice_number", i.invoiceNumber)
                        put("customer_name", i.customerName)
                        put("customer_phone", i.customerPhone)
                        put("customer_address", i.customerAddress)
                        put("date_millis", i.dateMillis)
                        put("subtotal", i.subtotal)
                        put("item_discounts_total", i.itemDiscountsTotal)
                        put("overall_discount", i.overallDiscount)
                        put("grand_total", i.grandTotal)
                        put("status", i.status)
                        put("notes", i.notes)
                    }
                    invoicesArr.put(obj)

                    for (item in inv.items) {
                        val itObj = JSONObject().apply {
                            put("invoice_number", i.invoiceNumber)
                            put("name", item.name)
                            put("category", item.category)
                            put("brand", item.brand)
                            if (item.mm != null) put("mm", item.mm)
                            if (item.transmission != null) put("transmission", item.transmission)
                            put("original_rate", item.originalRate)
                            put("discount_per_unit", item.discountPerUnit)
                            put("final_rate", item.finalRate)
                            put("quantity", item.quantity)
                            put("line_total", item.lineTotal)
                        }
                        itemsArr.put(itObj)
                    }
                }

                // Push invoices
                val invReq = Request.Builder()
                    .url("${config.supabaseUrl}/rest/v1/invoices")
                    .addHeader("apikey", config.anonKey)
                    .addHeader("Authorization", "Bearer ${config.anonKey}")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "resolution=merge-duplicates")
                    .post(invoicesArr.toString().toRequestBody(jsonMediaType))
                    .build()

                client.newCall(invReq).execute().use { res ->
                    if (res.isSuccessful) {
                        database.invoiceDao().markInvoicesSynced(unsyncedInvoices.map { it.invoice.id })
                    }
                }

                // Push invoice items
                if (itemsArr.length() > 0) {
                    val itReq = Request.Builder()
                        .url("${config.supabaseUrl}/rest/v1/invoice_items")
                        .addHeader("apikey", config.anonKey)
                        .addHeader("Authorization", "Bearer ${config.anonKey}")
                        .addHeader("Content-Type", "application/json")
                        .post(itemsArr.toString().toRequestBody(jsonMediaType))
                        .build()

                    client.newCall(itReq).execute().close()
                }
            }

            config.lastSyncTime = System.currentTimeMillis()

            Result.success("Sync complete! $itemsUploaded uploaded, $itemsDownloaded synced from Supabase.")
        } catch (e: Exception) {
            Result.failure(Exception("Sync failed: ${e.localizedMessage}"))
        }
    }
}
