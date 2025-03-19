package com.example.droid.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val FIREBASE_URL =
        "https://droid-blackjack-default-rtdb.europe-west1.firebasedatabase.app"

    val service: ServicioAPIRestFirebase by lazy {
        Retrofit.Builder()
            .baseUrl(FIREBASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServicioAPIRestFirebase::class.java)
    }
}