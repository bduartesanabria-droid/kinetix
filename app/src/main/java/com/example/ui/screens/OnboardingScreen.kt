package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.KinetixViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OnboardingScreen(
    viewModel: KinetixViewModel,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }

    // Step 0: Legal terms
    var acceptedTerms by remember { mutableStateOf(false) }

    // Step 1: User & Work
    var userName by remember { mutableStateOf("") }
    var workTitle by remember { mutableStateOf("Desarrollador de Software") }
    var workStart by remember { mutableStateOf("08:00") }
    var workEnd by remember { mutableStateOf("17:00") }
    var workLocation by remember { mutableStateOf("Oficina Central / Remoto") }

    // Step 2: Study
    var studyCareer by remember { mutableStateOf("Ingeniería de Sistemas") }
    var studyPlace by remember { mutableStateOf("Universidad Nacional") }
    var studyStart by remember { mutableStateOf("18:00") }
    var studyEnd by remember { mutableStateOf("21:30") }

    // Step 3: Finances
    var monthlyIncomeText by remember { mutableStateOf("4500000") }
    var rentText by remember { mutableStateOf("1200000") }
    var fixedExpensesText by remember { mutableStateOf("800000") }

    val totalSteps = 4
    val progress = (currentStep + 1).toFloat() / totalSteps.toFloat()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 0) {
                        IconButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.testTag("onboarding_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Atrás"
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    Text(
                        text = "Paso ${currentStep + 1} de $totalSteps",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(48.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "onboarding_step_animation",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { step ->
                when (step) {
                    0 -> LegalTermsStep(
                        accepted = acceptedTerms,
                        onAcceptedChange = { acceptedTerms = it },
                        onNext = { currentStep = 1 }
                    )
                    1 -> WorkSetupStep(
                        name = userName,
                        onNameChange = { userName = it },
                        workTitle = workTitle,
                        onWorkTitleChange = { workTitle = it },
                        workStart = workStart,
                        onWorkStartChange = { workStart = it },
                        workEnd = workEnd,
                        onWorkEndChange = { workEnd = it },
                        workLocation = workLocation,
                        onWorkLocationChange = { workLocation = it },
                        onNext = { currentStep = 2 }
                    )
                    2 -> StudySetupStep(
                        career = studyCareer,
                        onCareerChange = { studyCareer = it },
                        place = studyPlace,
                        onPlaceChange = { studyPlace = it },
                        start = studyStart,
                        onStartChange = { studyStart = it },
                        end = studyEnd,
                        onEndChange = { studyEnd = it },
                        onNext = { currentStep = 3 }
                    )
                    3 -> FinancialSetupStep(
                        income = monthlyIncomeText,
                        onIncomeChange = { monthlyIncomeText = it },
                        rent = rentText,
                        onRentChange = { rentText = it },
                        fixedExpenses = fixedExpensesText,
                        onFixedExpensesChange = { fixedExpensesText = it },
                        viewModel = viewModel,
                        onFinish = {
                            val inc = viewModel.parseCurrency(monthlyIncomeText)
                            val rnt = viewModel.parseCurrency(rentText)
                            val fxd = viewModel.parseCurrency(fixedExpensesText)
                            viewModel.completeOnboarding(
                                name = userName,
                                workTitle = workTitle,
                                workStart = workStart,
                                workEnd = workEnd,
                                workLocation = workLocation,
                                studyCareer = studyCareer,
                                studyPlace = studyPlace,
                                studyStart = studyStart,
                                studyEnd = studyEnd,
                                monthlyIncome = inc,
                                rent = rnt,
                                otherFixed = fxd
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LegalTermsStep(
    accepted: Boolean,
    onAcceptedChange: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Gavel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Bienvenido a Kinetix",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Términos Legales y Privacidad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LegalPointItem(
                        icon = Icons.Filled.Security,
                        title = "1. Privacidad y Datos Locales",
                        desc = "Tu información de tareas, horarios, reuniones y finanzas se almacena de forma segura en tu dispositivo. Kinetix no comparte tus datos personales con terceros."
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    LegalPointItem(
                        icon = Icons.Filled.AccountBalanceWallet,
                        title = "2. Gestión Financiera Responsable",
                        desc = "Los cálculos y proyecciones financieras son herramientas de organización personal para ayudarte a planificar tus ahorros y gastos."
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    LegalPointItem(
                        icon = Icons.Filled.School,
                        title = "3. Alertas y Horarios",
                        desc = "La aplicación utiliza notificaciones locales para recordarte reuniones y clases 1 hora antes para que nunca pierdas un compromiso importante."
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                onClick = { onAcceptedChange(!accepted) },
                shape = RoundedCornerShape(12.dp),
                color = if (accepted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = accepted,
                        onCheckedChange = { onAcceptedChange(it) },
                        modifier = Modifier.testTag("accept_terms_checkbox")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Acepto los Términos de Servicio y la Política de Privacidad de Kinetix.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            Button(
                onClick = onNext,
                enabled = accepted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("continue_terms_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Aceptar y Configurar mi Cuenta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun LegalPointItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun WorkSetupStep(
    name: String,
    onNameChange: (String) -> Unit,
    workTitle: String,
    onWorkTitleChange: (String) -> Unit,
    workStart: String,
    onWorkStartChange: (String) -> Unit,
    workEnd: String,
    onWorkEndChange: (String) -> Unit,
    workLocation: String,
    onWorkLocationChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Work,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Tu Perfil y Trabajo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Organizaremos tu jornada laboral",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("¿Cómo te llamas?") },
                placeholder = { Text("Ej. Carlos Gómez") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_name_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = workTitle,
                onValueChange = onWorkTitleChange,
                label = { Text("¿En qué trabajas o a qué te dedicas?") },
                placeholder = { Text("Ej. Gerente de Proyectos, Diseñador, Abogado...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_work_title_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Horario habitual de trabajo:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = workStart,
                    onValueChange = onWorkStartChange,
                    label = { Text("Hora Inicio") },
                    placeholder = { Text("08:00") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("onboarding_work_start_input")
                )
                OutlinedTextField(
                    value = workEnd,
                    onValueChange = onWorkEndChange,
                    label = { Text("Hora Fin") },
                    placeholder = { Text("17:00") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("onboarding_work_end_input")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = workLocation,
                onValueChange = onWorkLocationChange,
                label = { Text("Lugar u oficina") },
                placeholder = { Text("Ej. Torre Norte Piso 5 / Home Office") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_work_location_input")
            )

            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "💡 Crearemos automáticamente tus bloques de trabajo de Lunes a Viernes en el Horario para facilitarte la vida.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("continue_work_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Continuar a Estudios", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun StudySetupStep(
    career: String,
    onCareerChange: (String) -> Unit,
    place: String,
    onPlaceChange: (String) -> Unit,
    start: String,
    onStartChange: (String) -> Unit,
    end: String,
    onEndChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Tus Estudios",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Clases, universidad o cursos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = career,
                onValueChange = onCareerChange,
                label = { Text("¿Qué estudias o qué cursos realizas?") },
                placeholder = { Text("Ej. Ingeniería, Medicina, Inglés, o deja vacío si no estudias") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_study_career_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = place,
                onValueChange = onPlaceChange,
                label = { Text("¿Dónde estudias?") },
                placeholder = { Text("Ej. Universidad Nacional, Instituto, Online...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_study_place_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Horario habitual de clases:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = start,
                    onValueChange = onStartChange,
                    label = { Text("Hora Inicio") },
                    placeholder = { Text("18:00") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("onboarding_study_start_input")
                )
                OutlinedTextField(
                    value = end,
                    onValueChange = onEndChange,
                    label = { Text("Hora Fin") },
                    placeholder = { Text("21:30") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("onboarding_study_end_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "🎓 Agendaremos tus clases de Lunes a Jueves para que puedas equilibrar estudio y trabajo sin estrés.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("continue_study_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Continuar a Finanzas", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun FinancialSetupStep(
    income: String,
    onIncomeChange: (String) -> Unit,
    rent: String,
    onRentChange: (String) -> Unit,
    fixedExpenses: String,
    onFixedExpensesChange: (String) -> Unit,
    viewModel: KinetixViewModel,
    onFinish: () -> Unit
) {
    val scrollState = rememberScrollState()

    val incomeVal = viewModel.parseCurrency(income)
    val rentVal = viewModel.parseCurrency(rent)
    val fixedVal = viewModel.parseCurrency(fixedExpenses)
    val savingsVal = (incomeVal - rentVal - fixedVal).coerceAtLeast(0.0)

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Tus Finanzas y Balance",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Balance inicial automatizado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = income,
                onValueChange = onIncomeChange,
                label = { Text("¿Cuánto ganas al mes? (Ingresos)") },
                placeholder = { Text("Ej. 4500000") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_income_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = rent,
                onValueChange = onRentChange,
                label = { Text("¿Cuánto pagas de arriendo o vivienda?") },
                placeholder = { Text("Ej. 1200000") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_rent_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = fixedExpenses,
                onValueChange = onFixedExpensesChange,
                label = { Text("Otros gastos fijos (servicios, comida, etc.)") },
                placeholder = { Text("Ej. 800000") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_fixed_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Balance Card Preview
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📊 Balance Inicial Calculado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ingreso Mensual:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = currencyFormat.format(incomeVal),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Gastos Fijos Totales:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = currencyFormat.format(rentVal + fixedVal),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ahorro / Disponible Estimado:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = currencyFormat.format(savingsVal),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("finish_onboarding_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "¡Comenzar en Kinetix!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
