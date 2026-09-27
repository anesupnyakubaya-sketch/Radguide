package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.DowntimeLogEntity
import com.example.data.local.entity.EquipmentEntity
import com.example.data.local.entity.MaintenanceTaskEntity
import com.example.data.local.entity.TechnicianAlertEntity
import com.example.data.local.entity.TechnicianEntity
import com.example.data.repository.RadGuideRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RadGuideViewModel(private val repository: RadGuideRepository) : ViewModel() {

    private val _selectedHospital = MutableStateFlow("ALL") // "ALL", "PARIRENYATWA", "MPILO"
    val selectedHospital: StateFlow<String> = _selectedHospital.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun setSelectedHospital(hospital: String) {
        _selectedHospital.value = hospital
    }

    val equipmentList: StateFlow<List<EquipmentEntity>> = _selectedHospital
        .flatMapLatest { hospital -> repository.getEquipment(hospital) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val maintenanceTasks: StateFlow<List<MaintenanceTaskEntity>> = _selectedHospital
        .flatMapLatest { hospital -> repository.getMaintenanceTasks(hospital) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val downtimeLogs: StateFlow<List<DowntimeLogEntity>> = _selectedHospital
        .flatMapLatest { hospital -> repository.getDowntimeLogs(hospital) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val technicianAlerts: StateFlow<List<TechnicianAlertEntity>> = _selectedHospital
        .flatMapLatest { hospital -> repository.getTechnicianAlerts(hospital) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val technicians: StateFlow<List<TechnicianEntity>> = _selectedHospital
        .flatMapLatest { hospital -> repository.getTechnicians(hospital) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Fleet Summary Metrics
    val activeBreakdownsCount = downtimeLogs.flatMapLatest { logs ->
        MutableStateFlow(logs.count { !it.isResolved })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val overdueTasksCount = maintenanceTasks.flatMapLatest { tasks ->
        val now = System.currentTimeMillis()
        MutableStateFlow(tasks.count { !it.isCompleted && it.dueDate < now })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeAlertsCount = technicianAlerts.flatMapLatest { alerts ->
        MutableStateFlow(alerts.count { it.status == "ACTIVE" })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPatientsAffected = downtimeLogs.flatMapLatest { logs ->
        val sum = logs.filter { !it.isResolved }.sumOf { it.estimatedDailyPatientsAffected }
        MutableStateFlow(sum)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun completeMaintenanceTask(
        taskId: Long,
        completedBy: String,
        notes: String,
        equipmentId: Long,
        taskCategory: String
    ) {
        viewModelScope.launch {
            repository.completeMaintenanceTask(
                taskId = taskId,
                completedBy = completedBy,
                notes = notes,
                equipmentId = equipmentId,
                taskCategory = taskCategory
            )
            _userMessage.value = "Maintenance checklist verified and signed off. Equipment schedule updated."
        }
    }

    fun logEquipmentBreakdown(
        equipmentId: Long,
        failureSubsystem: String,
        severity: String,
        errorCode: String,
        description: String,
        patientsAffectedPerDay: Int,
        technicianToAlert: String?
    ) {
        viewModelScope.launch {
            repository.logEquipmentBreakdown(
                equipmentId = equipmentId,
                failureSubsystem = failureSubsystem,
                severity = severity,
                errorCode = errorCode,
                description = description,
                patientsAffectedPerDay = patientsAffectedPerDay,
                technicianToAlert = technicianToAlert
            )
            _userMessage.value = "Downtime logged & emergency alert dispatched to on-call technician."
        }
    }

    fun resolveDowntime(
        logId: Long,
        equipmentId: Long,
        rootCause: String,
        actionTaken: String,
        technician: String
    ) {
        viewModelScope.launch {
            repository.resolveDowntime(
                logId = logId,
                equipmentId = equipmentId,
                rootCause = rootCause,
                actionTaken = actionTaken,
                technician = technician
            )
            _userMessage.value = "Equipment fault resolved! Status restored to OPERATIONAL."
        }
    }

    fun acknowledgeAlert(alertId: Long, technicianName: String) {
        viewModelScope.launch {
            repository.acknowledgeAlert(alertId, technicianName)
            _userMessage.value = "Alert acknowledged by $technicianName. Response logged."
        }
    }

    fun resolveAlert(alertId: Long) {
        viewModelScope.launch {
            repository.resolveAlert(alertId)
            _userMessage.value = "Alert marked as RESOLVED."
        }
    }

    fun dispatchCustomAlert(
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
    ) {
        viewModelScope.launch {
            val alert = TechnicianAlertEntity(
                equipmentId = equipmentId,
                equipmentName = equipmentName,
                hospitalId = hospitalId,
                hospitalName = hospitalName,
                title = title,
                message = message,
                severity = severity,
                timestamp = System.currentTimeMillis(),
                status = "ACTIVE",
                assignedTechnicianName = technicianName,
                assignedTechnicianPhone = technicianPhone,
                channelSent = channel
            )
            repository.dispatchCustomAlert(alert)
            _userMessage.value = "Urgent $severity alert sent via $channel to $technicianName."
        }
    }

    fun updateEquipmentStatus(equipmentId: Long, status: String) {
        viewModelScope.launch {
            repository.updateEquipmentStatus(equipmentId, status)
            _userMessage.value = "Machine status updated to $status."
        }
    }

    fun updateEquipmentTelemetry(equipmentId: Long, temp: Double, pressure: Double) {
        viewModelScope.launch {
            repository.updateEquipmentTelemetry(equipmentId, temp, pressure)
            _userMessage.value = "Telemetry parameters recalibrated: Chiller ${temp}°C, SF6 ${pressure} psi."
        }
    }

    fun addMaintenanceTask(task: MaintenanceTaskEntity) {
        viewModelScope.launch {
            repository.insertMaintenanceTask(task)
            _userMessage.value = "New preventive maintenance protocol scheduled."
        }
    }

    fun toggleTechnicianOnCall(techId: Long, currentOnCall: Boolean) {
        viewModelScope.launch {
            repository.toggleTechnicianOnCall(techId, !currentOnCall)
        }
    }
}

class RadGuideViewModelFactory(private val repository: RadGuideRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RadGuideViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RadGuideViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
