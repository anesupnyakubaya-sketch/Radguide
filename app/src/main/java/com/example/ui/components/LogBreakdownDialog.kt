package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.EquipmentEntity
import com.example.ui.theme.StatusCritical

@Composable
fun LogBreakdownDialog(
    equipmentList: List<EquipmentEntity>,
    preselectedEquipmentId: Long? = null,
    onDismiss: () -> Unit,
    onSubmit: (
        equipmentId: Long,
        subsystem: String,
        severity: String,
        errorCode: String,
        description: String,
        patientsAffected: Int,
        technicianName: String
    ) -> Unit
) {
    val initialEquipId = preselectedEquipmentId ?: equipmentList.firstOrNull()?.id ?: 1L
    var selectedEquipId by remember { mutableLongStateOf(initialEquipId) }
    var selectedSubsystem by remember { mutableStateOf("RF_MICROWAVE_SYSTEM") }
    var selectedSeverity by remember { mutableStateOf("CRITICAL_DOWN") }
    var errorCode by remember { mutableStateOf("ERR-RF-804") }
    var description by remember {
        mutableStateOf("High reflected power trip during beam delivery. Warning tone in control console.")
    }
    var patientsAffected by remember { mutableIntStateOf(35) }
    var selectedTech by remember { mutableStateOf("Eng. Tinashe Moyo") }

    var equipDropdownExpanded by remember { mutableStateOf(false) }
    var subsystemDropdownExpanded by remember { mutableStateOf(false) }
    var severityDropdownExpanded by remember { mutableStateOf(false) }

    val subsystems = listOf(
        "RF_MICROWAVE_SYSTEM" to "RF Microwave / Magnetron / Klystron",
        "CHILLER_COOLING" to "Chiller & Secondary Water Cooling",
        "MLC_COLLIMATOR" to "Multileaf Collimator (MLC) / Gantry",
        "VACUUM_SF6" to "SF6 Gas Dielectric / Vacuum Manifold",
        "PATIENT_COUCH" to "Patient Support Couch Indexing",
        "SAFETY_INTERLOCK" to "Bunker Door / Radiation Interlock",
        "GRID_POWER_UPS" to "Grid Surge / UPS Power System"
    )

    val severities = listOf(
        "CRITICAL_DOWN" to "Critical Breakdown (Machine Down)",
        "MODERATE_DEGRADED" to "Moderate Fault (Degraded Energy)",
        "MINOR_WARNING" to "Minor Warning (Preventive Alert)"
    )

    val currentEquip = equipmentList.find { it.id == selectedEquipId }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = StatusCritical
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Radiotherapy Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StatusCritical
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_close_breakdown")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Automated downtime logging & dispatch to Harare / Bulawayo biomedical roster",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Equipment Selector
                    item {
                        Text(
                            text = "Affected Radiotherapy Unit",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { equipDropdownExpanded = true }
                                .testTag("select_breakdown_equipment_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentEquip?.name ?: "Select Equipment",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = currentEquip?.hospitalName ?: "",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = equipDropdownExpanded,
                                onDismissRequest = { equipDropdownExpanded = false }
                            ) {
                                equipmentList.forEach { equip ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(equip.name, fontWeight = FontWeight.SemiBold)
                                                Text(equip.hospitalName, fontSize = 11.sp)
                                            }
                                        },
                                        onClick = {
                                            selectedEquipId = equip.id
                                            equipDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Subsystem Failure
                    item {
                        Text(
                            text = "Failure Subsystem",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { subsystemDropdownExpanded = true }
                                .testTag("select_subsystem_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = subsystems.find { it.first == selectedSubsystem }?.second ?: selectedSubsystem,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = subsystemDropdownExpanded,
                                onDismissRequest = { subsystemDropdownExpanded = false }
                            ) {
                                subsystems.forEach { (key, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, fontSize = 13.sp) },
                                        onClick = {
                                            selectedSubsystem = key
                                            subsystemDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Severity Selector
                    item {
                        Text(
                            text = "Severity & Impact Level",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { severityDropdownExpanded = true }
                                .testTag("select_severity_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = severities.find { it.first == selectedSeverity }?.second ?: selectedSeverity,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedSeverity == "CRITICAL_DOWN") StatusCritical else MaterialTheme.colorScheme.onSurface
                                )
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = severityDropdownExpanded,
                                onDismissRequest = { severityDropdownExpanded = false }
                            ) {
                                severities.forEach { (key, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, fontSize = 13.sp) },
                                        onClick = {
                                            selectedSeverity = key
                                            severityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Error Code
                    item {
                        OutlinedTextField(
                            value = errorCode,
                            onValueChange = { errorCode = it },
                            label = { Text("Console Error Code") },
                            placeholder = { Text("e.g. ERR-RF-842, ERR-CHILL-102") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("breakdown_error_code_input")
                        )
                    }

                    // Symptom Description
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Fault Symptoms & Clinical Context") },
                            minLines = 2,
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("breakdown_description_input")
                        )
                    }

                    // Estimated Daily Patients Impacted
                    item {
                        OutlinedTextField(
                            value = patientsAffected.toString(),
                            onValueChange = { patientsAffected = it.toIntOrNull() ?: 0 },
                            label = { Text("Scheduled Patients Impacted / Day") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("breakdown_patients_input")
                        )
                    }

                    // Technician Dispatch
                    item {
                        OutlinedTextField(
                            value = selectedTech,
                            onValueChange = { selectedTech = it },
                            label = { Text("Biomedical Engineer to Dispatch") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("breakdown_tech_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom CTA
                Button(
                    onClick = {
                        onSubmit(
                            selectedEquipId,
                            selectedSubsystem,
                            selectedSeverity,
                            errorCode,
                            description,
                            patientsAffected,
                            selectedTech
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusCritical
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_log_breakdown_btn")
                ) {
                    Icon(imageVector = Icons.Default.CrisisAlert, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Downtime & Dispatch Alert", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
