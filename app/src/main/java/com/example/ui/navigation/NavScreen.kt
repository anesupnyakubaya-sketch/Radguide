package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavScreen(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Overview", Icons.Default.Dashboard, "nav_dashboard"),
    EQUIPMENT("Equipment", Icons.Default.MedicalServices, "nav_equipment"),
    MAINTENANCE("QA & PM", Icons.Default.Build, "nav_maintenance"),
    DOWNTIME("Downtime", Icons.Default.Warning, "nav_downtime"),
    ALERTS("Alerts & Techs", Icons.Default.NotificationsActive, "nav_alerts")
}
