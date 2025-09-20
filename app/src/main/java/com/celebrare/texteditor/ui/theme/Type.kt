package com.celebrare.texteditor.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.celebrare.texteditor.R

val LobsterFamily = FontFamily(
    Font(R.font.lobster_regular, FontWeight.Normal)
)

val MerriweatherFamily = FontFamily(
    Font(R.font.merriweather_regular, FontWeight.Normal)
)

val InconsolataFamily = FontFamily(
    Font(R.font.inconsolata_regular, FontWeight.Normal)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
