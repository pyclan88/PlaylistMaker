package com.practicum.playlistmaker

import android.os.Bundle
import android.util.Log
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

    private var ibBackButton: ImageButton? = null
    private var tvTrackName: TextView? = null
    private var tvArtistName: TextView? = null
    private var tvDuration: TextView? = null
    private var tvAlbum: TextView? = null
    private var tvYear: TextView? = null
    private var tvGenre: TextView? = null
    private var tvCountry: TextView? = null
    private lateinit var ivCover: ImageView

    companion object{
        lateinit var currentTrack: Track
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        ibBackButton = findViewById(R.id.backFromAudioPlayer)
        ibBackButton?.setOnClickListener {
            finish()
        }

        ivCover = findViewById(R.id.cover)
        Glide.with(applicationContext)
            .load(getCoverArtwork(currentTrack.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(COVER_CORNER_8))
            .into(ivCover)

        tvTrackName = findViewById(R.id.trackName)
        tvTrackName?.text = currentTrack.trackName

        tvArtistName = findViewById(R.id.artistName)
        tvArtistName?.text = currentTrack.artistName

        tvDuration = findViewById(R.id.durationValue)
        tvDuration?.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(currentTrack.trackTimeMillis.toLong())

        tvAlbum = findViewById(R.id.albumValue)
        tvAlbum?.text = currentTrack.collectionName

        tvYear = findViewById(R.id.yearValue)
        tvYear?.text = currentTrack.releaseDate.substring(0, 4)

        tvGenre = findViewById(R.id.genreValue)
        tvGenre?.text = currentTrack.primaryGenreName

        tvCountry = findViewById(R.id.countryValue)
        tvCountry?.text = currentTrack.country
    }

    private fun getCoverArtwork(artworkUrl100: String?) =
        artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

}