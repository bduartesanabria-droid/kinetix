package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.FinancialDao
import com.example.data.local.dao.FinancialGoalDao
import com.example.data.local.dao.GamificationDao
import com.example.data.local.dao.ScheduleDao
import com.example.data.local.dao.ShoppingItemDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.FinancialGoalEntity
import com.example.data.local.entity.FinancialProfileEntity
import com.example.data.local.entity.GamificationStatsEntity
import com.example.data.local.entity.ScheduleItemEntity
import com.example.data.local.entity.ShoppingItemEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        ShoppingItemEntity::class,
        GamificationStatsEntity::class,
        FinancialProfileEntity::class,
        FinancialGoalEntity::class,
        ScheduleItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class KinetixDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun gamificationDao(): GamificationDao
    abstract fun financialDao(): FinancialDao
    abstract fun financialGoalDao(): FinancialGoalDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: KinetixDatabase? = null

        const val DEFAULT_USER_ID = "user-kinetix-primary-001"
        const val AVATAR_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuCx5yTMecAFtMO2K-mXYp7egy-h682bOPce3X9rZc7TrITy8QDJoD77lrBgC_bxhz5UdB7VBH15LaqUSeq7x1wqQ83CLLqIB-F4pwMcV3YN_VwUy9HDRMO4PW8M3zHXft5JZrGhZNPdkjrBxicVKti1Rcejx7CDj9KYYpnA37xn_s9Hz4YHwLsF7H6eZ027RpB1o4cNJj8p7VXnCUm9_FFxHSZ4x3--IHyYMJpw2TXSbNqgCDyLoli_"
        const val WORKSPACE_BANNER_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuB4g9jn-bHnZvBC-PUsfCWmOuOJBrRGmGprsUjnSOIR3BUk1t8Q4lSgwkHNZkHA4a1iMoULe92nlXz09JM8FNUMMwRpd1r3WHqA065377t3FisQ8GnyWF45pNDiAEIdOHJ6_gcOiInLkgT6xdcbVy8SrKc5JQAyNyF9tSwHH79LETUauaJAzfGfc5NzKUpVSBuvgL3wnKNmpi4wPnhoceEoEZAea04MzRwTPGFHMtVZok-8MB9IlUA_"

        fun getInstance(context: Context): KinetixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KinetixDatabase::class.java,
                    "kinetix_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { seedDatabase(it) }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDatabase(db: KinetixDatabase) {
            // 1. User starts fresh with Level 1
            db.userDao().insertUser(
                UserEntity(
                    id = DEFAULT_USER_ID,
                    name = "Mi Perfil",
                    avatarUrl = AVATAR_URL,
                    levelTitle = "Nivel 1: Primeros Pasos",
                    levelNumber = 1,
                    statusTag = "Listo para comenzar"
                )
            )

            // 2. Gamification Stats starts completely at zero (0 racha, 0 XP, level 1)
            db.gamificationDao().insertOrUpdate(
                GamificationStatsEntity(
                    userId = DEFAULT_USER_ID,
                    currentStreak = 0,
                    recordStreak = 0,
                    currentXp = 0,
                    targetXp = 100,
                    todayXpGained = 0,
                    level = 1,
                    levelTitle = "Nivel 1: Primeros Pasos",
                    completedTasksCount = 0,
                    tasksOnTimePercent = 100,
                    impulseExpenses = 0.0
                )
            )

            // 3. Financial Profile starts clean for the user to calculate their real numbers
            db.financialDao().insertOrUpdate(
                FinancialProfileEntity(
                    id = "fin-default",
                    monthLabel = "Mes Actual",
                    monthlyIncome = 0.0,
                    rentHousing = 0.0,
                    otherFixedExpenses = 0.0,
                    emergencyFundCurrent = 0.0,
                    emergencyFundGoal = 0.0,
                    tripFundCurrent = 0.0,
                    tripFundGoal = 0.0,
                    statusTag = "Por configurar"
                )
            )

            // 4. Initial starter financial goals (Corto, Mediano, Largo plazo) ready for personalization
            db.financialGoalDao().insertGoals(
                listOf(
                    FinancialGoalEntity(
                        id = "goal-1",
                        title = "Fondo de Emergencia",
                        description = "Ahorro de respaldo para imprevistos",
                        term = "CORTO",
                        currentAmount = 0.0,
                        targetAmount = 1000000.0,
                        iconName = "shield",
                        isCompleted = false
                    ),
                    FinancialGoalEntity(
                        id = "goal-2",
                        title = "Nuevo Equipo / Computador",
                        description = "Para potenciar trabajo y estudio",
                        term = "MEDIANO",
                        currentAmount = 0.0,
                        targetAmount = 3000000.0,
                        iconName = "laptop",
                        isCompleted = false
                    ),
                    FinancialGoalEntity(
                        id = "goal-3",
                        title = "Ahorro Inversión o Vivienda",
                        description = "Proyecto a futuro de largo plazo",
                        term = "LARGO",
                        currentAmount = 0.0,
                        targetAmount = 10000000.0,
                        iconName = "home",
                        isCompleted = false
                    )
                )
            )

            // 5. Tasks and Shopping items start completely clean (empty) as requested!
        }
    }
}
