package com.example.brewkery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.brewkery.data.MenuItem
import com.example.brewkery.data.MilkOption
import com.example.brewkery.data.PriceCalculator
import com.example.brewkery.data.SizeOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    item: MenuItem,
    onBack: () -> Unit,
    onAdd: (SizeOption, MilkOption?, String, Int) -> Unit
) {
    val c = item.customizations
    var size by remember { mutableStateOf(c.sizes.first()) }
    var milk by remember { mutableStateOf(c.milkOptions.firstOrNull()) }
    var sugar by remember { mutableStateOf(c.sugarLevels.first()) }
    var qty by remember { mutableIntStateOf(1) }
    var fav by remember { mutableStateOf(false) }

    val unit = PriceCalculator.unitPrice(item.basePrice, size.extraPrice, milk?.extraPrice ?: 0.0)

    Column(Modifier.fillMaxSize().systemBarsPadding()) {
        TopBar("ITEM CUSTOMIZER", onBack) {
            CircleButton({ fav = !fav }) {
                Icon(
                    if (fav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favourite", tint = BK.Crimson, modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(170.dp).clip(RoundedCornerShape(16.dp)).background(BK.Chip)
                )
                item.badge?.let {
                    Text(
                        it,
                        Modifier.padding(12.dp).clip(RoundedCornerShape(6.dp)).background(Color(0x99000000))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = BK.Amber, fontSize = 10.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(item.name, Modifier.weight(1f), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    item.basePrice.money(),
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = BK.Accent
                )
            }

            Text(item.description, fontSize = 13.sp, color = BK.Muted, lineHeight = 19.sp)

            Text("KEY INGREDIENTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BK.Muted, letterSpacing = 0.5.sp)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) { item.ingredients.forEach { Tag(it) } }

            SectionCard("Size Selection") {
                Row(
                    Modifier.height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    c.sizes.forEach { s ->
                        val sel = s == size
                        OptionBox(
                            selected = sel,
                            onClick = { size = s },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            padding = PaddingValues(horizontal = 4.dp, vertical = 10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    s.label, fontSize = 12.sp, lineHeight = 15.sp, textAlign = TextAlign.Center,
                                    color = if (sel) BK.Accent else BK.Dark,
                                    fontWeight = if (sel) FontWeight.Medium else FontWeight.Normal
                                )
                                Text(
                                    "+" + s.extraPrice.money(),
                                    fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                                    color = if (sel) BK.Accent else BK.Muted
                                )
                            }
                        }
                    }
                }
            }

            SectionCard("Milk Options / Spreads") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    c.milkOptions.forEach { m ->
                        val sel = m == milk
                        OptionBox(sel, { milk = m }, Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Text(
                                    m.name, fontSize = 14.sp,
                                    color = if (sel) BK.Accent else BK.Muted,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    "+" + m.extraPrice.money(),
                                    fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                                    color = if (sel) BK.Accent else BK.Muted
                                )
                            }
                        }
                    }
                }
            }

            SectionCard("Sugar Levels / Serving") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) { c.sugarLevels.forEach { s -> Pill(s, s == sugar) { sugar = s } } }
            }

            Spacer(Modifier.height(4.dp))
        }

        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Stepper(
                qty, { if (qty > 1) qty-- }, { qty++ },
                Modifier.height(52.dp), background = Color.White
            )
            GradientButton(
                "Add to Cart  •  ${(unit * qty).money()}",
                { onAdd(size, milk, sugar, qty) },
                Modifier.weight(1f)
            )
        }
    }
}
