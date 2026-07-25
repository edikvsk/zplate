package com.zeneyestudio.zplate.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zeneyestudio.zplate.util.TimeHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    goalCalories: Int,
    goalProtein: Int,
    goalFat: Int,
    goalCarbs: Int,
    breakfastRange: TimeHelper.TimeRange,
    lunchRange: TimeHelper.TimeRange,
    dinnerRange: TimeHelper.TimeRange,
    onGoalCaloriesChange: (Int) -> Unit,
    onGoalProteinChange: (Int) -> Unit,
    onGoalFatChange: (Int) -> Unit,
    onGoalCarbsChange: (Int) -> Unit,
    onTimeRangeChange: (TimeHelper.TimeRange, TimeHelper.TimeRange, TimeHelper.TimeRange) -> Unit,
    onExportCsv: () -> Unit,
    onImportCsv: () -> Unit,
    onClearData: () -> Unit
) {
    var caloriesText by remember { mutableStateOf(goalCalories.toString()) }
    var proteinText by remember { mutableStateOf(goalProtein.toString()) }
    var fatText by remember { mutableStateOf(goalFat.toString()) }
    var carbsText by remember { mutableStateOf(goalCarbs.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Goals section
            Text(
                text = "Цель на день",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = {
                            caloriesText = it
                            it.toIntOrNull()?.let { c -> onGoalCaloriesChange(c) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Калории") },
                        suffix = { Text("ккал") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = proteinText,
                            onValueChange = {
                                proteinText = it
                                it.toIntOrNull()?.let { p -> onGoalProteinChange(p) }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Белки") },
                            suffix = { Text("г") },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = fatText,
                            onValueChange = {
                                fatText = it
                                it.toIntOrNull()?.let { f -> onGoalFatChange(f) }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Жиры") },
                            suffix = { Text("г") },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = carbsText,
                            onValueChange = {
                                carbsText = it
                                it.toIntOrNull()?.let { c -> onGoalCarbsChange(c) }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Углеводы") },
                            suffix = { Text("г") },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Schedule section
            Text(
                text = "Расписание приёмов пищи",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    TimeRow("Завтрак", breakfastRange)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    TimeRow("Обед", lunchRange)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    TimeRow("Ужин", dinnerRange)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Data section
            Text(
                text = "Данные",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Download,
                        title = "Экспорт в CSV",
                        onClick = onExportCsv
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        icon = Icons.Default.Upload,
                        title = "Импорт из CSV",
                        onClick = onImportCsv
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        icon = Icons.Default.Delete,
                        title = "Очистить данные",
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = onClearData
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "МойДневник",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "v1.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TimeRow(label: String, range: TimeHelper.TimeRange) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = TimeHelper.getMealTimeRangeText(range),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = titleColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
