package com.practicum.playlistmaker.search.data.dto

import com.practicum.playlistmaker.player.domain.model.Track

data class ITunesResponse(val results: List<Track>) : Response()