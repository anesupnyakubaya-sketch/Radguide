package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technician_alerts")
data class TechnicianAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipmentId: Long,
    val equipmentName: String,
    val hospitalId: String,
    val hospitalName: String,
    val title: String,
    val message: String,
    val severity: String, // "CRITICAL", "HIGH", "NORMAL"
    val timestamp: Long,
    val status: String = "ACTIVE", // "ACTIVE", "ACKNOWLEDGED", "RESOLVED"
    val assignedTechnicianName: String,
    val assignedTechnicianPhone: String,
    val channelSent: String // "SMS_EMERGENCY", "WHATSAPP_ROSTER", "IN_APP_PUSH"
)
