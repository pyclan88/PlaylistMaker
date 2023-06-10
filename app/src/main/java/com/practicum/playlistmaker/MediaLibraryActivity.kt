package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medialibrary)

        val backButton = findViewById<ImageButton>(R.id.back_from_medialibrary_button)

        backButton.setOnClickListener {
            finish()
        }
    }
}