package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestHerbs
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusDispatched
import com.example.ui.theme.StatusPreparing
import com.example.ui.theme.StatusReceived
import com.example.ui.theme.TerracottaAccent

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "RECEIVED" -> Triple(StatusReceived.copy(alpha = 0.2f), StatusReceived, "Order Received")
        "CONFIRMED" -> Triple(GoldPrimary.copy(alpha = 0.2f), GoldPrimary, "Confirmed")
        "PREPARING" -> Triple(StatusPreparing.copy(alpha = 0.2f), StatusPreparing, "Preparing")
        "READY_FOR_PICKUP" -> Triple(ForestHerbs.copy(alpha = 0.2f), ForestHerbs, "Ready for Pickup")
        "OUT_FOR_DELIVERY" -> Triple(StatusDispatched.copy(alpha = 0.2f), StatusDispatched, "Out for Delivery")
        "DELIVERED" -> Triple(ForestHerbs.copy(alpha = 0.25f), ForestHerbs, "Delivered / Completed")
        "CANCELLED" -> Triple(TerracottaAccent.copy(alpha = 0.2f), TerracottaAccent, "Cancelled")
        else -> Triple(Color.Gray.copy(alpha = 0.2f), Color.White, status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

object WhatsAppHelper {
    fun openWhatsApp(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
