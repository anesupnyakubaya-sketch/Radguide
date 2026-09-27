package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusMaintenance
import com.example.ui.theme.StatusOperational
import com.example.ui.theme.StatusWarning

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (label, bgColor, textColor, dotColor) = when (status.uppercase()) {
        "OPERATIONAL" -> Quadruple(
            "Operational",
            StatusOperational.copy(alpha = 0.15f),
            StatusOperational,
            StatusOperational
        )
        "WARNING" -> Quadruple(
            "Needs Service",
            StatusWarning.copy(alpha = 0.15f),
            Color(0xFFB45309),
            StatusWarning
        )
        "OFFLINE_DOWN" -> Quadruple(
            "Machine Down",
            StatusCritical.copy(alpha = 0.15f),
            StatusCritical,
            StatusCritical
        )
        "IN_MAINTENANCE" -> Quadruple(
            "In Maintenance",
            StatusMaintenance.copy(alpha = 0.15f),
            StatusMaintenance,
            StatusMaintenance
        )
        "ACTIVE" -> Quadruple(
            "Active Alert",
            StatusCritical.copy(alpha = 0.15f),
            StatusCritical,
            StatusCritical
        )
        "ACKNOWLEDGED" -> Quadruple(
            "Acknowledged",
            StatusWarning.copy(alpha = 0.15f),
            Color(0xFFB45309),
            StatusWarning
        )
        "RESOLVED" -> Quadruple(
            "Resolved",
            StatusOperational.copy(alpha = 0.15f),
            StatusOperational,
            StatusOperational
        )
        else -> Quadruple(
            status,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.primary
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
