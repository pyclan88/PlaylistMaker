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
import android.widget.ScrollView
import android.widget.TextView
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

    companion object {
        private const val SEARCH_INPUT = "SEARCH_INPUT"
        const val SEARCH_HISTORY_PREFERENCES = "search_history_preferences"
        const val SEARCH_HISTORY_KEY = "key_for_search_history"

        fun startActivity(context: Context) {
            val intent = Intent(context, SearchActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val historyAdapter: HistoryAdapter = HistoryAdapter()

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private var searchInput: String = ""
    private val trackList = ArrayList<Track>()
    private val trackAdapter: TrackAdapter = TrackAdapter(historyAdapter)

    private var ibBackButton: ImageButton? = null
    private var imClearButton: ImageView? = null
    private var etQueryInput: EditText? = null
    private var vgNothingFound: ViewGroup? = null
    private var vgInternetError: ViewGroup? = null
    private var bRefreshButton: Button? = null
    private var rvTracks: RecyclerView? = null
    private var tvYouSearched: TextView? = null
    private var bClearHistory: Button? = null
    private var svSearch: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        ibBackButton = findViewById(R.id.backFromSearchButton)
        imClearButton = findViewById(R.id.clear_icon)
        etQueryInput = findViewById(R.id.searchEditText)
        vgNothingFound = findViewById(R.id.linear_nothing_found)
        vgInternetError = findViewById(R.id.linear_internet_error)
        bRefreshButton = findViewById(R.id.refresh_button)
        rvTracks = findViewById(R.id.rv_search_track)
        tvYouSearched = findViewById(R.id.you_searched)
        bClearHistory = findViewById(R.id.clear_history)
        svSearch = findViewById(R.id.search_scroll)

        rvTracks?.layoutManager = LinearLayoutManager(this)
        rvTracks?.adapter = trackAdapter

        trackAdapter.tracks = trackList

        val historyOfTracks = historyAdapter.clickedTracks

        ibBackButton?.setOnClickListener {
            finish()
        }

        imClearButton?.setOnClickListener {
            etQueryInput?.setText("")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etQueryInput?.windowToken, 0)
            trackList.clear()
        }

        val sharedPrefs = getSharedPreferences(SEARCH_HISTORY_PREFERENCES, MODE_PRIVATE)

        bClearHistory?.setOnClickListener {
            historyOfTracks.clear()
            tvYouSearched?.visibility = View.GONE
            bClearHistory?.visibility = View.GONE
            SearchHistory(sharedPrefs).saveHistory(historyOfTracks)
            trackAdapter.notifyDataSetChanged()
        }

        if (historyOfTracks.isEmpty()) historyOfTracks.addAll(SearchHistory(sharedPrefs).readHistory())
        if (historyOfTracks.isNotEmpty()){
            rvTracks?.adapter = historyAdapter
            showHistory()
        }
        setupSearchListener()

        bRefreshButton?.setOnClickListener {
            performITunesSearch()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                imClearButton?.visibility = clearButtonVisibility(s)
                svSearch?.visibility = if (etQueryInput!!.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        etQueryInput?.addTextChangedListener(simpleTextWatcher)
    }

    override fun onStop() {
        super.onStop()

        val sharedPrefs = getSharedPreferences(SEARCH_HISTORY_PREFERENCES, MODE_PRIVATE)
        SearchHistory(sharedPrefs).saveHistory(historyAdapter.clickedTracks)
    }

    private fun showHistory() {
        historyAdapter.notifyDataSetChanged()
        tvYouSearched?.visibility = View.VISIBLE
        bClearHistory?.visibility = View.VISIBLE
    }

    private fun performITunesSearch() {
        rvTracks?.adapter = trackAdapter
        tvYouSearched?.visibility = View.GONE
        bClearHistory?.visibility = View.GONE
        svSearch?.visibility = View.VISIBLE

        iTunesService.search(etQueryInput?.text.toString())
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            trackAdapter.tracks = trackList
                            trackAdapter.notifyDataSetChanged()
                            vgNothingFound?.visibility = View.GONE
                            vgInternetError?.visibility = View.GONE
                        } else showMessage(vgNothingFound, vgInternetError, "")

                    } else showMessage(
                        vgInternetError, vgNothingFound, response.code().toString()
                    )
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    showMessage(
                        vgInternetError, vgNothingFound, t.message.toString()
                    )
                }

            })
    }

    private fun setupSearchListener() {
        etQueryInput?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etQueryInput?.text!!.isNotEmpty()) {
                    performITunesSearch()
                }
                true
            }
            false

        }
    }

    private fun showMessage(fstLinear: ViewGroup?, sndLinear: ViewGroup?, additionalMessage: String) {
        fstLinear?.visibility = View.VISIBLE
        sndLinear?.visibility = View.GONE
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
        searchInput = etQueryInput?.text.toString()
        outState.putString(SEARCH_INPUT, searchInput)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(SEARCH_INPUT, "")
        etQueryInput?.setText(searchInput)
    }

}