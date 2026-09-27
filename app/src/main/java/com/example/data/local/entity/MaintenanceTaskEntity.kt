package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_tasks")
data class MaintenanceTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipmentId: Long,
    val equipmentName: String,
    val hospitalId: String,
    val hospitalName: String,
    val title: String,
    val category: String, // "DAILY_QA", "WEEKLY_CHECK", "MONTHLY_CALIBRATION", "QUARTERLY_OEM", "ANNUAL_OVERHAUL"
    val priority: String, // "ROUTINE", "HIGH", "CRITICAL"
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val completedDate: Long? = null,
    val completedBy: String? = null,
    val technicianNotes: String? = null,
    val checklistItems: String // Separated by '|'
)
