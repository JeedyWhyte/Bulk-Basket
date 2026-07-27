package com.bulkbasket.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.bulkbasket.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val SoraFamily = FontFamily(
    Font(googleFont = GoogleFont("Sora"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Sora"), fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Sora"), fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = GoogleFont("Sora"), fontProvider = provider, weight = FontWeight.ExtraBold),
)

val InterFamily = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Bold),
)
