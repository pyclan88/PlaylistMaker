package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
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
        private const val EXTRA_KEY = "Track"
//        fun startActivity(context: Context, currentTrack: Track) {
//            val intent = Intent(context, AudioPlayerActivity::class.java)
//            intent.putExtra(EXTRA_KEY, currentTrack)
//            context.startActivity(intent)
//        }
        fun startActivity(context: Context, currentTrack: Track) {
//    val bundle = Bundle()
//    bundle.putParcelable()
            val intent = Intent(context, AudioPlayerActivity::class.java)
            intent.putExtra(EXTRA_KEY, currentTrack)
            context.startActivity(intent)
        }

    }

    private var currentTrack: Track? = null

    private var ibBackButton: ImageButton? = null
    private var tvTrackName: TextView? = null
    private var tvArtistName: TextView? = null
    private var tvDuration: TextView? = null
    private var tvAlbum: TextView? = null
    private var tvYear: TextView? = null
    private var tvGenre: TextView? = null
    private var tvCountry: TextView? = null
    private lateinit var ivCover: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        currentTrack = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("Track", Track::class.java)
        } else {
            intent.getParcelableExtra("Track")
        }

        initViews()

        setValues()

        setListeners()
    }

    private fun initViews() {
        ibBackButton = findViewById(R.id.backFromAudioPlayer)
        ivCover = findViewById(R.id.cover)
        tvTrackName = findViewById(R.id.trackName)
        tvArtistName = findViewById(R.id.artistName)
        tvDuration = findViewById(R.id.durationValue)
        tvAlbum = findViewById(R.id.albumValue)
        tvYear = findViewById(R.id.yearValue)
        tvGenre = findViewById(R.id.genreValue)
        tvCountry = findViewById(R.id.countryValue)
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
    }

    private fun setListeners() {
        ibBackButton?.setOnClickListener {
            finish()
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