package com.example.pizzahutappfinal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pizzahutappfinal.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle

// Define tus FontFamilies personalizadas
val BrixtonLeadFontFamily = FontFamily(
    // Solo tienes brixton_lead_vector.otf, asumo que es el peso normal para esta familia
    Font(R.font.brixton_lead_vector, FontWeight.Normal)
)

val SharpSansFontFamily = FontFamily(
    // Versiones Regular/Normal (Sharp Sans tiene muchas variantes, usaremos 'book' como base si no hay 'regular' explícito)
    Font(R.font.sharpsans_book, FontWeight.Normal),
    Font(R.font.sharpsans_bookitalic, FontWeight.Normal, FontStyle.Italic),

    // Pesos Light
    Font(R.font.sharpsans_light, FontWeight.Light),
    Font(R.font.sharpsans_lightitalic, FontWeight.Light, FontStyle.Italic),

    // Pesos Thin
    Font(R.font.sharpsans_thin, FontWeight.Thin),
    Font(R.font.sharpsans_thinitalic, FontWeight.Thin, FontStyle.Italic),

    // Pesos Medium
    Font(R.font.sharpsans_medium, FontWeight.Medium),
    Font(R.font.sharpsans_mediumitalic, FontWeight.Medium, FontStyle.Italic),

    // Pesos SemiBold
    Font(R.font.sharpsans_semibold, FontWeight.SemiBold),
    Font(R.font.sharpsans_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),

    // Pesos Bold
    Font(R.font.sharpsans_bold, FontWeight.Bold),
    Font(R.font.sharpsans_bolditalic, FontWeight.Bold, FontStyle.Italic),

    // Pesos ExtraBold
    Font(R.font.sharpsans_extrabold, FontWeight.ExtraBold),
    Font(R.font.sharpsans_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic)
)
// Set of Material typography styles to start with
val Typography = Typography(
    // Estilo para el cuerpo de texto principal
    bodyLarge = TextStyle(
        fontFamily = SharpSansFontFamily, // Usa SharpSansFontFamily como tu fuente principal para el cuerpo
        fontWeight = FontWeight.Normal, // Usará sharpsans_book.otf por defecto para FontWeight.Normal
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Estilo para títulos grandes (similar a headlineMedium en el ejemplo anterior de login)
    headlineLarge = TextStyle( // Nuevo estilo añadido para el título "INICIAR SESIÓN"
        fontFamily = SharpSansFontFamily, // Usa SharpSans para títulos grandes
        fontWeight = FontWeight.Bold, // Y el peso bold para este estilo
        fontSize = 32.sp, // Tamaño de fuente grande
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // Estilo para el título de la barra superior (ej. "INICIAR SESIÓN" si lo quieres más pequeño)
    titleLarge = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Bold, // Puedes usar Bold aquí o Semibold si prefieres
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Estilo para texto de botones o labels pequeños
    labelSmall = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Medium, // Usará sharpsans_medium.otf
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Si quieres usar BrixtonLead para algún título o estilo específico:
    headlineSmall = TextStyle(
        fontFamily = BrixtonLeadFontFamily,
        fontWeight = FontWeight.Normal, // O el que corresponda a tu archivo Brixton
        fontSize = 24.sp
    )
    /* Otros estilos predeterminados comentados que puedes descomentar y personalizar:
    displayLarge = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = SharpSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    */
)