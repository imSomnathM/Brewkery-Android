package com.example.brewkery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewkery.data.CartLine
import com.example.brewkery.data.PriceCalculator

@Composable
fun CartScreen(
    cart: List<CartLine>,
    deliveryFee: Double,
    taxRatePercent: Double,
    onBack: () -> Unit,
    onQtyChange: (String, Int) -> Unit,
    onClear: () -> Unit,
    onPlaceOrder: () -> Unit
) {
    val subtotal = cart.sumOf { it.unitPrice * it.quantity }
    val t = PriceCalculator.totals(subtotal, deliveryFee, taxRatePercent, cart.isNotEmpty())

    Column(Modifier.fillMaxSize().systemBarsPadding()) {
        TopBar("YOUR CART", onBack) {
            Text(
                "Clear Cart",
                Modifier.clickable(onClick = onClear),
                color = BK.Crimson, fontSize = 12.sp, fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (cart.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().card().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.ShoppingCart, null, tint = BK.Border, modifier = Modifier.size(34.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Your in-memory cart is empty.", fontSize = 13.sp, color = BK.Muted)
                    }
                }
            } else {
                items(cart, key = { it.key }) { line -> CartRow(line, onQtyChange) }
            }
        }

        Column(Modifier.padding(16.dp)) {
            Column(Modifier.fillMaxWidth().card().padding(14.dp)) {
                SummaryRow("Subtotal", t.subtotal.money())
                SummaryRow("Delivery Fee", t.deliveryFee.money())
                SummaryRow("Est. Tax ($taxRatePercent%)", t.tax.money())
                Spacer(Modifier.height(6.dp))
                HorizontalDivider(color = BK.Border)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Total Payable", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text(
                        t.total.money(),
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp, color = BK.Accent
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            GradientButton(
                "Place Order Now  •  ${t.total.money()}",
                onPlaceOrder,
                Modifier.fillMaxWidth(),
                icon = Icons.Filled.Lock,
                enabled = cart.isNotEmpty()
            )
        }
    }
}

@Composable
private fun CartRow(line: CartLine, onQtyChange: (String, Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().card().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(line.item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                listOfNotNull(line.size.label, line.milk?.name).joinToString(" • "),
                fontSize = 11.sp, color = BK.Muted
            )
            Spacer(Modifier.height(2.dp))
            Text(
                (line.unitPrice * line.quantity).money(),
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
                fontSize = 12.sp, color = BK.Accent
            )
        }
        Stepper(
            line.quantity,
            { onQtyChange(line.key, -1) },
            { onQtyChange(line.key, +1) }
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = BK.Muted)
        Text(value, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
