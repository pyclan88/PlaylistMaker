package com.practicum.playlistmaker.temp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R

class MediaLibraryActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MediaLibraryActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medialibrary)

        val backButton = findViewById<ImageButton>(R.id.backFromMedialibraryButton)

        backButton.setOnClickListener {
            finish()
        }
    }

}