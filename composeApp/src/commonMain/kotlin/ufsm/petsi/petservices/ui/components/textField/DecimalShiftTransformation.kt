package ufsm.petsi.petservices.ui.components.textField

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DecimalCommaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val cleanDigits = text.text.filter { it.isDigit() }
        val paddedDigits = cleanDigits.padStart(4, '0')

        val intPart = paddedDigits.dropLast(2)
        val decimalPart = paddedDigits.takeLast(2)
        val formattedText = "$intPart,$decimalPart"

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // If offset is at or beyond the end of the original text,
                // place the cursor at the very end of the formatted text.
                if (offset >= text.text.length) return formattedText.length

                // Count how many valid digits exist up to the current cursor position
                var digitCount = 0
                for (i in 0 until offset.coerceIn(0, text.text.length)) {
                    if (text.text[i].isDigit()) {
                        digitCount++
                    }
                }

                // Calculate how many raw digits were ignored due to bounds/filtering
                val totalDigits = cleanDigits.length
                val matchedIndex = totalDigits - digitCount

                // Find corresponding index in the formatted string from right to left
                // or locate the target character index safely
                val targetCharIndex = formattedText.length - (digitCount * (formattedText.length.toDouble() / totalDigits)).toInt()

                // Simpler, robust mapping: find the n-th digit occurrence in formatted text
                return findTransformedIndex(digitCount, cleanDigits.length, formattedText)
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Map back by counting how many digits appear before this offset in the transformed string
                var originalDigitIndex = 0
                for (i in 0 until offset.coerceIn(0, formattedText.length)) {
                    if (formattedText[i].isDigit()) {
                        originalDigitIndex++
                    }
                }

                // Convert back relative to original text length
                val totalOriginalDigits = cleanDigits.length
                val mappedOriginalCount = originalDigitIndex - (4 - totalOriginalDigits).coerceAtLeast(0)
                return mappedOriginalCount.coerceIn(0, text.text.length)
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }

    private fun findTransformedIndex(digitIndex: Int, totalCleanDigits: Int, formattedText: String): Int {
        // Find the index of the matching digit in the formatted string
        // Since our format always has 4+ digits separated by a comma, we can map linearly:
        // Let's count digits from the right side.
        val targetFromRight = totalCleanDigits - digitIndex
        var countedDigits = 0
        for (i in formattedText.indices.reversed()) {
            if (formattedText[i].isDigit()) {
                countedDigits++
                if (countedDigits == targetFromRight) {
                    return i + 1 // Cursor goes right after that digit
                }
            }
        }
        return 0
    }
}