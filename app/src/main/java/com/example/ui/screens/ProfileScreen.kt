package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.KinetixDatabase
import com.example.ui.theme.KinetixError
import com.example.ui.theme.KinetixOnPrimary
import com.example.ui.theme.KinetixOnPrimaryFixed
import com.example.ui.theme.KinetixOnSecondary
import com.example.ui.theme.KinetixOnSecondaryContainer
import com.example.ui.theme.KinetixOnTertiaryContainer
import com.example.ui.theme.KinetixOnTertiaryFixed
import com.example.ui.theme.KinetixPrimary
import com.example.ui.theme.KinetixPrimaryContainer
import com.example.ui.theme.KinetixPrimaryFixed
import com.example.ui.theme.KinetixSecondary
import com.example.ui.theme.KinetixSecondaryContainer
import com.example.ui.theme.KinetixSecondaryFixed
import com.example.ui.theme.KinetixTertiary
import com.example.ui.theme.KinetixTertiaryContainer
import com.example.ui.theme.KinetixTertiaryFixed
import com.example.ui.theme.KinetixTertiaryFixedDim
import com.example.ui.viewmodel.KinetixTab
import com.example.ui.viewmodel.KinetixViewModel
import com.example.ui.viewmodel.ThemeMode

@Composable
fun ProfileScreen(
    viewModel: KinetixViewModel,
    onNavigateToTab: (KinetixTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stats by viewModel.gamificationStats.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val userProfile by viewModel.user.collectAsState()
    val context = LocalContext.current
    var thumbsUpCelebration by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val currentStreak = stats?.currentStreak ?: 0
    val currentXp = stats?.currentXp ?: 0
    val targetXp = stats?.targetXp ?: 100
    val todayXp = stats?.todayXpGained ?: 0
    val xpProgress = if (targetXp > 0) (currentXp.toFloat() / targetXp.toFloat()).coerceIn(0f, 1f) else 0f

    val profileName = userProfile?.name ?: "Mi Perfil"
    val avatarUrl = userProfile?.avatarUrl ?: KinetixDatabase.AVATAR_URL
    val statusTag = userProfile?.statusTag ?: "Listo para comenzar"

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Profile & Hero Level Card
                ProfileHeroCard(
                    profileName = profileName,
                    avatarUrl = avatarUrl,
                    statusTag = statusTag,
                    currentStreak = currentStreak,
                    thumbsUpCelebration = thumbsUpCelebration,
                    onEditProfile = { showEditProfileDialog = true },
                    onStreakBoost = {
                        viewModel.boostStreak()
                        thumbsUpCelebration = true
                        Toast.makeText(context, "¡Racha reforzada! +25 XP", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                // Global XP & Level Progress Card
                GlobalXpCard(
                    currentXp = currentXp,
                    targetXp = targetXp,
                    todayXp = todayXp,
                    xpProgress = xpProgress
                )
            }

            item {
                // Quick Productivity Metrics Bento Grid
                ProductivityMetricsBentoGrid(
                    tasksOnTime = stats?.tasksOnTimePercent ?: 86,
                    completedThisWeek = stats?.completedTasksCount ?: 24,
                    impulseExpenses = stats?.impulseExpenses ?: 0.0
                )
            }

            item {
                // Theme Mode Customization Section
                ThemeSelectionSection(
                    currentThemeMode = themeMode,
                    onSelectThemeMode = { newMode ->
                        viewModel.setThemeMode(newMode)
                        val modeName = when (newMode) {
                            ThemeMode.LIGHT -> "Modo Claro activado"
                            ThemeMode.DARK -> "Modo Oscuro activado"
                            ThemeMode.SYSTEM -> "Modo Automático según el sistema"
                        }
                        Toast.makeText(context, modeName, Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                // Direct Navigation Shortcuts Section
                ProfileNavigationShortcuts(
                    onNavigateToTab = onNavigateToTab
                )
            }

            item {
                // Achievements and Badges
                AchievementsSection(
                    onShowReward = {
                        Toast.makeText(
                            context,
                            "✨ Recompensa Nivel 8: Desbloqueo de análisis predictivo y tema esmeralda.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }

            item {
                // Motivational Quote
                Text(
                    text = "“La consistencia diaria convierte hábitos ordinarios en resultados extraordinarios.”",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showEditProfileDialog) {
            EditProfileDialog(
                initialName = profileName,
                initialAvatarUrl = avatarUrl,
                initialStatusTag = statusTag,
                onDismiss = { showEditProfileDialog = false },
                onConfirm = { newName, newAvatarUrl, newStatusTag ->
                    viewModel.updateProfile(newName, newAvatarUrl, newStatusTag)
                    showEditProfileDialog = false
                    Toast.makeText(context, "¡Perfil actualizado con éxito!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun ProfileHeroCard(
    profileName: String,
    avatarUrl: String,
    statusTag: String,
    currentStreak: Int,
    thumbsUpCelebration: Boolean,
    onEditProfile: () -> Unit,
    onStreakBoost: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_hero_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar with glowing level ring and edit badge
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(KinetixPrimary, KinetixSecondary, KinetixPrimary)
                                )
                            )
                            .clickable(onClick = onEditProfile)
                            .padding(3.dp)
                    ) {
                        AsyncImage(
                            model = avatarUrl.ifBlank { KinetixDatabase.AVATAR_URL },
                            contentDescription = "Avatar de $profileName",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    // Level 7 Pill Badge
                    Surface(
                        shape = CircleShape,
                        color = KinetixPrimary,
                        shadowElevation = 2.dp,
                        modifier = Modifier.padding(bottom = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = KinetixSecondaryFixed,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Nv. 7",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = KinetixOnPrimary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Identity info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = KinetixPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Nivel 7: Maestro de la Rutina",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = KinetixPrimary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = profileName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = onEditProfile,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar perfil",
                                tint = KinetixPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = statusTag,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(KinetixSecondary, CircleShape)
                        )
                        Text(
                            text = "Enfocada",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KinetixSecondary
                        )
                    }
                }
            }

            // Streak Prominent Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = KinetixTertiaryFixed.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(KinetixTertiaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = KinetixOnTertiaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "¡$currentStreak Días Consecutivos!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "¡Racha récord! Mantén el ritmo hoy.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onStreakBoost,
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest, CircleShape)
                            .testTag("streak_boost_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Celebrar racha",
                            tint = if (thumbsUpCelebration) KinetixPrimary else KinetixTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlobalXpCard(
    currentXp: Int,
    targetXp: Int,
    todayXp: Int,
    xpProgress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("global_xp_card"),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(KinetixPrimaryContainer, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Experiencia Global",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = KinetixSecondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = KinetixOnSecondaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+$todayXp XP hoy",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KinetixOnSecondaryContainer
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Hacia el Nivel 8",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$currentXp / $targetXp XP",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = KinetixPrimary
                )
            }

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(xpProgress)
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(KinetixPrimary, KinetixPrimaryContainer, KinetixSecondary)
                            )
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Faltan ${targetXp - currentXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(xpProgress * 100).toInt()}% completado",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = KinetixSecondary
                )
            }
        }
    }
}

@Composable
private fun ProductivityMetricsBentoGrid(
    tasksOnTime: Int,
    completedThisWeek: Int,
    impulseExpenses: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rendimiento Semanal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Sincronizado hoy",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Metric 1: Tasks on time
            BentoMetricCard(
                icon = Icons.Default.TaskAlt,
                iconTint = KinetixPrimary,
                iconBg = KinetixPrimary.copy(alpha = 0.1f),
                value = "$tasksOnTime%",
                label = "Tareas a tiempo",
                modifier = Modifier.weight(1f)
            )

            // Metric 2: Completed this week
            BentoMetricCard(
                icon = Icons.Default.ChecklistRtl,
                iconTint = KinetixSecondary,
                iconBg = KinetixSecondary.copy(alpha = 0.15f),
                value = "$completedThisWeek",
                label = "Esta semana",
                modifier = Modifier.weight(1f)
            )

            // Metric 3: Impulse expenses
            BentoMetricCard(
                icon = Icons.Default.Savings,
                iconTint = KinetixTertiary,
                iconBg = KinetixTertiaryFixed.copy(alpha = 0.4f),
                value = "$${impulseExpenses.toInt()}",
                label = "Gastos impulso",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BentoMetricCard(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AchievementsSection(
    onShowReward: () -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Medallas y Logros",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "3 de 5 hitos conquistados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = KinetixPrimaryFixed.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = "3 / 5",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KinetixPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Badges List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Badge 1: Centella Matutina
                BadgeItemCard(
                    icon = Icons.Default.WbSunny,
                    iconBg = Brush.linearGradient(listOf(KinetixTertiaryFixed, KinetixTertiaryFixedDim)),
                    iconTint = KinetixOnTertiaryFixed,
                    title = "Centella Matutina",
                    subtitle = "Completar 3 tareas antes de las 10 AM",
                    badgeTag = "Desbloqueada",
                    badgeColor = KinetixTertiaryFixed.copy(alpha = 0.4f),
                    badgeTextColor = KinetixTertiary
                )

                // Badge 2: Financiero Impecable
                BadgeItemCard(
                    icon = Icons.Default.AccountBalanceWallet,
                    iconBg = Brush.linearGradient(listOf(KinetixSecondary, KinetixSecondaryContainer)),
                    iconTint = KinetixOnSecondary,
                    title = "Financiero Impecable",
                    subtitle = "Registrar gastos 3 semanas seguidas",
                    badgeTag = "Desbloqueada",
                    badgeColor = KinetixSecondaryContainer.copy(alpha = 0.5f),
                    badgeTextColor = KinetixSecondary
                )

                // Badge 3: Racha de Fuego 14x
                BadgeItemCard(
                    icon = Icons.Default.LocalFireDepartment,
                    iconBg = Brush.linearGradient(listOf(KinetixTertiary, KinetixError)),
                    iconTint = Color.White,
                    title = "Racha de Fuego 14x",
                    subtitle = "14 días seguidos de constancia total",
                    badgeTag = "¡Hoy!",
                    badgeColor = KinetixTertiaryFixed,
                    badgeTextColor = KinetixOnTertiaryFixed
                )

                // Badge 4: Maestro de Compras (In progress - 80%)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(KinetixPrimaryFixed, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCartCheckout,
                                    contentDescription = null,
                                    tint = KinetixOnPrimaryFixed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Maestro de Compras",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "80%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = KinetixPrimary
                                    )
                                }
                                Text(
                                    text = "Completar lista de mercado al 100%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { 0.8f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = KinetixPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    }
                }

                // Badge 5: Titán del Enfoque (Locked)
                BadgeItemCard(
                    icon = Icons.Default.Lock,
                    iconBg = Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.colorScheme.surfaceContainerHighest)),
                    iconTint = MaterialTheme.colorScheme.outline,
                    title = "Titán del Enfoque",
                    subtitle = "50 horas de estudio o trabajo cumplidas",
                    badgeTag = "Bloqueada",
                    badgeColor = MaterialTheme.colorScheme.surfaceContainer,
                    badgeTextColor = MaterialTheme.colorScheme.outline,
                    isLocked = true
                )
            }

            // Reward Callout trigger
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = KinetixPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Próxima recompensa a nivel 8",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    TextButton(onClick = onShowReward) {
                        Text(
                            text = "Ver regalo",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KinetixPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeItemCard(
    icon: ImageVector,
    iconBg: Brush,
    iconTint: Color,
    title: String,
    subtitle: String,
    badgeTag: String,
    badgeColor: Color,
    badgeTextColor: Color,
    isLocked: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = CircleShape,
                        color = badgeColor
                    ) {
                        Text(
                            text = badgeTag,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isLocked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ThemeSelectionSection(
    currentThemeMode: ThemeMode,
    onSelectThemeMode: (ThemeMode) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("theme_selection_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Apariencia y Tema",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = currentThemeMode.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "Personaliza los colores y estilos visuales de Kinetix con soporte dinámico para modo claro, oscuro o sincronización automática.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 3 options row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeOptionChip(
                    title = "Claro",
                    subtitle = "Luz suave",
                    icon = Icons.Default.LightMode,
                    isSelected = currentThemeMode == ThemeMode.LIGHT,
                    onClick = { onSelectThemeMode(ThemeMode.LIGHT) },
                    modifier = Modifier.weight(1f)
                )
                ThemeOptionChip(
                    title = "Oscuro",
                    subtitle = "OLED negro",
                    icon = Icons.Default.DarkMode,
                    isSelected = currentThemeMode == ThemeMode.DARK,
                    onClick = { onSelectThemeMode(ThemeMode.DARK) },
                    modifier = Modifier.weight(1f)
                )
                ThemeOptionChip(
                    title = "Sistema",
                    subtitle = "Automático",
                    icon = Icons.Default.BrightnessAuto,
                    isSelected = currentThemeMode == ThemeMode.SYSTEM,
                    onClick = { onSelectThemeMode(ThemeMode.SYSTEM) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionChip(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceContainerLow
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("theme_option_${title.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Activo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileNavigationShortcuts(
    onNavigateToTab: (KinetixTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_navigation_shortcuts_card"),
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
                    text = "Navegación Rápida y Enlaces",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Desplázate directamente entre pantallas clave mediante enlaces directos:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Direct Links Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShortcutNavButton(
                    icon = Icons.Default.Checklist,
                    title = "Tareas",
                    description = "Prioridades",
                    onClick = { onNavigateToTab(KinetixTab.TAREAS) },
                    modifier = Modifier.weight(1f)
                )
                ShortcutNavButton(
                    icon = Icons.Default.CalendarToday,
                    title = "Horario",
                    description = "Rutina",
                    onClick = { onNavigateToTab(KinetixTab.HORARIO) },
                    modifier = Modifier.weight(1f)
                )
                ShortcutNavButton(
                    icon = Icons.Default.ShoppingCart,
                    title = "Mercado",
                    description = "Compras",
                    onClick = { onNavigateToTab(KinetixTab.MERCADO) },
                    modifier = Modifier.weight(1f)
                )
                ShortcutNavButton(
                    icon = Icons.Default.Calculate,
                    title = "Finanzas",
                    description = "Presupuesto",
                    onClick = { onNavigateToTab(KinetixTab.FINANZAS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ShortcutNavButton(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("shortcut_to_${title.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialAvatarUrl: String,
    initialStatusTag: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, avatarUrl: String, statusTag: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var avatarUrl by remember { mutableStateOf(initialAvatarUrl) }
    var statusTag by remember { mutableStateOf(initialStatusTag) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            avatarUrl = uri.toString()
        }
    }

    val presetAvatars = listOf(
        KinetixDatabase.AVATAR_URL,
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300&auto=format&fit=crop&q=80"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Modificar Perfil",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Avatar preview & Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        AsyncImage(
                            model = avatarUrl.ifBlank { KinetixDatabase.AVATAR_URL },
                            contentDescription = "Avatar actual",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Elegir foto", fontSize = 12.sp)
                        }
                    }
                }

                // Preset avatars quick selection
                Text(
                    text = "O elige un avatar sugerido:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetAvatars.forEach { preset ->
                        val isSelected = avatarUrl == preset
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .clickable { avatarUrl = preset }
                        ) {
                            AsyncImage(
                                model = preset,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = statusTag,
                    onValueChange = { statusTag = it },
                    label = { Text("Estado o Lema personal") },
                    placeholder = { Text("Ej. Optimizando cada día") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), avatarUrl.trim(), statusTag.trim())
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

