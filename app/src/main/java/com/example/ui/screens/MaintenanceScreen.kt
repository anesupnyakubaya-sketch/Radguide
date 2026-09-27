package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.MaintenanceTaskEntity
import com.example.ui.components.HospitalFilterBar
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusOperational
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.RadGuideViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MaintenanceScreen(
    viewModel: RadGuideViewModel,
    onExecuteTask: (MaintenanceTaskEntity) -> Unit
) {
    val selectedHospital by viewModel.selectedHospital.collectAsStateWithLifecycle()
    val maintenanceTasks by viewModel.maintenanceTasks.collectAsStateWithLifecycle()
    val overdueCount by viewModel.overdueTasksCount.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Pending/Due, 1 = Completed Log
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val now = System.currentTimeMillis()

    val pendingTasks = maintenanceTasks.filter { !it.isCompleted }
    val completedTasks = maintenanceTasks.filter { it.isCompleted }

    val currentList = if (selectedTab == 0) pendingTasks else completedTasks

    val filteredList = currentList.filter { task ->
        if (selectedCategoryFilter == "ALL") true else task.category == selectedCategoryFilter
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("maintenance_screen_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Title Bar
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Preventive Maintenance & QA Protocols",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "AAPM TG-142 daily, weekly, monthly and OEM calibration checklists",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Hospital filter
        item {
            HospitalFilterBar(
                selectedHospital = selectedHospital,
                onSelectHospital = { viewModel.setSelectedHospital(it) }
            )
        }

        // Tabs: Pending vs History
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Scheduled & Due (${pendingTasks.size})")
                            if (overdueCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StatusCritical)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$overdueCount Overdue",
                                        color = androidx.compose.ui.graphics.Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.testTag("tab_pending_pm")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Sign-off Log (${completedTasks.size})") },
                    modifier = Modifier.testTag("tab_history_pm")
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "ALL",
                        onClick = { selectedCategoryFilter = "ALL" },
                        label = { Text("All Protocols") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "DAILY_QA",
                        onClick = { selectedCategoryFilter = "DAILY_QA" },
                        label = { Text("Daily Morning QA") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "WEEKLY_CHECK",
                        onClick = { selectedCategoryFilter = "WEEKLY_CHECK" },
                        label = { Text("Weekly Checks") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "MONTHLY_CALIBRATION",
                        onClick = { selectedCategoryFilter = "MONTHLY_CALIBRATION" },
                        label = { Text("Monthly Calibration") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "QUARTERLY_OEM",
                        onClick = { selectedCategoryFilter = "QUARTERLY_OEM" },
                        label = { Text("Quarterly / OEM") }
                    )
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusOperational,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTab == 0) "No pending maintenance tasks!" else "No completed protocols yet.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "All scheduled clinical quality assurance protocols are in order.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // List of tasks
        items(filteredList, key = { it.id }) { task ->
            val isOverdue = !task.isCompleted && task.dueDate < now
            val itemsCount = task.checklistItems.split("|").filter { it.isNotBlank() }.size

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("maintenance_task_card_${task.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOverdue) StatusCritical.copy(alpha = 0.05f)
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${task.equipmentName} • ${task.hospitalName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Priority or Overdue badge
                        if (isOverdue) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StatusCritical.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "OVERDUE",
                                    color = StatusCritical,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        } else if (task.isCompleted) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StatusOperational.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "VERIFIED",
                                    color = StatusOperational,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Due date & Category row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = if (isOverdue) StatusCritical else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (task.isCompleted && task.completedDate != null)
                                "Completed: ${dateFormat.format(Date(task.completedDate))}"
                            else "Due: ${dateFormat.format(Date(task.dueDate))}",
                            fontSize = 12.sp,
                            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal,
                            color = if (isOverdue) StatusCritical else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "• $itemsCount protocol steps",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Completed Sign-off details
                    if (task.isCompleted) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Signed off by: ${task.completedBy ?: "Medical Physicist"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (!task.technicianNotes.isNullOrBlank()) {
                                    Text(
                                        text = "\"${task.technicianNotes}\"",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Action button for pending tasks
                    if (!task.isCompleted) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onExecuteTask(task) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOverdue) StatusCritical else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("execute_task_btn_${task.id}")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOverdue) "Execute Overdue Protocol Now" else "Execute Maintenance Checklist",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
