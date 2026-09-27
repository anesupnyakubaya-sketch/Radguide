package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technicians")
data class TechnicianEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val hospitalId: String,
    val hospitalName: String,
    val role: String, // "Biomedical Engineer", "Chief Medical Physicist", "OEM Service Engineer", "Radiotherapy Technologist"
    val phone: String,
    val email: String,
    val isOnCall: Boolean = true,
    val shift: String = "Day Shift"
)
