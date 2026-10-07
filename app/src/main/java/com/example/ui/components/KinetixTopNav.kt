package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.KinetixDatabase
import com.example.ui.viewmodel.KinetixTab
import com.example.ui.viewmodel.ThemeMode

@Composable
fun KinetixTopNav(
    selectedTab: KinetixTab,
    onTabSelected: (KinetixTab) -> Unit,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onToggleTheme: () -> Unit = {},
    userAvatarUrl: String = KinetixDatabase.AVATAR_URL,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 2.dp
    ) {
        // Upper Header (Brand + Subtitle + Theme Switcher + Notifications + Profile Avatar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_kinetix_logo),
                    contentDescription = "Kinetix Logo",
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Kinetix",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = selectedTab.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action Icons (Theme Switcher, Notifications, Avatar)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Theme Switcher Button
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("theme_toggle_button")
                ) {
                    val (themeIcon, themeDesc) = when (themeMode) {
                        ThemeMode.LIGHT -> Pair(Icons.Default.LightMode, "Tema actual: Claro. Toca para Oscuro")
                        ThemeMode.DARK -> Pair(Icons.Default.DarkMode, "Tema actual: Oscuro. Toca para Sistema")
                        ThemeMode.SYSTEM -> Pair(Icons.Default.BrightnessAuto, "Tema actual: Sistema. Toca para Claro")
                    }
                    Icon(
                        imageVector = themeIcon,
                        contentDescription = themeDesc,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = { onTabSelected(KinetixTab.HORARIO) },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Avatar
                AsyncImage(
                    model = userAvatarUrl.ifBlank { KinetixDatabase.AVATAR_URL },
                    contentDescription = "Perfil de usuario",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable { onTabSelected(KinetixTab.PERFIL) }
                        .testTag("topbar_avatar")
                )
            }
        }
    }
}
