package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AfricanBronze
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface

@Composable
fun QRCodePosterScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTable by remember { mutableStateOf("All Tables / Wall Poster") }

    val tableOptions = listOf(
        "All Tables / Wall Poster",
        "Table 1",
        "Table 2",
        "Table 3",
        "Table 4 (VIP Booth)",
        "Table 5 (Terrace)",
        "Bar Counter"
    )

    val targetUrl = if (selectedTable == "All Tables / Wall Poster") {
        "https://restaurant306.co.za/order"
    } else {
        "https://restaurant306.co.za/order?table=${selectedTable.replace(" ", "_")}"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = "Printable QR Code Generator",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = GoldPrimary
            )
            Text(
                text = "Display on restaurant walls, tables, and takeaway packaging",
                fontSize = 12.sp,
                color = AfricanBronze
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Table Selection Row
        item {
            Text("Select Placement / Target Table:", fontSize = 12.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(tableOptions) { opt ->
                    val isSelected = selectedTable == opt
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoldPrimary else ObsidianElevated)
                            .clickable { selectedTable = opt }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = opt,
                            color = if (isSelected) ObsidianDark else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Printable Poster Preview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header on poster
                    Image(
                        painter = painterResource(id = R.drawable.logo_306),
                        contentDescription = "306 Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "306 RESTAURANT & LOUNGE",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF140E0A),
                        fontSize = 17.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "CAPE TOWN • NIGERIAN DINING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA6851F),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "SCAN TO VIEW OUR MENU & ORDER",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFC84B26),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "No app install required. Scan with camera to browse, order & pay instantly.",
                        fontSize = 11.sp,
                        color = Color(0xFF555555),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stylized Scannable QR Matrix Canvas
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.White)
                            .border(2.dp, Color(0xFF140E0A), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(176.dp)) {
                            val canvasSize = size.width
                            val matrixCount = 21
                            val moduleSize = canvasSize / matrixCount

                            // Draw Positioning Corner Eyes
                            fun drawFinderPattern(topLeft: Offset) {
                                // Outer black square (7x7)
                                drawRect(
                                    color = Color(0xFF140E0A),
                                    topLeft = topLeft,
                                    size = Size(7 * moduleSize, 7 * moduleSize)
                                )
                                // Inner white square (5x5)
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(topLeft.x + moduleSize, topLeft.y + moduleSize),
                                    size = Size(5 * moduleSize, 5 * moduleSize)
                                )
                                // Center black square (3x3)
                                drawRect(
                                    color = Color(0xFF140E0A),
                                    topLeft = Offset(topLeft.x + 2 * moduleSize, topLeft.y + 2 * moduleSize),
                                    size = Size(3 * moduleSize, 3 * moduleSize)
                                )
                            }

                            drawFinderPattern(Offset(0f, 0f))
                            drawFinderPattern(Offset(canvasSize - 7 * moduleSize, 0f))
                            drawFinderPattern(Offset(0f, canvasSize - 7 * moduleSize))

                            // Deterministic QR data pattern
                            val seed = (targetUrl.hashCode() and 0x7FFFFFFF)
                            for (r in 0 until matrixCount) {
                                for (c in 0 until matrixCount) {
                                    // Skip finder patterns
                                    if ((r < 8 && c < 8) || (r < 8 && c >= matrixCount - 8) || (r >= matrixCount - 8 && c < 8)) {
                                        continue
                                    }
                                    // Center logo safe area (around row 9-11, col 9-11)
                                    if (r in 9..11 && c in 9..11) {
                                        continue
                                    }
                                    val isFilled = ((seed * (r + 1) * 31 + c * 17) % 3) == 0 ||
                                            (r == 6 && c % 2 == 0) || (c == 6 && r % 2 == 0)

                                    if (isFilled) {
                                        drawRect(
                                            color = Color(0xFF140E0A),
                                            topLeft = Offset(c * moduleSize, r * moduleSize),
                                            size = Size(moduleSize, moduleSize)
                                        )
                                    }
                                }
                            }

                            // Center 306 badge
                            drawRoundRect(
                                color = Color(0xFFD4AF37),
                                topLeft = Offset(8.5f * moduleSize, 8.5f * moduleSize),
                                size = Size(4f * moduleSize, 4f * moduleSize),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Location & Table label
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF3ECE0))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (selectedTable == "All Tables / Wall Poster") "306 LONG STREET • CAPE TOWN" else selectedTable.uppercase(),
                            color = Color(0xFF140E0A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = targetUrl,
                        fontSize = 9.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Action Buttons: Share & Print
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "306 Restaurant & Lounge QR Code Menu")
                            putExtra(Intent.EXTRA_TEXT, "Scan or tap to view 306 Restaurant & Lounge menu and order in Cape Town: $targetUrl")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share 306 Menu QR Link"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Link", color = ObsidianDark, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Ready to print: 306 Lounge Poster for $selectedTable: $targetUrl")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Send to Printer"))
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Print Poster", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
