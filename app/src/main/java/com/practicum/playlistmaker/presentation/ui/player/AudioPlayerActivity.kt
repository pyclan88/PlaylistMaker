package com.practicum.playlistmaker.presentation.ui.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.DateTimeUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SearchActivity
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.entity.PlayerState
import com.practicum.playlistmaker.domain.models.Track


class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val DELAY_MILLIS = 300L

        fun startActivity(context: Context) {
            val intent = Intent(context, AudioPlayerActivity::class.java)
            context.startActivity(intent)
        }

    }

    private var mainThreadHandler: Handler? = null
    private lateinit var runnableTimer: Runnable

    private var playerState = PlayerState.DEFAULT

    private var currentTrack: Track? = null

    private var ibBackButton: ImageButton? = null
    private var tvTrackName: TextView? = null
    private var tvArtistName: TextView? = null
    private var tvDuration: TextView? = null
    private var tvAlbum: TextView? = null
    private var tvYear: TextView? = null
    private var tvGenre: TextView? = null
    private var tvCountry: TextView? = null
    private var ibPlayButton: ImageButton? = null
    private var tvPlaybackTime: TextView? = null
    private lateinit var ivCover: ImageView

    private lateinit var getPlayerInteractor: PlayerInteractor
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        mainThreadHandler = Handler(Looper.getMainLooper())

        getPlayerInteractor = Creator.providePlayerInteractor(this)

        currentTrack = getPlayerInteractor.getTrack()

        initViews()

        setValues()

        setListeners()

        runnableTimer = object : Runnable {
            override fun run() {
                tvPlaybackTime?.text =
                    DateTimeUtil.formatTime(getPlayerInteractor.getCurrentPosition())
                mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
            }
        }

        preparePlayer()

        ibPlayButton?.setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        getPlayerInteractor.releasePlayer()
        mainThreadHandler?.removeCallbacks(runnableTimer)
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()

            PlayerState.PREPARED,
            PlayerState.PAUSED -> startPlayer()

            else -> {}
        }
    }

    private fun preparePlayer() {
        getPlayerInteractor.preparePlayer(
            { playerState = PlayerState.PREPARED },
            {
                ibPlayButton?.setImageResource(R.drawable.ic_play_button)
                tvPlaybackTime?.text = getString(R.string.playback_time)
                playerState = PlayerState.PREPARED
                mainThreadHandler?.removeCallbacks(runnableTimer)
            }
        )
    }

    private fun startPlayer() {
        getPlayerInteractor.startPlayer()
        ibPlayButton?.setImageResource(R.drawable.ic_pause_button)
        playerState = PlayerState.PLAYING
        mainThreadHandler?.post(runnableTimer)
    }

    private fun pausePlayer() {
        getPlayerInteractor.pausePlayer()
        ibPlayButton?.setImageResource(R.drawable.ic_play_button)
        playerState = PlayerState.PAUSED
        mainThreadHandler?.removeCallbacks(runnableTimer)
    }

    private fun initViews() {
        ibBackButton = findViewById(R.id.back_from_audio_player)
        ivCover = findViewById(R.id.cover)
        tvTrackName = findViewById(R.id.track_name_player)
        tvArtistName = findViewById(R.id.artist_name_player)
        tvDuration = findViewById(R.id.duration_value)
        tvAlbum = findViewById(R.id.album_value)
        tvYear = findViewById(R.id.year_value)
        tvGenre = findViewById(R.id.genre_value)
        tvCountry = findViewById(R.id.country_value)
        ibPlayButton = findViewById(R.id.play_button)
        tvPlaybackTime = findViewById(R.id.playback_time)
    }

    private fun setValues() {
        tvTrackName?.text = currentTrack?.trackName
        tvArtistName?.text = currentTrack?.artistName
        tvDuration?.text = DateTimeUtil.formatTime(currentTrack?.trackTimeMillis?.toInt())
        tvAlbum?.text = currentTrack?.collectionName
        tvYear?.text = currentTrack?.releaseDate?.substring(0, 4)
        tvGenre?.text = currentTrack?.primaryGenreName
        tvCountry?.text = currentTrack?.country
        loadCoverToPlayer()
        url = currentTrack?.previewUrl
    }

    private fun setListeners() {
        ibBackButton?.setOnClickListener {
            SearchActivity.startActivity(this)
        }
        ibPlayButton?.setOnClickListener {

        }
    }

    private fun loadCoverToPlayer() {
        Glide.with(applicationContext)
            .load(getCoverArtwork(currentTrack?.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner8)))
            .into(ivCover)
    }

    private fun getCoverArtwork(artworkUrl100: String?) =
        artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

}