package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.KinetixTab

@Composable
fun KinetixBottomNav(
    selectedTab: KinetixTab,
    onTabSelected: (KinetixTab) -> Unit,
    pendingTasksCount: Int = 0,
    pendingShoppingCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("kinetix_bottom_navigation"),
        windowInsets = WindowInsets.navigationBars,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // 1. Tareas
        val isTareas = selectedTab == KinetixTab.TAREAS
        NavigationBarItem(
            selected = isTareas,
            onClick = { onTabSelected(KinetixTab.TAREAS) },
            alwaysShowLabel = true,
            icon = {
                if (pendingTasksCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = if (pendingTasksCount > 99) "99+" else pendingTasksCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isTareas) Icons.Filled.Checklist else Icons.Outlined.Checklist,
                            contentDescription = "Tareas"
                        )
                    }
                } else {
                    Icon(
                        imageVector = if (isTareas) Icons.Filled.Checklist else Icons.Outlined.Checklist,
                        contentDescription = "Tareas"
                    )
                }
            },
            label = {
                Text(
                    text = "Tareas",
                    fontSize = 11.sp,
                    fontWeight = if (isTareas) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("bottom_nav_tareas")
        )

        // 2. Horario (Nuevo apartado de horario)
        val isHorario = selectedTab == KinetixTab.HORARIO
        NavigationBarItem(
            selected = isHorario,
            onClick = { onTabSelected(KinetixTab.HORARIO) },
            alwaysShowLabel = true,
            icon = {
                Icon(
                    imageVector = if (isHorario) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                    contentDescription = "Horario"
                )
            },
            label = {
                Text(
                    text = "Horario",
                    fontSize = 11.sp,
                    fontWeight = if (isHorario) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("bottom_nav_horario")
        )

        // 3. Mercado
        val isMercado = selectedTab == KinetixTab.MERCADO
        NavigationBarItem(
            selected = isMercado,
            onClick = { onTabSelected(KinetixTab.MERCADO) },
            alwaysShowLabel = true,
            icon = {
                if (pendingShoppingCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ) {
                                Text(
                                    text = if (pendingShoppingCount > 99) "99+" else pendingShoppingCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isMercado) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                            contentDescription = "Mercado"
                        )
                    }
                } else {
                    Icon(
                        imageVector = if (isMercado) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = "Mercado"
                    )
                }
            },
            label = {
                Text(
                    text = "Mercado",
                    fontSize = 11.sp,
                    fontWeight = if (isMercado) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("bottom_nav_mercado")
        )

        // 4. Finanzas
        val isFinanzas = selectedTab == KinetixTab.FINANZAS
        NavigationBarItem(
            selected = isFinanzas,
            onClick = { onTabSelected(KinetixTab.FINANZAS) },
            alwaysShowLabel = true,
            icon = {
                Icon(
                    imageVector = if (isFinanzas) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                    contentDescription = "Finanzas"
                )
            },
            label = {
                Text(
                    text = "Finanzas",
                    fontSize = 11.sp,
                    fontWeight = if (isFinanzas) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("bottom_nav_finanzas")
        )

        // 5. Perfil
        val isPerfil = selectedTab == KinetixTab.PERFIL
        NavigationBarItem(
            selected = isPerfil,
            onClick = { onTabSelected(KinetixTab.PERFIL) },
            alwaysShowLabel = true,
            icon = {
                Icon(
                    imageVector = if (isPerfil) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Perfil"
                )
            },
            label = {
                Text(
                    text = "Perfil",
                    fontSize = 11.sp,
                    fontWeight = if (isPerfil) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("bottom_nav_perfil")
        )
    }
}
