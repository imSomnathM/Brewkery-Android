package com.example.brewkery.data

/** One line in the cart = item + chosen options + quantity. */
data class CartLine(
    val key: String,
    val item: MenuItem,
    val size: SizeOption,
    val milk: MilkOption?,
    val sugar: String,
    val quantity: Int
) {
    val unitPrice: Double
        get() = PriceCalculator.unitPrice(item.basePrice, size.extraPrice, milk?.extraPrice ?: 0.0)
}

object PriceCalculator {

    fun round2(value: Double): Double = Math.round(value * 100.0) / 100.0

    fun unitPrice(base: Double, sizeExtra: Double, milkExtra: Double): Double =
        round2(base + sizeExtra + milkExtra)

    data class Totals(
        val subtotal: Double,
        val deliveryFee: Double,
        val tax: Double,
        val total: Double
    )

    /** Tax applies to the subtotal only. An empty cart totals $0.00 (delivery fee is still displayed). */
    fun totals(subtotal: Double, deliveryFee: Double, taxRatePercent: Double, hasItems: Boolean): Totals {
        if (!hasItems) return Totals(0.0, deliveryFee, 0.0, 0.0)
        val sub = round2(subtotal)
        val tax = round2(sub * taxRatePercent / 100.0)
        return Totals(sub, deliveryFee, tax, round2(sub + deliveryFee + tax))
    }
}
