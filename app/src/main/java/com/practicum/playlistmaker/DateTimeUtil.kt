package com.practicum.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {

    private const val TIME_FORMAT_PATTERN = "mm:ss"
    fun formatTime(timeText: Int?): String {
        return SimpleDateFormat(
            TIME_FORMAT_PATTERN,
            Locale.getDefault()
        ).format(timeText)
    }
}