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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.EquipmentEntity
import com.example.data.local.entity.TechnicianEntity
import com.example.ui.theme.StatusCritical

@Composable
fun DispatchAlertDialog(
    equipmentList: List<EquipmentEntity>,
    technicianList: List<TechnicianEntity>,
    onDismiss: () -> Unit,
    onDispatch: (
        equipmentId: Long,
        equipmentName: String,
        hospitalId: String,
        hospitalName: String,
        title: String,
        message: String,
        severity: String,
        technicianName: String,
        technicianPhone: String,
        channel: String
    ) -> Unit
) {
    val initialEquip = equipmentList.firstOrNull()
    var selectedEquipId by remember { mutableLongStateOf(initialEquip?.id ?: 1L) }
    var selectedSeverity by remember { mutableStateOf("CRITICAL") }
    var title by remember { mutableStateOf("EMERGENCY: Urgent On-Site Biomedical Response Required") }
    var message by remember {
        mutableStateOf("High-voltage interlock tripped on LINAC gantry. Please report immediately to bunker for emergency diagnostic.")
    }
    var selectedChannel by remember { mutableStateOf("SMS_EMERGENCY") }
    val initialTech = technicianList.firstOrNull { it.isOnCall } ?: technicianList.firstOrNull()
    var selectedTechName by remember { mutableStateOf(initialTech?.name ?: "Eng. Tinashe Moyo") }
    var selectedTechPhone by remember { mutableStateOf(initialTech?.phone ?: "+263 77 214 8831") }

    var equipDropdownExpanded by remember { mutableStateOf(false) }
    var techDropdownExpanded by remember { mutableStateOf(false) }

    val currentEquip = equipmentList.find { it.id == selectedEquipId } ?: initialEquip

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
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
                            text = "Dispatch Technician Alert",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_close_dispatch")
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
                    // Equipment Selection
                    item {
                        Text(
                            text = "Target Radiotherapy Machine",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { equipDropdownExpanded = true }
                                .testTag("dispatch_target_equip_card")
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
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = currentEquip?.hospitalName ?: "",
                                        fontSize = 11.sp,
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
                                        text = { Text("${equip.name} (${equip.hospitalId})") },
                                        onClick = {
                                            selectedEquipId = equip.id
                                            equipDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Technician Target
                    item {
                        Text(
                            text = "Assigned Engineer / Physicist",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { techDropdownExpanded = true }
                                .testTag("dispatch_target_tech_card")
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
                                        text = selectedTechName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = selectedTechPhone,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = techDropdownExpanded,
                                onDismissRequest = { techDropdownExpanded = false }
                            ) {
                                technicianList.forEach { tech ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text("${tech.name} ${if (tech.isOnCall) "• On-Call" else ""}")
                                                Text("${tech.role} • ${tech.hospitalId}", fontSize = 11.sp)
                                            }
                                        },
                                        onClick = {
                                            selectedTechName = tech.name
                                            selectedTechPhone = tech.phone
                                            techDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Severity Row
                    item {
                        Text(
                            text = "Alert Severity",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("CRITICAL", "HIGH", "NORMAL").forEach { sev ->
                                Button(
                                    onClick = { selectedSeverity = sev },
                                    colors = if (selectedSeverity == sev) {
                                        ButtonDefaults.buttonColors(
                                            containerColor = if (sev == "CRITICAL") StatusCritical else MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        ButtonDefaults.outlinedButtonColors()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(sev, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Dispatch Channel
                    item {
                        Text(
                            text = "Broadcast Channel",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "SMS_EMERGENCY" to "SMS Alert",
                                "WHATSAPP_ROSTER" to "WhatsApp",
                                "IN_APP_PUSH" to "App Push"
                            ).forEach { (channelKey, label) ->
                                Button(
                                    onClick = { selectedChannel = channelKey },
                                    colors = if (selectedChannel == channelKey) {
                                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    } else {
                                        ButtonDefaults.outlinedButtonColors()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(label, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Alert Title & Message
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Alert Subject") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dispatch_title_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Emergency Dispatch Message") },
                            minLines = 2,
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dispatch_message_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val equip = currentEquip ?: return@Button
                        onDispatch(
                            equip.id,
                            equip.name,
                            equip.hospitalId,
                            equip.hospitalName,
                            title,
                            message,
                            selectedSeverity,
                            selectedTechName,
                            selectedTechPhone,
                            selectedChannel
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCritical),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_dispatch_alert_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Broadcast Emergency Alert", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
