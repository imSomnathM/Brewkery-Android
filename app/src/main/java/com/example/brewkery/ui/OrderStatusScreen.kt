package com.example.brewkery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderStatusScreen(order: ActiveOrder, eta: String, onBackToMenu: () -> Unit) {
    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Box(Modifier.size(88.dp).clip(CircleShape).background(BK.Banner).border(2.dp, BK.Accent, CircleShape), Alignment.Center) {
            Text("☕", fontSize = 40.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("ORDER DISPATCHED", color = BK.Accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Spacer(Modifier.height(4.dp))
        Text("Brewing in Progress!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(
            "Your ticket was dispatched to our barista.",
            color = BK.Muted, fontSize = 13.sp, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        Column(Modifier.fillMaxWidth().card().padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column {
                    Text("Order Ticket", fontSize = 12.sp, color = BK.Muted)
                    Text(
                        "#${order.ticketId}",
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp
                    )
                }
                Text(
                    "PREPARING",
                    Modifier.clip(RoundedCornerShape(50)).background(BK.Green.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    color = BK.Green, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = BK.Border)
            Spacer(Modifier.height(10.dp))
            InfoRow("Estimated Wait", eta.replace("-", "–").replace("mins", "minutes"))
            InfoRow("Items Ordered", if (order.itemCount == 1) "1 Item" else "${order.itemCount} Items")
            InfoRow("Status", "Barista accepted your order!")
        }

        Spacer(Modifier.weight(1f))
        GradientButton("Back to Menu", onBackToMenu, Modifier.fillMaxWidth())
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = BK.Muted)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
