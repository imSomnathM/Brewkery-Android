package com.example.brewkery

import com.example.brewkery.data.PriceCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceCalculatorTest {

    @Test
    fun unitPrice_addsSizeAndMilkExtras() {
        assertEquals(5.50, PriceCalculator.unitPrice(4.85, 0.65, 0.0), 0.001)
        assertEquals(6.60, PriceCalculator.unitPrice(4.85, 1.25, 0.50), 0.001)
    }

    @Test
    fun totals_matchDesignExample() {
        // Grande macchiato $5.50 + croissant $3.90 = $9.40
        val t = PriceCalculator.totals(9.40, 2.50, 8.0, hasItems = true)
        assertEquals(0.75, t.tax, 0.001)
        assertEquals(12.65, t.total, 0.001)
    }

    @Test
    fun totals_singleItemExample() {
        val t = PriceCalculator.totals(4.85, 2.50, 8.0, hasItems = true)
        assertEquals(0.39, t.tax, 0.001)
        assertEquals(7.74, t.total, 0.001)
    }

    @Test
    fun totals_emptyCartIsZero() {
        val t = PriceCalculator.totals(0.0, 2.50, 8.0, hasItems = false)
        assertEquals(0.0, t.total, 0.001)
        assertEquals(2.50, t.deliveryFee, 0.001)
    }
}
