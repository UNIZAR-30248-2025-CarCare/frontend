package eina.unizar.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class NavTab(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Inicio : NavTab("home", Icons.Filled.Home, "Inicio")
    object Mapa : NavTab("mapa", Icons.Filled.LocationOn, "Mapa")
    object Incidencias : NavTab("incidencias", Icons.Filled.Warning, "Incidencias")
    object Reservas : NavTab("reservas", Icons.Filled.DateRange, "Reservas")
}


@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavTab.Inicio,
        NavTab.Mapa,
        NavTab.Incidencias,
        NavTab.Reservas
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.shadow(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFEF4444),
                    selectedTextColor = Color(0xFFEF4444),
                    unselectedIconColor = Color(0xFF6B7280),
                    unselectedTextColor = Color(0xFF6B7280),
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}
