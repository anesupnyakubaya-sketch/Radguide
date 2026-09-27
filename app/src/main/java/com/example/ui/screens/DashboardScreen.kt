package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entity.EquipmentEntity
import com.example.data.local.entity.MaintenanceTaskEntity
import com.example.ui.components.HospitalFilterBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusOperational
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.RadGuideViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: RadGuideViewModel,
    onNavigateToEquipment: () -> Unit,
    onNavigateToMaintenance: () -> Unit,
    onNavigateToDowntime: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onSelectEquipment: (EquipmentEntity) -> Unit,
    onStartMaintenance: (MaintenanceTaskEntity) -> Unit,
    onOpenLogBreakdown: () -> Unit,
    onOpenDispatchAlert: () -> Unit
) {
    val selectedHospital by viewModel.selectedHospital.collectAsStateWithLifecycle()
    val equipmentList by viewModel.equipmentList.collectAsStateWithLifecycle()
    val maintenanceTasks by viewModel.maintenanceTasks.collectAsStateWithLifecycle()
    val downtimeLogs by viewModel.downtimeLogs.collectAsStateWithLifecycle()
    val activeBreakdownsCount by viewModel.activeBreakdownsCount.collectAsStateWithLifecycle()
    val overdueTasksCount by viewModel.overdueTasksCount.collectAsStateWithLifecycle()
    val activeAlertsCount by viewModel.activeAlertsCount.collectAsStateWithLifecycle()
    val totalPatientsAffected by viewModel.totalPatientsAffected.collectAsStateWithLifecycle()

    val operationalCount = equipmentList.count { it.status == "OPERATIONAL" }
    val totalMachines = equipmentList.size
    val averageUptime = if (totalMachines > 0) {
        equipmentList.map { it.uptimePercentage }.average().toInt()
    } else 0

    val activeDowntime = downtimeLogs.firstOrNull { !it.isResolved }
    val nextPendingQA = maintenanceTasks.firstOrNull { !it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Banner with Parirenyatwa & Mpilo Context
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {
                // Generated Banner Image
                Image(
                    painter = painterResource(id = R.drawable.rad_machine_banner_1790540727984),
                    contentDescription = "Radiotherapy Centre",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color(0xFF0F172A).copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Hero Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "ZIMBABWE NATIONAL RADIOTHERAPY",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Parirenyatwa & Mpilo PM Portal",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time telemetry, automated downtime logs & technician alerts",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Hospital Switcher Filter
        item {
            HospitalFilterBar(
                selectedHospital = selectedHospital,
                onSelectHospital = { viewModel.setSelectedHospital(it) }
            )
        }

        // Active Urgent Attention Alert (If breakdown or overdue task exists)
        if (activeBreakdownsCount > 0 || overdueTasksCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("urgent_alert_banner"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (activeBreakdownsCount > 0) StatusCritical.copy(alpha = 0.12f)
                        else StatusWarning.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (activeBreakdownsCount > 0) Icons.Default.Error else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (activeBreakdownsCount > 0) StatusCritical else StatusWarning,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (activeBreakdownsCount > 0) "$activeBreakdownsCount Critical Machine Breakdown!"
                                else "$overdueTasksCount Overdue Preventive QA Task",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (activeBreakdownsCount > 0) StatusCritical else Color(0xFFB45309)
                            )
                            Text(
                                text = if (activeBreakdownsCount > 0)
                                    "~$totalPatientsAffected daily cancer patients impacted across centers. Engineers dispatched."
                                else "Clinical safety checks past tolerance deadline. Complete QA now.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Key Metrics Summary Cards
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Fleet Health & Telemetry",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Fleet Uptime
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_card_uptime"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Fleet Uptime", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$averageUptime%",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (averageUptime >= 80) StatusOperational else StatusCritical
                            )
                            Text("$operationalCount / $totalMachines online", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Active Downtimes
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToDowntime() }
                            .testTag("metric_card_downtime"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (activeBreakdownsCount > 0) StatusCritical.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Active Down", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$activeBreakdownsCount",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (activeBreakdownsCount > 0) StatusCritical else StatusOperational
                            )
                            Text(
                                text = if (activeBreakdownsCount > 0) "$totalPatientsAffected patients" else "Zero backlogs",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Overdue PMs
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToMaintenance() }
                            .testTag("metric_card_overdue"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (overdueTasksCount > 0) StatusWarning.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Overdue PM", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$overdueTasksCount",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (overdueTasksCount > 0) StatusWarning else StatusOperational
                            )
                            Text("QA protocols", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Quick Clinical Action Bar
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenLogBreakdown,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCritical),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_log_breakdown_btn")
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Fault", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (nextPendingQA != null) {
                                onStartMaintenance(nextPendingQA)
                            } else {
                                onNavigateToMaintenance()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_run_qa_btn")
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start QA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onOpenDispatchAlert,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_alert_tech_btn")
                    ) {
                        Icon(Icons.Default.CrisisAlert, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Alert SOS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Radiotherapy Equipment Snapshot Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Radiotherapy Equipment (${equipmentList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View All",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { onNavigateToEquipment() }
                        .padding(4.dp)
                        .testTag("view_all_equipment_link")
                )
            }
        }

        // Equipment items list (up to 4 on dashboard)
        items(equipmentList.take(4)) { equip ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onSelectEquipment(equip) }
                    .testTag("dashboard_equip_item_${equip.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = equip.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${equip.locationBunker} • ${equip.hospitalName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(status = equip.status)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Telemetry row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DeviceThermostat,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (equip.chillerTempCelsius > 18.0) StatusWarning else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Chiller: ${String.format(Locale.US, "%.1f", equip.chillerTempCelsius)}°C",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (equip.sf6PressurePsi > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Speed,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (equip.sf6PressurePsi < 30.0) StatusWarning else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SF6: ${String.format(Locale.US, "%.1f", equip.sf6PressurePsi)} psi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = "Uptime ${equip.uptimePercentage}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (equip.uptimePercentage >= 85) StatusOperational else StatusCritical
                        )
                    }
                }
            }
        }

        // Bottom space
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
