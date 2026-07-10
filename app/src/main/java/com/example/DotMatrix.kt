package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object DotMatrix {
    val GLYPHS = mapOf(
        'A' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b11111,
            0b10001,
            0b10001,
            0b10001
        ),
        'B' to intArrayOf(
            0b11110,
            0b10001,
            0b11110,
            0b10001,
            0b10001,
            0b10001,
            0b11110
        ),
        'C' to intArrayOf(
            0b01110,
            0b10001,
            0b10000,
            0b10000,
            0b10000,
            0b10001,
            0b01110
        ),
        'D' to intArrayOf(
            0b11100,
            0b10010,
            0b10001,
            0b10001,
            0b10001,
            0b10010,
            0b11100
        ),
        'E' to intArrayOf(
            0b11111,
            0b10000,
            0b10000,
            0b11110,
            0b10000,
            0b10000,
            0b11111
        ),
        'F' to intArrayOf(
            0b11111,
            0b10000,
            0b10000,
            0b11110,
            0b10000,
            0b10000,
            0b10000
        ),
        'G' to intArrayOf(
            0b01110,
            0b10001,
            0b10000,
            0b10111,
            0b10001,
            0b10001,
            0b01111
        ),
        'H' to intArrayOf(
            0b10001,
            0b10001,
            0b10001,
            0b11111,
            0b10001,
            0b10001,
            0b10001
        ),
        'I' to intArrayOf(
            0b01110,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b01110
        ),
        'J' to intArrayOf(
            0b00111,
            0b00010,
            0b00010,
            0b00010,
            0b10010,
            0b10010,
            0b01100
        ),
        'K' to intArrayOf(
            0b10001,
            0b10010,
            0b10100,
            0b11000,
            0b10100,
            0b10010,
            0b10001
        ),
        'L' to intArrayOf(
            0b10000,
            0b10000,
            0b10000,
            0b10000,
            0b10000,
            0b10000,
            0b11111
        ),
        'M' to intArrayOf(
            0b10001,
            0b11011,
            0b10101,
            0b10101,
            0b10001,
            0b10001,
            0b10001
        ),
        'N' to intArrayOf(
            0b10001,
            0b11001,
            0b10101,
            0b10011,
            0b10001,
            0b10001,
            0b10001
        ),
        'O' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b01110
        ),
        'P' to intArrayOf(
            0b11110,
            0b10001,
            0b10001,
            0b11110,
            0b10000,
            0b10000,
            0b10000
        ),
        'Q' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b10001,
            0b10101,
            0b10011,
            0b01111
        ),
        'R' to intArrayOf(
            0b11110,
            0b10001,
            0b10001,
            0b11110,
            0b10100,
            0b10010,
            0b10001
        ),
        'S' to intArrayOf(
            0b01111,
            0b10000,
            0b10000,
            0b01110,
            0b00001,
            0b00001,
            0b11110
        ),
        'T' to intArrayOf(
            0b11111,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b00100
        ),
        'U' to intArrayOf(
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b01110
        ),
        'V' to intArrayOf(
            0b10001,
            0b10001,
            0b10001,
            0b10001,
            0b01010,
            0b01010,
            0b00100
        ),
        'W' to intArrayOf(
            0b10001,
            0b10001,
            0b10001,
            0b10101,
            0b10101,
            0b11011,
            0b10001
        ),
        'X' to intArrayOf(
            0b10001,
            0b10001,
            0b01010,
            0b00100,
            0b01010,
            0b10001,
            0b10001
        ),
        'Y' to intArrayOf(
            0b10001,
            0b10001,
            0b01010,
            0b00100,
            0b00100,
            0b00100,
            0b00100
        ),
        'Z' to intArrayOf(
            0b11111,
            0b00001,
            0b00010,
            0b00100,
            0b01000,
            0b10000,
            0b11111
        ),
        '0' to intArrayOf(
            0b01110,
            0b10011,
            0b10101,
            0b11001,
            0b10001,
            0b10001,
            0b01110
        ),
        '1' to intArrayOf(
            0b00100,
            0b01100,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b01110
        ),
        '2' to intArrayOf(
            0b01110,
            0b10001,
            0b00001,
            0b00010,
            0b00100,
            0b01000,
            0b11111
        ),
        '3' to intArrayOf(
            0b11111,
            0b00010,
            0b00100,
            0b00010,
            0b00001,
            0b10001,
            0b01110
        ),
        '4' to intArrayOf(
            0b00010,
            0b00110,
            0b01010,
            0b10010,
            0b11111,
            0b00010,
            0b00010
        ),
        '5' to intArrayOf(
            0b11111,
            0b10000,
            0b11110,
            0b00001,
            0b00001,
            0b10001,
            0b01110
        ),
        '6' to intArrayOf(
            0b01110,
            0b10000,
            0b11110,
            0b10001,
            0b10001,
            0b10001,
            0b01110
        ),
        '7' to intArrayOf(
            0b11111,
            0b00001,
            0b00010,
            0b00100,
            0b01000,
            0b01000,
            0b01000
        ),
        '8' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b01110,
            0b10001,
            0b10001,
            0b01110
        ),
        '9' to intArrayOf(
            0b01110,
            0b10001,
            0b10001,
            0b01111,
            0b00001,
            0b10001,
            0b01110
        ),
        ':' to intArrayOf(
            0b00000,
            0b01100,
            0b01100,
            0b00000,
            0b01100,
            0b01100,
            0b00000
        ),
        '.' to intArrayOf(
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b01100,
            0b01100
        ),
        '-' to intArrayOf(
            0b00000,
            0b00000,
            0b00000,
            0b01110,
            0b00000,
            0b00000,
            0b00000
        ),
        ' ' to intArrayOf(
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000,
            0b00000
        )
    )
}

@Composable
fun DotMatrixChar(
    char: Char,
    color: Color,
    modifier: Modifier = Modifier,
    dotSize: Dp = 3.dp,
    spacing: Dp = 1.5.dp
) {
    val glyph = DotMatrix.GLYPHS[char.uppercaseChar()] ?: DotMatrix.GLYPHS[' ']!!
    val rows = 7
    val cols = 5

    Canvas(
        modifier = modifier
            .size(
                width = (dotSize * cols) + (spacing * (cols - 1)),
                height = (dotSize * rows) + (spacing * (rows - 1))
            )
    ) {
        val sizePx = dotSize.toPx()
        val spacingPx = spacing.toPx()

        for (r in 0 until rows) {
            val rowBits = glyph[r]
            for (c in 0 until cols) {
                // Read from left to right (bit index 4 down to 0)
                val bitIndex = cols - 1 - c
                val bitActive = (rowBits shr bitIndex) and 1 == 1

                if (bitActive) {
                    val x = c * (sizePx + spacingPx) + (sizePx / 2)
                    val y = r * (sizePx + spacingPx) + (sizePx / 2)
                    drawCircle(
                        color = color,
                        radius = sizePx / 2,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

@Composable
fun DotMatrixText(
    text: String,
    dotColor: Color,
    modifier: Modifier = Modifier,
    dotSize: Dp = 3.dp,
    spacing: Dp = 1.2.dp,
    charSpacing: Dp = 6.dp
) {
    Row(modifier = modifier) {
        text.forEachIndexed { index, char ->
            DotMatrixChar(
                char = char,
                color = dotColor,
                dotSize = dotSize,
                spacing = spacing,
                modifier = Modifier.padding(
                    end = if (index < text.length - 1) charSpacing else 0.dp
                )
            )
        }
    }
}
