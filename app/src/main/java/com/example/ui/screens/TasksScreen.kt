package com.example.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.SectionTutorialCard
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.KinetixDatabase
import com.example.data.local.entity.ScheduleItemEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.notifications.NotificationHelper
import com.example.ui.theme.KinetixError
import com.example.ui.theme.KinetixErrorContainer
import com.example.ui.theme.KinetixOnErrorContainer
import com.example.ui.theme.KinetixOnPrimary
import com.example.ui.theme.KinetixOnPrimaryContainer
import com.example.ui.theme.KinetixOnPrimaryFixed
import com.example.ui.theme.KinetixOnSecondaryContainer
import com.example.ui.theme.KinetixOnTertiaryFixed
import com.example.ui.theme.KinetixPrimary
import com.example.ui.theme.KinetixPrimaryContainer
import com.example.ui.theme.KinetixPrimaryFixed
import com.example.ui.theme.KinetixSecondary
import com.example.ui.theme.KinetixSecondaryContainer
import com.example.ui.theme.KinetixSecondaryFixed
import com.example.ui.theme.KinetixTertiary
import com.example.ui.theme.KinetixTertiaryFixed
import com.example.ui.viewmodel.KinetixTab
import com.example.ui.viewmodel.KinetixViewModel

@Composable
fun TasksScreen(
    viewModel: KinetixViewModel,
    onNavigateToTab: (KinetixTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pendingTasks by viewModel.filteredPendingTasks.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val selectedFilter by viewModel.selectedCategoryFilter.collectAsState()
    val isFocusModeActive by viewModel.isFocusModeActive.collectAsState()
    val showAddTaskDialog by viewModel.showAddTaskDialog.collectAsState()
    val gamificationStats by viewModel.gamificationStats.collectAsState()
    val taskCategories by viewModel.taskCategories.collectAsState()
    val dismissedTutorials by viewModel.dismissedTutorials.collectAsState()
    val showTutorial = dismissedTutorials["TAREAS"] != true

    val completedCount = allTasks.count { it.isCompleted }
    val totalCount = allTasks.size.coerceAtLeast(1)
    val completionRatio = completedCount.toFloat() / totalCount.toFloat()
    val scheduleItems by viewModel.scheduleItems.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionTutorialCard(
                    isVisible = showTutorial,
                    title = "Gestión de Tareas",
                    subtitle = "Trabajo, Estudio y Vida Cotidiana con categorías personalizables",
                    tips = listOf(
                        "Organiza y filtra tus tareas por Trabajo, Estudio, Vida Cotidiana o agrega nuevas categorías con '+ Categoría'.",
                        "Marca tareas completadas para ganar XP y mantener tu racha activa.",
                        "Configura recordatorios y nivel de urgencia para entregas a tiempo.",
                        "Usa el Modo Enfoque ⚡ para concentrarte en tus prioridades de hoy."
                    ),
                    onDismiss = { viewModel.dismissTutorial("TAREAS") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Gamification Progress & Momentum Banner
                GamificationHeaderCard(
                    completedCount = completedCount,
                    totalCount = totalCount,
                    completionRatio = completionRatio,
                    currentStreak = gamificationStats?.currentStreak ?: 0,
                    levelNumber = gamificationStats?.level ?: 1,
                    levelTitle = gamificationStats?.levelTitle ?: "Nivel 1: Primeros Pasos"
                )
            }

            item {
                // Section: Horario de Estudio y Trabajo (Top Hero Quick Access)
                ScheduleHeroBanner(
                    scheduleItemsCount = scheduleItems.size,
                    onOpenSchedule = { onNavigateToTab(KinetixTab.HORARIO) }
                )
            }

            item {
                // Horizontal Filter Chips with dynamic category management
                CategoryChipRow(
                    categories = taskCategories,
                    selectedCategory = selectedFilter,
                    onSelectCategory = { viewModel.selectCategoryFilter(it) },
                    onAddCategory = { viewModel.addCategory("TASK", it) },
                    onDeleteCategory = { viewModel.deleteCategory("TASK", it) },
                    defaultCategories = setOf("Trabajo", "Estudio", "Vida Cotidiana")
                )
            }

            item {
                // Section: Tareas Pendientes Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = KinetixPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Tareas Pendientes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "MENOR TIEMPO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // List of pending tasks sorted by deadline
            items(pendingTasks, key = { it.id }) { task ->
                TaskItemCard(
                    task = task,
                    onToggleComplete = { completed ->
                        viewModel.toggleTaskCompletion(task.id, completed)
                    },
                    onDelete = { viewModel.deleteTask(task.id) }
                )
            }

            if (pendingTasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "¡No tienes tareas pendientes en esta categoría!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                // Visual Insight / Hero Banner
                VisualInsightBanner()
            }

            item {
                // Section: Horario Diario (Daily Schedule Timeline)
                DailyScheduleSection(
                    scheduleItems = scheduleItems,
                    onNavigateToTab = onNavigateToTab
                )
            }

            item {
                // Quick Navigation Cards
                TasksCrossNavigationCard(
                    onNavigateToTab = onNavigateToTab
                )
            }

            item {
                // Bottom spacing for sticky action dock
                Spacer(modifier = Modifier.height(96.dp))
            }
        }

        // Sticky Ergonomic Quick Action Floating Bar
        FloatingActionDock(
            isFocusModeActive = isFocusModeActive,
            onToggleFocusMode = { viewModel.toggleFocusMode() },
            onAddTaskClick = { viewModel.setShowAddTaskDialog(true) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        )
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        val context = LocalContext.current
        AddTaskDialog(
            availableCategories = taskCategories,
            onDismiss = {
                NotificationHelper.stopAlarmSound()
                viewModel.setShowAddTaskDialog(false)
            },
            onConfirm = { title, category, deadlineMs, difficulty, duration, project, reminder, isSpecificDayOnly ->
                viewModel.addNewTask(
                    title = title,
                    category = category,
                    deadlineEpochMs = deadlineMs,
                    difficulty = difficulty,
                    duration = duration,
                    project = project,
                    isSpecificDayOnly = isSpecificDayOnly
                )
                if (reminder) {
                    NotificationHelper.showTaskReminder(
                        context = context,
                        title = title,
                        message = "Alarma programada ($difficulty) para $title",
                        ringAlarm = true
                    )
                }
            }
        )
    }
}

@Composable
private fun GamificationHeaderCard(
    completedCount: Int,
    totalCount: Int,
    completionRatio: Float,
    currentStreak: Int = 0,
    levelNumber: Int = 1,
    levelTitle: String = "Primeros Pasos"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gamification_progress_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(KinetixTertiaryFixed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = KinetixTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Racha: $currentStreak tareas seguidas",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = KinetixPrimaryFixed.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "NIVEL $levelNumber • ${levelTitle.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KinetixPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Tu ritmo de hoy",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$completedCount de $totalCount completadas (${(completionRatio * 100).toInt()}%)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Gradient Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(completionRatio.coerceIn(0.05f, 1f))
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
private fun FilterChipsRow(
    selectedFilter: String,
    totalTasksCount: Int,
    onSelectFilter: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Todas Chip
        FilterChip(
            label = "Todas",
            count = totalTasksCount,
            isSelected = selectedFilter == "Todas",
            icon = Icons.Default.GridView,
            onClick = { onSelectFilter("Todas") }
        )
        // Trabajo Chip
        FilterChip(
            label = "Trabajo",
            dotColor = KinetixPrimary,
            isSelected = selectedFilter == "Trabajo",
            onClick = { onSelectFilter("Trabajo") }
        )
        // Estudio Chip
        FilterChip(
            label = "Estudio",
            dotColor = KinetixTertiary,
            isSelected = selectedFilter == "Estudio",
            onClick = { onSelectFilter("Estudio") }
        )
        // Vida Cotidiana Chip
        FilterChip(
            label = "Vida Cotidiana",
            dotColor = KinetixSecondary,
            isSelected = selectedFilter == "Vida",
            onClick = { onSelectFilter("Vida") }
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    count: Int? = null,
    dotColor: Color? = null,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) KinetixPrimary else MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag("filter_chip_$label")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) KinetixOnPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            } else if (dotColor != null) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(dotColor, CircleShape)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) KinetixOnPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (count != null) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(
                            if (isSelected) KinetixOnPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceContainerHigh,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) KinetixOnPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemCard(
    task: TaskEntity,
    onToggleComplete: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Checkbox button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (task.isCompleted) KinetixSecondary else MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    .clickable { onToggleComplete(!task.isCompleted) }
                    .testTag("task_check_${task.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completado",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Category & Urgency Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category & Difficulty Badges
                    val (catBg, catColor) = when (task.category) {
                        "Estudio" -> Pair(KinetixTertiaryFixed, KinetixOnTertiaryFixed)
                        "Trabajo" -> Pair(KinetixPrimaryFixed, KinetixOnPrimaryFixed)
                        else -> Pair(KinetixSecondaryContainer, KinetixOnSecondaryContainer)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = catBg
                        ) {
                            Text(
                                text = task.category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = catColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        val (diffBg, diffColor) = when (task.difficulty) {
                            "ALTA" -> Pair(KinetixErrorContainer, KinetixOnErrorContainer)
                            "MEDIA" -> Pair(KinetixTertiaryFixed, KinetixOnTertiaryFixed)
                            else -> Pair(KinetixSecondaryContainer, KinetixOnSecondaryContainer)
                        }
                        Surface(
                            shape = CircleShape,
                            color = diffBg
                        ) {
                            Text(
                                text = "Dif: ${task.difficulty}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = diffColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Urgency Badge
                    when (task.urgency) {
                        "ALTA" -> {
                            Surface(
                                shape = CircleShape,
                                color = KinetixErrorContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = null,
                                        tint = KinetixOnErrorContainer,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = task.deadlineLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = KinetixOnErrorContainer
                                    )
                                }
                            }
                        }
                        "MEDIA" -> {
                            Surface(
                                shape = CircleShape,
                                color = KinetixTertiaryFixed
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = KinetixOnTertiaryFixed,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = task.deadlineLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = KinetixOnTertiaryFixed
                                    )
                                }
                            }
                        }
                        else -> {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = task.deadlineLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )

                // Subtitle metadata (duration + project)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = task.durationLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val projectIcon = when (task.contextIcon) {
                            "school" -> Icons.Default.School
                            "group" -> Icons.Default.Group
                            "home" -> Icons.Default.Home
                            else -> Icons.Default.MenuBook
                        }
                        val projectColor = when (task.category) {
                            "Estudio" -> KinetixTertiary
                            "Trabajo" -> KinetixPrimary
                            else -> KinetixSecondary
                        }
                        Icon(
                            imageVector = projectIcon,
                            contentDescription = null,
                            tint = projectColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = task.projectTag,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = projectColor
                        )
                    }
                }
            }

            // Optional delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun VisualInsightBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = KinetixDatabase.WORKSPACE_BANNER_URL,
                contentDescription = "Espacio de trabajo minimalista y productivo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay for pristine text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xDD131B2E),
                                Color(0x99131B2E),
                                Color(0x22131B2E)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ESTADO DE FLUJO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = KinetixPrimaryFixed,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Un bloque a la vez.",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tu atención es tu mayor activo financiero y personal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun ScheduleHeroBanner(
    scheduleItemsCount: Int,
    onOpenSchedule: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenSchedule() }
            .testTag("schedule_hero_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Mi Horario (Estudio y Trabajo)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (scheduleItemsCount > 0)
                            "$scheduleItemsCount clases/turnos activos • Ver horario"
                        else
                            "Toca para ver o crear tu horario de estudio y trabajo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "Abrir",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun DailyScheduleSection(
    scheduleItems: List<ScheduleItemEntity> = emptyList(),
    onNavigateToTab: (KinetixTab) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarViewDay,
                    contentDescription = null,
                    tint = KinetixPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Horario Semanal Registrado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = KinetixPrimaryContainer,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigateToTab(KinetixTab.HORARIO) }
                    .testTag("btn_header_ver_horario")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = KinetixOnPrimaryContainer,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Ver Horario",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KinetixOnPrimaryContainer
                    )
                }
            }
        }

        // Timeline Container Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (scheduleItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Aún no tienes bloques de horario agregados",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Registra tus materias de estudio o turnos de trabajo para tener presente tu rutina y recibir alertas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    scheduleItems.take(4).forEachIndexed { idx, item ->
                        ScheduleSlotItem(
                            time = "${item.startTime} – ${item.endTime}",
                            title = item.title,
                            subtitle = "${item.type} • ${item.location} • ${item.dayName}",
                            isActive = idx == 0,
                            tag = if (idx == 0) item.dayName else null,
                            trailingIcon = if (item.type == "Estudio") Icons.Default.School else Icons.Default.Schedule
                        )
                    }
                }

                // Direct button to Horario
                Button(
                    onClick = { onNavigateToTab(KinetixTab.HORARIO) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_ir_al_horario"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Gestionar Horario de Estudio y Trabajo",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleSlotItem(
    time: String,
    title: String,
    subtitle: String,
    progressPercent: Int? = null,
    isActive: Boolean = false,
    tag: String? = null,
    trailingIcon: ImageVector? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline indicator node
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(if (isActive) 14.dp else 10.dp)
                .background(
                    if (isActive) KinetixPrimary else MaterialTheme.colorScheme.surfaceContainerHighest,
                    CircleShape
                )
                .then(
                    if (isActive) Modifier.border(3.dp, KinetixPrimaryFixed, CircleShape) else Modifier
                )
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isActive) KinetixPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (tag != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = KinetixPrimaryFixed
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KinetixOnPrimaryFixed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Content card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (progressPercent != null) {
                            Text(
                                text = "$progressPercent%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else if (trailingIcon != null) {
                            Icon(
                                imageVector = trailingIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (progressPercent != null) {
                        LinearProgressIndicator(
                            progress = { progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = KinetixPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    }

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingActionDock(
    isFocusModeActive: Boolean,
    onToggleFocusMode: () -> Unit,
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("floating_action_dock"),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.96f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onToggleFocusMode)
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = if (isFocusModeActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isFocusModeActive) "Modo enfoque activo" else "Modo normal",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onAddTaskClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = CircleShape,
                modifier = Modifier.testTag("btn_nueva_tarea")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Nueva Tarea",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    availableCategories: List<String> = listOf("Trabajo", "Estudio", "Vida Cotidiana"),
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, deadlineMs: Long, difficulty: String, duration: String, project: String, reminder: Boolean, isSpecificDayOnly: Boolean) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(availableCategories.firstOrNull() ?: "Trabajo") }
    var difficulty by remember { mutableStateOf("MEDIA") }
    var duration by remember { mutableStateOf("Duración: 1h") }
    var project by remember { mutableStateOf("General") }
    var isSpecificDayOnly by remember { mutableStateOf(false) }
    var isAlarmTesting by remember { mutableStateOf(false) }

    val now = remember { System.currentTimeMillis() }
    val deadlinePresets = remember {
        listOf(
            Triple("+3 horas (Hoy)", now + 3 * 3600 * 1000L, "ALTA"),
            Triple("Mañana (+24h)", now + 24 * 3600 * 1000L, "ALTA"),
            Triple("En 2 días (+48h)", now + 48 * 3600 * 1000L, "MEDIA"),
            Triple("En 3 días (+72h)", now + 72 * 3600 * 1000L, "MEDIA"),
            Triple("En 1 semana", now + 7 * 24 * 3600 * 1000L, "BAJA")
        )
    }
    var selectedPresetIndex by remember { mutableStateOf(1) } // Mañana
    val selectedDeadlineMs = deadlinePresets[selectedPresetIndex].second

    // Automatic urgency calculation based on the deadline
    val hoursRemaining = ((selectedDeadlineMs - System.currentTimeMillis()) / (3600 * 1000L)).coerceAtLeast(0)
    val calculatedUrgency = when {
        hoursRemaining <= 24 -> "ALTA"
        hoursRemaining <= 72 -> "MEDIA"
        else -> "BAJA"
    }

    var reminderEnabled by remember { mutableStateOf(true) }
    var hasNotifPerm by remember {
        mutableStateOf(NotificationHelper.hasNotificationPermission(context))
    }

    val permLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotifPerm = isGranted
        if (isGranted) {
            Toast.makeText(context, "Permisos de alerta activados", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = {
            NotificationHelper.stopAlarmSound()
            onDismiss()
        },
        title = {
            Text(
                text = "Crear Nueva Tarea",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la tarea") },
                    placeholder = { Text("Ej: Entregar informe final") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    singleLine = true
                )

                // Category selection
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableCategories.forEach { cat ->
                        Surface(
                            shape = CircleShape,
                            color = if (category == cat) KinetixPrimary else MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { category = cat }
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (category == cat) FontWeight.Bold else FontWeight.Normal,
                                color = if (category == cat) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                // Modalidad de fecha (Fecha límite vs Día específico)
                Text(
                    text = "Tipo de programación",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Pair(false, "Hasta ese día (Límite)"),
                        Pair(true, "En día específico")
                    ).forEach { (specific, label) ->
                        val isSelected = isSpecificDayOnly == specific
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { isSpecificDayOnly = specific }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                // Deadline selection (determines urgency)
                Text(
                    text = if (isSpecificDayOnly) "Día fijado para hacerla" else "Fecha límite de entrega",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    deadlinePresets.forEachIndexed { index, (label, _, _) ->
                        val isSelected = selectedPresetIndex == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedPresetIndex = index }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Automatic Urgency Banner derived from deadline
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (calculatedUrgency) {
                        "ALTA" -> KinetixErrorContainer
                        "MEDIA" -> KinetixTertiaryFixed
                        else -> KinetixSecondaryContainer
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = when (calculatedUrgency) {
                                "ALTA" -> Icons.Default.Alarm
                                "MEDIA" -> Icons.Default.Schedule
                                else -> Icons.Default.CalendarToday
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Urgencia calculada: $calculatedUrgency (por tiempo restante)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Difficulty selector (Baja, Media, Alta)
                Text(
                    text = "Dificultad de la tarea",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("BAJA", "MEDIA", "ALTA").forEach { diff ->
                        val isSelected = difficulty == diff
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) {
                                when (diff) {
                                    "ALTA" -> KinetixErrorContainer
                                    "MEDIA" -> KinetixTertiaryFixed
                                    else -> KinetixSecondaryContainer
                                }
                            } else MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { difficulty = diff }
                        ) {
                            Text(
                                text = diff,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = project,
                    onValueChange = { project = it },
                    label = { Text("Proyecto / Materia") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Notification and Alarm section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Alarma Sonora y Notificación",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (hasNotifPerm) "Suena con tono de alarma del sistema" else "Requiere permiso del dispositivo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { checked ->
                            reminderEnabled = checked
                            if (checked && !hasNotifPerm) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    hasNotifPerm = true
                                }
                            }
                        }
                    )
                }

                // Audio Alarm Tester Button
                if (reminderEnabled) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isAlarmTesting) KinetixErrorContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (isAlarmTesting) {
                                    NotificationHelper.stopAlarmSound()
                                    isAlarmTesting = false
                                } else {
                                    NotificationHelper.playAlarmSound(context)
                                    isAlarmTesting = true
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isAlarmTesting) Icons.Default.Alarm else Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = if (isAlarmTesting) KinetixError else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isAlarmTesting) "🔊 Sonando alarma... Toca para detener" else "🔔 Probar cómo sonará la alarma",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isAlarmTesting) KinetixError else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    NotificationHelper.stopAlarmSound()
                    onConfirm(title, category, selectedDeadlineMs, difficulty, duration, project, reminderEnabled, isSpecificDayOnly)
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = KinetixPrimary),
                modifier = Modifier.testTag("confirm_add_task")
            ) {
                Text("Guardar Tarea", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                NotificationHelper.stopAlarmSound()
                onDismiss()
            }) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun TasksCrossNavigationCard(
    onNavigateToTab: (KinetixTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tasks_cross_navigation_card"),
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
                    text = "Acceso Rápido a Otras Pantallas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Navega con un toque a tus listas de compras, estadísticas de perfil o presupuesto financiero:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onNavigateToTab(KinetixTab.HORARIO) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_btn_to_horario"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Horario", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onNavigateToTab(KinetixTab.MERCADO) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_btn_to_mercado"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Mercado", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onNavigateToTab(KinetixTab.FINANZAS) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_btn_to_finanzas"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
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
                        .testTag("nav_btn_to_perfil"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface
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
