package com.example.traveloop

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelloop.models.PackingItemDto
import com.example.travelloop.models.TripNoteDto

// ─── Budget Screen ────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    tripId: Int,
    onBack: () -> Unit,
    vm: BudgetViewModel = viewModel()
) {
    val summary by vm.summary.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    LaunchedEffect(tripId) { vm.load(tripId) }

    Scaffold(
        topBar = { TraveloopTopBar(title = "Budget", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = Amber400,
                contentColor = Slate900,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, null) }
        }
    ) { padding ->
        if (loading) { LoadingContent(); return@Scaffold }
        error?.let { ErrorMessage(it); return@Scaffold }

        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            summary?.let { s ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Budget Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                BudgetStat("Total Budget", "$${s.totalBudget}", Amber400)
                                BudgetStat("Spent", "$${s.totalSpent}", ErrorRed)
                                BudgetStat("Remaining", "$${s.remaining}", SuccessGreen)
                            }
                            Spacer(Modifier.height(16.dp))
                            if (s.totalBudget > 0) {
                                val progress = (s.totalSpent / s.totalBudget).coerceIn(0.0, 1.0).toFloat()
                                val color = when {
                                    progress > 0.9f -> ErrorRed
                                    progress > 0.7f -> Amber400
                                    else -> SuccessGreen
                                }
                                Text("${(progress * 100).toInt()}% used", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = color,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeCap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                if (s.byCategory.isNotEmpty()) {
                    item {
                        Text("By Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                s.byCategory.entries.forEachIndexed { i, (cat, amt) ->
                                    if (i > 0) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(cat.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium)
                                        Text("$$amt", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Amber400)
                                    }
                                }
                            }
                        }
                    }
                }

                if (s.items.isNotEmpty()) {
                    item { Text("All Expenses", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
                    items(s.items) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    Text(item.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("$${item.amount}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Amber400)
                                Spacer(Modifier.width(8.dp))
                                IconButton(onClick = { vm.deleteItem(tripId, item.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, null, tint = ErrorRed.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            } ?: item {
                EmptyState(icon = Icons.Default.AccountBalanceWallet, title = "No budget items yet", subtitle = "Tap + to add an expense")
            }
        }
    }

    if (showAdd) {
        AddBudgetItemSheet(
            onDismiss = { showAdd = false },
            onConfirm = { cat, label, amount ->
                vm.addItem(tripId, cat, label, amount)
                showAdd = false
            }
        )
    }
}

@Composable
private fun BudgetStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBudgetItemSheet(onDismiss: () -> Unit, onConfirm: (String, String, Double) -> Unit) {
    var category by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val categories = listOf("Transport", "Stay", "Food", "Activities", "Shopping", "Other")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Add Expense", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Text("Category", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.forEach { c ->
                    FilterChip(selected = category == c, onClick = { category = if (category == c) "" else c }, label = { Text(c, style = MaterialTheme.typography.labelSmall) })
                }
            }
            Spacer(Modifier.height(12.dp))
            TraveloopTextField(value = label, onValueChange = { label = it }, label = "Description", leadingIcon = Icons.Default.Notes)
            Spacer(Modifier.height(12.dp))
            TraveloopTextField(value = amount, onValueChange = { amount = it }, label = "Amount ($)", leadingIcon = Icons.Default.AttachMoney)
            Spacer(Modifier.height(24.dp))
            TraveloopButton(
                text = "Add Expense",
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (label.isNotBlank()) onConfirm(category.ifBlank { "Other" }, label, amt)
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.Add
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingListScreen(
    tripId: Int,
    onBack: () -> Unit,
    vm: PackingViewModel = viewModel()
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    LaunchedEffect(tripId) { vm.load(tripId) }

    val packed = items.count { it.isPacked }
    val total = items.size

    Scaffold(
        topBar = {
            TraveloopTopBar(title = "Packing List", onBack = onBack, actions = {
                if (items.isNotEmpty()) {
                    TextButton(onClick = { vm.reset(tripId) }) {
                        Text("Reset", color = Amber400)
                    }
                }
            })
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = Amber400,
                contentColor = Slate900,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, null) }
        }
    ) { padding ->
        if (loading) { LoadingContent(); return@Scaffold }

        Column(modifier = Modifier.padding(padding)) {
            if (total > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$packed / $total packed", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text("${if (total > 0) (packed * 100 / total) else 0}%", style = MaterialTheme.typography.labelMedium, color = SuccessGreen)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { if (total > 0) packed.toFloat() / total else 0f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = SuccessGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }

            if (items.isEmpty()) {
                EmptyState(icon = Icons.Default.CheckBox, title = "Nothing to pack yet", subtitle = "Add items to your packing list")
            } else {
                val grouped = items.groupBy { it.category ?: "Uncategorized" }
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    grouped.forEach { (cat, catItems) ->
                        item {
                            Text(
                                text = cat.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(catItems, key = { it.id }) { item ->
                            PackingItemRow(item = item, onToggle = { vm.toggle(tripId, item.id) }, onDelete = { vm.delete(tripId, item.id) })
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showAdd) {
        AddPackingItemSheet(
            onDismiss = { showAdd = false },
            onConfirm = { name, cat ->
                vm.add(tripId, name, cat.takeIf { it.isNotBlank() })
                showAdd = false
            }
        )
    }
}

@Composable
private fun PackingItemRow(item: PackingItemDto, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPacked) SuccessGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = item.isPacked, onCheckedChange = { onToggle() }, colors = CheckboxDefaults.colors(checkedColor = SuccessGreen))
            Text(
                text = item.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = if (item.isPacked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, null, tint = ErrorRed.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPackingItemSheet(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("") }
    val cats = listOf("Clothing", "Documents", "Electronics", "Toiletries", "Medicine", "Other")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Add Item", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            TraveloopTextField(value = name, onValueChange = { name = it }, label = "Item name", leadingIcon = Icons.Default.Add)
            Spacer(Modifier.height(12.dp))
            Text("Category", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                cats.forEach { c ->
                    FilterChip(selected = cat == c, onClick = { cat = if (cat == c) "" else c }, label = { Text(c, style = MaterialTheme.typography.labelSmall) })
                }
            }
            Spacer(Modifier.height(24.dp))
            TraveloopButton("Add Item", { if (name.isNotBlank()) onConfirm(name.trim(), cat) }, modifier = Modifier.fillMaxWidth(), icon = Icons.Default.Add)
            Spacer(Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    tripId: Int,
    onBack: () -> Unit,
    vm: NotesViewModel = viewModel()
) {
    val notes by vm.notes.collectAsState()
    val loading by vm.loading.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<TripNoteDto?>(null) }

    LaunchedEffect(tripId) { vm.load(tripId) }

    Scaffold(
        topBar = { TraveloopTopBar(title = "Trip Notes", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = Color(0xFFEC4899),
                contentColor = White,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, null) }
        }
    ) { padding ->
        if (loading) { LoadingContent(); return@Scaffold }

        if (notes.isEmpty()) {
            EmptyState(icon = Icons.Default.Notes, title = "No notes yet", subtitle = "Jot down trip reminders, ideas, or check-in info")
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteCard(note = note, onEdit = { editingNote = note }, onDelete = { vm.delete(tripId, note.id) })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showAdd) {
        NoteSheet(
            existing = null,
            onDismiss = { showAdd = false },
            onSave = { content -> vm.create(tripId, content); showAdd = false }
        )
    }

    editingNote?.let { note ->
        NoteSheet(
            existing = note.content,
            onDismiss = { editingNote = null },
            onSave = { content -> vm.update(tripId, note.id, content); editingNote = null }
        )
    }
}

@Composable
private fun NoteCard(note: TripNoteDto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(note.updatedAt.take(10), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, null, tint = Teal600, modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, null, tint = ErrorRed.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteSheet(existing: String?, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var content by remember { mutableStateOf(existing ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(if (existing == null) "New Note" else "Edit Note", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth().height(160.dp),
                label = { Text("Write your note\u2026") },
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(24.dp))
            TraveloopButton(
                text = "Save Note",
                onClick = { if (content.isNotBlank()) onSave(content.trim()) },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.Save
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}