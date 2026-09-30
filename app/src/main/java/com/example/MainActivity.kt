package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.model.InvoiceWithItems
import com.example.data.remote.SupabaseConfig
import com.example.data.remote.SupabaseSyncService
import com.example.data.repository.InventoryRepository
import com.example.data.repository.InvoiceRepository
import com.example.ui.screens.CategoriesBrandsSheet
import com.example.ui.screens.InvoiceBuilderScreen
import com.example.ui.screens.InvoiceHistoryScreen
import com.example.ui.screens.MarketingSearchScreen
import com.example.ui.screens.SpreadsheetManagerSheet
import com.example.ui.screens.SupabaseSettingsSheet
import com.example.ui.theme.MintContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.StatusGood
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.InventoryViewModel
import com.example.ui.viewmodel.InvoiceViewModel
import com.example.utils.PdfInvoiceGenerator
import java.text.NumberFormat
import java.util.Locale

enum class AppScreen {
    MARKETING_SEARCH,
    INVOICE_BUILDER,
    INVOICE_HISTORY,
    CATEGORIES_BRANDS,
    SPREADSHEET_MANAGER,
    SUPABASE_SETTINGS
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val inventoryRepo = InventoryRepository(database)
        val invoiceRepo = InvoiceRepository(database)
        val supabaseConfig = SupabaseConfig(applicationContext)
        val supabaseSyncService = SupabaseSyncService(supabaseConfig, database)

        setContent {
            MyApplicationTheme {
                val inventoryViewModel: InventoryViewModel = viewModel(
                    factory = InventoryViewModel.provideFactory(
                        inventoryRepo,
                        invoiceRepo,
                        supabaseConfig,
                        supabaseSyncService
                    )
                )
                val invoiceViewModel: InvoiceViewModel = viewModel(
                    factory = InvoiceViewModel.provideFactory(invoiceRepo)
                )

                var currentScreen by remember { mutableStateOf(AppScreen.MARKETING_SEARCH) }
                var recentlyCreatedInvoice by remember { mutableStateOf<InvoiceWithItems?>(null) }
                val context = LocalContext.current

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BoxWithPadding(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            AppScreen.MARKETING_SEARCH -> {
                                MarketingSearchScreen(
                                    viewModel = inventoryViewModel,
                                    onOpenInvoiceBuilder = { currentScreen = AppScreen.INVOICE_BUILDER },
                                    onOpenInvoicesHistory = { currentScreen = AppScreen.INVOICE_HISTORY },
                                    onOpenCategoriesBrands = { currentScreen = AppScreen.CATEGORIES_BRANDS },
                                    onOpenSpreadsheetManager = { currentScreen = AppScreen.SPREADSHEET_MANAGER },
                                    onOpenSupabaseSettings = { currentScreen = AppScreen.SUPABASE_SETTINGS }
                                )
                            }

                            AppScreen.INVOICE_BUILDER -> {
                                BackHandler { currentScreen = AppScreen.MARKETING_SEARCH }
                                InvoiceBuilderScreen(
                                    viewModel = inventoryViewModel,
                                    onBack = { currentScreen = AppScreen.MARKETING_SEARCH },
                                    onInvoiceCreated = { created ->
                                        recentlyCreatedInvoice = created
                                        currentScreen = AppScreen.MARKETING_SEARCH
                                    }
                                )
                            }

                            AppScreen.INVOICE_HISTORY -> {
                                BackHandler { currentScreen = AppScreen.MARKETING_SEARCH }
                                InvoiceHistoryScreen(
                                    viewModel = invoiceViewModel,
                                    onBack = { currentScreen = AppScreen.MARKETING_SEARCH }
                                )
                            }

                            AppScreen.CATEGORIES_BRANDS -> {
                                BackHandler { currentScreen = AppScreen.MARKETING_SEARCH }
                                CategoriesBrandsSheet(
                                    viewModel = inventoryViewModel,
                                    onBack = { currentScreen = AppScreen.MARKETING_SEARCH }
                                )
                            }

                            AppScreen.SPREADSHEET_MANAGER -> {
                                BackHandler { currentScreen = AppScreen.MARKETING_SEARCH }
                                SpreadsheetManagerSheet(
                                    viewModel = inventoryViewModel,
                                    onBack = { currentScreen = AppScreen.MARKETING_SEARCH }
                                )
                            }

                            AppScreen.SUPABASE_SETTINGS -> {
                                BackHandler { currentScreen = AppScreen.MARKETING_SEARCH }
                                SupabaseSettingsSheet(
                                    viewModel = inventoryViewModel,
                                    onBack = { currentScreen = AppScreen.MARKETING_SEARCH }
                                )
                            }
                        }
                    }

                    // Redesigned Invoice Created Success Popup Banner
                    recentlyCreatedInvoice?.let { invoiceWithItems ->
                        val inv = invoiceWithItems.invoice
                        val fmt = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }

                        androidx.compose.ui.window.Dialog(
                            onDismissRequest = { recentlyCreatedInvoice = null }
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                shadowElevation = 16.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(22.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Top Success Emblem
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(com.example.ui.theme.BrandYellowContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = StatusGood,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Invoice Created Successfully!",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = com.example.ui.theme.DarkSlate
                                    )

                                    Surface(
                                        color = com.example.ui.theme.BrandYellowContainer,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = inv.invoiceNumber,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = com.example.ui.theme.OnBrandYellowContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Receipt Summary Card
                                    Surface(
                                        color = com.example.ui.theme.CanvasBg,
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.LineBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Customer", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                                Text(inv.customerName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkSlate)
                                            }
                                            if (inv.customerPhone.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Phone", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                                    Text(inv.customerPhone, style = MaterialTheme.typography.bodySmall, color = com.example.ui.theme.DarkSlate)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Items Count", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                                Text("${invoiceWithItems.items.size} line items", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = com.example.ui.theme.DarkSlate)
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(com.example.ui.theme.LineBorder))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Grand Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkSlate)
                                                Text(
                                                    "Rs " + fmt.format(inv.grandTotal),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = com.example.ui.theme.BrandYellowDark
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Action Buttons
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                val pdfFile = PdfInvoiceGenerator.generatePdf(context, invoiceWithItems)
                                                val uri = FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    pdfFile
                                                )
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "application/pdf"
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share PDF Invoice"))
                                                recentlyCreatedInvoice = null
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = com.example.ui.theme.BrandYellow,
                                                contentColor = com.example.ui.theme.DarkSlate
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().height(44.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Share PDF Invoice", fontWeight = FontWeight.ExtraBold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val msg = invoiceViewModel.buildWhatsAppText(invoiceWithItems)
                                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, msg)
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "Send Quote"))
                                                recentlyCreatedInvoice = null
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().height(42.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = com.example.ui.theme.BrandBlue)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Send WhatsApp Quote", fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkSlate)
                                        }

                                        TextButton(
                                            onClick = { recentlyCreatedInvoice = null },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Done", color = TextMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxWithPadding(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(modifier = modifier.fillMaxSize()) {
        content()
    }
}
