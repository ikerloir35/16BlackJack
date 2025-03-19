package com.example.droid.gestores


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.telephony.TelephonyManager
import com.example.droid.R

object GestorMusica {

    var mediaPlayer: MediaPlayer? = null
    var sonando = false
    private var llamada = false
    private var uriActual: Uri? = null
    private var callStateReceiver: BroadcastReceiver? = null

    // Inicia la música
    fun iniciaMusica(context: Context, uri: Uri?) {
        if (mediaPlayer?.isPlaying == true || llamada) return

        uriActual =
            uri ?: Uri.parse("android.resource://${context.packageName}/${R.raw.cancion_default}")
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, uriActual)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
            sonando = true
        } catch (e: Exception) {
            e.printStackTrace()

            mediaPlayer = null
            sonando = false
        }
    }

    // Detiene la música
    fun detenMusica() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        sonando = false
    }

    // Comprueba si la música está reproduciéndose
    fun estaSonando(): Boolean = sonando

    // Configurar el BroadcastReceiver para llamadas
    fun configLlamada(context: Context) {
        val intentFilter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        callStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

                when (state) {
                    TelephonyManager.EXTRA_STATE_RINGING, TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                        llamada = true
                        if (sonando) detenMusica()
                    }

                    TelephonyManager.EXTRA_STATE_IDLE -> {
                        llamada = false
                        if (!sonando) iniciaMusica(context, uriActual)
                    }
                }
            }
        }

        context.registerReceiver(callStateReceiver, intentFilter)
    }

    // Liberar el BroadcastReceiver cuando no sea necesario
    fun cerrarLlamada(context: Context) {
        callStateReceiver?.let {
            context.unregisterReceiver(it)
        }
        callStateReceiver = null
    }
}
