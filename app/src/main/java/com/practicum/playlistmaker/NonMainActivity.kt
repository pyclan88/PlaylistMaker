package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast

class NonMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lord_of_rings)

        val image = findViewById<ImageView>(R.id.poster)

//        Способ 1. Реализация анонимного класса
        /*val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Здесь какой-то текст", Toast.LENGTH_LONG).show()
            }
        }

        image.setOnClickListener(imageClickListener)*/

//        Способ 2. Лямбда-выражение
        image.setOnClickListener {
            Toast.makeText(this@NonMainActivity, "Здесь какой-то текст", Toast.LENGTH_LONG).show()
        }

        image.scaleType = ImageView.ScaleType.CENTER_CROP
        image.setImageResource(R.drawable.barsik)
    }
}

/*
class MainActivity : AppCompatActivity(), View.OnClickListener {
//    Способ 3. Реализация OnClickListener на Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lord_of_rings)

        val image = findViewById<ImageView>(R.id.poster)

        image.setOnClickListener(this@MainActivity)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.poster -> {
                Toast.makeText(this, "Нажали на картинку!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
*/