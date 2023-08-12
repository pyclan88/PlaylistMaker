package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.backFromSettingsButton)
        backButton.setOnClickListener { finish() }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        val shareButton = findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                val textToShare = getString(R.string.share_link)
                putExtra(Intent.EXTRA_TEXT, textToShare)
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        val supportButton = findViewById<Button>(R.id.supportButton)
        supportButton.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply { // }
                data = Uri.parse("mailto:")
                val recipientEmail = getString(R.string.email_recipient)
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
                val subject = getString(R.string.email_subject)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                val message = getString(R.string.email_message)
                putExtra(Intent.EXTRA_TEXT, message)
                startActivity(Intent.createChooser(this, null))
            }
        }

        val agreementButton = findViewById<Button>(R.id.agreementButton)
        agreementButton.setOnClickListener {
            val websiteUrl = getString(R.string.agreement_website)
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
            startActivity(agreementIntent)
        }

    }

    override fun onStop() {
        super.onStop()

        val sharedPrefs = getSharedPreferences(THEME_SWITCH_PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(THEME_SWITCH_KEY, (applicationContext as App).darkTheme)
            .apply()
    }
}