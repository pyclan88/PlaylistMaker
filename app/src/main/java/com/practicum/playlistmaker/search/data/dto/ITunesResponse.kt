package com.practicum.playlistmaker.search.data.dto

import com.practicum.playlistmaker.player.domain.model.Track

class ITunesResponse(val results: List<Track>) : Response()