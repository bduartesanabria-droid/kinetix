package com.example.data.repository

import com.example.data.local.KinetixDatabase
import com.example.data.local.entity.AppCategoryEntity
import com.example.data.local.entity.FinancialGoalEntity
import com.example.data.local.entity.FinancialProfileEntity
import com.example.data.local.entity.GamificationStatsEntity
import com.example.data.local.entity.MeetingEntity
import com.example.data.local.entity.ScheduleItemEntity
import com.example.data.local.entity.ShoppingItemEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class KinetixRepository(private val database: KinetixDatabase) {

    private val taskDao = database.taskDao()
    private val shoppingItemDao = database.shoppingItemDao()
    private val gamificationDao = database.gamificationDao()
    private val financialDao = database.financialDao()
    private val userDao = database.userDao()
    private val scheduleDao = database.scheduleDao()
    private val financialGoalDao = database.financialGoalDao()
    private val expenseDao = database.expenseDao()
    private val categoryDao = database.categoryDao()
    private val meetingDao = database.meetingDao()

    val user: Flow<UserEntity?> = userDao.getUser()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val pendingTasksOrdered: Flow<List<TaskEntity>> = taskDao.getPendingTasksOrderedByDeadline()
    val allShoppingItems: Flow<List<ShoppingItemEntity>> = shoppingItemDao.getAllShoppingItems()
    val gamificationStats: Flow<GamificationStatsEntity?> = gamificationDao.getPrimaryStats()
    val financialProfile: Flow<FinancialProfileEntity?> = financialDao.getFinancialProfile()
    val scheduleItems: Flow<List<ScheduleItemEntity>> = scheduleDao.getAllScheduleItems()
    val financialGoals: Flow<List<FinancialGoalEntity>> = financialGoalDao.getAllGoals()
    val allExpenses: Flow<List<com.example.data.local.entity.ExpenseTransactionEntity>> = expenseDao.getAllExpenses()
    val allCategories: Flow<List<AppCategoryEntity>> = categoryDao.getAllCategories()
    val allMeetings: Flow<List<MeetingEntity>> = meetingDao.getAllMeetings()

    fun getCategoriesByType(type: String): Flow<List<AppCategoryEntity>> = categoryDao.getCategoriesByType(type)

    suspend fun setTaskCompletion(taskId: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        taskDao.completeTaskAtomic(
            taskId = taskId,
            userId = KinetixDatabase.DEFAULT_USER_ID,
            isCompleted = isCompleted,
            now = now,
            xpDelta = 40
        )
    }

    suspend fun addNewTask(
        title: String,
        category: String,
        deadlineEpochMs: Long,
        difficulty: String = "MEDIA",
        durationLabel: String = "Duración: 1h",
        projectTag: String = "General",
        isSpecificDayOnly: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val diffMs = deadlineEpochMs - System.currentTimeMillis()
        val hours = diffMs / (1000 * 60 * 60)
        val urgency = when {
            hours <= 24 -> "ALTA"
            hours <= 72 -> "MEDIA"
            else -> "BAJA"
        }

        val dateFmt = SimpleDateFormat("d MMM, hh:mm a", Locale("es", "ES"))
        val formattedDate = dateFmt.format(Date(deadlineEpochMs))
        val prefix = if (isSpecificDayOnly) "Para hacer el" else "Entrega hasta"
        val deadlineLabel = "$prefix $formattedDate • Urgencia $urgency"

        val task = TaskEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            category = category,
            urgency = urgency,
            deadline = deadlineEpochMs,
            deadlineLabel = deadlineLabel,
            durationLabel = durationLabel.ifBlank { "Duración: 1h" },
            projectTag = projectTag.ifBlank { "General" },
            contextIcon = when (category) {
                "Estudio" -> "school"
                "Trabajo" -> "group"
                else -> "home"
            },
            isCompleted = false,
            timeSlot = formattedDate,
            progressPercent = 0,
            difficulty = difficulty
        )
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(taskId: String) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun addShoppingItem(
        name: String,
        isPriority: Boolean = false,
        category: String = "General",
        storeCategory: String = "Supermercado"
    ) = withContext(Dispatchers.IO) {
        val item = ShoppingItemEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            category = category,
            iconName = when (storeCategory) {
                "Tecnología" -> "devices"
                "Mercado Libre" -> "local_shipping"
                "Temu" -> "card_giftcard"
                else -> "shopping_bag"
            },
            isBought = false,
            isPriority = isPriority,
            estimatedPrice = 0.0,
            boughtAt = null,
            storeCategory = storeCategory
        )
        shoppingItemDao.insertItem(item)
    }

    suspend fun addExpense(
        amount: Double,
        concept: String,
        category: String,
        rawVoiceNote: String? = null
    ) = withContext(Dispatchers.IO) {
        val expense = com.example.data.local.entity.ExpenseTransactionEntity(
            id = UUID.randomUUID().toString(),
            amount = amount,
            concept = concept,
            category = category,
            timestamp = System.currentTimeMillis(),
            rawVoiceNote = rawVoiceNote
        )
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(id: String) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpense(id)
    }

    suspend fun toggleShoppingItem(itemId: String, isBought: Boolean) = withContext(Dispatchers.IO) {
        val boughtAt = if (isBought) System.currentTimeMillis() else null
        shoppingItemDao.setItemBoughtWithTimestamp(itemId, isBought, boughtAt)
    }

    suspend fun purgeOldBoughtShoppingItems() = withContext(Dispatchers.IO) {
        // Items bought more than 2 days ago (2 * 24 * 3600 * 1000 ms) are automatically deleted
        val twoDaysAgo = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L)
        shoppingItemDao.deleteOldPurchasedItems(twoDaysAgo)
    }

    suspend fun deleteShoppingItem(itemId: String) = withContext(Dispatchers.IO) {
        shoppingItemDao.deleteItemById(itemId)
    }

    suspend fun clearBoughtShoppingItems() = withContext(Dispatchers.IO) {
        shoppingItemDao.clearBoughtItems()
    }

    suspend fun updateFinancials(income: Double, rent: Double, otherFixed: Double) = withContext(Dispatchers.IO) {
        financialDao.insertOrUpdate(
            FinancialProfileEntity(
                id = "fin-default",
                monthLabel = "Mes Actual",
                monthlyIncome = income,
                rentHousing = rent,
                otherFixedExpenses = otherFixed
            )
        )
    }

    suspend fun addFinancialGoal(
        title: String,
        description: String,
        term: String, // "CORTO", "MEDIANO", "LARGO"
        targetAmount: Double,
        iconName: String = "flag"
    ) = withContext(Dispatchers.IO) {
        val goal = FinancialGoalEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            term = term,
            currentAmount = 0.0,
            targetAmount = targetAmount,
            iconName = iconName,
            isCompleted = false
        )
        financialGoalDao.insertGoal(goal)
    }

    suspend fun contributeToGoal(goalId: String, amount: Double) = withContext(Dispatchers.IO) {
        financialGoalDao.contributeToGoal(goalId, amount)
    }

    suspend fun deleteFinancialGoal(goalId: String) = withContext(Dispatchers.IO) {
        financialGoalDao.deleteGoal(goalId)
    }

    suspend fun addScheduleItem(
        title: String,
        type: String, // "Estudio" or "Trabajo"
        dayOfWeek: Int,
        dayName: String,
        startTime: String,
        endTime: String,
        location: String,
        notes: String = "",
        colorHex: String = "#2563EB",
        reminderEnabled: Boolean = true
    ) = withContext(Dispatchers.IO) {
        val item = ScheduleItemEntity(
            id = UUID.randomUUID().toString(),
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
        scheduleDao.insertScheduleItem(item)
    }

    suspend fun toggleScheduleReminder(id: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        scheduleDao.setReminderEnabled(id, enabled)
    }

    suspend fun deleteScheduleItem(id: String) = withContext(Dispatchers.IO) {
        scheduleDao.deleteScheduleItem(id)
    }

    suspend fun updateProfile(name: String, avatarUrl: String, statusTag: String) = withContext(Dispatchers.IO) {
        userDao.updateProfile(KinetixDatabase.DEFAULT_USER_ID, name, avatarUrl, statusTag)
    }

    suspend fun updateFullProfile(
        name: String,
        avatarUrl: String,
        statusTag: String,
        bio: String,
        customAvatarUri: String?
    ) = withContext(Dispatchers.IO) {
        userDao.updateFullProfile(
            id = KinetixDatabase.DEFAULT_USER_ID,
            name = name,
            avatarUrl = avatarUrl,
            statusTag = statusTag,
            bio = bio,
            customAvatarUri = customAvatarUri
        )
    }

    suspend fun boostStreak() = withContext(Dispatchers.IO) {
        gamificationDao.incrementStreak(KinetixDatabase.DEFAULT_USER_ID)
        gamificationDao.addXp(KinetixDatabase.DEFAULT_USER_ID, 25)
    }

    suspend fun addCategory(type: String, name: String) = withContext(Dispatchers.IO) {
        if (name.isBlank()) return@withContext
        val category = AppCategoryEntity(
            id = UUID.randomUUID().toString(),
            type = type,
            name = name.trim(),
            isDefault = false
        )
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(id: String) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategoryById(id)
    }

    suspend fun deleteCategoryByNameAndType(name: String, type: String) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategoryByNameAndType(name, type)
    }

    suspend fun addMeeting(
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
    ) = withContext(Dispatchers.IO) {
        val meeting = MeetingEntity(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            description = description.trim(),
            dateKey = dateKey,
            dateDisplay = dateDisplay,
            timeDisplay = timeDisplay,
            startTimeEpoch = startTimeEpoch,
            isVirtual = isVirtual,
            platformOrLink = platformOrLink.trim(),
            physicalLocation = physicalLocation.trim(),
            notifyOneHourBefore = notifyOneHourBefore,
            rawVoiceNote = rawVoiceNote
        )
        meetingDao.insertMeeting(meeting)
    }

    suspend fun deleteMeeting(id: String) = withContext(Dispatchers.IO) {
        meetingDao.deleteMeetingById(id)
    }

    suspend fun toggleMeetingCompleted(id: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        meetingDao.toggleMeetingCompleted(id, isCompleted)
    }
}
