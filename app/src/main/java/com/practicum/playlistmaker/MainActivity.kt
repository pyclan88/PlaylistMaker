package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)

        searchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        val mediaLibraryButton = findViewById<Button>(R.id.media_library_button)

        mediaLibraryButton.setOnClickListener {
            val mediaLibraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryIntent)
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}

/*
//                anonymous
        val searchButton = findViewById<Button>(R.id.search_button)
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Тост: Выпьем за Поиск!",
                Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(buttonClickListener)

//        lambda
        val mediaLibraryButton = findViewById<Button>(R.id.media_library_button)
        mediaLibraryButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Тост: Выпьем за Медиатеку!",
            Toast.LENGTH_LONG).show()
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Здесь настройки всего мира",
            Toast.LENGTH_SHORT).show()
        }

        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)

        val url = Uri.parse("https://ya.ru")
        val intent1 = Intent(Intent.ACTION_VIEW, url)
        startActivity(intent1)
*/