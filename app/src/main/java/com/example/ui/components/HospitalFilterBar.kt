package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun HospitalFilterBar(
    selectedHospital: String,
    onSelectHospital: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedHospital == "ALL",
                onClick = { onSelectHospital("ALL") },
                label = { Text("National Fleet (All)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "All Zimbabwe Hospitals",
                        modifier = Modifier.padding(2.dp)
                    )
                },
                modifier = Modifier.testTag("filter_all_hospitals"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        item {
            FilterChip(
                selected = selectedHospital == "PARIRENYATWA",
                onClick = { onSelectHospital("PARIRENYATWA") },
                label = { Text("Parirenyatwa (Harare)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = "Parirenyatwa Hospital",
                        modifier = Modifier.padding(2.dp)
                    )
                },
                modifier = Modifier.testTag("filter_pari_hospital"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        item {
            FilterChip(
                selected = selectedHospital == "MPILO",
                onClick = { onSelectHospital("MPILO") },
                label = { Text("Mpilo (Bulawayo)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = "Mpilo Central Hospital",
                        modifier = Modifier.padding(2.dp)
                    )
                },
                modifier = Modifier.testTag("filter_mpilo_hospital"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}
