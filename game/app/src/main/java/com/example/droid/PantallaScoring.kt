package com.example.droid
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.droid.SQLite.DatabaseHelper
import com.example.droid.gestores.GestorMusica
import com.example.droid.model.ListaScores
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class PantallaScoring : AppCompatActivity() {

    private lateinit var scoresRecView: RecyclerView
    private val compositeDisposable = CompositeDisposable()
    private lateinit var db: DatabaseHelper
    private lateinit var email: String
    private lateinit var textoPuntosScoring : TextView

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoring)

        //Llamadas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_PHONE_STATE),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                GestorMusica.configLlamada(this)
            }
        }

        //muisca
        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        val musicEnabled = sharedPreferences.getBoolean("musicEnabled", true)
        val musicUriString = sharedPreferences.getString("selectedMusicUri", null)
        val uri = musicUriString?.let { Uri.parse(it) }

        if (musicEnabled && !GestorMusica.estaSonando()) {
            GestorMusica.iniciaMusica(this, uri)
        }
        //vista
        val emailViewText = findViewById<TextView>(R.id.textoEmail)
        email = intent.getStringExtra("email") ?: ""
        emailViewText.text = email

        val cantidadMonedasView = findViewById<TextView>(R.id.cantidadMonedasView2)
        val monedas: Int = intent.getIntExtra("monedas",0)
        cantidadMonedasView.text= monedas.toString()

        textoPuntosScoring = findViewById<TextView>(R.id.textoPuntosScoring)


        db = DatabaseHelper(this)
        scoresRecView = findViewById(R.id.scoresRecView)
        scoresRecView.layoutManager = LinearLayoutManager(this)

        // Cargar los datos del jugador
        cargarUltimaPuntuacion()
        cargarScores()

        val botonMenuPrincipa = findViewById<Button>(R.id.botonMenuPrincipal)
        botonMenuPrincipa.setOnClickListener(){
            var intent = Intent(this,MenuPrincipal::class.java)
            intent.putExtra("email",email)
            startActivity(intent)
        }
    }

    private fun cargarScores() {
        // Llamar a la base de datos para obtener los scores por email
        val disposable = db.scoresXJugadorRx(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ scoresList ->
                // Configurar el Adapter del RecyclerView con los datos
                scoresRecView.adapter = ListaScores(scoresList)
            }, { error ->
                error.printStackTrace()
            })

        compositeDisposable.add(disposable)
    }

    private fun cargarUltimaPuntuacion() {
        val disposable = db.ultimoScoringRx(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ ultimaPuntuacion ->
                textoPuntosScoring.text = ultimaPuntuacion.toString()
            }, { error ->
                error.printStackTrace()
            })
        compositeDisposable.add(disposable)
    }

    override fun onResume() {
        super.onResume()
        // Reanudar la música al volver a la actividad
        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        val musicEnabled = sharedPreferences.getBoolean("musicEnabled", true)
        val musicUriString = sharedPreferences.getString("selectedMusicUri", null)
        val uri = musicUriString?.let { Uri.parse(it) }

        if (musicEnabled) {
            GestorMusica.iniciaMusica(this, uri)
        }
    }

    override fun onPause() {
        super.onPause()
        // Detener la música cuando se pausa la actividad
        if (GestorMusica.estaSonando()) {
            GestorMusica.detenMusica()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear() // Liberar las suscripciones
        db.close()
    }
}