package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.OilBarrel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ShoppingItemEntity
import com.example.ui.theme.KinetixOnPrimary
import com.example.ui.theme.KinetixOnSecondary
import com.example.ui.theme.KinetixOnSecondaryContainer
import com.example.ui.theme.KinetixOnTertiaryFixed
import com.example.ui.theme.KinetixPrimary
import com.example.ui.theme.KinetixSecondary
import com.example.ui.theme.KinetixSecondaryContainer
import com.example.ui.theme.KinetixTertiary
import com.example.ui.theme.KinetixTertiaryFixed
import com.example.ui.viewmodel.KinetixTab
import com.example.ui.viewmodel.KinetixViewModel

@Composable
fun ShoppingScreen(
    viewModel: KinetixViewModel,
    onNavigateToTab: (KinetixTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val allItems by viewModel.allShoppingItems.collectAsState()
    var newItemText by remember { mutableStateOf("") }

    val pendingItems = allItems.filter { !it.isBought }
    val boughtItems = allItems.filter { it.isBought }
    val totalCount = allItems.size.coerceAtLeast(1)
    val boughtCount = boughtItems.size
    val progress = (boughtCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Status & Context Card
                ShoppingStatusCard(
                    boughtCount = boughtCount,
                    totalCount = totalCount,
                    progress = progress
                )
            }

            item {
                // Quick Add Bar
                QuickAddBar(
                    text = newItemText,
                    onTextChange = { newItemText = it },
                    onAdd = {
                        if (newItemText.isNotBlank()) {
                            viewModel.addShoppingItem(newItemText.trim())
                            newItemText = ""
                        }
                    }
                )
            }

            // Section: Pendientes
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Pendientes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = "${pendingItems.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Toca para tachar",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(pendingItems, key = { it.id }) { item ->
                ShoppingItemRow(
                    item = item,
                    onToggleBought = { viewModel.toggleShoppingItem(item.id, it) },
                    onDelete = { viewModel.deleteShoppingItem(item.id) }
                )
            }

            if (pendingItems.isEmpty()) {
                item {
                    Text(
                        text = "¡Todo el mercado está en el carrito!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Section: Comprados
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Comprados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = CircleShape,
                            color = KinetixSecondaryContainer
                        ) {
                            Text(
                                text = "${boughtItems.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = KinetixOnSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Listo en carrito",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = KinetixSecondary
                    )
                }
            }

            items(boughtItems, key = { it.id }) { item ->
                ShoppingItemRow(
                    item = item,
                    onToggleBought = { viewModel.toggleShoppingItem(item.id, it) },
                    onDelete = { viewModel.deleteShoppingItem(item.id) }
                )
            }

            item {
                // Direct navigation shortcuts
                ShoppingCrossNavigationCard(
                    onNavigateToTab = onNavigateToTab
                )
            }

            item {
                // Bottom spacing for sticky dock
                Spacer(modifier = Modifier.height(96.dp))
            }
        }

        // Bottom Floating Dock
        ShoppingBottomDock(
            totalEstimated = "$42.50",
            onClearCompleted = { viewModel.clearBoughtShoppingItems() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
private fun ShoppingStatusCard(
    boughtCount: Int,
    totalCount: Int,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shopping_status_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(KinetixPrimary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalMall,
                                contentDescription = null,
                                tint = KinetixPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Text(
                            text = "LISTA ACTIVA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KinetixPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Mercado del Hogar",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = KinetixPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "$boughtCount de $totalCount",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Text(
                        text = "${(progress * 100).toInt()}% completado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0.05f, 1f))
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(KinetixPrimary, KinetixSecondary)
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun QuickAddBar(
    text: String,
    onTextChange: (String) -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AddShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(20.dp)
            )

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = {
                    Text(
                        text = "Añadir ítem (ej. Leche, Manzanas)...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("new_item_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() })
            )

            IconButton(
                onClick = onAdd,
                modifier = Modifier
                    .size(44.dp)
                    .background(KinetixPrimary, RoundedCornerShape(10.dp))
                    .testTag("add_item_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar ítem",
                    tint = KinetixOnPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun ShoppingItemRow(
    item: ShoppingItemEntity,
    onToggleBought: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shopping_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isBought) MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Circle Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isBought) KinetixSecondary else MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    .clickable { onToggleBought(!item.isBought) }
                    .testTag("shopping_check_${item.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (item.isBought) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Comprado",
                        tint = KinetixOnSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Icon for category/item
            val itemIcon = when (item.iconName) {
                "water_drop" -> Icons.Default.WaterDrop
                "egg" -> Icons.Default.Egg
                "coffee" -> Icons.Default.Coffee
                "nutrition" -> Icons.Default.Eco
                "clean_hands" -> Icons.Default.CleanHands
                "bakery_dining" -> Icons.Default.BakeryDining
                "oil_barrel" -> Icons.Default.OilBarrel
                "grain" -> Icons.Default.Grain
                "eco" -> Icons.Default.Eco
                else -> Icons.Default.ShoppingBag
            }
            Icon(
                imageVector = itemIcon,
                contentDescription = null,
                tint = if (item.isBought) MaterialTheme.colorScheme.outlineVariant else if (item.isPriority) KinetixTertiary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )

            // Name
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (item.isBought) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (item.isBought) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
            )

            // Priority Badge if any
            if (item.isPriority && !item.isBought) {
                Surface(
                    shape = CircleShape,
                    color = KinetixTertiaryFixed
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PriorityHigh,
                            contentDescription = null,
                            tint = KinetixOnTertiaryFixed,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Prioridad",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KinetixOnTertiaryFixed
                        )
                    }
                }
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = if (item.isBought) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ShoppingBottomDock(
    totalEstimated: String,
    onClearCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("shopping_bottom_dock"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.98f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Presupuesto sugerido",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total estimado: $totalEstimated",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onClearCompleted)
                    .testTag("btn_clear_completed")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveDone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Limpiar comprados",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun ShoppingCrossNavigationCard(
    onNavigateToTab: (KinetixTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shopping_cross_navigation_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Navegar a otras secciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Accede a tus tareas pendientes, finanzas o estado general del perfil:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onNavigateToTab(KinetixTab.TAREAS) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_btn_shopping_to_tareas"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Tareas", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onNavigateToTab(KinetixTab.HORARIO) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_btn_shopping_to_horario"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Horario", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onNavigateToTab(KinetixTab.FINANZAS) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_btn_shopping_to_finanzas"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Finanzas", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onNavigateToTab(KinetixTab.PERFIL) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_btn_shopping_to_perfil"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Perfil", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
