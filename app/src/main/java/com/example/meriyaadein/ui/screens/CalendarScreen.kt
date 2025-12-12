package com.example.meriyaadein.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.ui.components.DiaryCard
import com.example.meriyaadein.ui.components.EmptyState
import com.example.meriyaadein.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Calendar screen to view entries by date
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    entryDates: List<Long>,
    entriesForSelectedDate: List<DiaryEntry>,
    onDateSelected: (Long) -> Unit,
    onEntryClick: (DiaryEntry) -> Unit,
    onFavoriteClick: (DiaryEntry) -> Unit,
    onLockClick: (DiaryEntry) -> Unit,
    onDeleteClick: (DiaryEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendar",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = VelvetBurgundy
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CreamPaper
                )
            )
        },
        containerColor = CreamPaper,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Month Navigation
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlushMist.copy(alpha = 0.3f)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            currentMonth = (currentMonth.clone() as Calendar).apply {
                                add(Calendar.MONTH, -1)
                            }
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Month",
                                tint = VelvetBurgundy
                            )
                        }
                        
                        Text(
                            text = monthFormat.format(currentMonth.time),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = VelvetBurgundy
                        )
                        
                        IconButton(onClick = {
                            currentMonth = (currentMonth.clone() as Calendar).apply {
                                add(Calendar.MONTH, 1)
                            }
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Month",
                                tint = VelvetBurgundy
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Day Headers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                color = CharcoalSlate.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Calendar Grid
                    CalendarGrid(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        entryDates = entryDates,
                        onDateClick = { date ->
                            selectedDate = date
                            onDateSelected(date)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Entries for selected date
            Text(
                text = "Memories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = CharcoalSlate
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (entriesForSelectedDate.isEmpty()) {
                EmptyState(
                    title = "No memories",
                    subtitle = "No memories recorded for this date"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(entriesForSelectedDate, key = { it.id }) { entry ->
                        DiaryCard(
                            entry = entry,
                            index = 0, // Index not strictly needed in calendar view
                            onClick = { onEntryClick(entry) },
                            onFavoriteClick = { onFavoriteClick(entry) },
                            onLockClick = { onLockClick(entry) },
                            onDeleteClick = { onDeleteClick(entry) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: Calendar,
    selectedDate: Long,
    entryDates: List<Long>,
    onDateClick: (Long) -> Unit
) {
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = (currentMonth.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }.get(Calendar.DAY_OF_WEEK) - 1
    
    val totalCells = ((daysInMonth + firstDayOfMonth + 6) / 7) * 7
    
    val entryDateSet = remember(entryDates) {
        entryDates.map { date ->
            val cal = Calendar.getInstance().apply { timeInMillis = date }
            Triple(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        }.toSet()
    }
    
    val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDate }
    
    Column {
        for (week in 0 until (totalCells / 7)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) {
                    val cellIndex = week * 7 + dayOfWeek
                    val dayNumber = cellIndex - firstDayOfMonth + 1
                    
                    if (dayNumber in 1..daysInMonth) {
                        val dateCalendar = (currentMonth.clone() as Calendar).apply {
                            set(Calendar.DAY_OF_MONTH, dayNumber)
                        }
                        val hasEntry = entryDateSet.contains(
                            Triple(
                                dateCalendar.get(Calendar.YEAR),
                                dateCalendar.get(Calendar.MONTH),
                                dayNumber
                            )
                        )
                        val isSelected = selectedCal.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
                                selectedCal.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH) &&
                                selectedCal.get(Calendar.DAY_OF_MONTH) == dayNumber
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> VelvetBurgundy
                                        hasEntry -> DustyRose.copy(alpha = 0.5f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onDateClick(dateCalendar.timeInMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) CreamPaper else CharcoalSlate
                                )
                                if (hasEntry && !isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(VelvetBurgundy)
                                    )
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}
