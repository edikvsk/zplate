package com.zeneyestudio.zplate.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    onNameChange: (String) -> Unit,
    onCaloriesChange: (Int) -> Unit,
    onProteinChange: (Float) -> Unit,
    onFatChange: (Float) -> Unit,
    onCarbsChange: (Float) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Прочее") }

    val categories = listOf("Белки", "Жиры", "Углеводы", "Овощи", "Фрукты", "Молочные", "Прочее")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новый продукт") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
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
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    onNameChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Название продукта") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "КБЖУ на 100г",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = calories,
                onValueChange = {
                    calories = it
                    it.toIntOrNull()?.let { c -> onCaloriesChange(c) }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Калории") },
                suffix = { Text("ккал") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = protein,
                    onValueChange = {
                        protein = it
                        it.toFloatOrNull()?.let { p -> onProteinChange(p) }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Белки") },
                    suffix = { Text("г") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fat,
                    onValueChange = {
                        fat = it
                        it.toFloatOrNull()?.let { f -> onFatChange(f) }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Жиры") },
                    suffix = { Text("г") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = carbs,
                    onValueChange = {
                        carbs = it
                        it.toFloatOrNull()?.let { c -> onCarbsChange(c) }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Углеводы") },
                    suffix = { Text("г") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Категория",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(4).forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = {
                            category = cat
                            onCategoryChange(cat)
                        },
                        label = { Text(cat) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.drop(4).forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = {
                            category = cat
                            onCategoryChange(cat)
                        },
                        label = { Text(cat) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                if (categories.drop(4).size < 4) {
                    repeat(4 - categories.drop(4).size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Сохранить",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
