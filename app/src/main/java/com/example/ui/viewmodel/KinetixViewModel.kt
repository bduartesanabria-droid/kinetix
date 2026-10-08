package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.KinetixDatabase
import com.example.data.local.entity.FinancialGoalEntity
import com.example.data.local.entity.FinancialProfileEntity
import com.example.data.local.entity.GamificationStatsEntity
import com.example.data.local.entity.ScheduleItemEntity
import com.example.data.local.entity.ShoppingItemEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.KinetixRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import android.content.Context
import com.example.data.local.entity.AppCategoryEntity
import com.example.data.local.entity.MeetingEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class ThemeMode(val label: String, val iconDesc: String) {
    SYSTEM("Sistema", "Seguir configuración del dispositivo"),
    LIGHT("Claro", "Tema luminoso de alto contraste"),
    DARK("Oscuro", "Tema oscuro para descanso visual")
}

enum class KinetixTab(val title: String, val subtitle: String) {
    TAREAS("Kinetix", "Tareas"),
    HORARIO("Kinetix", "Horario"),
    REUNIONES("Kinetix", "Reuniones"),
    MERCADO("Kinetix", "Mercado"),
    FINANZAS("Kinetix", "Finanzas"),
    PERFIL("Kinetix", "Perfil")
}

data class ParsedVoiceMeeting(
    val title: String,
    val dateKey: String,
    val dateDisplay: String,
    val timeDisplay: String,
    val isVirtual: Boolean,
    val platformOrLink: String,
    val physicalLocation: String
)

data class FinancialProjection(
    val monthlyIncome: Double,
    val rent: Double,
    val otherFixed: Double,
    val variableExpenses: Double,
    val projectedSavings: Double,
    val savingsPercent: Double,
    val rentPercent: Double,
    val otherFixedPercent: Double,
    val variablePercent: Double,
    val isHealthy: Boolean
)

class KinetixViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KinetixRepository
    private val prefs = application.getSharedPreferences("kinetix_preferences", Context.MODE_PRIVATE)

    // Onboarding & Legal Terms
    private val _hasCompletedOnboarding = MutableStateFlow(prefs.getBoolean("has_completed_onboarding", false))
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    // Tutorials visibility tracking per section
    private val _dismissedTutorials = MutableStateFlow<Map<String, Boolean>>(
        mapOf(
            "TAREAS" to prefs.getBoolean("tutorial_TAREAS", false),
            "HORARIO" to prefs.getBoolean("tutorial_HORARIO", false),
            "REUNIONES" to prefs.getBoolean("tutorial_REUNIONES", false),
            "MERCADO" to prefs.getBoolean("tutorial_MERCADO", false),
            "FINANZAS" to prefs.getBoolean("tutorial_FINANZAS", false),
            "PERFIL" to prefs.getBoolean("tutorial_PERFIL", false)
        )
    )
    val dismissedTutorials: StateFlow<Map<String, Boolean>> = _dismissedTutorials.asStateFlow()

    init {
        val database = KinetixDatabase.getInstance(application)
        repository = KinetixRepository(database)
        viewModelScope.launch {
            // Automatically purge shopping items bought > 2 days ago
            repository.purgeOldBoughtShoppingItems()
        }
        viewModelScope.launch {
            repository.financialProfile.collect { profile ->
                if (profile != null && profile.monthlyIncome > 0) {
                    incomeInput.value = profile.monthlyIncome.toLong().toString()
                    rentInput.value = profile.rentHousing.toLong().toString()
                    otherFixedInput.value = profile.otherFixedExpenses.toLong().toString()
                }
            }
        }
    }

    // UI state
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _selectedTab = MutableStateFlow(KinetixTab.TAREAS)
    val selectedTab: StateFlow<KinetixTab> = _selectedTab.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("Todas")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _isFocusModeActive = MutableStateFlow(false)
    val isFocusModeActive: StateFlow<Boolean> = _isFocusModeActive.asStateFlow()

    private val _showAddTaskDialog = MutableStateFlow(false)
    val showAddTaskDialog: StateFlow<Boolean> = _showAddTaskDialog.asStateFlow()

    // Financial input fields (interactive)
    val incomeInput = MutableStateFlow("4500000")
    val rentInput = MutableStateFlow("1200000")
    val otherFixedInput = MutableStateFlow("800000")

    private val _isRecalculating = MutableStateFlow(false)
    val isRecalculating: StateFlow<Boolean> = _isRecalculating.asStateFlow()

    // Schedule filter state
    // Current day of week: Calendar.MONDAY = 2, convert to 1=Lunes, 7=Domingo
    private val currentDayIndex: Int = run {
        val cal = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        // Calendar.SUNDAY = 1, MONDAY = 2, ..., SATURDAY = 7
        when (cal) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    }
    private val _selectedScheduleDay = MutableStateFlow(currentDayIndex)
    val selectedScheduleDay: StateFlow<Int> = _selectedScheduleDay.asStateFlow()

    private val _selectedScheduleTypeFilter = MutableStateFlow("Todos")
    val selectedScheduleTypeFilter: StateFlow<String> = _selectedScheduleTypeFilter.asStateFlow()

    // Data streams from Room
    val user: StateFlow<UserEntity?> = repository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingTasksOrdered: StateFlow<List<TaskEntity>> = repository.pendingTasksOrdered.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredPendingTasks: StateFlow<List<TaskEntity>> = combine(
        pendingTasksOrdered,
        selectedCategoryFilter
    ) { tasks, filter ->
        if (filter.equals("Todas", ignoreCase = true)) {
            tasks
        } else if (filter.contains("Vida", ignoreCase = true)) {
            tasks.filter { it.category.contains("Vida", ignoreCase = true) }
        } else {
            tasks.filter { it.category.equals(filter, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allShoppingItems: StateFlow<List<ShoppingItemEntity>> = repository.allShoppingItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingTasksCount: StateFlow<Int> = pendingTasksOrdered.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val pendingShoppingCount: StateFlow<Int> = allShoppingItems.map { items -> items.count { !it.isBought } }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val gamificationStats: StateFlow<GamificationStatsEntity?> = repository.gamificationStats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val financialProfile: StateFlow<FinancialProfileEntity?> = repository.financialProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val scheduleItems: StateFlow<List<ScheduleItemEntity>> = repository.scheduleItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val financialGoals: StateFlow<List<FinancialGoalEntity>> = repository.financialGoals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allExpenses: StateFlow<List<com.example.data.local.entity.ExpenseTransactionEntity>> = repository.allExpenses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val expensesByCategory: StateFlow<Map<String, Double>> = allExpenses.map { list ->
        list.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val totalExpensesAmount: StateFlow<Double> = allExpenses.map { list ->
        list.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val topExpenseCategory: StateFlow<Pair<String, Double>?> = expensesByCategory.map { map ->
        map.maxByOrNull { it.value }?.toPair()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Exact Financial Real-time Balance
    val monthlyIncomeAmount: StateFlow<Double> = incomeInput.map { parseCurrency(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 4500000.0
    )

    val netAvailableBalance: StateFlow<Double> = combine(
        monthlyIncomeAmount,
        totalExpensesAmount
    ) { income, expenses ->
        (income - expenses).coerceAtLeast(0.0)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 4500000.0
    )

    // Dynamic Categories Streams
    val allCategories: StateFlow<List<AppCategoryEntity>> = repository.allCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val taskCategories: StateFlow<List<String>> = allCategories.map { list ->
        val names = list.filter { it.type == "TASK" }.map { it.name }
        if (names.isNotEmpty()) names else listOf("Trabajo", "Estudio", "Vida Cotidiana")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Trabajo", "Estudio", "Vida Cotidiana"))

    val scheduleCategories: StateFlow<List<String>> = allCategories.map { list ->
        val names = list.filter { it.type == "SCHEDULE" }.map { it.name }
        if (names.isNotEmpty()) names else listOf("Estudio", "Trabajo")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Estudio", "Trabajo"))

    val shoppingCategories: StateFlow<List<String>> = allCategories.map { list ->
        val names = list.filter { it.type == "SHOPPING" }.map { it.name }
        if (names.isNotEmpty()) names else listOf("Supermercado", "Mercado Libre", "Temu", "Farmacia & Hogar", "Tecnología")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Supermercado", "Mercado Libre", "Temu", "Farmacia & Hogar", "Tecnología"))

    val expenseCategories: StateFlow<List<String>> = allCategories.map { list ->
        val names = list.filter { it.type == "EXPENSE" }.map { it.name }
        if (names.isNotEmpty()) names else listOf("Comida", "Juegos", "Salidas", "Transporte", "Tecnología", "Ropa", "Hogar", "Otros")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Comida", "Juegos", "Salidas", "Transporte", "Tecnología", "Ropa", "Hogar", "Otros"))

    // Meetings Stream
    val allMeetings: StateFlow<List<MeetingEntity>> = repository.allMeetings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Calculated projection dynamically updating on input changes
    val financialProjection: StateFlow<FinancialProjection> = combine(
        incomeInput,
        rentInput,
        otherFixedInput
    ) { incStr, rentStr, otherStr ->
        val income = parseCurrency(incStr)
        val rent = parseCurrency(rentStr)
        val other = parseCurrency(otherStr)
        val safeIncome = if (income > 0.0) income else 1.0

        val projectedSavings = (income - rent - other).coerceAtLeast(0.0)

        val savingsPct = if (income > 0) (projectedSavings / safeIncome) * 100.0 else 0.0
        val rentPct = if (income > 0) (rent / safeIncome) * 100.0 else 0.0
        val otherPct = if (income > 0) (other / safeIncome) * 100.0 else 0.0
        val variablePct = 0.0

        FinancialProjection(
            monthlyIncome = income,
            rent = rent,
            otherFixed = other,
            variableExpenses = 0.0,
            projectedSavings = projectedSavings,
            savingsPercent = savingsPct,
            rentPercent = rentPct,
            otherFixedPercent = otherPct,
            variablePercent = variablePct,
            isHealthy = savingsPct >= 20.0 && income > 0
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialProjection(
            monthlyIncome = 4500000.0,
            rent = 1200000.0,
            otherFixed = 800000.0,
            variableExpenses = 0.0,
            projectedSavings = 2500000.0,
            savingsPercent = 55.5,
            rentPercent = 26.6,
            otherFixedPercent = 17.8,
            variablePercent = 0.0,
            isHealthy = true
        )
    )

    fun selectTab(tab: KinetixTab) {
        _selectedTab.value = tab
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun toggleTheme() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
    }

    fun selectCategoryFilter(filter: String) {
        _selectedCategoryFilter.value = filter
    }

    fun toggleFocusMode() {
        _isFocusModeActive.value = !_isFocusModeActive.value
    }

    fun setShowAddTaskDialog(show: Boolean) {
        _showAddTaskDialog.value = show
    }

    fun selectScheduleDay(dayIndex: Int) {
        _selectedScheduleDay.value = dayIndex
    }

    fun selectScheduleTypeFilter(filter: String) {
        _selectedScheduleTypeFilter.value = filter
    }

    // Atomic transaction call to complete/uncomplete task
    fun toggleTaskCompletion(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompletion(taskId, completed)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun addNewTask(
        title: String,
        category: String,
        deadlineEpochMs: Long,
        difficulty: String,
        duration: String = "Duración: 1h",
        project: String = "General",
        isSpecificDayOnly: Boolean = false
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addNewTask(
                title = title,
                category = category,
                deadlineEpochMs = deadlineEpochMs,
                difficulty = difficulty,
                durationLabel = duration,
                projectTag = project,
                isSpecificDayOnly = isSpecificDayOnly
            )
            _showAddTaskDialog.value = false
        }
    }

    fun addShoppingItem(
        name: String,
        isPriority: Boolean = false,
        category: String = "General",
        storeCategory: String = "Supermercado"
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addShoppingItem(name, isPriority, category, storeCategory)
        }
    }

    fun toggleShoppingItem(itemId: String, isBought: Boolean) {
        viewModelScope.launch {
            repository.toggleShoppingItem(itemId, isBought)
        }
    }

    fun deleteShoppingItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteShoppingItem(itemId)
        }
    }

    fun clearBoughtShoppingItems() {
        viewModelScope.launch {
            repository.clearBoughtShoppingItems()
        }
    }

    fun recalculateFinancials() {
        viewModelScope.launch {
            _isRecalculating.value = true
            val income = parseCurrency(incomeInput.value)
            val rent = parseCurrency(rentInput.value)
            val other = parseCurrency(otherFixedInput.value)

            if (income > 0) incomeInput.value = income.toLong().toString()
            if (rent > 0) rentInput.value = rent.toLong().toString()
            if (other > 0) otherFixedInput.value = other.toLong().toString()

            repository.updateFinancials(income, rent, other)
            delay(350)
            _isRecalculating.value = false
        }
    }

    fun addFinancialGoal(
        title: String,
        description: String,
        term: String,
        targetAmount: Double,
        iconName: String = "flag"
    ) {
        if (title.isBlank() || targetAmount <= 0) return
        viewModelScope.launch {
            repository.addFinancialGoal(title, description, term, targetAmount, iconName)
        }
    }

    fun contributeToGoal(goalId: String, amount: Double) {
        if (amount <= 0) return
        viewModelScope.launch {
            repository.contributeToGoal(goalId, amount)
        }
    }

    fun deleteFinancialGoal(goalId: String) {
        viewModelScope.launch {
            repository.deleteFinancialGoal(goalId)
        }
    }

    fun addScheduleItem(
        title: String,
        type: String,
        dayOfWeek: Int,
        dayName: String,
        startTime: String,
        endTime: String,
        location: String,
        notes: String = "",
        colorHex: String = "#2563EB",
        reminderEnabled: Boolean = true
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addScheduleItem(
                title = title,
                type = type,
                dayOfWeek = dayOfWeek,
                dayName = dayName,
                startTime = startTime,
                endTime = endTime,
                location = location,
                notes = notes,
                colorHex = colorHex,
                reminderEnabled = reminderEnabled
            )
        }
    }

    fun toggleScheduleReminder(id: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleScheduleReminder(id, enabled)
        }
    }

    fun deleteScheduleItem(id: String) {
        viewModelScope.launch {
            repository.deleteScheduleItem(id)
        }
    }

    fun addExpense(
        amount: Double,
        concept: String,
        category: String,
        rawVoiceNote: String? = null
    ) {
        if (amount <= 0.0 || concept.isBlank()) return
        viewModelScope.launch {
            repository.addExpense(amount, concept, category, rawVoiceNote)
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    fun parseVoiceExpense(voiceText: String): Triple<Double, String, String> {
        val text = voiceText.lowercase(Locale("es", "ES")).trim()

        var extractedAmount = 0.0
        val milRegex = Regex("(\\d+(?:[.,]\\d+)?)\\s*(?:mil|k)", RegexOption.IGNORE_CASE)
        val numberRegex = Regex("(\\d{1,3}(?:[.,]\\d{3})*|\\d+)")

        val milMatch = milRegex.find(text)
        if (milMatch != null) {
            val numPart = milMatch.groupValues[1].replace(",", ".")
            val numVal = numPart.toDoubleOrNull() ?: 0.0
            extractedAmount = numVal * 1000.0
        } else {
            val matches = numberRegex.findAll(text).toList()
            for (m in matches) {
                val raw = m.value.replace(".", "").replace(",", "")
                val parsed = raw.toDoubleOrNull() ?: 0.0
                if (parsed > 0) {
                    extractedAmount = parsed
                    break
                }
            }
        }

        val category = when {
            listOf("hamburguesa", "comida", "almuerzo", "cena", "desayuno", "pizza", "restaurante", "snack", "cafe", "pan", "mercado", "sushi", "empanada", "helado", "dulce", "gaseosa").any { text.contains(it) } -> "Comida"
            listOf("juego", "videojuego", "playstation", "xbox", "steam", "skin", "consola", "nintendo", "game").any { text.contains(it) } -> "Juegos"
            listOf("salida", "cine", "fiesta", "cerveza", "bar", "paseo", "rumba", "discoteca", "trago", "concierto", "evento").any { text.contains(it) } -> "Salidas"
            listOf("uber", "taxi", "bus", "transmilenio", "gasolina", "pasaje", "metro", "didi", "transporte", "peaje").any { text.contains(it) } -> "Transporte"
            listOf("tecnologia", "celular", "audifonos", "cable", "mouse", "teclado", "computador", "gadget", "cargador", "pantalla").any { text.contains(it) } -> "Tecnología"
            listOf("ropa", "zapato", "camisa", "pantalon", "zapatillas", "gorra").any { text.contains(it) } -> "Ropa"
            listOf("arriendo", "luz", "agua", "internet", "gas", "servicio", "factura").any { text.contains(it) } -> "Hogar"
            else -> "Otros"
        }

        var cleanConcept = voiceText
        listOf("me gasté", "me gaste", "gasté", "gaste", "hoy", "ayer", "pesos", "cop", "pagué", "pague", "compré", "compre", "en una", "en un", "en el", "en la", "en").forEach { word ->
            cleanConcept = cleanConcept.replace(Regex("(?i)\\b$word\\b"), "")
        }
        cleanConcept = cleanConcept.replace(Regex("\\d+(?:[.,]\\d+)?"), "").replace(Regex("(?i)\\bmil\\b|\\bk\\b"), "").trim()
        val concept = if (cleanConcept.isNotBlank()) {
            cleanConcept.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } else {
            category
        }

        return Triple(extractedAmount, concept, category)
    }

    fun updateProfile(name: String, avatarUrl: String, statusTag: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.updateProfile(name, avatarUrl, statusTag)
        }
    }

    fun updateFullProfile(
        name: String,
        avatarUrl: String,
        statusTag: String,
        bio: String,
        customAvatarUri: String?
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.updateFullProfile(name, avatarUrl, statusTag, bio, customAvatarUri)
        }
    }

    fun boostStreak() {
        viewModelScope.launch {
            repository.boostStreak()
        }
    }

    fun parseCurrency(value: String): Double {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return 0.0
        var s = trimmed.replace("$", "").replace(" ", "").trim()

        if (s.contains(".") && s.contains(",")) {
            if (s.lastIndexOf(",") > s.lastIndexOf(".")) {
                // e.g. 1.200.000,50 -> 1200000.50
                s = s.replace(".", "").replace(",", ".")
            } else {
                // e.g. 1,200,000.50 -> 1200000.50
                s = s.replace(",", "")
            }
        } else if (s.contains(".")) {
            val dotCount = s.count { it == '.' }
            if (dotCount > 1) {
                s = s.replace(".", "")
            } else {
                val parts = s.split(".")
                if (parts.size == 2 && parts[1].length == 3) {
                    s = s.replace(".", "")
                }
            }
        } else if (s.contains(",")) {
            val commaCount = s.count { it == ',' }
            if (commaCount > 1) {
                s = s.replace(",", "")
            } else {
                val parts = s.split(",")
                if (parts.size == 2 && parts[1].length == 3) {
                    s = s.replace(",", "")
                } else {
                    s = s.replace(",", ".")
                }
            }
        }

        val clean = s.replace("[^0-9.]".toRegex(), "")
        return clean.toDoubleOrNull() ?: 0.0
    }

    // Dynamic Category Management
    fun addCategory(type: String, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addCategory(type, name)
        }
    }

    fun deleteCategory(type: String, name: String) {
        viewModelScope.launch {
            repository.deleteCategoryByNameAndType(name, type)
        }
    }

    // Meetings Management
    fun addMeeting(
        title: String,
        dateKey: String,
        dateDisplay: String,
        timeDisplay: String,
        startTimeEpoch: Long,
        isVirtual: Boolean,
        platformOrLink: String = "",
        physicalLocation: String = "",
        notifyOneHourBefore: Boolean = true,
        description: String = "",
        rawVoiceNote: String? = null
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addMeeting(
                title = title,
                dateKey = dateKey,
                dateDisplay = dateDisplay,
                timeDisplay = timeDisplay,
                startTimeEpoch = startTimeEpoch,
                isVirtual = isVirtual,
                platformOrLink = platformOrLink,
                physicalLocation = physicalLocation,
                notifyOneHourBefore = notifyOneHourBefore,
                description = description,
                rawVoiceNote = rawVoiceNote
            )
        }
    }

    fun deleteMeeting(id: String) {
        viewModelScope.launch {
            repository.deleteMeeting(id)
        }
    }

    fun toggleMeetingCompleted(id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleMeetingCompleted(id, isCompleted)
        }
    }

    fun testMeetingNotification(meeting: MeetingEntity) {
        val app = getApplication<Application>()
        com.example.ui.notifications.NotificationHelper.showMeetingReminder(
            context = app,
            title = meeting.title,
            time = meeting.timeDisplay,
            isVirtual = meeting.isVirtual,
            locationOrLink = if (meeting.isVirtual) meeting.platformOrLink else meeting.physicalLocation,
            ringAlarm = true
        )
    }

    // Voice Dictation Parsing for Meetings
    fun parseVoiceMeeting(voiceText: String): ParsedVoiceMeeting {
        val raw = voiceText.trim()
        val text = raw.lowercase(Locale("es", "ES"))
        val cal = Calendar.getInstance()

        val dateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displaySdf = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
        val timeSdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

        if (text.contains("mañana")) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        } else if (text.contains("pasado mañana")) {
            cal.add(Calendar.DAY_OF_YEAR, 2)
        } else if (text.contains("lunes")) {
            adjustToNextDayOfWeek(cal, Calendar.MONDAY)
        } else if (text.contains("martes")) {
            adjustToNextDayOfWeek(cal, Calendar.TUESDAY)
        } else if (text.contains("miércoles") || text.contains("miercoles")) {
            adjustToNextDayOfWeek(cal, Calendar.WEDNESDAY)
        } else if (text.contains("jueves")) {
            adjustToNextDayOfWeek(cal, Calendar.THURSDAY)
        } else if (text.contains("viernes")) {
            adjustToNextDayOfWeek(cal, Calendar.FRIDAY)
        } else if (text.contains("sábado") || text.contains("sabado")) {
            adjustToNextDayOfWeek(cal, Calendar.SATURDAY)
        } else if (text.contains("domingo")) {
            adjustToNextDayOfWeek(cal, Calendar.SUNDAY)
        }

        var hour = 10
        var minute = 0
        var isPm = false

        if (text.contains("tarde") || text.contains("noche") || text.contains("pm") || text.contains("p.m.")) {
            isPm = true
        }

        val matchTime = Regex("(?:a las|a la)\\s+(\\d{1,2})(?::(\\d{2}))?").find(text)
        if (matchTime != null) {
            val h = matchTime.groupValues[1].toIntOrNull() ?: 10
            val m = matchTime.groupValues.getOrNull(2)?.toIntOrNull() ?: 0
            hour = h
            minute = m
        } else {
            val colonMatch = Regex("(\\d{1,2}):(\\d{2})").find(text)
            if (colonMatch != null) {
                hour = colonMatch.groupValues[1].toIntOrNull() ?: 10
                minute = colonMatch.groupValues[2].toIntOrNull() ?: 0
            }
        }

        if (isPm && hour < 12) hour += 12
        if (!isPm && hour == 12 && (text.contains("mañana") || text.contains("am"))) hour = 0

        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)

        val isVirtual = listOf("virtual", "meet", "zoom", "teams", "remoto", "enlace", "link", "llamada", "videollamada", "discord", "skype").any { text.contains(it) }

        var platform = ""
        var location = ""

        if (isVirtual) {
            platform = when {
                text.contains("meet") -> "Google Meet"
                text.contains("zoom") -> "Zoom"
                text.contains("teams") -> "Microsoft Teams"
                text.contains("discord") -> "Discord"
                text.contains("skype") -> "Skype"
                else -> "Google Meet (Reunión Virtual)"
            }
        } else {
            val locMatch = Regex("(?:en|lugar:?)\\s+([a-zA-Z0-9áéíóúñÁÉÍÓÚÑ\\s]+?)(?:\\s+(?:a las|el|mañana|hoy|para)|$)", RegexOption.IGNORE_CASE).find(text)
            if (locMatch != null && locMatch.groupValues[1].trim().length > 2) {
                location = locMatch.groupValues[1].trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            } else {
                location = "Oficina / Sala de Juntas"
            }
        }

        var cleanTitle = raw
        listOf("agendar reunión", "agendar reunion", "reunión con", "reunion con", "reunión de", "reunion de", "reunión", "reunion").forEach { prefix ->
            if (cleanTitle.lowercase(Locale.getDefault()).startsWith(prefix)) {
                // Keep meaningful context
            }
        }
        if (cleanTitle.length > 3) {
            cleanTitle = cleanTitle.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } else {
            cleanTitle = "Reunión de Coordinación"
        }

        return ParsedVoiceMeeting(
            title = cleanTitle,
            dateKey = dateSdf.format(cal.time),
            dateDisplay = displaySdf.format(cal.time).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            timeDisplay = timeSdf.format(cal.time),
            isVirtual = isVirtual,
            platformOrLink = platform,
            physicalLocation = location
        )
    }

    private fun adjustToNextDayOfWeek(cal: Calendar, targetDay: Int) {
        val currentDay = cal.get(Calendar.DAY_OF_WEEK)
        var diff = targetDay - currentDay
        if (diff <= 0) diff += 7
        cal.add(Calendar.DAY_OF_YEAR, diff)
    }

    // Section Tutorials Management
    fun dismissTutorial(sectionKey: String) {
        prefs.edit().putBoolean("tutorial_$sectionKey", true).apply()
        val current = _dismissedTutorials.value.toMutableMap()
        current[sectionKey] = true
        _dismissedTutorials.value = current
    }

    fun resetTutorials() {
        val editor = prefs.edit()
        listOf("TAREAS", "HORARIO", "REUNIONES", "MERCADO", "FINANZAS", "PERFIL").forEach {
            editor.putBoolean("tutorial_$it", false)
        }
        editor.apply()
        _dismissedTutorials.value = mapOf(
            "TAREAS" to false,
            "HORARIO" to false,
            "REUNIONES" to false,
            "MERCADO" to false,
            "FINANZAS" to false,
            "PERFIL" to false
        )
    }

    // First-Launch Onboarding Completion
    fun completeOnboarding(
        name: String,
        workTitle: String,
        workStart: String,
        workEnd: String,
        workLocation: String,
        studyCareer: String,
        studyPlace: String,
        studyStart: String,
        studyEnd: String,
        monthlyIncome: Double,
        rent: Double,
        otherFixed: Double
    ) {
        viewModelScope.launch {
            val finalName = if (name.isNotBlank()) name.trim() else "Usuario Kinetix"
            val statusTag = if (workTitle.isNotBlank()) workTitle.trim() else "Profesional"
            val bio = if (studyCareer.isNotBlank()) "Estudiante de $studyCareer en $studyPlace" else "Enfocado en productividad, disciplina y finanzas"
            repository.updateFullProfile(finalName, "https://images.unsplash.com/photo-1534528741775-53994a69daeb", statusTag, bio, null)

            incomeInput.value = monthlyIncome.toLong().toString()
            rentInput.value = rent.toLong().toString()
            otherFixedInput.value = otherFixed.toLong().toString()
            repository.updateFinancials(monthlyIncome, rent, otherFixed)

            // Auto-generate Work Schedule (Lunes a Viernes)
            if (workTitle.isNotBlank()) {
                val days = listOf(1 to "Lunes", 2 to "Martes", 3 to "Miércoles", 4 to "Jueves", 5 to "Viernes")
                for ((dayNum, dayName) in days) {
                    repository.addScheduleItem(
                        title = "Jornada: $workTitle",
                        type = "Trabajo",
                        dayOfWeek = dayNum,
                        dayName = dayName,
                        startTime = workStart.ifBlank { "08:00" },
                        endTime = workEnd.ifBlank { "17:00" },
                        location = workLocation.ifBlank { "Empresa / Oficina" },
                        notes = "Horario laboral automático de onboarding",
                        colorHex = "#2563EB",
                        reminderEnabled = true
                    )
                }
            }

            // Auto-generate Study Schedule (Lunes a Jueves)
            if (studyCareer.isNotBlank()) {
                val days = listOf(1 to "Lunes", 2 to "Martes", 3 to "Miércoles", 4 to "Jueves")
                for ((dayNum, dayName) in days) {
                    repository.addScheduleItem(
                        title = "Clases: $studyCareer",
                        type = "Estudio",
                        dayOfWeek = dayNum,
                        dayName = dayName,
                        startTime = studyStart.ifBlank { "18:00" },
                        endTime = studyEnd.ifBlank { "21:30" },
                        location = studyPlace.ifBlank { "Universidad" },
                        notes = "Horario académico automático de onboarding",
                        colorHex = "#7C3AED",
                        reminderEnabled = true
                    )
                }
            }

            prefs.edit().putBoolean("has_completed_onboarding", true).apply()
            _hasCompletedOnboarding.value = true
        }
    }
}
