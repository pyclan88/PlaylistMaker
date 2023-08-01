package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private var searchInput: String = ""
    private val trackList = ArrayList<Track>()
    private val trackAdapter: TrackAdapter = TrackAdapter()

    private lateinit var backButton: ImageButton
    private lateinit var clearButton: ImageView
    private lateinit var queryInput: EditText
    private lateinit var linearNothingFound: ViewGroup
    private lateinit var linearInternetError: ViewGroup
    private lateinit var refreshButton: Button
    private lateinit var trackListReVi: RecyclerView

    companion object {
        private const val SEARCH_INPUT = "SEARCH_INPUT"

        fun startActivity(context: Context) {
            val intent = Intent(context, SearchActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.backFromSearchButton)
        clearButton = findViewById(R.id.clearIcon)
        queryInput = findViewById(R.id.searchEditText)
        linearNothingFound = findViewById(R.id.linear_nothing_found)
        linearInternetError = findViewById(R.id.linear_internet_error)
        refreshButton = findViewById(R.id.refresh_button)
        trackListReVi = findViewById<RecyclerView>(R.id.rv_search_track)

        trackListReVi.layoutManager = LinearLayoutManager(this)
        trackListReVi.adapter = trackAdapter
        trackAdapter.tracks = trackList

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            queryInput.setText("")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(queryInput.windowToken, 0)

            trackList.clear()
            trackAdapter.notifyDataSetChanged()
        }

        setupSearchListener()

        refreshButton.setOnClickListener {
            performITunesSearch()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        queryInput.addTextChangedListener(simpleTextWatcher)
    }


    private fun performITunesSearch() {
        iTunesService.search(queryInput.text.toString())
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                            linearNothingFound.visibility = View.GONE
                            linearInternetError.visibility = View.GONE
                        } else showMessage(linearNothingFound, linearInternetError, "")

                    } else showMessage(
                        linearInternetError, linearNothingFound, response.code().toString()
                    )
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    showMessage(linearInternetError, linearNothingFound, t.message.toString()
                    )
                }

            })
    }


    private fun setupSearchListener() {
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (queryInput.text.isNotEmpty()) {
                    performITunesSearch()
                }
                true
            }
            false

        }
    }


    private fun showMessage(fstLinear: ViewGroup, sndLinear: ViewGroup, additionalMessage: String) {
        fstLinear.visibility = View.VISIBLE
        sndLinear.visibility = View.GONE
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        if (additionalMessage.isNotEmpty()) {
            Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                .show()
        }
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchInput = queryInput.text.toString()
        outState.putString(SEARCH_INPUT, searchInput)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(SEARCH_INPUT, "")
        queryInput.setText(searchInput)
    }

}
