package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.DowntimeLogEntity
import com.example.data.local.entity.EquipmentEntity
import com.example.data.local.entity.MaintenanceTaskEntity
import com.example.data.local.entity.TechnicianAlertEntity
import com.example.data.local.entity.TechnicianEntity
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class RadGuideRepository(private val database: AppDatabase) {

    private val equipmentDao = database.equipmentDao()
    private val maintenanceDao = database.maintenanceTaskDao()
    private val downtimeDao = database.downtimeLogDao()
    private val alertDao = database.technicianAlertDao()
    private val technicianDao = database.technicianDao()

    fun getEquipment(hospitalFilter: String): Flow<List<EquipmentEntity>> {
        return if (hospitalFilter == "ALL") {
            equipmentDao.getAllEquipment()
        } else {
            equipmentDao.getEquipmentByHospital(hospitalFilter)
        }
    }

    fun getEquipmentByIdFlow(id: Long): Flow<EquipmentEntity?> {
        return equipmentDao.getEquipmentByIdFlow(id)
    }

    suspend fun getEquipmentById(id: Long): EquipmentEntity? {
        return equipmentDao.getEquipmentById(id)
    }

    suspend fun updateEquipmentStatus(id: Long, status: String) {
        equipmentDao.updateStatus(id, status)
    }

    suspend fun updateEquipmentTelemetry(id: Long, temp: Double, pressure: Double) {
        equipmentDao.updateTelemetry(id, temp, pressure)
    }

    suspend fun insertEquipment(equipment: EquipmentEntity): Long {
        return equipmentDao.insertEquipment(equipment)
    }

    fun getMaintenanceTasks(hospitalFilter: String): Flow<List<MaintenanceTaskEntity>> {
        return if (hospitalFilter == "ALL") {
            maintenanceDao.getAllTasks()
        } else {
            maintenanceDao.getTasksByHospital(hospitalFilter)
        }
    }

    fun getOverdueCount(currentTime: Long): Flow<Int> {
        return maintenanceDao.getOverdueCount(currentTime)
    }

    suspend fun completeMaintenanceTask(
        taskId: Long,
        completedBy: String,
        notes: String,
        equipmentId: Long,
        taskCategory: String
    ) {
        val now = System.currentTimeMillis()
        maintenanceDao.markTaskCompleted(taskId, now, completedBy, notes)

        // Calculate next maintenance due date based on category
        val nextDueInterval = when (taskCategory) {
            "DAILY_QA" -> TimeUnit.DAYS.toMillis(1)
            "WEEKLY_CHECK" -> TimeUnit.DAYS.toMillis(7)
            "MONTHLY_CALIBRATION" -> TimeUnit.DAYS.toMillis(30)
            "QUARTERLY_OEM" -> TimeUnit.DAYS.toMillis(90)
            "ANNUAL_OVERHAUL" -> TimeUnit.DAYS.toMillis(365)
            else -> TimeUnit.DAYS.toMillis(14)
        }
        val nextDue = now + nextDueInterval

        // Update equipment last maintenance date and ensure status is operational if was in maintenance
        val equipment = equipmentDao.getEquipmentById(equipmentId)
        if (equipment != null) {
            val updated = equipment.copy(
                lastMaintenanceDate = now,
                nextMaintenanceDue = nextDue,
                status = if (equipment.status == "IN_MAINTENANCE") "OPERATIONAL" else equipment.status
            )
            equipmentDao.updateEquipment(updated)
        }
    }

    suspend fun insertMaintenanceTask(task: MaintenanceTaskEntity): Long {
        return maintenanceDao.insertTask(task)
    }

    fun getDowntimeLogs(hospitalFilter: String): Flow<List<DowntimeLogEntity>> {
        return if (hospitalFilter == "ALL") {
            downtimeDao.getAllLogs()
        } else {
            downtimeDao.getLogsByHospital(hospitalFilter)
        }
    }

    fun getActiveDowntimes(): Flow<List<DowntimeLogEntity>> {
        return downtimeDao.getActiveDowntimes()
    }

    suspend fun logEquipmentBreakdown(
        equipmentId: Long,
        failureSubsystem: String,
        severity: String,
        errorCode: String,
        description: String,
        patientsAffectedPerDay: Int,
        technicianToAlert: String?,
        alertSeverity: String = "CRITICAL"
    ): Long {
        val equipment = equipmentDao.getEquipmentById(equipmentId) ?: return -1L
        val now = System.currentTimeMillis()

        // 1. Create downtime log
        val log = DowntimeLogEntity(
            equipmentId = equipmentId,
            equipmentName = equipment.name,
            hospitalId = equipment.hospitalId,
            hospitalName = equipment.hospitalName,
            failureSubsystem = failureSubsystem,
            severity = severity,
            errorCode = errorCode,
            description = description,
            startTime = now,
            estimatedDailyPatientsAffected = patientsAffectedPerDay,
            isResolved = false
        )
        val logId = downtimeDao.insertLog(log)

        // 2. Set machine status to OFFLINE_DOWN or WARNING depending on severity
        val newStatus = if (severity == "CRITICAL_DOWN") "OFFLINE_DOWN" else "WARNING"
        equipmentDao.updateStatus(equipmentId, newStatus)

        // 3. Dispatch automated technician alert
        val techName = technicianToAlert ?: equipment.primaryTechnicianName
        val alert = TechnicianAlertEntity(
            equipmentId = equipmentId,
            equipmentName = "${equipment.name} (${equipment.hospitalName})",
            hospitalId = equipment.hospitalId,
            hospitalName = equipment.hospitalName,
            title = "AUTO-ALERT: $failureSubsystem Breakdown ($errorCode)",
            message = "Equipment failure logged: $description. Severity: $severity. Estimated daily patients impacted: $patientsAffectedPerDay.",
            severity = alertSeverity,
            timestamp = now,
            status = "ACTIVE",
            assignedTechnicianName = techName,
            assignedTechnicianPhone = "+263 77 000 0000",
            channelSent = "SMS_EMERGENCY"
        )
        alertDao.insertAlert(alert)

        return logId
    }

    suspend fun resolveDowntime(
        logId: Long,
        equipmentId: Long,
        rootCause: String,
        actionTaken: String,
        technician: String
    ) {
        val now = System.currentTimeMillis()
        downtimeDao.resolveDowntime(
            id = logId,
            resolvedTime = now,
            rootCause = rootCause,
            action = actionTaken,
            tech = technician
        )

        // Return machine status to OPERATIONAL
        equipmentDao.updateStatus(equipmentId, "OPERATIONAL")
    }

    fun getTechnicianAlerts(hospitalFilter: String): Flow<List<TechnicianAlertEntity>> {
        return if (hospitalFilter == "ALL") {
            alertDao.getAllAlerts()
        } else {
            alertDao.getAlertsByHospital(hospitalFilter)
        }
    }

    suspend fun acknowledgeAlert(alertId: Long, technicianName: String) {
        alertDao.acknowledgeAlert(alertId, "ACKNOWLEDGED", technicianName)
    }

    suspend fun resolveAlert(alertId: Long) {
        alertDao.updateStatus(alertId, "RESOLVED")
    }

    suspend fun dispatchCustomAlert(alert: TechnicianAlertEntity): Long {
        return alertDao.insertAlert(alert)
    }

    fun getTechnicians(hospitalFilter: String): Flow<List<TechnicianEntity>> {
        return if (hospitalFilter == "ALL") {
            technicianDao.getAllTechnicians()
        } else {
            technicianDao.getTechniciansByHospital(hospitalFilter)
        }
    }

    suspend fun toggleTechnicianOnCall(id: Long, isOnCall: Boolean) {
        technicianDao.updateOnCall(id, isOnCall)
    }
}
