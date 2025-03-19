package com.example.droid.logica

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

import android.content.ContentValues

import android.icu.util.TimeZone
import android.media.SoundPool
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.example.droid.R
import com.example.droid.SQLite.DatabaseHelper
import com.example.droid.model.Carta
import org.example.calcularValorMano
import com.example.droid.model.EstadoPartida
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import com.example.droid.gestores.GestorAnimaciones
import com.example.droid.gestores.GestorDBFirebase.actualizarBote
import com.example.droid.gestores.GestorDBFirebase.actualizarPartidasGanadasUsuario
import com.example.droid.gestores.GestorDBFirebase.actualizarScoreUsuario
import com.example.droid.gestores.GestorNotificaciones


fun determinarGanador(
    longitud: Double,
    latitud: Double,
    manoDealer: List<Carta>,
    manoJugador: List<Carta>,
    resultadosLayout: LinearLayout,
    resultadoView: ImageView,
    email: String,
    apuesta:Int,
    monedas: Int,
    db: DatabaseHelper,
    compositeDisposable: CompositeDisposable,
    soundPool: SoundPool,
    soundIdGanar: Int,
    soundIdPerder: Int,
    contexto : Context
) {



    val contadorFinalJugador: Int = calcularValorMano(manoJugador)
    val contadorFinalDealer: Int = calcularValorMano(manoDealer)

    // Initialize GestorNotificaciones
    var gestorNotificaciones = GestorNotificaciones(contexto)



    Log.d("determinarGanador", "Longitud: $longitud")


    //Verificación de blackjack. Solo despues de repartir las dos primeras manos
    val dealerTieneBlackjack = manoDealer.size == 2 && contadorFinalDealer == 21
    val jugadorTieneBlackjack = manoJugador.size == 2 && contadorFinalJugador == 21

    val estadoPartida = when {

        dealerTieneBlackjack && jugadorTieneBlackjack -> {
            GestorAnimaciones.mostrarFin(R.drawable.empate,resultadosLayout,resultadoView)
            EstadoPartida.EMPATE
        }

        dealerTieneBlackjack -> {
            GestorAnimaciones.mostrarFin(R.drawable.perder,resultadosLayout,resultadoView)
            EstadoPartida.PERDIDA
        }

        jugadorTieneBlackjack -> {
            GestorAnimaciones.mostrarFin(R.drawable.ganas,resultadosLayout,resultadoView)
            EstadoPartida.GANADA
        }

        contadorFinalJugador > 21 -> {
            GestorAnimaciones.mostrarFin(R.drawable.perder,resultadosLayout,resultadoView)
            EstadoPartida.PERDIDA
            //println("Has perdido!")
        }

        contadorFinalDealer > 21 -> {
            //println("Has ganado!")
            GestorAnimaciones.mostrarFin(R.drawable.ganas,resultadosLayout,resultadoView)
            EstadoPartida.GANADA
        }

        contadorFinalDealer == contadorFinalJugador -> {
            //println("Empate")
            GestorAnimaciones.mostrarFin(R.drawable.empate,resultadosLayout,resultadoView)
            EstadoPartida.EMPATE
        }

        contadorFinalDealer < contadorFinalJugador -> {
            //println("Has ganado!")
            GestorAnimaciones.mostrarFin(R.drawable.ganas,resultadosLayout,resultadoView)
            EstadoPartida.GANADA
        }

        else -> {
            GestorAnimaciones.mostrarFin(R.drawable.perder,resultadosLayout,resultadoView)
            EstadoPartida.PERDIDA
            //println("Has perdido!")
        }
    }

    when (estadoPartida) {
        EstadoPartida.GANADA -> {
            soundPool.play(soundIdGanar, 1f, 1f, 0, 0, 1f)
            actualizarPartidasGanadasUsuario(email)
        }
        EstadoPartida.PERDIDA -> {
            soundPool.play(soundIdPerder, 1f, 1f, 0, 0, 1f)
            actualizarBote()
        }
        EstadoPartida.EMPATE -> {
            //no suena nada en caso de empate.
        }
    }

    val puntos = when (estadoPartida) {
        EstadoPartida.GANADA -> 100
        EstadoPartida.EMPATE -> 25
        EstadoPartida.PERDIDA -> 0
    }

    val signo = when (estadoPartida){
        EstadoPartida.GANADA -> 1
        EstadoPartida.EMPATE -> 0
        EstadoPartida.PERDIDA -> -1

    }
    //añadimos al calendario y realizamos notificación en pantalla
    if (estadoPartida == EstadoPartida.GANADA){
        anadirPartidaAlCalendario(contexto,"&DROID","Has ganado", System.currentTimeMillis(),System.currentTimeMillis())
        gestorNotificaciones.NotificacionVictoria()
    }

    val sumaMonedas = monedas + (signo*apuesta)
    var scoringFinal = 0
    val fecha =System.currentTimeMillis()

    val disposable : Disposable = db.ultimoScoringRx(email)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMapCompletable {ultimoScoring ->
            val scoringInicial = ultimoScoring
            scoringFinal = scoringInicial + puntos

            db.agregarScoreRx(
                fecha = fecha,
                estado = estadoPartida,
                scoringInicial = scoringInicial,
                scoringFinal = scoringFinal,
                jugador = email,
                longitud = longitud,
                latitud = latitud

            )   .andThen(db.actualizarMonedasRx(email, sumaMonedas))
        }
        .subscribe({
            actualizarScoreUsuario(email, puntos,sumaMonedas)
            Log.i("determinarGanador","monedas y scoring actualizados en BBDD")
        }, { error ->
            Log.e("determinarGanador","error al actualizar scoring y/o monedas")
            error.printStackTrace()
        })

    compositeDisposable.add(disposable)
}


fun mostrarFin(imagen: Int, resultadosLayout: LinearLayout, resultadoView: ImageView){
    resultadoView.setImageResource(imagen)
    resultadosLayout.visibility = View.VISIBLE
}

fun anadirPartidaAlCalendario(
    context: Context,
    title: String,
    description: String,
    startTime: Long,
    endTime: Long
) {
    val values = ContentValues().apply {
        put(CalendarContract.Events.CALENDAR_ID, getPrimaryCalendarId(context))
        put(CalendarContract.Events.TITLE, title)
        put(CalendarContract.Events.DESCRIPTION, description)
        put(CalendarContract.Events.DTSTART, startTime)
        put(CalendarContract.Events.DTEND, endTime)
        put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
    }

    val uri: Uri? = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    if (uri != null) {
        val eventId = uri.lastPathSegment?.toLongOrNull()
        if (eventId != null) {
            Toast.makeText(context, "Partida añadida al calendario", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Error al añadir al calendario", Toast.LENGTH_SHORT).show()
    }
}


private fun getPrimaryCalendarId(context: Context): Long {
    val projection = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY)
    val uri = CalendarContract.Calendars.CONTENT_URI
    val selection = "${CalendarContract.Calendars.IS_PRIMARY} = ?"
    val selectionArgs = arrayOf("1")

    context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID)
            return cursor.getLong(idIndex)
        }
    }
    throw IllegalStateException("No se encontró un calendario principal.")
}






