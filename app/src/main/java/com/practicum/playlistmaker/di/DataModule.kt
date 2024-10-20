package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single {
        MediaPlayer()
    }

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single(named("themeSwitchPrefs")) {
        androidContext()
            .getSharedPreferences("theme_switch_preferences", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(
            iTunesService = get(),
            context = get()
        )
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(
            context = get()
        )
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS playlist_table (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            name TEXT NOT NULL,
            description TEXT,
            coverPath TEXT,
            idList TEXT NOT NULL,
            count INTEGER NOT NULL,
            lastModifiedAt INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS playlist_track_entity (
            trackId INTEGER PRIMARY KEY NOT NULL,
            trackName TEXT NOT NULL,
            artistName TEXT NOT NULL,
            trackTimeMillis TEXT NOT NULL,
            artworkUrl100 TEXT NOT NULL,
            collectionName TEXT NOT NULL,
            releaseDate TEXT NOT NULL,
            primaryGenreName TEXT NOT NULL,
            country TEXT NOT NULL,
            previewUrl TEXT,
            isFavorite INTEGER NOT NULL
            )
        """.trimIndent())
    }

}