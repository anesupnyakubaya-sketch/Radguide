package com.example.data.local

import com.example.data.local.entity.DowntimeLogEntity
import com.example.data.local.entity.EquipmentEntity
import com.example.data.local.entity.MaintenanceTaskEntity
import com.example.data.local.entity.TechnicianAlertEntity
import com.example.data.local.entity.TechnicianEntity
import java.util.concurrent.TimeUnit

object DatabaseInitializer {

    suspend fun populateInitialData(database: AppDatabase) {
        val count = database.equipmentDao().getCount()
        if (count > 0) return // Already seeded

        val now = System.currentTimeMillis()
        val oneDay = TimeUnit.DAYS.toMillis(1)
        val oneHour = TimeUnit.HOURS.toMillis(1)

        val equipmentList = listOf(
            EquipmentEntity(
                id = 1L,
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                name = "LINAC 1 - Varian Clinac iX",
                model = "Varian Clinac iX Dual Energy",
                serialNumber = "VAR-IX-5421",
                modality = "Dual Photon (6MV/10MV) & 5 Electron Energies (6-15MeV)",
                locationBunker = "Bunker 1 - Radiotherapy Centre",
                status = "OPERATIONAL",
                chillerTempCelsius = 16.2,
                vacuumTorr = 2.1, // 2.1 x 10^-7 Torr
                sf6PressurePsi = 32.4,
                uptimePercentage = 94,
                lastMaintenanceDate = now - (7 * oneDay),
                nextMaintenanceDue = now + (2 * oneHour), // Today!
                primaryTechnicianName = "Eng. Tinashe Moyo",
                notes = "Primary high-throughput machine treating ~60 cancer patients daily. Daily output constancy within ±1.2%."
            ),
            EquipmentEntity(
                id = 2L,
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                name = "LINAC 2 - Varian TrueBeam",
                model = "Varian TrueBeam STx",
                serialNumber = "VAR-TB-9104",
                modality = "High-Definition SRS/SBRT, FFF Beams (6X-FFF, 10X-FFF)",
                locationBunker = "Bunker 2 - Radiotherapy Centre",
                status = "WARNING",
                chillerTempCelsius = 18.4, // Approaching trip point 19.5°C
                vacuumTorr = 2.8,
                sf6PressurePsi = 30.5,
                uptimePercentage = 81,
                lastMaintenanceDate = now - (14 * oneDay),
                nextMaintenanceDue = now - (1 * oneDay), // Overdue
                primaryTechnicianName = "Dr. Farai Chidzero",
                notes = "Chiller secondary loop temperature elevating under continuous clinical load. Filter replacement scheduled."
            ),
            EquipmentEntity(
                id = 3L,
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                name = "Bebig Multisource HDR Brachytherapy",
                model = "Eckert & Ziegler Bebig HDR",
                serialNumber = "BEB-HDR-1120",
                modality = "Cobalt-60 / Iridium-192 High Dose Rate Afterloader",
                locationBunker = "Brachytherapy Suite Room 3",
                status = "OPERATIONAL",
                chillerTempCelsius = 15.0,
                vacuumTorr = 1.0,
                sf6PressurePsi = 0.0,
                uptimePercentage = 98,
                lastMaintenanceDate = now - (5 * oneDay),
                nextMaintenanceDue = now + (3 * oneDay),
                primaryTechnicianName = "Dr. Farai Chidzero",
                notes = "Vital for cervical cancer brachytherapy treatments. Transit drive and optical safety interlocks fully verified."
            ),
            EquipmentEntity(
                id = 4L,
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                name = "Philips Brilliance Big Bore CT Sim",
                model = "Philips Big Bore 16-Slice",
                serialNumber = "PH-CTB-3312",
                modality = "85cm Bore 4D Oncology Virtual Simulation",
                locationBunker = "CT Simulation Suite",
                status = "OPERATIONAL",
                chillerTempCelsius = 15.8,
                vacuumTorr = 1.5,
                sf6PressurePsi = 0.0,
                uptimePercentage = 96,
                lastMaintenanceDate = now - (10 * oneDay),
                nextMaintenanceDue = now + (4 * oneDay),
                primaryTechnicianName = "David Mutasa",
                notes = "Laser repositioning and couch longitudinal indexing verified with phantom."
            ),
            EquipmentEntity(
                id = 5L,
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                name = "LINAC A - Elekta Synergy",
                model = "Elekta Synergy Agility 160-MLC",
                serialNumber = "ELK-SYN-7801",
                modality = "Photon (6MV/10MV) & Electrons (4-12MeV), IMRT/VMAT",
                locationBunker = "Bunker A - Oncology Wing",
                status = "OFFLINE_DOWN",
                chillerTempCelsius = 21.0,
                vacuumTorr = 5.9,
                sf6PressurePsi = 27.2,
                uptimePercentage = 45,
                lastMaintenanceDate = now - (28 * oneDay),
                nextMaintenanceDue = now - (4 * oneDay), // Heavily Overdue
                primaryTechnicianName = "Eng. Sibongile Ndlovu",
                notes = "CRITICAL BREAKDOWN: RF waveguide arcing and magnetron pulse degradation. Patient sessions temporarily suspended pending replacement parts."
            ),
            EquipmentEntity(
                id = 6L,
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                name = "Nucletron MicroSelectron HDR",
                model = "Elekta / Nucletron Digital HDR",
                serialNumber = "NUC-HDR-4402",
                modality = "Ir-192 High Dose Rate 18-Channel Afterloader",
                locationBunker = "Bunker B - GYN Brachy Suite",
                status = "IN_MAINTENANCE",
                chillerTempCelsius = 15.1,
                vacuumTorr = 1.0,
                sf6PressurePsi = 0.0,
                uptimePercentage = 76,
                lastMaintenanceDate = now - (2 * oneDay),
                nextMaintenanceDue = now + (1 * oneDay),
                primaryTechnicianName = "Blessed Masuku",
                notes = "Quarterly transit guide tube inspection and emergency source retraction friction test underway."
            ),
            EquipmentEntity(
                id = 7L,
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                name = "Siemens SOMATOM CT Simulator",
                model = "Siemens SOMATOM Definition AS",
                serialNumber = "SIE-SOM-8841",
                modality = "High-precision Radiation Therapy Simulation CT",
                locationBunker = "Simulation Lab Room 2",
                status = "OPERATIONAL",
                chillerTempCelsius = 16.5,
                vacuumTorr = 1.4,
                sf6PressurePsi = 0.0,
                uptimePercentage = 91,
                lastMaintenanceDate = now - (8 * oneDay),
                nextMaintenanceDue = now + (6 * oneDay),
                primaryTechnicianName = "Eng. Sibongile Ndlovu",
                notes = "HU calibration and geometric distortion checks within IAEA tolerance (<1mm)."
            )
        )
        database.equipmentDao().insertAllEquipment(equipmentList)

        val tasksList = listOf(
            MaintenanceTaskEntity(
                id = 1L,
                equipmentId = 1L,
                equipmentName = "LINAC 1 - Varian Clinac iX",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                title = "Daily Morning Clinical QA (AAPM TG-142)",
                category = "DAILY_QA",
                priority = "CRITICAL",
                dueDate = now + (2 * oneHour),
                isCompleted = false,
                checklistItems = "Door safety interlocks operational|Audio-visual patient monitors functioning|Radiation warning lights functional|Optical Distance Indicator (ODI) accuracy within 1mm|Laser isocenter alignment checked (<1.5mm)|Morning 6MV beam output constancy check (<±2%)"
            ),
            MaintenanceTaskEntity(
                id = 2L,
                equipmentId = 2L,
                equipmentName = "LINAC 2 - Varian TrueBeam",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                title = "Secondary Chiller De-scaling & Water Filter Inspection",
                category = "WEEKLY_CHECK",
                priority = "HIGH",
                dueDate = now - (6 * oneHour), // Overdue
                isCompleted = false,
                checklistItems = "Verify primary & secondary loop coolant levels|Inspect closed-loop de-ionized water resistivity (>3MΩ-cm)|Clean heat exchanger condenser fins|Inspect water filter differential pressure gauge|Check coolant flow sensor alarm cutoff"
            ),
            MaintenanceTaskEntity(
                id = 3L,
                equipmentId = 5L,
                equipmentName = "LINAC A - Elekta Synergy",
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                title = "Monthly Dosimetry & Beam Symmetry Calibration",
                category = "MONTHLY_CALIBRATION",
                priority = "CRITICAL",
                dueDate = now - (3 * oneDay), // Overdue
                isCompleted = false,
                checklistItems = "Set up 1D water phantom / Farmer ionization chamber|Measure 6MV beam central axis output calibration (cGy/MU)|Verify beam flatness & symmetry (<2% across 80% field width)|Electrometer temperature & barometric pressure correction|Confirm monitor chamber dual-channel linearity"
            ),
            MaintenanceTaskEntity(
                id = 4L,
                equipmentId = 3L,
                equipmentName = "Bebig Multisource HDR Brachytherapy",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                title = "Weekly Source Transit & Emergency Retraction Drill",
                category = "WEEKLY_CHECK",
                priority = "HIGH",
                dueDate = now + (1 * oneDay),
                isCompleted = false,
                checklistItems = "Verify channel 1-18 indexer positioning|Perform manual emergency crank return test|Check radiation survey meter at safe container surface (<5μSv/h)|Check audio chime & room radiation warning strobe"
            ),
            MaintenanceTaskEntity(
                id = 5L,
                equipmentId = 5L,
                equipmentName = "LINAC A - Elekta Synergy",
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                title = "Waveguide SF6 Gas Pressure Refill & Leak Test",
                category = "QUARTERLY_OEM",
                priority = "CRITICAL",
                dueDate = now - (2 * oneDay),
                isCompleted = false,
                checklistItems = "Inspect waveguide ceramic window for micro-fissures|Evacuate vacuum manifold & measure baseline Torr|Charge dry dielectric SF6 gas cylinder to 32 psi|Sniff joints with electronic halogen leak detector|Log pressure retention over 4-hour stabilization"
            ),
            MaintenanceTaskEntity(
                id = 6L,
                equipmentId = 6L,
                equipmentName = "Nucletron MicroSelectron HDR",
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                title = "Quarterly Applicator Integrity & Guide Tube QA",
                category = "QUARTERLY_OEM",
                priority = "ROUTINE",
                dueDate = now + (1 * oneDay),
                isCompleted = false,
                checklistItems = "Inspect tandem and ovoid applicators under magnification|Check transfer tube locking pins & friction clutches|Verify treatment planning system decay tables with physicist"
            ),
            MaintenanceTaskEntity(
                id = 7L,
                equipmentId = 1L,
                equipmentName = "LINAC 1 - Varian Clinac iX",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                title = "Weekly Couch Mechanical & MLC Leaf Trajectory QA",
                category = "WEEKLY_CHECK",
                priority = "ROUTINE",
                dueDate = now + (3 * oneDay),
                isCompleted = false,
                checklistItems = "Test table vertical/lateral/longitudinal optical scales|Run DMLC picket fence calibration test on portal imager|Inspect leaf travel motor currents and carriage limits"
            ),
            MaintenanceTaskEntity(
                id = 8L,
                equipmentId = 4L,
                equipmentName = "Philips Brilliance Big Bore CT Sim",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                title = "Monthly CT Number (Hounsfield Unit) Calibration",
                category = "MONTHLY_CALIBRATION",
                priority = "ROUTINE",
                dueDate = now + (5 * oneDay),
                isCompleted = false,
                checklistItems = "Scan electron density phantom (water, air, bone, lung, acrylic)|Verify CT number of water = 0 ± 5 HU|Verify spatial resolution using line-pair insert|Export calibration curves to TPS treatment planning system"
            )
        )
        database.maintenanceTaskDao().insertAllTasks(tasksList)

        val downtimeLogs = listOf(
            DowntimeLogEntity(
                id = 1L,
                equipmentId = 5L,
                equipmentName = "LINAC A - Elekta Synergy",
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                failureSubsystem = "RF_MICROWAVE_SYSTEM",
                severity = "CRITICAL_DOWN",
                errorCode = "ERR-RF-842: Reflected Power Waveguide Trip",
                description = "During morning 10MV high-energy calibration, the magnetron reflected power exceeded threshold causing immediate beam interlock. High arcing noise noted in gantry wave guide.",
                startTime = now - (2 * oneDay + 4 * oneHour),
                resolvedTime = null,
                isResolved = false,
                estimatedDailyPatientsAffected = 42,
                rootCause = "High-voltage breakdown in ceramic RF window due to dielectric SF6 pressure drop to 27 psi.",
                resolutionAction = "Emergency requisition for OEM service engineer and SF6 gas canister sent to Ministry of Health & Child Care procurement.",
                resolvedByTechnician = null
            ),
            DowntimeLogEntity(
                id = 2L,
                equipmentId = 2L,
                equipmentName = "LINAC 2 - Varian TrueBeam",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                failureSubsystem = "CHILLER_COOLING",
                severity = "MODERATE_DEGRADED",
                errorCode = "WARN-CHILL-204: Secondary Coolant Temp High (18.4°C)",
                description = "Chiller secondary loop water temperature elevated to 18.4°C under sustained midday patient load. Safety trip set point is 19.5°C.",
                startTime = now - (6 * oneHour),
                resolvedTime = null,
                isResolved = false,
                estimatedDailyPatientsAffected = 20,
                rootCause = "Partially clogged external heat exchanger condenser fins and scaling on inline particulate strainer.",
                resolutionAction = "Technician dispatched to flush secondary loop and clean condenser radiator fins.",
                resolvedByTechnician = null
            ),
            DowntimeLogEntity(
                id = 3L,
                equipmentId = 1L,
                equipmentName = "LINAC 1 - Varian Clinac iX",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                failureSubsystem = "MLC_COLLIMATOR",
                severity = "MODERATE_DEGRADED",
                errorCode = "ERR-MLC-112: Bank B Leaf 42 Motor Jam",
                description = "Millennium 120 MLC reported position mismatch on leaf #42 during IMRT field transition. Machine auto-interlocked.",
                startTime = now - (3 * oneDay),
                resolvedTime = now - (2 * oneDay + 18 * oneHour),
                isResolved = true,
                estimatedDailyPatientsAffected = 15,
                rootCause = "Carbon dust accumulation on leaf encoder optical pickup and fatigued micro-drive motor.",
                resolutionAction = "Cleaned optical sensor track with isopropyl alcohol, replaced leaf 42 drive motor from local spare parts inventory, and recalibrated with Leaf Motion test.",
                resolvedByTechnician = "Eng. Tinashe Moyo"
            )
        )
        database.downtimeLogDao().insertAllLogs(downtimeLogs)

        val techniciansList = listOf(
            TechnicianEntity(
                id = 1L,
                name = "Eng. Tinashe Moyo",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                role = "Lead Biomedical Engineer",
                phone = "+263 77 214 8831",
                email = "t.moyo@pari.org.zw",
                isOnCall = true,
                shift = "Day / Emergency Call"
            ),
            TechnicianEntity(
                id = 2L,
                name = "Dr. Farai Chidzero",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                role = "Chief Medical Physicist",
                phone = "+263 71 390 1254",
                email = "f.chidzero@pari.org.zw",
                isOnCall = true,
                shift = "On-site Supervision"
            ),
            TechnicianEntity(
                id = 3L,
                name = "David Mutasa",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                role = "Senior Radiotherapy Technologist",
                phone = "+263 77 482 9910",
                email = "d.mutasa@pari.org.zw",
                isOnCall = false,
                shift = "Morning Shift"
            ),
            TechnicianEntity(
                id = 4L,
                name = "Eng. Sibongile Ndlovu",
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                role = "Senior Radiotherapy Biomedical Engineer",
                phone = "+263 77 503 6629",
                email = "s.ndlovu@mpilo.org.zw",
                isOnCall = true,
                shift = "Emergency Response"
            ),
            TechnicianEntity(
                id = 5L,
                name = "Blessed Masuku",
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                role = "Clinical Medical Physicist (Brachy Specialist)",
                phone = "+263 78 211 4055",
                email = "b.masuku@mpilo.org.zw",
                isOnCall = true,
                shift = "Full Day Shift"
            )
        )
        database.technicianDao().insertAll(techniciansList)

        val alertsList = listOf(
            TechnicianAlertEntity(
                id = 1L,
                equipmentId = 5L,
                equipmentName = "LINAC A - Elekta Synergy (Mpilo)",
                hospitalId = "MPILO",
                hospitalName = "Mpilo Central Hospital (Bulawayo)",
                title = "CRITICAL BREAKDOWN: RF Waveguide Arcing / Vacuum Trip",
                message = "Elekta Synergy Bunker A beam interlocked during 10MV delivery. Magnetron reflected power exceeded trip limit. 42 scheduled cancer patients unable to receive treatment today. Urgent engineer dispatch required.",
                severity = "CRITICAL",
                timestamp = now - (2 * oneDay + 4 * oneHour),
                status = "ACTIVE",
                assignedTechnicianName = "Eng. Sibongile Ndlovu",
                assignedTechnicianPhone = "+263 77 503 6629",
                channelSent = "SMS_EMERGENCY"
            ),
            TechnicianAlertEntity(
                id = 2L,
                equipmentId = 2L,
                equipmentName = "LINAC 2 - Varian TrueBeam (Parirenyatwa)",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                title = "PREVENTIVE WARNING: Chiller Secondary Temp At 18.4°C",
                message = "High ambient heat and flow resistance in heat exchanger secondary loop. Temperature reaching 18.4°C. Perform filter backwash and fin cleaning before safety auto-trip at 19.5°C.",
                severity = "HIGH",
                timestamp = now - (6 * oneHour),
                status = "ACKNOWLEDGED",
                assignedTechnicianName = "Eng. Tinashe Moyo",
                assignedTechnicianPhone = "+263 77 214 8831",
                channelSent = "WHATSAPP_ROSTER"
            ),
            TechnicianAlertEntity(
                id = 3L,
                equipmentId = 1L,
                equipmentName = "LINAC 1 - Varian Clinac iX (Parirenyatwa)",
                hospitalId = "PARIRENYATWA",
                hospitalName = "Parirenyatwa Group of Hospitals (Harare)",
                title = "REMINDER: Daily TG-142 Morning QA Required",
                message = "Mandatory safety interlock, laser isocenter (<1mm), and 6MV output constancy check must be signed off by physicist before commencing morning clinical sessions.",
                severity = "NORMAL",
                timestamp = now - (1 * oneHour),
                status = "ACTIVE",
                assignedTechnicianName = "Dr. Farai Chidzero",
                assignedTechnicianPhone = "+263 71 390 1254",
                channelSent = "IN_APP_PUSH"
            )
        )
        database.technicianAlertDao().insertAllAlerts(alertsList)
    }
}
