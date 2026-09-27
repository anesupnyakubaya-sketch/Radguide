package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.DowntimeLogEntity
import com.example.data.local.entity.EquipmentEntity
import com.example.data.local.entity.MaintenanceTaskEntity
import com.example.ui.components.ChecklistExecutionDialog
import com.example.ui.components.DispatchAlertDialog
import com.example.ui.components.EquipmentDetailDialog
import com.example.ui.components.LogBreakdownDialog
import com.example.ui.components.ResolveBreakdownDialog
import com.example.ui.navigation.NavScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DowntimeLogsScreen
import com.example.ui.screens.EquipmentScreen
import com.example.ui.screens.MaintenanceScreen
import com.example.ui.screens.TechnicianAlertsScreen
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusOperational
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.RadGuideViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadGuideApp(viewModel: RadGuideViewModel) {
    var currentScreen by remember { mutableStateOf(NavScreen.DASHBOARD) }

    // Dialog States
    var selectedEquipmentForDetail by remember { mutableStateOf<EquipmentEntity?>(null) }
    var selectedTaskForExecution by remember { mutableStateOf<MaintenanceTaskEntity?>(null) }
    var selectedDowntimeForResolution by remember { mutableStateOf<DowntimeLogEntity?>(null) }
    var showLogBreakdownDialog by remember { mutableStateOf(false) }
    var preselectedEquipIdForBreakdown by remember { mutableStateOf<Long?>(null) }
    var showDispatchAlertDialog by remember { mutableStateOf(false) }

    val equipmentList by viewModel.equipmentList.collectAsStateWithLifecycle()
    val technicians by viewModel.technicians.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val activeBreakdownsCount by viewModel.activeBreakdownsCount.collectAsStateWithLifecycle()
    val overdueTasksCount by viewModel.overdueTasksCount.collectAsStateWithLifecycle()
    val activeAlertsCount by viewModel.activeAlertsCount.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearUserMessage()
        }
    }

    // Handle back button when not on dashboard or when dialog is open
    BackHandler(enabled = currentScreen != NavScreen.DASHBOARD) {
        currentScreen = NavScreen.DASHBOARD
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RadGuide",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Parirenyatwa & Mpilo Radiotherapy PM",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (activeBreakdownsCount > 0) {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(CircleShape)
                                .background(StatusCritical.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(StatusCritical)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$activeBreakdownsCount DOWN",
                                    color = StatusCritical,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                NavScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    val badgeCount = when (screen) {
                        NavScreen.DOWNTIME -> activeBreakdownsCount
                        NavScreen.MAINTENANCE -> overdueTasksCount
                        NavScreen.ALERTS -> activeAlertsCount
                        else -> 0
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            if (badgeCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = if (screen == NavScreen.DOWNTIME) StatusCritical else StatusWarning,
                                            contentColor = Color.White
                                        ) {
                                            Text("$badgeCount")
                                        }
                                    }
                                ) {
                                    Icon(imageVector = screen.icon, contentDescription = screen.title)
                                }
                            } else {
                                Icon(imageVector = screen.icon, contentDescription = screen.title)
                            }
                        },
                        label = { Text(screen.title, fontSize = 10.sp) },
                        modifier = Modifier.testTag(screen.testTag)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                NavScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToEquipment = { currentScreen = NavScreen.EQUIPMENT },
                    onNavigateToMaintenance = { currentScreen = NavScreen.MAINTENANCE },
                    onNavigateToDowntime = { currentScreen = NavScreen.DOWNTIME },
                    onNavigateToAlerts = { currentScreen = NavScreen.ALERTS },
                    onSelectEquipment = { equip -> selectedEquipmentForDetail = equip },
                    onStartMaintenance = { task -> selectedTaskForExecution = task },
                    onOpenLogBreakdown = {
                        preselectedEquipIdForBreakdown = null
                        showLogBreakdownDialog = true
                    },
                    onOpenDispatchAlert = { showDispatchAlertDialog = true }
                )
                NavScreen.EQUIPMENT -> EquipmentScreen(
                    viewModel = viewModel,
                    onSelectEquipment = { equip -> selectedEquipmentForDetail = equip },
                    onLogBreakdown = { equip ->
                        preselectedEquipIdForBreakdown = equip.id
                        showLogBreakdownDialog = true
                    }
                )
                NavScreen.MAINTENANCE -> MaintenanceScreen(
                    viewModel = viewModel,
                    onExecuteTask = { task -> selectedTaskForExecution = task }
                )
                NavScreen.DOWNTIME -> DowntimeLogsScreen(
                    viewModel = viewModel,
                    onOpenLogBreakdown = {
                        preselectedEquipIdForBreakdown = null
                        showLogBreakdownDialog = true
                    },
                    onResolveBreakdown = { log -> selectedDowntimeForResolution = log }
                )
                NavScreen.ALERTS -> TechnicianAlertsScreen(
                    viewModel = viewModel,
                    onOpenDispatchAlert = { showDispatchAlertDialog = true }
                )
            }
        }
    }

    // Modal Dialog: Checklist Execution (AAPM TG-142)
    selectedTaskForExecution?.let { task ->
        ChecklistExecutionDialog(
            task = task,
            onDismiss = { selectedTaskForExecution = null },
            onComplete = { completedBy, notes ->
                viewModel.completeMaintenanceTask(
                    taskId = task.id,
                    completedBy = completedBy,
                    notes = notes,
                    equipmentId = task.equipmentId,
                    taskCategory = task.category
                )
                selectedTaskForExecution = null
            }
        )
    }

    // Modal Dialog: Log Machine Breakdown
    if (showLogBreakdownDialog) {
        LogBreakdownDialog(
            equipmentList = equipmentList,
            preselectedEquipmentId = preselectedEquipIdForBreakdown,
            onDismiss = { showLogBreakdownDialog = false },
            onSubmit = { equipId, subsystem, severity, errorCode, desc, patients, tech ->
                viewModel.logEquipmentBreakdown(
                    equipmentId = equipId,
                    failureSubsystem = subsystem,
                    severity = severity,
                    errorCode = errorCode,
                    description = desc,
                    patientsAffectedPerDay = patients,
                    technicianToAlert = tech
                )
                showLogBreakdownDialog = false
            }
        )
    }

    // Modal Dialog: Resolve Machine Breakdown
    selectedDowntimeForResolution?.let { log ->
        ResolveBreakdownDialog(
            downtimeLog = log,
            onDismiss = { selectedDowntimeForResolution = null },
            onResolve = { rootCause, actionTaken, tech ->
                viewModel.resolveDowntime(
                    logId = log.id,
                    equipmentId = log.equipmentId,
                    rootCause = rootCause,
                    actionTaken = actionTaken,
                    technician = tech
                )
                selectedDowntimeForResolution = null
            }
        )
    }

    // Modal Dialog: Equipment Digital Passport & Telemetry
    selectedEquipmentForDetail?.let { equip ->
        EquipmentDetailDialog(
            equipment = equip,
            onDismiss = { selectedEquipmentForDetail = null },
            onUpdateStatus = { newStatus ->
                viewModel.updateEquipmentStatus(equip.id, newStatus)
                // Refresh local dialog reference
                selectedEquipmentForDetail = equip.copy(status = newStatus)
            },
            onUpdateTelemetry = { temp, pressure ->
                viewModel.updateEquipmentTelemetry(equip.id, temp, pressure)
                selectedEquipmentForDetail = equip.copy(chillerTempCelsius = temp, sf6PressurePsi = pressure)
            },
            onLogFault = {
                val currentId = equip.id
                selectedEquipmentForDetail = null
                preselectedEquipIdForBreakdown = currentId
                showLogBreakdownDialog = true
            }
        )
    }

    // Modal Dialog: Dispatch Emergency Alert SOS
    if (showDispatchAlertDialog) {
        DispatchAlertDialog(
            equipmentList = equipmentList,
            technicianList = technicians,
            onDismiss = { showDispatchAlertDialog = false },
            onDispatch = { equipId, equipName, hospId, hospName, title, msg, sev, tech, phone, ch ->
                viewModel.dispatchCustomAlert(
                    equipmentId = equipId,
                    equipmentName = equipName,
                    hospitalId = hospId,
                    hospitalName = hospName,
                    title = title,
                    message = msg,
                    severity = sev,
                    technicianName = tech,
                    technicianPhone = phone,
                    channel = ch
                )
                showDispatchAlertDialog = false
            }
        )
    }
}
