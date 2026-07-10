package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CmfWhite,
    secondary = CmfCoolGray,
    tertiary = CmfOrange,
    background = CmfBlack,
    surface = CmfDarkGray,
    onPrimary = CmfBlack,
    onSecondary = CmfWhite,
    onTertiary = CmfWhite,
    onBackground = CmfWhite,
    onSurface = CmfWhite,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CmfBlack,
    secondary = CmfCoolGray,
    tertiary = CmfOrange,
    background = CmfWhite,
    surface = CmfLightGray,
    onPrimary = CmfWhite,
    onSecondary = CmfBlack,
    onTertiary = CmfWhite,
    onBackground = CmfBlack,
    onSurface = CmfBlack,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color to enforce Nothing OS strict monochrome branding
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
