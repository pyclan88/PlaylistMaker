package com.practicum.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {

    private const val TIME_FORMAT_PATTERN = "mm:ss"
    fun formatTime(timeText: Int?): String {
        return SimpleDateFormat(
            TIME_FORMAT_PATTERN,
            Locale.getDefault()
        ).format(timeText) ?: "00:00"
    }

}