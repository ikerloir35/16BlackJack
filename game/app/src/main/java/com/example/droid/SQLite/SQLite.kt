package com.example.droid.SQLite
/*
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        // Crear la(s) tabla(s)
        val CREATE_TABLE =
            "CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE,monedas INTEGER,scoring INTEGER)"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Actualizar la base de datos si cambia la versión
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    fun usuarioExiste(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE email = ?", arrayOf(email))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun agregarUsuario(email: String, monedas: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("email", email)
            put("monedas", monedas)
        }
        db.insert("usuarios", null, values)
    }
    fun obtenerMonedas(email: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT monedas FROM usuarios WHERE email = ?", arrayOf(email))
        var monedas = 0
        if (cursor.moveToFirst()) {
            monedas = cursor.getInt(cursor.getColumnIndexOrThrow("monedas"))
        }
        cursor.close()
        return monedas
    }
    fun actualizarMonedas(email: String, nuevasMonedas: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("monedas", nuevasMonedas)
        }
        val filasActualizadas = db.update("usuarios", values, "email = ?", arrayOf(email))
        db.close()
        return filasActualizadas > 0 // Retorna true si se actualizó al menos una fila
    }

    companion object {
        private const val DATABASE_NAME = "black_jack.db"
        private const val DATABASE_VERSION = 2
    }
}*/
