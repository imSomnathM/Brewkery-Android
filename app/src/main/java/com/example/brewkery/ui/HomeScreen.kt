package com.example.brewkery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.brewkery.data.CartLine
import com.example.brewkery.data.MenuItem
import com.example.brewkery.data.Meta

@Composable
fun HomeScreen(
    menu: MenuUiState,
    cart: List<CartLine>,
    order: ActiveOrder?,
    onRetry: () -> Unit,
    onItemClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onTrack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf<String?>(null) }
    val cartCount = cart.sumOf { it.quantity }
    val cartTotal = cart.sumOf { it.unitPrice * it.quantity }

    Box(Modifier.fillMaxSize().systemBarsPadding()) {
        Column(Modifier.fillMaxSize()) {
            Header(cartCount, onCartClick)

            when (menu) {
                MenuUiState.Loading -> Box(Modifier.fillMaxWidth().weight(1f), Alignment.Center) {
                    CircularProgressIndicator(color = BK.Accent)
                }

                is MenuUiState.Error -> Box(Modifier.fillMaxWidth().weight(1f), Alignment.Center) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Couldn't load the menu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(menu.message, color = BK.Muted, fontSize = 13.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = BK.Accent)) {
                            Text("Retry")
                        }
                    }
                }

                is MenuUiState.Success -> {
                    val data = menu.data
                    Banner(order, data.meta, onTrack)
                    SearchBar(query) { query = it }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { Pill("All Items", selectedCat == null) { selectedCat = null } }
                        items(data.categories) { c ->
                            Pill("${c.icon} ${c.name}", selectedCat == c.id) { selectedCat = c.id }
                        }
                    }

                    val filtered = data.items.filter {
                        (selectedCat == null || it.categoryId == selectedCat) &&
                            (query.isBlank() || it.name.contains(query, ignoreCase = true) ||
                                it.description.contains(query, ignoreCase = true))
                    }

                    LazyColumn(
                        Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (filtered.isEmpty()) {
                            item {
                                Text(
                                    "No items found",
                                    Modifier.fillMaxWidth().padding(24.dp),
                                    color = BK.Muted, textAlign = TextAlign.Center
                                )
                            }
                        }
                        items(filtered, key = { it.id }) { ItemCard(it) { onItemClick(it.id) } }
                    }
                }
            }
        }

        if (cartCount > 0) CartBar(cartCount, cartTotal, onCartClick)
    }
}

@Composable
private fun Header(cartCount: Int, onCartClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(BK.Dark), Alignment.Center) {
            Text("BK", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Fresh Roast & Bakes", fontSize = 11.sp, color = BK.Muted)
            Text("Brewkery Artisans", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Box(Modifier.size(44.dp)) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, BK.Border, CircleShape)
                    .clickable(onClick = onCartClick),
                Alignment.Center
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart", modifier = Modifier.size(18.dp))
            }
            Box(
                Modifier.align(Alignment.TopEnd).size(20.dp).clip(CircleShape).background(BK.Accent),
                Alignment.Center
            ) {
                Text(cartCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Banner(order: ActiveOrder?, meta: Meta, onTrack: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(BK.Banner)
            .border(1.dp, BK.Border, shape)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White), Alignment.Center) {
            Text(if (order != null) "🟢" else "🛵", fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (order != null) "ACTIVE ORDER" else "STORE INFO",
                    color = if (order != null) BK.Green else BK.Accent,
                    fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp
                )
                Spacer(Modifier.width(5.dp))
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color.Red))
            }
            Text(
                if (order != null) "Active Order #${order.ticketId}"
                else "Delivery in ${meta.estimatedDeliveryTime.replace("-", "–")}",
                fontWeight = FontWeight.Bold, fontSize = 15.sp
            )
            Text(
                if (order != null) "Preparing (Arriving in 25 mins)" else "${meta.deliveryFee.money()} flat fee",
                fontSize = 12.sp, color = BK.Muted
            )
        }
        if (order != null) {
            Text(
                "Track",
                Modifier.clip(RoundedCornerShape(8.dp)).background(BK.Green).clickable(onClick = onTrack)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp
            )
        } else {
            Text(
                "Open",
                Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White)
                    .border(1.dp, BK.Border, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold, fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SearchBar(query: String, onChange: (String) -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .height(46.dp)
            .card(12.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Search, contentDescription = null, tint = BK.Muted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text("Search roast, cold brew, pastry...", color = BK.Muted, fontSize = 14.sp)
            }
            BasicTextField(
                value = query,
                onValueChange = onChange,
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = BK.Dark),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ItemCard(item: MenuItem, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().card().clickable(onClick = onClick).padding(10.dp)) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)).background(BK.Chip)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            item.badge?.let {
                BadgeChip(it)
                Spacer(Modifier.height(4.dp))
            }
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = BK.Amber, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("${item.rating} (${item.reviewCount})", fontSize = 12.sp, color = BK.Muted)
            }
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.basePrice.money(),
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, color = BK.Accent
                )
                Text(
                    "+ Customize",
                    Modifier.clip(RoundedCornerShape(6.dp)).background(BK.Accent)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BoxScope.CartBar(count: Int, total: Double, onClick: () -> Unit) {
    Row(
        Modifier
            .align(Alignment.BottomCenter)
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BK.Dark)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(28.dp).clip(CircleShape).background(BK.Accent), Alignment.Center) {
            Text(count.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("View Your Cart", color = Color.White, fontSize = 11.sp)
            Text(total.money(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Text("Proceed to Checkout →", color = BK.Amber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
