package com.practicum.playlistmaker.main.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.temp.MediaLibraryActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.settings.ui.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchButton.setOnClickListener {
            SearchActivity.startActivity(this)
        }

        binding.mediaLibraryButton.setOnClickListener {
            MediaLibraryActivity.startActivity(this)
        }

        binding.settingsButton.setOnClickListener {
            SettingsActivity.startActivity(this)
        }
    }
}