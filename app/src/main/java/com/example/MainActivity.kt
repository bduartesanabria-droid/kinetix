package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.KinetixBottomNav
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.ShoppingScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.KinetixTheme
import com.example.ui.viewmodel.KinetixTab
import com.example.ui.viewmodel.KinetixViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: KinetixViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()

            KinetixTheme(themeMode = themeMode) {
                val selectedTab by viewModel.selectedTab.collectAsState()
                val pendingTasksCount by viewModel.pendingTasksCount.collectAsState()
                val pendingShoppingCount by viewModel.pendingShoppingCount.collectAsState()
                val user by viewModel.user.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        KinetixBottomNav(
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.selectTab(it) },
                            pendingTasksCount = pendingTasksCount,
                            pendingShoppingCount = pendingShoppingCount
                        )
                    }
                ) { innerPadding ->
                    Crossfade(
                        targetState = selectedTab,
                        label = "screen_crossfade"
                    ) { tab ->
                        when (tab) {
                            KinetixTab.TAREAS -> TasksScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { viewModel.selectTab(it) },
                                modifier = Modifier.padding(innerPadding)
                            )
                            KinetixTab.HORARIO -> ScheduleScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { viewModel.selectTab(it) },
                                modifier = Modifier.padding(innerPadding)
                            )
                            KinetixTab.MERCADO -> ShoppingScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { viewModel.selectTab(it) },
                                modifier = Modifier.padding(innerPadding)
                            )
                            KinetixTab.FINANZAS -> FinanceScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { viewModel.selectTab(it) },
                                modifier = Modifier.padding(innerPadding)
                            )
                            KinetixTab.PERFIL -> ProfileScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { viewModel.selectTab(it) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
