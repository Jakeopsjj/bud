package com.dialysis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.dialysis.app.data.AppPreferences
import com.dialysis.app.ui.*
import com.dialysis.app.ui.components.TabItem
import com.dialysis.app.ui.theme.DialysisAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val prefs = AppPreferences(this)
        setContent {
            DialysisAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    val isFirstLaunch = remember { mutableStateOf(prefs.isFirstLaunch()) }

                    if (isFirstLaunch.value) {
                        OnboardingScreen(
                            onComplete = {
                                prefs.setFirstLaunchCompleted()
                                isFirstLaunch.value = false
                            }
                        )
                    } else {
                        var selectedTab by remember { mutableStateOf(TabItem.Home) }

                        when (selectedTab) {
                            TabItem.Home -> HomeScreen(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it },
                                prefs = prefs
                            )
                            TabItem.Records -> RecordsScreen(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it },
                                prefs = prefs
                            )
                            TabItem.Schedule -> ScheduleScreen(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it },
                                prefs = prefs
                            )
                            TabItem.Contacts -> ContactsScreen(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it },
                                prefs = prefs
                            )
                        }
                    }
                }
            }
        }
    }
}
