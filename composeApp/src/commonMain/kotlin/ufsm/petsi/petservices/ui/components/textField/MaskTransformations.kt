package ufsm.petsi.petservices.ui.components.textField

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private fun digitOffsetMapping(originalText: String, formattedText: String): OffsetMapping {
    return object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val digitsBeforeCursor =
                (0 until offset.coerceIn(0, originalText.length)).count { originalText[it].isDigit() }
            return formattedIndexOfDigit(formattedText, digitsBeforeCursor)
        }

        override fun transformedToOriginal(offset: Int): Int {
            val digitsBeforeCursor =
                (0 until offset.coerceIn(0, formattedText.length)).count { formattedText[it].isDigit() }
            return digitsBeforeCursor.coerceIn(0, originalText.length)
        }
    }
}

private fun formattedIndexOfDigit(formattedText: String, digitCount: Int): Int {
    if (digitCount <= 0) return 0
    var count = 0
    for (i in formattedText.indices) {
        if (formattedText[i].isDigit()) {
            count++
            if (count == digitCount) return i + 1
        }
    }
    return formattedText.length
}

fun formatCpf(input: String): String {
    val digits = input.filter { it.isDigit() }.take(11)
    return buildString {
        digits.forEachIndexed { index, c ->
            if (index == 3 || index == 6) append('.')
            if (index == 9) append('-')
            append(c)
        }
    }
}

fun formatPhone(input: String): String {
    val digits = input.filter { it.isDigit() }.take(11)
    val hyphenBefore =  7
    return buildString {
        digits.forEachIndexed { index, c ->
            when (index) {
                0 -> append('(')
                2 -> append(") ")
                hyphenBefore -> append('-')
            }
            append(c)
        }
    }
}

class CpfVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = formatCpf(text.text)
        return TransformedText(
            AnnotatedString(formatted),
            digitOffsetMapping(text.text, formatted)
        )
    }
}

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = formatPhone(text.text)
        return TransformedText(
            AnnotatedString(formatted),
            digitOffsetMapping(text.text, formatted)
        )
    }
}