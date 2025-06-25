package com.project.zeescraper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.zeescraper.log.AppLogger
import com.project.zeescraper.log.LogEntry
import com.project.zeescraper.log.LogLevel
import kotlinx.coroutines.delay

@Composable
fun LogScreen() {
    val logs = remember { mutableStateListOf<LogEntry>() }

    // Refresh logs every 1s
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            logs.clear()
            logs.addAll(AppLogger.logs)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "App Logs",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(onClick = { AppLogger.clear(); logs.clear() }) {
                    Text("Clear Logs")
                }
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(logs) { logEntry ->
                    val color = when (logEntry.level) {
                        LogLevel.INFO -> Color.Gray
                        LogLevel.WARNING -> Color(0xFFFFA000) // Amber
                        LogLevel.ERROR -> Color.Red
                    }
                    Text(
                        text = "[${logEntry.tag}] ${logEntry.message}",
                        color = color,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
