package com.project.zeescraper.log

import android.util.Log

enum class LogLevel { INFO, WARNING, ERROR }

data class LogEntry(val level: LogLevel, val tag: String, val message: String)

object AppLogger {
    private val _logs = mutableListOf<LogEntry>()
    val logs: List<LogEntry> get() = _logs

    fun log(level: LogLevel, tag: String, message: String) {
        val entry = LogEntry(level, tag, message)
        _logs.add(0, entry) // log terbaru di atas

        // Cetak ke Logcat juga
        when (level) {
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.WARNING -> Log.w(tag, message)
            LogLevel.ERROR -> Log.e(tag, message)
        }
    }

    fun clear() {
        _logs.clear()
    }
}