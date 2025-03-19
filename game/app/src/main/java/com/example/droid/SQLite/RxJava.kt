package com.example.droid.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.droid.model.EstadoPartida
import com.example.droid.model.Scores
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.Date

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear la(s) tabla(s)
        val crearTablaJugador =
            "CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, monedas INTEGER, scoring INTEGER)"
        db.execSQL(crearTablaJugador)

        val crearTablaScoring = """
        CREATE TABLE scores (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            fecha INTEGER,
            estado INTEGER,
            scoringInicial INTEGER,
            scoringFinal INTEGER,
            jugador TEXT,
            longitud Double,
            latitud Double,
            FOREIGN KEY (jugador) REFERENCES usuarios(email) ON DELETE CASCADE
        )
    """.trimIndent()
        db.execSQL(crearTablaScoring)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Actualizar la base de datos si cambia la versión
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS scores")
        onCreate(db)
    }


    fun usuarioExisteRx(email: String): Single<Boolean> {
        return Single.fromCallable {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM usuarios WHERE email = ?", arrayOf(email))
            val exists = cursor.moveToFirst()
            cursor.close()
            //db.close()
            exists
        }
    }


    fun agregarUsuarioRx(email: String, monedas: Int): Completable {
        return Completable.fromAction {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put("email", email)
                put("monedas", monedas)
                put("scoring", 0) // Iniciamos el scoring del jugaor en 0
            }
            db.insert("usuarios", null, values)
            //db.close()
        }
    }


    fun obtenerMonedasRx(email: String): Single<Int> {
        return Single.fromCallable {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT monedas FROM usuarios WHERE email = ?", arrayOf(email))
            var monedas = 0
            if (cursor.moveToFirst()) {
                monedas = cursor.getInt(cursor.getColumnIndexOrThrow("monedas"))
            }
            cursor.close()
            //db.close()
            monedas
        }
        //Log.d("Rx obtener monedas","se obtienen las monedas del jugador")
    }







    fun actualizarMonedasRx(email: String, nuevasMonedas: Int): Completable {
        return Completable.fromAction {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put("monedas", nuevasMonedas)
            }
            db.update("usuarios", values, "email = ?", arrayOf(email))
            //db.close()
        }
        //Log.d("Rx actualizar monedas","se han actualizado")
    }

    fun agregarScoreRx(fecha: Long, estado: EstadoPartida, scoringInicial: Int, scoringFinal: Int, jugador: String, longitud: Double, latitud: Double): Completable {
        return Completable.fromAction {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put("fecha", fecha)
                put("estado", when (estado){
                    EstadoPartida.GANADA -> 1
                    EstadoPartida.EMPATE -> 0
                    EstadoPartida.PERDIDA -> -1
                })
                put("scoringInicial", scoringInicial)
                put("scoringFinal", scoringFinal)
                put("jugador", jugador)
                put("longitud", longitud)
                put("latitud", latitud)
            }
            db.insert("scores", null, values)
            //db.close()
        }
        //Log.d("Rx agregar scores","se han actualizado")
    }

    fun scoresXJugadorRx(jugador: String): Single<List<Scores>> {
        return Single.fromCallable {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM scores WHERE jugador = ?", arrayOf(jugador))
            val scoresList = mutableListOf<Scores>()

            if (cursor.moveToFirst()) {
                do {
                    val fecha = cursor.getLong(cursor.getColumnIndexOrThrow("fecha"))
                    val estadoInt = cursor.getInt(cursor.getColumnIndexOrThrow("estado"))
                    val estado = when (estadoInt) {
                        1 -> EstadoPartida.GANADA
                        0 -> EstadoPartida.EMPATE
                        -1 -> EstadoPartida.PERDIDA
                        else -> throw IllegalArgumentException("Estado desconocido: $estadoInt")
                    }
                    //

                    //
                    val scoringInicial = cursor.getInt(cursor.getColumnIndexOrThrow("scoringInicial"))
                    val scoringFinal = cursor.getInt(cursor.getColumnIndexOrThrow("scoringFinal"))
                    scoresList.add(Scores(Date(fecha), estado, scoringInicial, scoringFinal, jugador))
                } while (cursor.moveToNext())
            }
            cursor.close()
            //db.close()
            scoresList
        }
    }
    fun ultimoScoringRx(jugador: String): Single<Int> {
        return Single.fromCallable {
            val db = this.readableDatabase
            val cursor = db.rawQuery(
                "SELECT scoringFinal FROM scores WHERE jugador = ? ORDER BY id DESC LIMIT 1",
                arrayOf(jugador)
            )

            val ultimoScoring = if (cursor.moveToFirst()) cursor.getInt(0) else 0
            cursor.close()
            //db.close()
            ultimoScoring
        }
    }

    companion object {
        private const val DATABASE_NAME = "black_jack.db"
        private const val DATABASE_VERSION = 7
    }
}
