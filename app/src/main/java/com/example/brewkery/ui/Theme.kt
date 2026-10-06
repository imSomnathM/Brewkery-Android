package com.example.brewkery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale

object BK {
    val Cream = Color(0xFFFFF8F4)
    val Dark = Color(0xFF1A0F0B)
    val Accent = Color(0xFFD4502B)
    val AccentDark = Color(0xFF8E2A12)
    val Border = Color(0xFFEBD5C8)
    val Banner = Color(0xFFF7E8DE)
    val SelectedBg = Color(0xFFFBEBE2)
    val Chip = Color(0xFFF5E6DC)
    val Green = Color(0xFF0E9F6E)
    val BadgeBg = Color(0xFFFFF0C2)
    val Muted = Color(0xFF6B5A52)
    val Crimson = Color(0xFFD6174F)
    val Amber = Color(0xFFFFA812)
}

fun Double.money(): String = String.format(Locale.US, "$%.2f", this)

/** White rounded card with a thin peach border. */
fun Modifier.card(radius: Dp = 14.dp): Modifier {
    val shape = RoundedCornerShape(radius)
    return this.clip(shape).background(Color.White).border(1.dp, BK.Border, shape)
}

@Composable
fun BrewkeryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BK.Accent,
            background = BK.Cream,
            surface = Color.White,
            onBackground = BK.Dark,
            onSurface = BK.Dark
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = BK.Cream, contentColor = BK.Dark, content = content)
    }
}
