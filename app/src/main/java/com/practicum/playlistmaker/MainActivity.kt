package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)

        searchButton.setOnClickListener {
            SearchActivity.startActivity(this)
        }

        val mediaLibraryButton = findViewById<Button>(R.id.media_library_button)

        mediaLibraryButton.setOnClickListener {
            MediaLibraryActivity.startActivity(this)
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener {
            SettingsActivity.startActivity(this)
        }
    }
}