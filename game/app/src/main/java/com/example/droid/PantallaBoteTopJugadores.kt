package com.example.droid

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.droid.adaptadores.JugadoresAdapter
import com.example.droid.gestores.GestorDBFirebase
import com.example.droid.model.Jugador
import com.example.droid.modelgson.BoteGson
import com.example.droid.modelgson.DBGson
import com.example.droid.retrofit.RetrofitClient
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantallaBoteTopJugadores : AppCompatActivity() {

    private lateinit var boteLabel: TextView
    private lateinit var boteInfo: TextView
    private lateinit var btonMenuPrincipal: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var jugadoresAdapter: JugadoresAdapter
    private lateinit var email: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bote_top_jugadores)

        boteLabel = findViewById(R.id.bote_label)
        boteInfo = findViewById(R.id.bote_info)
        recyclerView = findViewById(R.id.recycler_view_top_jugadores)

        email = intent.getStringExtra("email") ?: ""

        // Configuración del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        jugadoresAdapter = JugadoresAdapter() // Adaptador para el RecyclerView
        recyclerView.adapter = jugadoresAdapter

        btonMenuPrincipal = findViewById<Button>(R.id.button_mainmenu)
        btonMenuPrincipal.setOnClickListener(){
            var intent = Intent(this,MenuPrincipal::class.java)
            intent.putExtra("email",email)
            startActivity(intent)}

        // Obtener y mostrar el bote
        cargarBote()

        // Obtener y mostrar el top 10 de jugadores
        cargarTopJugadores()
    }
    /*
    private fun obtenerBote() {
        val boteRef = GestorDBFirebase.obtenerBote()

        boteRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val data = task.result
                val premioAcumulado = data?.child("premioAcumulado")?.getValue(Int::class.java) ?: 0

                Log.d("PantallaBote", "Premio acumulado: $premioAcumulado")

                // Mostrar el valor del bote en el TextView
                boteInfo.text = "$$premioAcumulado"
            } else {
                Log.e("Firebase", "Error al obtener el bote: ${task.exception?.message}")
            }
        }
    }*/
    private fun cargarBote() {
        val call = RetrofitClient.service.obtenerBote()
        call.enqueue(object : Callback<BoteGson> {
            override fun onResponse(call: Call<BoteGson>, response: Response<BoteGson>) {
                if (response.isSuccessful) {
                    val bote = response.body()
                    val premioAcumulado = bote?.premioAcumulado ?: 0
                    Log.d("PantallaBoteTopJugadores", "Bote cargado: $bote")
                    boteInfo.text = "$$premioAcumulado"
                } else {
                    Log.e("PantallaBoteTopJugadores", "Error al cargar el bote: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BoteGson>, t: Throwable) {
                Log.e("PantallaBoteTopJugadores", "Fallo en la conexión: ${t.message}")
            }
        })
    }

    private fun cargarTopJugadores() {
        val call = RetrofitClient.service.obtenerBaseDeDatos()

        call.enqueue(object : Callback<DBGson> {
            override fun onResponse(call: Call<DBGson>, response: Response<DBGson>) {
                if (response.isSuccessful) {
                    val dbGson = response.body()

                    // Extraer y procesar los datos de los jugadores
                    val jugadoresList = dbGson?.usuarios?.map { (email, usuarioGson) ->
                        val puntuacionTotal = usuarioGson.puntuacionTotal.toIntOrNull() ?: 0
                        Jugador(email, puntuacionTotal)
                    } ?: emptyList()

                    // Ordenar por puntuación en orden descendente y tomar los 10 mejores
                    val jugadoresOrdenados = jugadoresList.sortedByDescending { it.puntuacionTotal }
                        .take(10)

                    // Mostrar los jugadores en el RecyclerView
                    jugadoresAdapter.submitList(jugadoresOrdenados)

                    Log.d("PantallaTopJugadores", "Top jugadores cargados correctamente")
                } else {
                    Log.e(
                        "PantallaTopJugadores",
                        "Error al cargar la base de datos: ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<DBGson>, t: Throwable) {
                Log.e("PantallaTopJugadores", "Fallo en la conexión: ${t.message}")
            }
        })

    }
}
