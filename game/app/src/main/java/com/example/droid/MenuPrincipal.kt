package com.example.droid

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.droid.SQLite.DatabaseHelper
import com.example.droid.gestores.GestorDBFirebase.traspasarbote
import com.example.droid.gestores.GestorMusica
import com.example.droid.logica.añadirBote
import com.example.droid.logica.determinarganador
import com.example.droid.retrofit.RetrofitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MenuPrincipal : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var db: DatabaseHelper
    private var monedas: Int = 0
    private lateinit var cantidadMonedasView: TextView
    private var email :String?= null
    private lateinit var textoPuntosScoring : TextView


    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        //Permiso de ubicacion
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menuprincipal)




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
        //permios de calendario
        requestCalendarPermissions()



        //permisos de acceso a calendario
        solicitarAccesoCalendario()

        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        val musicUriString = sharedPreferences.getString("selectedMusicUri", null)
        val musicEnabled = sharedPreferences.getBoolean("musicEnable",true)
        val uri = musicUriString?.let { Uri.parse(it) }

        if (!GestorMusica.estaSonando()&& musicEnabled){
            GestorMusica.iniciaMusica(this,uri)
        }
        email =intent.getStringExtra("email")
        db = DatabaseHelper(this)
        monedas = 0
        val emailViewText = findViewById<TextView>(R.id.textoEmail)
        cantidadMonedasView = findViewById<TextView>(R.id.cantidadMonedasView)
        var botonInicioPartida = findViewById<Button>(R.id.button_iniciar)
        var botonScoring = findViewById<Button>(R.id.button_scores)
        var botonSettings = findViewById<Button>(R.id.button_settings)
        var botonAyuda = findViewById<Button>(R.id.button_ayuda)
        var botonBote = findViewById<Button>(R.id.button_jackpot)


    //Comprobamos si el usuario existe
        if (email != null) {
            val disposable = db.usuarioExisteRx(email.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable { existe ->
                    if (!existe) {
                        // Si el usuario no existe, lo creamos con 1000 monedas
                        db.agregarUsuarioRx(email.toString(), 1000)
                            .doOnComplete { Log.d("MenuPrincipal", "Usuario creado con 1000 monedas") }
                    } else {
                        Log.d("MenuPrincipal", "Usuario existe")
                        Completable.complete()
                    }
                }
                .andThen(db.obtenerMonedasRx(email.toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ monedasObtenidas ->
                    // Actualizar interfaz
                    monedas = monedasObtenidas
                    emailViewText.text = email
                    cantidadMonedasView.text = monedas.toString()
                }, { error ->
                    Log.e("MenuPrincipal", "Error en mail", error)
                })

            compositeDisposable.add(disposable)
        } else {
            emailViewText.text = "No has introducido email"
            cantidadMonedasView.text = "0"
        }

        textoPuntosScoring = findViewById<TextView>(R.id.textoPuntosScoring)
        cargarUltimaPuntuacion()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inicial)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        botonAyuda.setOnClickListener(){
            Log.d("Menu principal","Boton ayuda")
            var intent = Intent(this,PantallaAyuda::class.java)
            startActivity(intent)
        }

        botonInicioPartida.setOnClickListener(){
            Log.d("Menu principal","Boton inicio de partida")
            var intent = Intent(this,PantallaPartida::class.java)
            Log.d("Menu principal","Monedas : $monedas")
            intent.putExtra("email",email)

            startActivity(intent)
        }

        botonScoring.setOnClickListener(){
            Log.d("Menu principal","Boton scoring")
            var intent = Intent(this,PantallaScoring::class.java)
            intent.putExtra("email",email)
            intent.putExtra("monedas",monedas)
            startActivity(intent)
        }

        botonBote.setOnClickListener {
            Log.d("MenuPrincipal", "Boton Bote presionado")
            val intent = Intent(this, PantallaBoteTopJugadores::class.java)
            intent.putExtra("email",email)
            startActivity(intent)
        }

        botonSettings.setOnClickListener(){
            Log.d("Settings","Boton settings clickado")
            var intent = Intent(this,PantallaSettings::class.java)
            intent.putExtra("email",email)
            startActivity(intent)
        }

        añadirBote()
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        val musicEnabled = sharedPreferences.getBoolean("musicEnabled", true)
        val musicUriString = sharedPreferences.getString("selectedMusicUri", null)
        val uri = musicUriString?.let { Uri.parse(it) }

        if (musicEnabled && !GestorMusica.estaSonando()) {
            GestorMusica.iniciaMusica(this, uri)
        } else if (!musicEnabled && GestorMusica.estaSonando()) {
            GestorMusica.detenMusica()
        }
        actualizarMonedas()
    }

    override fun onPause() {
        super.onPause()
        GestorMusica.detenMusica()

    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiar todas las suscripciones activas
        compositeDisposable.clear()
        db.close()

    }
    private fun requestCalendarPermissions() {
        val permissions = arrayOf(android.Manifest.permission.WRITE_CALENDAR, android.Manifest.permission.READ_CALENDAR)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    //Override para forzar de nuevo permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            }
        }
    }

     fun actualizarMonedas() {
         monedas = 0
        val disposable = db.obtenerMonedasRx(email.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ monedasObtenidas ->
                // Actualizar interfaz con el valor de monedas obtenido
                monedas = monedasObtenidas
                cantidadMonedasView.text = monedas.toString()
            }, { error ->
                Log.e("MenuPrincipal", "Error al cargar monedas", error)
            })

        compositeDisposable.add(disposable)
    }
    private fun cargarUltimaPuntuacion() {
        val disposable = db.ultimoScoringRx(email.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ ultimaPuntuacion ->
                textoPuntosScoring.text = ultimaPuntuacion.toString()
            }, { error ->
                error.printStackTrace()
            })
        compositeDisposable.add(disposable)
    }
    private fun solicitarAccesoCalendario() {
        val permissions = arrayOf(android.Manifest.permission.WRITE_CALENDAR, android.Manifest.permission.READ_CALENDAR)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }
}


