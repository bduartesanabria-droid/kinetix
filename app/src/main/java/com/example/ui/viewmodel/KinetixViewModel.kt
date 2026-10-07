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
import java.util.Calendar
import java.util.Locale

enum class ThemeMode(val label: String, val iconDesc: String) {
    SYSTEM("Sistema", "Seguir configuración del dispositivo"),
    LIGHT("Claro", "Tema luminoso de alto contraste"),
    DARK("Oscuro", "Tema oscuro para descanso visual")
}

enum class KinetixTab(val title: String, val subtitle: String) {
    TAREAS("Kinetix", "Tareas"),
    HORARIO("Kinetix", "Horario"),
    MERCADO("Kinetix", "Mercado"),
    FINANZAS("Kinetix", "Finanzas"),
    PERFIL("Kinetix", "Perfil")
}

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
        when (filter) {
            "Trabajo" -> tasks.filter { it.category.equals("Trabajo", ignoreCase = true) }
            "Estudio" -> tasks.filter { it.category.equals("Estudio", ignoreCase = true) }
            "Vida" -> tasks.filter { it.category.contains("Vida", ignoreCase = true) }
            else -> tasks
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
        project: String = "General"
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addNewTask(
                title = title,
                category = category,
                deadlineEpochMs = deadlineEpochMs,
                difficulty = difficulty,
                durationLabel = duration,
                projectTag = project
            )
            _showAddTaskDialog.value = false
        }
    }

    fun addShoppingItem(name: String, isPriority: Boolean = false, category: String = "General") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addShoppingItem(name, isPriority, category)
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

    fun updateProfile(name: String, avatarUrl: String, statusTag: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.updateProfile(name, avatarUrl, statusTag)
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
}
