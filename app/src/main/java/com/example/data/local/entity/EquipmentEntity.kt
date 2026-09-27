package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hospitalId: String, // "PARIRENYATWA" or "MPILO"
    val hospitalName: String,
    val name: String,
    val model: String,
    val serialNumber: String,
    val modality: String,
    val locationBunker: String,
    val status: String, // "OPERATIONAL", "WARNING", "OFFLINE_DOWN", "IN_MAINTENANCE"
    val chillerTempCelsius: Double,
    val vacuumTorr: Double,
    val sf6PressurePsi: Double,
    val uptimePercentage: Int,
    val lastMaintenanceDate: Long,
    val nextMaintenanceDue: Long,
    val primaryTechnicianName: String,
    val notes: String
)
