package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MeetingEntity
import com.example.ui.components.SectionTutorialCard
import com.example.ui.viewmodel.KinetixTab
import com.example.ui.viewmodel.KinetixViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingsScreen(
    viewModel: KinetixViewModel,
    onNavigateToTab: (KinetixTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val meetings by viewModel.allMeetings.collectAsState()
    val dismissedTutorials by viewModel.dismissedTutorials.collectAsState()
    val showTutorial = dismissedTutorials["REUNIONES"] != true

    var selectedFilter by remember { mutableStateOf("Todas") } // "Todas", "Virtuales", "Presenciales", "Hoy"
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog form states
    var meetingTitle by remember { mutableStateOf("") }
    var meetingDateDisplay by remember { mutableStateOf("Hoy") }
    var meetingDateKey by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)) }
    var meetingTimeDisplay by remember { mutableStateOf("10:00 AM") }
    var isVirtualMeeting by remember { mutableStateOf(true) }
    var platformOrLink by remember { mutableStateOf("Google Meet") }
    var physicalLocation by remember { mutableStateOf("Oficina Central") }
    var notify1hBefore by remember { mutableStateOf(true) }
    var meetingDescription by remember { mutableStateOf("") }

    // Speech Recognizer launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                val parsed = viewModel.parseVoiceMeeting(spokenText)
                meetingTitle = parsed.title
                meetingDateKey = parsed.dateKey
                meetingDateDisplay = parsed.dateDisplay
                meetingTimeDisplay = parsed.timeDisplay
                isVirtualMeeting = parsed.isVirtual
                platformOrLink = parsed.platformOrLink
                physicalLocation = parsed.physicalLocation
                showAddDialog = true
                Toast.makeText(context, "🎙️ Dictado procesado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    val filteredMeetings = meetings.filter { m ->
        when (selectedFilter) {
            "Virtuales" -> m.isVirtual
            "Presenciales" -> !m.isVirtual
            "Hoy" -> m.dateKey == todayKey
            else -> true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Reuniones & Calendario",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${meetings.count { !it.isCompleted }} reuniones programadas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicta tu reunión (Ej: Reunión con cliente mañana a las 3 pm por Meet)")
                            }
                            try {
                                speechLauncher.launch(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Dictado por voz no disponible en este dispositivo", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("meetings_mic_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Dictar reunión",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mic voice dictation secondary FAB
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicta tu reunión (Ej: Reunión con cliente mañana a las 3 pm por Meet)")
                        }
                        try {
                            speechLauncher.launch(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Dictado por voz no disponible en este dispositivo", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.testTag("meetings_voice_fab")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Dictar por voz"
                    )
                }

                // Add Meeting primary FAB
                FloatingActionButton(
                    onClick = {
                        meetingTitle = ""
                        meetingDescription = ""
                        meetingDateDisplay = "Hoy"
                        meetingDateKey = todayKey
                        meetingTimeDisplay = "10:00 AM"
                        isVirtualMeeting = true
                        platformOrLink = "Google Meet"
                        physicalLocation = "Oficina Central"
                        notify1hBefore = true
                        showAddDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("meetings_add_fab")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agendar Reunión"
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Tutorial Section
            item {
                SectionTutorialCard(
                    isVisible = showTutorial,
                    title = "Reuniones & Calendario",
                    subtitle = "Agenda tus compromisos con notificaciones 1h antes",
                    tips = listOf(
                        "Agenda reuniones con fecha y hora exacta.",
                        "Recibe notificación sonora 1 hora antes para no llegar tarde.",
                        "Identifica si es virtual (con enlace de Meet/Zoom) o presencial con ubicación física.",
                        "¡Usa el botón de micrófono 🎙️ para dictar reuniones por voz de manera rápida y productiva!"
                    ),
                    onDismiss = { viewModel.dismissTutorial("REUNIONES") }
                )
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Todas", "Hoy", "Virtuales", "Presenciales").forEach { filterName ->
                        val isSelected = selectedFilter == filterName
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filterName },
                            label = { Text(filterName, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("filter_meetings_$filterName")
                        )
                    }
                }
            }

            // Empty State
            if (filteredMeetings.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay reuniones en esta vista",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pulsa '+' o el micrófono para dictar tu próxima reunión.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(
                    items = filteredMeetings,
                    key = { it.id }
                ) { meeting ->
                    MeetingCardItem(
                        meeting = meeting,
                        onToggleCompleted = { viewModel.toggleMeetingCompleted(meeting.id, !meeting.isCompleted) },
                        onDelete = { viewModel.deleteMeeting(meeting.id) },
                        onTestNotification = {
                            viewModel.testMeetingNotification(meeting)
                            Toast.makeText(context, "🔔 Alarma de prueba activada para: ${meeting.title}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Agendar Nueva Reunión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                val dialogScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(dialogScroll),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = meetingTitle,
                        onValueChange = { meetingTitle = it },
                        label = { Text("Título de la reunión *") },
                        placeholder = { Text("Ej. Sincronización semanal") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("meeting_title_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = meetingDateDisplay,
                            onValueChange = { meetingDateDisplay = it },
                            label = { Text("Fecha") },
                            placeholder = { Text("Hoy / Mañana / Viernes") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("meeting_date_input")
                        )
                        OutlinedTextField(
                            value = meetingTimeDisplay,
                            onValueChange = { meetingTimeDisplay = it },
                            label = { Text("Hora") },
                            placeholder = { Text("10:00 AM") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("meeting_time_input")
                        )
                    }

                    // Virtual vs Presencial Switch
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isVirtualMeeting) "🌐 Reunión Virtual" else "📍 Reunión Presencial",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isVirtualMeeting) "Google Meet / Zoom / Enlace" else "Lugar físico especificado",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isVirtualMeeting,
                                onCheckedChange = { isVirtualMeeting = it },
                                modifier = Modifier.testTag("meeting_is_virtual_switch")
                            )
                        }
                    }

                    if (isVirtualMeeting) {
                        OutlinedTextField(
                            value = platformOrLink,
                            onValueChange = { platformOrLink = it },
                            label = { Text("Plataforma o enlace virtual") },
                            placeholder = { Text("Ej. Google Meet, Zoom, https://meet.google.com/...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("meeting_platform_input")
                        )
                    } else {
                        OutlinedTextField(
                            value = physicalLocation,
                            onValueChange = { physicalLocation = it },
                            label = { Text("Lugar presencial") },
                            placeholder = { Text("Ej. Oficina Piso 4, Sala de Juntas, Café...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("meeting_location_input")
                        )
                    }

                    // Notification 1h before toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "🔔 Notificar 1 hora antes",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Alarma sonora y mensaje en pantalla",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = notify1hBefore,
                            onCheckedChange = { notify1hBefore = it },
                            modifier = Modifier.testTag("meeting_notify_switch")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (meetingTitle.isNotBlank()) {
                            viewModel.addMeeting(
                                title = meetingTitle,
                                dateKey = meetingDateKey,
                                dateDisplay = meetingDateDisplay,
                                timeDisplay = meetingTimeDisplay,
                                startTimeEpoch = System.currentTimeMillis(),
                                isVirtual = isVirtualMeeting,
                                platformOrLink = if (isVirtualMeeting) platformOrLink else "",
                                physicalLocation = if (!isVirtualMeeting) physicalLocation else "",
                                notifyOneHourBefore = notify1hBefore,
                                description = meetingDescription
                            )
                            showAddDialog = false
                            Toast.makeText(context, "Reunión agendada", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_meeting_button")
                ) {
                    Text("Agendar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MeetingCardItem(
    meeting: MeetingEntity,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit,
    onTestNotification: () -> Unit
) {
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("meeting_card_${meeting.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (meeting.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onToggleCompleted,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_meeting_${meeting.id}")
                    ) {
                        Icon(
                            imageVector = if (meeting.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = if (meeting.isCompleted) "Completada" else "Pendiente",
                            tint = if (meeting.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = meeting.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (meeting.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (meeting.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${meeting.dateDisplay} • ${meeting.timeDisplay}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("delete_meeting_${meeting.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar reunión",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges & Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (meeting.isVirtual) {
                    AssistChip(
                        onClick = {
                            if (meeting.platformOrLink.startsWith("http")) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meeting.platformOrLink))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        },
                        label = {
                            Text(
                                text = meeting.platformOrLink.ifBlank { "Reunión Virtual" },
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.VideoCameraFront,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = meeting.physicalLocation.ifBlank { "Lugar acordado" },
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                if (meeting.notifyOneHourBefore) {
                    AssistChip(
                        onClick = onTestNotification,
                        label = { Text("Alarma 1h", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}
