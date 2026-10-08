package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import com.example.data.local.entity.ExpenseTransactionEntity
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.SectionTutorialCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.data.local.entity.FinancialGoalEntity
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KinetixOnPrimary
import com.example.ui.theme.KinetixOnPrimaryContainer
import com.example.ui.theme.KinetixOnSecondaryContainer
import com.example.ui.theme.KinetixPrimary
import com.example.ui.theme.KinetixPrimaryContainer
import com.example.ui.theme.KinetixPrimaryFixed
import com.example.ui.theme.KinetixSecondary
import com.example.ui.theme.KinetixSecondaryContainer
import com.example.ui.theme.KinetixSecondaryFixed
import com.example.ui.theme.KinetixSurfaceVariant
import com.example.ui.theme.KinetixTertiary
import com.example.ui.theme.KinetixTertiaryFixed
import com.example.ui.theme.KinetixTertiaryFixedDim
import com.example.ui.viewmodel.FinancialProjection
import com.example.ui.viewmodel.KinetixTab
import com.example.ui.viewmodel.KinetixViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinanceScreen(
    viewModel: KinetixViewModel,
    onNavigateToTab: (KinetixTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val projection by viewModel.financialProjection.collectAsState()
    val incomeInput by viewModel.incomeInput.collectAsState()
    val rentInput by viewModel.rentInput.collectAsState()
    val otherFixedInput by viewModel.otherFixedInput.collectAsState()
    val isRecalculating by viewModel.isRecalculating.collectAsState()
    val financialGoals by viewModel.financialGoals.collectAsState()
    val allExpenses by viewModel.allExpenses.collectAsState()
    val expensesByCategory by viewModel.expensesByCategory.collectAsState()
    val totalExpensesAmount by viewModel.totalExpensesAmount.collectAsState()
    val topExpenseCategory by viewModel.topExpenseCategory.collectAsState()
    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val monthlyIncomeAmount by viewModel.monthlyIncomeAmount.collectAsState()
    val netAvailableBalance by viewModel.netAvailableBalance.collectAsState()
    val dismissedTutorials by viewModel.dismissedTutorials.collectAsState()
    val showTutorial = dismissedTutorials["FINANZAS"] != true

    val context = LocalContext.current
    var selectedExpenseCategoryFilter by remember { mutableStateOf("Todas") }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var goalToContribute by remember { mutableStateOf<FinancialGoalEntity?>(null) }

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var prefilledConcept by remember { mutableStateOf("") }
    var prefilledAmount by remember { mutableStateOf("") }
    var prefilledCategory by remember { mutableStateOf("Comida") }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenTexts = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = spokenTexts?.firstOrNull().orEmpty()
            if (text.isNotBlank()) {
                val (amount, concept, category) = viewModel.parseVoiceExpense(text)
                if (amount > 0) {
                    viewModel.addExpense(amount, concept, category, text)
                    Toast.makeText(context, "Gasto de voz añadido: $concept ($${amount.toLong()}) en $category", Toast.LENGTH_LONG).show()
                } else {
                    prefilledConcept = concept
                    prefilledCategory = category
                    showAddExpenseDialog = true
                    Toast.makeText(context, "Indica el monto para: \"$text\"", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
                    title = "Control Financiero & Balance",
                    subtitle = "Ingresos, gastos, ahorro y dictado de gastos por voz",
                    tips = listOf(
                        "Visualiza tu balance exacto: cuánto ganas, cuánto ahorras y cuánto gastas al mes.",
                        "Dicta tus gastos por voz (ej. 'Me gasté 15 mil en comida') con el micrófono 🎙️.",
                        "Personaliza tus categorías de gastos (Comida, Juegos, Salidas, Transporte, Tecnología o agrega las tuyas con '+ Categoría').",
                        "Define metas de ahorro a corto, mediano y largo plazo."
                    ),
                    onDismiss = { viewModel.dismissTutorial("FINANZAS") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Encabezado Resumen Mensual
                FinanceMonthlyHeader()
            }

            item {
                // Apartado de Balance Exacto: Estoy Ahorrando, Ganando, Gastando y Balance Neto
                ExactFinancialBalanceCard(
                    income = monthlyIncomeAmount,
                    expenses = totalExpensesAmount,
                    savings = projection.projectedSavings,
                    balance = netAvailableBalance
                )
            }

            item {
                // Categorías de Gastos Dinámicas
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Categorías de Gastos",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    CategoryChipRow(
                        categories = expenseCategories,
                        selectedCategory = selectedExpenseCategoryFilter,
                        onSelectCategory = { selectedExpenseCategoryFilter = it },
                        onAddCategory = { viewModel.addCategory("EXPENSE", it) },
                        onDeleteCategory = { viewModel.deleteCategory("EXPENSE", it) },
                        defaultCategories = setOf("Comida", "Juegos", "Salidas", "Transporte", "Tecnología")
                    )
                }
            }

            item {
                // Tarjeta Destacada: Proyección Estimada de Margen de Ahorro
                SavingsMarginProjectionCard(projection = projection)
            }

            item {
                // Registro de Gastos por Voz & Balance por Categorías ("En qué estoy gastando más")
                VoiceExpensesSection(
                    allExpenses = allExpenses,
                    expensesByCategory = expensesByCategory,
                    totalExpensesAmount = totalExpensesAmount,
                    topExpenseCategory = topExpenseCategory,
                    onStartVoiceRecording = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla tu gasto: ej. 'Me gasté hoy 15000 pesos en una hamburguesa'")
                            }
                            speechLauncher.launch(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Entrada manual de gasto", Toast.LENGTH_SHORT).show()
                            showAddExpenseDialog = true
                        }
                    },
                    onOpenManualAdd = {
                        prefilledConcept = ""
                        prefilledAmount = ""
                        prefilledCategory = "Comida"
                        showAddExpenseDialog = true
                    },
                    onDeleteExpense = { viewModel.deleteExpense(it) }
                )
            }

            item {
                // Formulario para Ingreso de Datos Financieros Base
                BaseFinancialsFormCard(
                    incomeInput = incomeInput,
                    rentInput = rentInput,
                    otherFixedInput = otherFixedInput,
                    onIncomeChange = { viewModel.incomeInput.value = it },
                    onRentChange = { viewModel.rentInput.value = it },
                    onOtherFixedChange = { viewModel.otherFixedInput.value = it },
                    isRecalculating = isRecalculating,
                    onRecalculate = { viewModel.recalculateFinancials() }
                )
            }

            item {
                // Metas Financieras a Corto, Mediano y Largo Plazo
                FinancialGoalsSection(
                    goals = financialGoals,
                    onAddGoalClick = { showAddGoalDialog = true },
                    onContributeClick = { goalToContribute = it },
                    onDeleteGoalClick = { viewModel.deleteFinancialGoal(it) }
                )
            }

            item {
                // Acceso rápido a otras pantallas
                FinanceCrossNavigationCard(
                    onNavigateToTab = onNavigateToTab
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        if (showAddGoalDialog) {
            AddFinancialGoalDialog(
                onDismiss = { showAddGoalDialog = false },
                onConfirm = { title, description, term, targetAmount, iconName ->
                    viewModel.addFinancialGoal(
                        title = title,
                        description = description,
                        term = term,
                        targetAmount = targetAmount,
                        iconName = iconName
                    )
                    showAddGoalDialog = false
                }
            )
        }

        goalToContribute?.let { goal ->
            ContributeGoalDialog(
                goal = goal,
                onDismiss = { goalToContribute = null },
                onConfirm = { amount ->
                    viewModel.contributeToGoal(goal.id, amount)
                    goalToContribute = null
                }
            )
        }

        if (showAddExpenseDialog) {
            AddExpenseDialog(
                initialConcept = prefilledConcept,
                initialAmount = prefilledAmount,
                initialCategory = prefilledCategory,
                availableCategories = expenseCategories,
                onDismiss = { showAddExpenseDialog = false },
                onConfirm = { concept, amount, category ->
                    viewModel.addExpense(amount, concept, category)
                    showAddExpenseDialog = false
                    Toast.makeText(context, "Gasto registrado con éxito", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun FinanceMonthlyHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("finance_header_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Control Financiero Mensual",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = KinetixPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Octubre 2024",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = KinetixSecondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(KinetixSecondary, CircleShape)
                    )
                    Text(
                        text = "Saludable",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = KinetixOnSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SavingsMarginProjectionCard(projection: FinancialProjection) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    val formattedSavings = formatter.format(projection.projectedSavings.toLong())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("savings_projection_card"),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            KinetixPrimaryContainer,
                            KinetixPrimary,
                            KinetixPrimaryContainer
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "MARGEN DE AHORRO PROYECTADO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KinetixOnPrimaryContainer.copy(alpha = 0.8f),
                            letterSpacing = 1.sp
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "$ $formattedSavings",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "/ mes",
                                style = MaterialTheme.typography.labelMedium,
                                color = KinetixOnPrimaryContainer.copy(alpha = 0.85f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = KinetixPrimaryFixed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = KinetixSecondaryFixed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f%% de tus ingresos disponibles", projection.savingsPercent),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = KinetixSecondaryFixed
                    )
                }

                // Distribution Bar (multi-segment)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f))
                            .padding(1.dp),
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        // 1. Savings
                        val savingsWeight = (projection.savingsPercent / 100.0).toFloat().coerceIn(0.01f, 1f)
                        Box(
                            modifier = Modifier
                                .weight(savingsWeight)
                                .height(8.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                                .background(KinetixPrimaryFixed)
                        )
                        // 2. Rent
                        val rentWeight = (projection.rentPercent / 100.0).toFloat().coerceIn(0.01f, 1f)
                        Box(
                            modifier = Modifier
                                .weight(rentWeight)
                                .height(8.dp)
                                .background(KinetixSurfaceVariant)
                        )
                        // 3. Other fixed
                        val otherWeight = (projection.otherFixedPercent / 100.0).toFloat().coerceIn(0.01f, 1f)
                        Box(
                            modifier = Modifier
                                .weight(otherWeight)
                                .height(8.dp)
                                .background(KinetixTertiaryFixed)
                        )
                        // 4. Variables
                        val variableWeight = (projection.variablePercent / 100.0).toFloat().coerceIn(0.01f, 1f)
                        Box(
                            modifier = Modifier
                                .weight(variableWeight)
                                .height(8.dp)
                                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                                .background(KinetixSecondaryFixed)
                        )
                    }

                    // Legend Grid 2x2
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LegendItem(
                                    color = KinetixPrimaryFixed,
                                    label = String.format(Locale.US, "Ahorro Disp. (%.1f%%)", projection.savingsPercent)
                                )
                                LegendItem(
                                    color = KinetixSurfaceVariant,
                                    label = String.format(Locale.US, "Arriendo (%.1f%%)", projection.rentPercent)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LegendItem(
                                    color = KinetixTertiaryFixed,
                                    label = String.format(Locale.US, "Otros Fijos (%.1f%%)", projection.otherFixedPercent)
                                )
                                LegendItem(
                                    color = KinetixSecondaryFixed,
                                    label = String.format(Locale.US, "Variables (%.1f%%)", projection.variablePercent)
                                )
                            }
                        }
                    }
                }

                // Diagnóstico Financiero Inteligente
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = KinetixTertiaryFixed,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (projection.isHealthy) {
                                "¡Excelente capacidad de ahorro! Estás optimizando tu flujo por encima de la regla clásica 50/30/20."
                            } else {
                                "Atención: tus gastos fijos superan el umbral recomendado. Revisa opciones de optimización."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.95f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.95f)
        )
    }
}

@Composable
private fun BaseFinancialsFormCard(
    incomeInput: String,
    rentInput: String,
    otherFixedInput: String,
    onIncomeChange: (String) -> Unit,
    onRentChange: (String) -> Unit,
    onOtherFixedChange: (String) -> Unit,
    isRecalculating: Boolean,
    onRecalculate: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("base_financials_card"),
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
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = KinetixPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Datos Financieros Base",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Text(
                        text = "COP / Mensual",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Input 1: Ingreso Mensual Neto
            FinanceInputField(
                label = "Ingreso Mensual Neto",
                value = incomeInput,
                onValueChange = onIncomeChange,
                icon = Icons.Default.AccountBalanceWallet,
                testTag = "income_input"
            )

            // Input 2: Gasto Fijo de Arriendo / Vivienda
            FinanceInputField(
                label = "Gasto Fijo de Arriendo / Vivienda",
                value = rentInput,
                onValueChange = onRentChange,
                icon = Icons.Default.Cottage,
                testTag = "rent_input"
            )

            // Input 3: Otros Gastos Fijos
            FinanceInputField(
                label = "Otros Gastos Fijos (Servicios, Mercado)",
                value = otherFixedInput,
                onValueChange = onOtherFixedChange,
                icon = Icons.Default.ReceiptLong,
                testTag = "other_fixed_input"
            )

            val context = LocalContext.current
            // Button: Recalcular Proyección
            Button(
                onClick = {
                    onRecalculate()
                    Toast.makeText(context, "Proyección recalculada exitosamente", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("recalculate_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KinetixPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .then(if (isRecalculating) Modifier.rotate(rotation) else Modifier)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRecalculating) "Actualizando Proyección..." else "Recalcular Proyección",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FinanceInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = KinetixPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Text(
                    text = "$",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            },
            trailingIcon = {
                Text(
                    text = "COP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = KinetixPrimary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
private fun FinancialGoalsSection(
    goals: List<FinancialGoalEntity>,
    onAddGoalClick: () -> Unit,
    onContributeClick: (FinancialGoalEntity) -> Unit,
    onDeleteGoalClick: (String) -> Unit
) {
    var selectedTerm by remember { mutableStateOf("TODAS") }

    val filteredGoals = when (selectedTerm) {
        "CORTO" -> goals.filter { it.term.equals("CORTO", ignoreCase = true) }
        "MEDIANO" -> goals.filter { it.term.equals("MEDIANO", ignoreCase = true) }
        "LARGO" -> goals.filter { it.term.equals("LARGO", ignoreCase = true) }
        else -> goals
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = KinetixTertiary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Metas Financieras",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (goals.isNotEmpty()) {
                    Surface(
                        shape = CircleShape,
                        color = KinetixTertiary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${goals.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KinetixTertiary,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Button(
                onClick = onAddGoalClick,
                colors = ButtonDefaults.buttonColors(containerColor = KinetixPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Nueva Meta",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Plazo Filter Chips (Corto, Mediano, Largo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "TODAS" to "Todas (${goals.size})",
                "CORTO" to "Corto Plazo (< 1 año)",
                "MEDIANO" to "Mediano Plazo (1-3 años)",
                "LARGO" to "Largo Plazo (> 3 años)"
            ).forEach { (key, label) ->
                FilterChip(
                    selected = selectedTerm == key,
                    onClick = { selectedTerm = key },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTerm == key) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KinetixPrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        if (filteredGoals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(KinetixPrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = KinetixPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Sin metas registradas en este plazo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Registra lo que deseas comprar (ej: celular, moto, viaje, vivienda), su costo y tus aportes programados.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = onAddGoalClick,
                        colors = ButtonDefaults.buttonColors(containerColor = KinetixPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Crear mi primera meta")
                    }
                }
            }
        } else {
            filteredGoals.forEach { goal ->
                FinancialGoalItem(
                    goal = goal,
                    onContributeClick = { onContributeClick(goal) },
                    onDeleteClick = { onDeleteGoalClick(goal.id) }
                )
            }
        }
    }
}

@Composable
private fun FinancialGoalItem(
    goal: FinancialGoalEntity,
    onContributeClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0) else 0.0
    val percent = (progress * 100).toInt()
    val isCompleted = goal.isCompleted || goal.currentAmount >= goal.targetAmount

    val iconVector = when (goal.iconName.lowercase()) {
        "phone" -> Icons.Default.PhoneAndroid
        "car" -> Icons.Default.DirectionsCar
        "cottage" -> Icons.Default.Cottage
        "flight" -> Icons.Default.FlightTakeoff
        "school" -> Icons.Default.School
        "shopping" -> Icons.Default.ShoppingBag
        "fitness" -> Icons.Default.FitnessCenter
        else -> Icons.Default.AccountBalanceWallet
    }

    val (termLabel, termColor) = when (goal.term.uppercase()) {
        "CORTO" -> "Corto Plazo (< 1 año)" to KinetixTertiary
        "MEDIANO" -> "Mediano Plazo (1-3 años)" to KinetixPrimary
        "LARGO" -> "Largo Plazo (> 3 años)" to KinetixSecondary
        else -> "Meta" to KinetixPrimary
    }

    val progressBrush = when (goal.term.uppercase()) {
        "CORTO" -> Brush.horizontalGradient(listOf(KinetixTertiaryFixedDim, KinetixTertiary))
        "MEDIANO" -> Brush.horizontalGradient(listOf(KinetixPrimary, KinetixSecondary))
        else -> Brush.horizontalGradient(listOf(KinetixSecondary, KinetixTertiary))
    }

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
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(termColor.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = termColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = goal.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isCompleted) {
                                Surface(
                                    shape = CircleShape,
                                    color = KinetixSecondary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "¡Alcanzada! 🎉",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = KinetixSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = termColor.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = termLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = termColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (goal.description.isNotBlank()) {
                            Text(
                                text = goal.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar meta",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
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
                        .fillMaxWidth(progress.toFloat())
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(progressBrush)
                )
            }

            // Amount summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ahorrado: $ ${NumberFormat.getInstance(Locale("es", "CO")).format(goal.currentAmount.toLong())} COP",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
                    Text(
                        text = if (isCompleted) "¡Meta 100% completada!" else "Falta: $ ${NumberFormat.getInstance(Locale("es", "CO")).format(remaining.toLong())} COP",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCompleted) KinetixSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = termColor
                    )

                    Button(
                        onClick = onContributeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = termColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+ Aportar",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddFinancialGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, term: String, targetAmount: Double, iconName: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetCostStr by remember { mutableStateOf("") }
    var term by remember { mutableStateOf("CORTO") } // CORTO, MEDIANO, LARGO
    var monthlyPlanNote by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("shopping") }

    val iconOptions = listOf(
        "phone" to ("Celular / Tech" to Icons.Default.PhoneAndroid),
        "car" to ("Vehículo / Moto" to Icons.Default.DirectionsCar),
        "cottage" to ("Casa / Vivienda" to Icons.Default.Cottage),
        "flight" to ("Viaje / Vacaciones" to Icons.Default.FlightTakeoff),
        "school" to ("Estudio / Curso" to Icons.Default.School),
        "shopping" to ("Compras / Bienes" to Icons.Default.ShoppingBag),
        "wallet" to ("Fondo / Ahorro" to Icons.Default.AccountBalanceWallet),
        "fitness" to ("Salud / Deporte" to Icons.Default.FitnessCenter)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = KinetixPrimary
                )
                Text(
                    text = "Nueva Meta Financiera",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("¿Qué deseas comprar?") },
                    placeholder = { Text("Ej: Moto Yamaha, iPhone 15, Casa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = targetCostStr,
                    onValueChange = { targetCostStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Costo Total Estimado (COP)") },
                    placeholder = { Text("Ej: 4500000") },
                    leadingIcon = { Text("$", fontWeight = FontWeight.Bold) },
                    trailingIcon = { Text("COP", style = MaterialTheme.typography.labelSmall) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Quick Amount Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        1000000L to "$1M",
                        2000000L to "$2M",
                        5000000L to "$5M",
                        10000000L to "$10M"
                    ).forEach { (amount, label) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { targetCostStr = amount.toString() }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Plazo de la Meta",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "CORTO" to "Corto (< 1a)",
                        "MEDIANO" to "Mediano (1-3a)",
                        "LARGO" to "Largo (> 3a)"
                    ).forEach { (termKey, termText) ->
                        FilterChip(
                            selected = term == termKey,
                            onClick = { term = termKey },
                            label = {
                                Text(
                                    text = termText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (term == termKey) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KinetixPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = monthlyPlanNote,
                    onValueChange = { monthlyPlanNote = it },
                    label = { Text("¿Cuánto vas a aportar cada mes o cuando puedas?") },
                    placeholder = { Text("Ej: $200.000 COP / mes o aportes libres") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text(
                    text = "Ícono de lo que deseas comprar",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    iconOptions.forEach { (iconKey, pair) ->
                        val isSelected = selectedIcon == iconKey
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) KinetixPrimary else MaterialTheme.colorScheme.surfaceContainer
                                )
                                .clickable { selectedIcon = iconKey },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = pair.second,
                                contentDescription = pair.first,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            val context = LocalContext.current
            Button(
                onClick = {
                    val cost = targetCostStr.toDoubleOrNull() ?: 0.0
                    when {
                        title.isBlank() -> {
                            Toast.makeText(context, "Escribe el nombre de la meta que deseas", Toast.LENGTH_SHORT).show()
                        }
                        cost <= 0.0 -> {
                            Toast.makeText(context, "Ingresa un costo estimado válido para tu meta", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val desc = if (monthlyPlanNote.isNotBlank()) {
                                "Plan: $monthlyPlanNote"
                            } else {
                                "Aportes periódicos"
                            }
                            onConfirm(title.trim(), desc, term, cost, selectedIcon)
                            Toast.makeText(context, "¡Meta creada exitosamente!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = KinetixPrimary),
                modifier = Modifier.testTag("btn_confirm_add_goal")
            ) {
                Text("Crear Meta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ContributeGoalDialog(
    goal: FinancialGoalEntity,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = KinetixSecondary
                )
                Text(
                    text = "Aportar a Meta",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Ahorrado actual: $ ${NumberFormat.getInstance(Locale("es", "CO")).format(goal.currentAmount.toLong())} COP\nFalta para completar: $ ${NumberFormat.getInstance(Locale("es", "CO")).format(remaining.toLong())} COP",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Aportes rápidos:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(50000L, 100000L, 200000L, 500000L).forEach { quickAmount ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val cur = amountStr.toDoubleOrNull() ?: 0.0
                                    amountStr = (cur + quickAmount).toLong().toString()
                                }
                        ) {
                            Text(
                                text = "+$${quickAmount / 1000}k",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Monto a Aportar (COP)") },
                    placeholder = { Text("Ej: 100000") },
                    leadingIcon = { Text("$", fontWeight = FontWeight.Bold) },
                    trailingIcon = { Text("COP", style = MaterialTheme.typography.labelSmall) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount)
                    }
                },
                enabled = (amountStr.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = KinetixSecondary)
            ) {
                Text("Registrar Aporte")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun FinanceCrossNavigationCard(
    onNavigateToTab: (KinetixTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("finance_cross_navigation_card"),
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
                    text = "Navegación Rápida",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Conecta tu presupuesto con tus tareas diarias y listas de compra:",
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
                        .testTag("nav_btn_finance_to_tareas"),
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
                        .testTag("nav_btn_finance_to_horario"),
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
                    onClick = { onNavigateToTab(KinetixTab.MERCADO) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_btn_finance_to_mercado"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Mercado", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onNavigateToTab(KinetixTab.PERFIL) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_btn_finance_to_perfil"),
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

@Composable
private fun VoiceExpensesSection(
    allExpenses: List<ExpenseTransactionEntity>,
    expensesByCategory: Map<String, Double>,
    totalExpensesAmount: Double,
    topExpenseCategory: Pair<String, Double>?,
    onStartVoiceRecording: () -> Unit,
    onOpenManualAdd: () -> Unit,
    onDeleteExpense: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voice_expenses_section_card"),
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
            // Header
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
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Registro por Voz & Gastos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Traduce tu voz a texto y suma en tu balance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Audio Record Call to Action Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Graba diciendo: \"Me gasté hoy 15.000 pesos en una hamburguesa\" o \"Compré un juego por 80.000\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onStartVoiceRecording,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_record_voice_expense"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Grabar Audio", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onOpenManualAdd,
                            modifier = Modifier.testTag("btn_manual_add_expense"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Manual", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Balance Summary: "En qué estoy gastando más"
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
                maximumFractionDigits = 0
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Balance: ¿En qué gastas más?",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Total: ${numberFormat.format(totalExpensesAmount)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (topExpenseCategory != null && topExpenseCategory.second > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "🔥 Mayor gasto actual: ${topExpenseCategory.first} (${numberFormat.format(topExpenseCategory.second)})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Category breakdown progress bars
                    val categoriesToDisplay = listOf(
                        Triple("Comida", Icons.Default.Fastfood, MaterialTheme.colorScheme.primary),
                        Triple("Juegos", Icons.Default.SportsEsports, MaterialTheme.colorScheme.tertiary),
                        Triple("Salidas", Icons.Default.Celebration, KinetixSecondary),
                        Triple("Transporte", Icons.Default.DirectionsCar, MaterialTheme.colorScheme.primary),
                        Triple("Tecnología", Icons.Default.Devices, KinetixTertiaryFixed),
                        Triple("Otros", Icons.Default.Payments, MaterialTheme.colorScheme.secondary)
                    )

                    categoriesToDisplay.forEach { (catName, icon, color) ->
                        val catTotal = expensesByCategory[catName] ?: 0.0
                        val percent = if (totalExpensesAmount > 0) (catTotal / totalExpensesAmount).toFloat() else 0f
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = color)
                                    Text(
                                        text = catName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${numberFormat.format(catTotal)} (${(percent * 100).toInt()}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            LinearProgressIndicator(
                                progress = { percent.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = color,
                                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        }
                    }
                }
            }

            // Recent Expenses List
            if (allExpenses.isNotEmpty()) {
                Text(
                    text = "Registro Reciente de Gastos",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                allExpenses.take(6).forEach { exp ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when (exp.category) {
                                                "Comida" -> Icons.Default.Fastfood
                                                "Juegos" -> Icons.Default.SportsEsports
                                                "Salidas" -> Icons.Default.Celebration
                                                "Transporte" -> Icons.Default.DirectionsCar
                                                "Tecnología" -> Icons.Default.Devices
                                                else -> Icons.Default.Payments
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = exp.concept,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${exp.category}${if (exp.rawVoiceNote != null) " • Voz: \"${exp.rawVoiceNote}\"" else ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "-${numberFormat.format(exp.amount)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                IconButton(
                                    onClick = { onDeleteExpense(exp.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddExpenseDialog(
    initialConcept: String = "",
    initialAmount: String = "",
    initialCategory: String = "Comida",
    availableCategories: List<String> = listOf("Comida", "Juegos", "Salidas", "Transporte", "Tecnología", "Hogar", "Otros"),
    onDismiss: () -> Unit,
    onConfirm: (concept: String, amount: Double, category: String) -> Unit
) {
    var concept by remember { mutableStateOf(initialConcept) }
    var amountStr by remember { mutableStateOf(initialAmount) }
    var category by remember { mutableStateOf(initialCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Registrar Gasto", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = concept,
                    onValueChange = { concept = it },
                    label = { Text("Concepto") },
                    placeholder = { Text("Ej: Hamburguesa, Salida a cine, Juego") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Valor en COP") },
                    placeholder = { Text("Ej: 15000") },
                    leadingIcon = { Text("$", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Text("Categoría del gasto", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableCategories) { cat ->
                        val isSelected = category == cat
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { category = cat }
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (concept.isNotBlank() && amount > 0) {
                        onConfirm(concept.trim(), amount, category)
                    }
                },
                enabled = concept.isNotBlank() && (amountStr.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Guardar Gasto")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ExactFinancialBalanceCard(
    income: Double,
    expenses: Double,
    savings: Double,
    balance: Double
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exact_financial_balance_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Balance Exacto y Flujo Mensual",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Control en tiempo real de ingresos, ahorros y gastos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 4 metrics grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Estoy ganando",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormat.format(income),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Estoy gastando",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormat.format(expenses),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Estoy ahorrando",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormat.format(savings),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Balance exacto",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormat.format(balance),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

