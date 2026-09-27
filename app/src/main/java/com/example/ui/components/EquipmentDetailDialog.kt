package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.EquipmentEntity
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusOperational
import com.example.ui.theme.StatusWarning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EquipmentDetailDialog(
    equipment: EquipmentEntity,
    onDismiss: () -> Unit,
    onUpdateStatus: (newStatus: String) -> Unit,
    onUpdateTelemetry: (temp: Double, pressure: Double) -> Unit,
    onLogFault: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    var currentTemp by remember { mutableDoubleStateOf(equipment.chillerTempCelsius) }
    var currentPressure by remember { mutableDoubleStateOf(equipment.sf6PressurePsi) }
    var showTelemetryAdjuster by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = equipment.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${equipment.locationBunker} • ${equipment.hospitalName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_close_equip_detail")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Status and Uptime row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(status = equipment.status)
                            Text(
                                text = "30-Day Uptime: ${equipment.uptimePercentage}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (equipment.uptimePercentage >= 85) StatusOperational else StatusCritical
                            )
                        }
                    }

                    // Machine Passport specs
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Technical Passport",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Model: ${equipment.model}", fontSize = 12.sp)
                                Text("Serial: ${equipment.serialNumber}", fontSize = 12.sp)
                                Text("Modality: ${equipment.modality}", fontSize = 12.sp)
                                Text("Primary Lead: ${equipment.primaryTechnicianName}", fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Next PM Due: ${dateFormat.format(Date(equipment.nextMaintenanceDue))}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Live Subsystem Telemetry Readings
                    item {
                        Text(
                            text = "Live Subsystem Telemetry",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Chiller Temp Card
                            val tempColor = when {
                                currentTemp > 19.5 -> StatusCritical
                                currentTemp > 18.0 -> StatusWarning
                                else -> StatusOperational
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = tempColor.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.DeviceThermostat,
                                            contentDescription = null,
                                            tint = tempColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Chiller Temp", fontSize = 11.sp, color = tempColor)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", currentTemp)} °C",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = tempColor
                                    )
                                    Text("Normal: 15-18°C", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // SF6 Pressure Card
                            val sf6Color = when {
                                currentPressure < 28.0 && currentPressure > 0 -> StatusCritical
                                currentPressure < 30.0 && currentPressure > 0 -> StatusWarning
                                else -> StatusOperational
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = sf6Color.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Speed,
                                            contentDescription = null,
                                            tint = sf6Color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("SF6 Gas", fontSize = 11.sp, color = sf6Color)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (currentPressure > 0) "${String.format(Locale.US, "%.1f", currentPressure)} psi" else "N/A",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = sf6Color
                                    )
                                    Text("Normal: 30-34 psi", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // Telemetry Adjuster / Calibration Simulator
                    item {
                        OutlinedButton(
                            onClick = { showTelemetryAdjuster = !showTelemetryAdjuster },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("toggle_telemetry_adjuster_btn")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showTelemetryAdjuster) "Hide Sensor Simulator" else "Recalibrate Sensors / Test Telemetry")
                        }

                        if (showTelemetryAdjuster) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Adjust Chiller Temperature: ${String.format(Locale.US, "%.1f", currentTemp)} °C", fontSize = 12.sp)
                                    Slider(
                                        value = currentTemp.toFloat(),
                                        onValueChange = { currentTemp = it.toDouble() },
                                        valueRange = 14f..23f,
                                        modifier = Modifier.testTag("temp_slider")
                                    )

                                    if (equipment.sf6PressurePsi > 0) {
                                        Text("Adjust SF6 Dielectric Pressure: ${String.format(Locale.US, "%.1f", currentPressure)} psi", fontSize = 12.sp)
                                        Slider(
                                            value = currentPressure.toFloat(),
                                            onValueChange = { currentPressure = it.toDouble() },
                                            valueRange = 24f..36f,
                                            modifier = Modifier.testTag("sf6_slider")
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            onUpdateTelemetry(currentTemp, currentPressure)
                                        },
                                        modifier = Modifier
                                            .align(Alignment.End)
                                            .testTag("save_telemetry_changes_btn")
                                    ) {
                                        Text("Save Telemetry")
                                    }
                                }
                            }
                        }
                    }

                    // Machine Clinical Notes
                    item {
                        Text(
                            text = "Clinical & Operational Notes",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = equipment.notes,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Quick Status Overrides
                    item {
                        Text(
                            text = "Update Operating State",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { onUpdateStatus("OPERATIONAL") },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusOperational),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("set_operational_btn")
                            ) {
                                Text("Online", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onUpdateStatus("IN_MAINTENANCE") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("set_maintenance_btn")
                            ) {
                                Text("Service", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onUpdateStatus("OFFLINE_DOWN") },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusCritical),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("set_down_btn")
                            ) {
                                Text("Halt", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Fault Logger Shortcut
                Button(
                    onClick = onLogFault,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCritical),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("log_fault_for_machine_btn")
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report Breakdown for this Machine")
                }
            }
        }
    }
}
