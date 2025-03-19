package com.example.droid.gestores


import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object GestorIdioma {

    fun cambiarIdioma(contexto: Context, codigoIdioma: String) {
        val locale = Locale(codigoIdioma)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        contexto.resources.updateConfiguration(config, contexto.resources.displayMetrics)
    }
    fun guardarPreferenciasLenguaje(context: Context, languageCode: String) {
        val preferences = context.getSharedPreferences("language_pref", Context.MODE_PRIVATE)
        preferences.edit().putString("language", languageCode).apply()
    }

    fun getLenguajeSalvado(context: Context): String {
        val preferences = context.getSharedPreferences("language_pref", Context.MODE_PRIVATE)
        return preferences.getString("language", "es") ?: "es" //idioma por defecto
    }
}