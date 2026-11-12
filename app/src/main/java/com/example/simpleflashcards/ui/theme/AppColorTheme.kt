
package com.example.simpleflashcards.ui.theme

import androidx.compose.ui.graphics.Color

data class AppColorPalette(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val text: Color,
    val cardStart: Color,
    val cardEnd: Color,
    val onPrimary: Color
)

enum class AppColorTheme(
    val displayName: String,
    val description: String,
    val zenLight: AppColorPalette,
    val vibrantLight: AppColorPalette,
    val zenDark: AppColorPalette,
    val vibrantDark: AppColorPalette
) {

    OCEAN(
        "Ocean", "Calm blues and teal",

        AppColorPalette(
            Color(0xFF4D91B8), Color(0xFF75B3CC),
            Color(0xFFF2F8FC), Color(0xFFFFFFFF),
            Color(0xFF18364A),
            Color(0xFFEAF5FC), Color(0xFFDCECF8),
            Color.White
        ),

        AppColorPalette(
            Color(0xFF087DBB), Color(0xFF35A3D5),
            Color(0xFFEFF8FD), Color(0xFFFFFFFF),
            Color(0xFF10334B),
            Color(0xFFE1F4FE), Color(0xFFCBE9FA),
            Color.White
        ),

        AppColorPalette(
            Color(0xFF67B6D9), Color(0xFF83C5D8),
            Color(0xFF0D202D), Color(0xFF142C3A),
            Color(0xFFE5F1F7),
            Color(0xFF19394D), Color(0xFF153247),
            Color(0xFF102A38)
        ),

        AppColorPalette(
            Color(0xFF29A9E5), Color(0xFF50BCEB),
            Color(0xFF071B2C), Color(0xFF102A3E),
            Color(0xFFE5F5FF),
            Color(0xFF10446A), Color(0xFF0B3455),
            Color(0xFF071B2C)
        )
    ),

    FOREST(
        "Forest", "Natural greens",

        AppColorPalette(
            Color(0xFF579A70), Color(0xFF80B28D),
            Color(0xFFF1F9F3), Color(0xFFFFFFFF),
            Color(0xFF1C3D2A),
            Color(0xFFE6F5E9), Color(0xFFD7EEDF),
            Color.White
        ),

        AppColorPalette(
            Color(0xFF168348), Color(0xFF42A86C),
            Color(0xFFEFF9F1), Color(0xFFFFFFFF),
            Color(0xFF123B26),
            Color(0xFFE0F5E5), Color(0xFFC9EBD4),
            Color.White
        ),

        AppColorPalette(
            Color(0xFF78BF91), Color(0xFF91CBA3),
            Color(0xFF10271D), Color(0xFF193729),
            Color(0xFFE7F5EA),
            Color(0xFF1D4432), Color(0xFF193A2C),
            Color(0xFF10271D)
        ),

        AppColorPalette(
            Color(0xFF39C477), Color(0xFF60D58F),
            Color(0xFF09251A), Color(0xFF103A29),
            Color(0xFFE5F9EB),
            Color(0xFF125137), Color(0xFF0C402C),
            Color(0xFF09251A)
        )
    ),

    LAVENDER(
        "Lavender", "Soft purple tones",

        AppColorPalette(
            Color(0xFF9672C4), Color(0xFFB49BD5),
            Color(0xFFF7F3FC), Color(0xFFFFFFFF),
            Color(0xFF382452),
            Color(0xFFF1EAFB), Color(0xFFE7DDF7),
            Color.White
        ),

        AppColorPalette(
            Color(0xFF8538C5), Color(0xFFA46BD5),
            Color(0xFFF7F1FD), Color(0xFFFFFFFF),
            Color(0xFF35164F),
            Color(0xFFF0E3FC), Color(0xFFE2CDF8),
            Color.White
        ),

        AppColorPalette(
            Color(0xFFB18AE0), Color(0xFFC3A6E9),
            Color(0xFF21182E), Color(0xFF30233F),
            Color(0xFFF1EAF9),
            Color(0xFF44305D), Color(0xFF38264F),
            Color(0xFF21182E)
        ),

        AppColorPalette(
            Color(0xFFBD70F4), Color(0xFFD092FA),
            Color(0xFF1D1030), Color(0xFF34204B),
            Color(0xFFF7EEFF),
            Color(0xFF54247B), Color(0xFF421B65),
            Color(0xFF1D1030)
        )
    ),

    SUNRISE(
        "Sunrise", "Warm peach and amber",

        AppColorPalette(
            Color(0xFFC88B4B), Color(0xFFE0AC79),
            Color(0xFFFFF8EF), Color(0xFFFFFFFF),
            Color(0xFF51351F),
            Color(0xFFFFF1DB), Color(0xFFFBE5C9),
            Color(0xFF38200D)
        ),

        AppColorPalette(
            Color(0xFFD97722), Color(0xFFE99A4D),
            Color(0xFFFFF7EF), Color(0xFFFFFFFF),
            Color(0xFF522B16),
            Color(0xFFFFECCF), Color(0xFFFFDFB4),
            Color(0xFF38200D)
        ),

        AppColorPalette(
            Color(0xFFE5AC6D), Color(0xFFF0C18E),
            Color(0xFF302116), Color(0xFF453020),
            Color(0xFFFFF1DF),
            Color(0xFF60402A), Color(0xFF50351F),
            Color(0xFF302116)
        ),

        AppColorPalette(
            Color(0xFFF39A3F), Color(0xFFFFB66B),
            Color(0xFF29190F), Color(0xFF402719),
            Color(0xFFFFF2E4),
            Color(0xFF75401D), Color(0xFF603115),
            Color(0xFF29190F)
        )
    ),

    ROSE(
        "Rose", "Gentle pink and burgundy",

        AppColorPalette(
            Color(0xFFC47C99), Color(0xFFD99AAF),
            Color(0xFFFCF3F6), Color(0xFFFFFFFF),
            Color(0xFF512A3B),
            Color(0xFFF9E8EF), Color(0xFFF4DDE7),
            Color.White
        ),

        AppColorPalette(
            Color(0xFFD93E79), Color(0xFFE76A98),
            Color(0xFFFFF2F6), Color(0xFFFFFFFF),
            Color(0xFF51172F),
            Color(0xFFFDE3EC), Color(0xFFF8CDDF),
            Color.White
        ),

        AppColorPalette(
            Color(0xFFD98EAD), Color(0xFFE5A8BE),
            Color(0xFF2D1923), Color(0xFF422534),
            Color(0xFFF9EAF0),
            Color(0xFF563044), Color(0xFF48263A),
            Color(0xFF2D1923)
        ),

        AppColorPalette(
            Color(0xFFEE5790), Color(0xFFF47EAA),
            Color(0xFF290F1D), Color(0xFF44192D),
            Color(0xFFFFEAF2),
            Color(0xFF67213F), Color(0xFF501832),
            Color(0xFF290F1D)
        )
    ),

    SLATE(
        "Slate", "Neutral slate and charcoal",

        AppColorPalette(
            Color(0xFF788592), Color(0xFF9AA6B0),
            Color(0xFFF4F5F6), Color(0xFFFFFFFF),
            Color(0xFF303A43),
            Color(0xFFEAEDF0), Color(0xFFDDE2E7),
            Color.White
        ),

        AppColorPalette(
            Color(0xFF536474), Color(0xFF778A9B),
            Color(0xFFF3F4F5), Color(0xFFFFFFFF),
            Color(0xFF27333D),
            Color(0xFFE6E9ED), Color(0xFFD5DBE1),
            Color.White
        ),

        AppColorPalette(
            Color(0xFFA1ADB8), Color(0xFFB3BEC7),
            Color(0xFF1B2025), Color(0xFF2A323A),
            Color(0xFFEDF0F2),
            Color(0xFF333C45), Color(0xFF2A323A),
            Color(0xFF1B2025)
        ),

        AppColorPalette(
            Color(0xFF8295A7), Color(0xFF9DADBC),
            Color(0xFF151B21), Color(0xFF26323D),
            Color(0xFFF0F3F5),
            Color(0xFF394754), Color(0xFF2D3945),
            Color(0xFF151B21)
        )
    );

    fun palette(
        style: AppColorStyle,
        darkTheme: Boolean
    ): AppColorPalette {
        return when {
            darkTheme && style == AppColorStyle.VIBRANT -> vibrantDark
            darkTheme -> zenDark
            style == AppColorStyle.VIBRANT -> vibrantLight
            else -> zenLight
        }
    }

    // Compatibility properties for existing theme previews.
    val lightPrimary get() = zenLight.primary
    val lightSecondary get() = zenLight.secondary
    val lightBackground get() = zenLight.background
    val lightSurface get() = zenLight.surface
    val lightText get() = zenLight.text

    val darkPrimary get() = zenDark.primary
    val darkSecondary get() = zenDark.secondary
    val darkBackground get() = zenDark.background
    val darkSurface get() = zenDark.surface
    val darkText get() = zenDark.text

    companion object {
        fun fromPreference(value: String?): AppColorTheme {
            return values().firstOrNull { it.name == value } ?: OCEAN
        }
    }
}
