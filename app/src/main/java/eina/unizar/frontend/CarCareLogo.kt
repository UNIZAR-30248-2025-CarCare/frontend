package eina.unizar.frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LogoCarCare(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val isDarkTheme = isSystemInDarkTheme()

    val logoRes = if (isDarkTheme) {
        R.drawable.carcare_logo_dark  // Logo para modo oscuro
    } else {
        R.drawable.carcare_logo // Logo para modo claro
    }

    Image(
        painter = painterResource(id = logoRes),
        contentDescription = "CarCare Logo",
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}