package com.practicum.playlistmaker.utils

object WordUtils {

    fun getDeclension(count: Int, wordForms: List<String>): String {
        val formIndex = when {
            count % 10 == 1 && count % 100 != 11 -> 0
            count % 10 in 2..4 && (count % 100 < 10 || count % 100 > 20) -> 1
            else -> 2
        }
        return "$count ${wordForms[formIndex]}"
    }

}