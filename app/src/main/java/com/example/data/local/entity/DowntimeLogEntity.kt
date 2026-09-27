package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downtime_logs")
data class DowntimeLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipmentId: Long,
    val equipmentName: String,
    val hospitalId: String,
    val hospitalName: String,
    val failureSubsystem: String, // "RF_MICROWAVE_SYSTEM", "CHILLER_COOLING", "MLC_COLLIMATOR", "VACUUM_SF6", "PATIENT_COUCH", "SAFETY_INTERLOCK", "GRID_POWER_UPS"
    val severity: String, // "CRITICAL_DOWN", "MODERATE_DEGRADED", "MINOR_WARNING"
    val errorCode: String,
    val description: String,
    val startTime: Long,
    val resolvedTime: Long? = null,
    val isResolved: Boolean = false,
    val estimatedDailyPatientsAffected: Int,
    val rootCause: String? = null,
    val resolutionAction: String? = null,
    val resolvedByTechnician: String? = null
)
