package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Automotive Gold / High-Contrast Yellow Palette
val BrandYellow = Color(0xFFF59E0B)       // Rich automotive amber gold
val BrandYellowDark = Color(0xFFD97706)   // Deep amber for buttons/borders
val BrandYellowLight = Color(0xFFFBBF24)  // Vibrant gold
val BrandYellowContainer = Color(0xFFFEF3C7) // Light yellow tinted surface
val OnBrandYellowContainer = Color(0xFF78350F) // Deep bronze on yellow

// Dark Slate / Graphite for crisp executive contrast
val DarkSlate = Color(0xFF0F172A)
val DarkSlateLight = Color(0xFF1E293B)
val DarkSlateSurface = Color(0xFF18202F)

// AAC Official Royal Blue accents
val BrandBlue = Color(0xFF024B9E)
val BrandBlueContainer = Color(0xFFE2EDF9)
val OnBrandBlueContainer = Color(0xFF00224C)

// Surfaces & Canvas
val CanvasBg = Color(0xFFF8FAFC)
val SurfaceCard = Color(0xFFFFFFFF)
val SurfaceCardHover = Color(0xFFF1F5F9)
val LineBorder = Color(0xFFE2E8F0)

// Text
val TextPrimary = Color(0xFF0F172A)
val TextMuted = Color(0xFF64748B)
val TextLight = Color(0xFF94A3B8)

// Status
val StatusGood = Color(0xFF10B981)
val StatusWarn = Color(0xFFF59E0B)
val StatusDanger = Color(0xFFEF4444)

// Backward-compatibility aliases re-mapped to the new yellow / dark slate theme
val PrimaryGreen = DarkSlate
val PrimaryGreenDark = Color(0xFF0A0F1D)
val PrimaryGreenLight = DarkSlateLight
val MintContainer = BrandYellowContainer
val OnMintContainer = OnBrandYellowContainer
val AccentOrange = BrandYellow
val AccentOrangeDark = BrandYellowDark
val AccentOrangeContainer = BrandYellowContainer
val OnAccentOrangeContainer = OnBrandYellowContainer
