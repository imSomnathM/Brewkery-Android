package com.example.brewkery.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircleButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, BK.Border, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
fun TopBar(title: String, onBack: () -> Unit, right: @Composable () -> Unit = {}) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
        CircleButton(onBack, Modifier.align(Alignment.CenterStart)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(18.dp))
        }
        Text(
            title,
            Modifier.align(Alignment.Center),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp
        )
        Box(Modifier.align(Alignment.CenterEnd)) { right() }
    }
}

/** Rounded chip. Selected = dark fill. Used for categories and sugar levels. */
@Composable
fun Pill(text: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        Modifier
            .clip(shape)
            .background(if (selected) BK.Dark else Color.White)
            .border(1.dp, if (selected) BK.Dark else BK.Border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text,
            color = if (selected) Color.White else BK.Dark,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/** Small read-only outlined tag (ingredients). */
@Composable
fun Tag(text: String) {
    Text(
        text,
        Modifier
            .border(1.dp, BK.Border, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        fontSize = 11.sp,
        color = BK.Muted
    )
}

@Composable
fun BadgeChip(text: String) {
    Text(
        text,
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(BK.BadgeBg)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF5A3A00),
        letterSpacing = 0.4.sp
    )
}

@Composable
fun Stepper(
    qty: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = BK.Chip
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier.clip(shape).background(background).border(1.dp, BK.Border, shape).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "-",
            Modifier.clickable(onClick = onMinus).padding(horizontal = 10.dp, vertical = 8.dp),
            fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp
        )
        Text(qty.toString(), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(
            "+",
            Modifier.clickable(onClick = onPlus).padding(horizontal = 10.dp, vertical = 8.dp),
            fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp
        )
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Row(
        modifier
            .alpha(if (enabled) 1f else 0.5f)
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(BK.Accent, BK.AccentDark)))
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().card().padding(12.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(Modifier.height(10.dp))
        content()
    }
}

/** Selectable option box (sizes, milk). Selected = peach fill + orange border. */
@Composable
fun OptionBox(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier
            .clip(shape)
            .background(if (selected) BK.SelectedBg else Color.White)
            .border(1.dp, if (selected) BK.Accent else BK.Border, shape)
            .clickable(onClick = onClick)
            .padding(padding),
        contentAlignment = Alignment.Center
    ) { content() }
}

/** Dark pill at the top of the screen with a green check, e.g. "Cart cleared". */
@Composable
fun ToastHost(toast: ToastMsg?, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    if (toast != null && text != toast.text) text = toast.text

    AnimatedVisibility(
        visible = toast != null,
        modifier = modifier.statusBarsPadding().padding(top = 8.dp, start = 16.dp, end = 16.dp),
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Row(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(BK.Dark)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(18.dp).clip(CircleShape).background(BK.Green), Alignment.Center) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
