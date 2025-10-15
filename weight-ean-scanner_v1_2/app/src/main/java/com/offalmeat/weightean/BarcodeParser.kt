package com.offalmeat.weightean

data class ParsedBarcode(
    val raw: String,
    val prefix: String,
    val plu: String,
    val valueRaw: String,
    val weightKg: Double? = null,
    val price: Double? = null
)

enum class ValueMode { WEIGHT, PRICE }

object BarcodeParser {
    fun isValidEan13(ean: String): Boolean {
        if (!ean.matches(Regex("^\d{13}$"))) return false
        val digits = ean.map { it.digitToInt() }
        val sum = digits.take(12).mapIndexed { idx, d -> if (idx % 2 == 0) d else d * 3 }.sum()
        val check = (10 - (sum % 10)) % 10
        return check == digits.last()
    }

    /**
     * @param allowedPrefixes set of string prefixes like {"20","21","23"}
     * @param valueMode WEIGHT -> BBBBB = grams/centi-kg; PRICE -> BBBBB = price in cents
     * @param scale for WEIGHT mode: divide BBBBB by scale to get kg (1000 => grams)
     */
    fun parse(
        ean: String,
        allowedPrefixes: Set<String>,
        valueMode: ValueMode = ValueMode.WEIGHT,
        scale: Int = 1000
    ): ParsedBarcode {
        require(isValidEan13(ean)) { "Неверный EAN-13" }
        val prefix = ean.substring(0,2)
        require(allowedPrefixes.contains(prefix)) { "Префикс $prefix не разрешён" }
        val plu = ean.substring(2,7)
        val valueRaw = ean.substring(7,12)
        return when (valueMode) {
            ValueMode.WEIGHT -> {
                val weightKg = valueRaw.toInt() / scale.toDouble()
                ParsedBarcode(ean, prefix, plu, valueRaw, weightKg = weightKg)
            }
            ValueMode.PRICE -> {
                val price = valueRaw.toInt() / 100.0
                ParsedBarcode(ean, prefix, plu, valueRaw, price = price)
            }
        }
    }
}
