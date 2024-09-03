package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.R

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val buttonImage: Int,
    val progress: String,
) {

    class Default : PlayerState(false, R.drawable.ic_play_button_not_prepared, "00:00")

    class Prepared : PlayerState(true, R.drawable.ic_play_button, "00:00")

    class Playing(progress: String) : PlayerState(true, R.drawable.ic_pause_button, progress)

    class Paused(progress: String) : PlayerState(true, R.drawable.ic_play_button, progress)

}