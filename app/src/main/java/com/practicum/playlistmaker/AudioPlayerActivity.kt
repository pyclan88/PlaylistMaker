package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale


const val COVER_CORNER_8 = 8

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY = 300L

        private const val EXTRA_KEY = "Track"

        fun startActivity(context: Context, currentTrack: Track) {
            val intent = Intent(context, AudioPlayerActivity::class.java)
            intent.putExtra(EXTRA_KEY, currentTrack)
            context.startActivity(intent)
        }

    }

    private var mainThreadHandler: Handler? = null
    private lateinit var runnableTimer: Runnable

    private var playerState = STATE_DEFAULT

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

    private var mediaPlayer = MediaPlayer()
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        mainThreadHandler = Handler(Looper.getMainLooper())

        currentTrack = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("Track", Track::class.java)
        } else {
            intent.getParcelableExtra("Track")
        }

        initViews()

        setValues()

        setListeners()

        runnableTimer = object : Runnable {
            override fun run() {
                tvPlaybackTime?.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)
                mainThreadHandler?.postDelayed(this, DELAY)
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
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(runnableTimer)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()

            STATE_PREPARED,
            STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            ibPlayButton?.setImageResource(R.drawable.ic_play_button)
            tvPlaybackTime?.text = getString(R.string.playback_time)
            playerState = STATE_PREPARED
            mainThreadHandler?.removeCallbacks(runnableTimer)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        ibPlayButton?.setImageResource(R.drawable.ic_pause_button)
        playerState = STATE_PLAYING
        mainThreadHandler?.post(runnableTimer)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        ibPlayButton?.setImageResource(R.drawable.ic_play_button)
        playerState = STATE_PAUSED
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
        tvDuration?.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(currentTrack?.trackTimeMillis?.toLong())
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
            .transform(RoundedCorners(COVER_CORNER_8))
            .into(ivCover)
    }

    private fun getCoverArtwork(artworkUrl100: String?) =
        artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

}