package com.dialysis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dialysis.app.ui.*
import com.dialysis.app.ui.components.TabItem
import com.dialysis.app.ui.theme.DialysisAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DialysisAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    var selectedTab by remember { mutableStateOf(TabItem.Home) }

                    when (selectedTab) {
                        TabItem.Home -> HomeScreen(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                        TabItem.Records -> RecordsScreen(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                        TabItem.Schedule -> ScheduleScreen(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                        TabItem.Contacts -> ContactsScreen(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }
                }
            }
        }
    }
}
