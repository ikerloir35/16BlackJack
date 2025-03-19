package com.example.droid

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.droid.logica.determinarGanador
import com.example.droid.logica.dp
import com.example.droid.model.Baraja
import com.example.droid.model.Carta
import org.example.calcularValorMano
import com.example.droid.SQLite.DatabaseHelper
import com.example.droid.gestores.GestorCapturas
import com.example.droid.gestores.GestorMusica
import com.example.droid.gestores.GestorNotificaciones

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch




class PantallaPartida: AppCompatActivity(), PartidaOpcionesDialogo.PartidaOpcionesListener {

    private var baraja = Baraja()
    private var manoUnoJugador: MutableList<Carta> = mutableListOf()
    private var manoDealer: MutableList<Carta> = mutableListOf()
    private var jugadorSePlanta: Boolean = false
    private var jugadorSePasa: Boolean = false
    private var monedas = 0
    private var apuestaMano = 50
    private var iJugador = 0
    private var iDealer = 0
    private var laSegundaDealer: MutableList<ImageView> = mutableListOf()
    private var db = DatabaseHelper(this)
    private val compositeDisposable = CompositeDisposable()

    private var longitudcontext: Double = 0.0
    private var latitudcontext: Double = 0.0

    //declaración de variables view con inizialización posterior después del setContentView
    private lateinit var valorMonedasView: TextView
    private lateinit var valorApuestaView: TextView
    private lateinit var botonHit: Button
    private lateinit var botonStand: Button
    private lateinit var email: String
    private lateinit var valorDealerView: TextView
    private lateinit var valorJugadorView: TextView
    private lateinit var cartasJugadorLayout: LinearLayout
    private lateinit var cartasDealerLayout: LinearLayout
    private lateinit var resultadosLayout: LinearLayout
    private lateinit var resultadoView: ImageView
    private lateinit var soundPool: SoundPool
    private var ganarSoundId: Int = 0
    private var perderSoundId: Int = 0


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //variables para guardar captura de pantalla
    //private val CAPTURA_SOLICITUD_GUARDADO = 1001
    private lateinit var capturaBitmap: Bitmap
    private val capturaPantallaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data?.data != null) {
                val uri = result.data?.data
                uri?.let {
                    val bitmap = GestorCapturas.capturarPantalla(window.decorView.rootView)
                    if (bitmap != null) {
                        GestorCapturas.guardarCaptura(this, uri, bitmap)
                    } else {
                        Toast.makeText(this, "Error al capturar la pantalla.", Toast.LENGTH_SHORT).show()
                        Log.e("PantallaPartida", "Bitmap capturado es nulo.")
                    }
                } ?: Toast.makeText(this, "Error al guardar la captura.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Guardado cancelado.", Toast.LENGTH_SHORT).show()
            }
        }


    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_partida2)

        // Initialize SoundPool
        soundPool = SoundPool.Builder().setMaxStreams(2).build()
        ganarSoundId = soundPool.load(this, R.raw.win_sound, 1)
        perderSoundId = soundPool.load(this, R.raw.lose_sound, 1)

        // Initialize LocationServices
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
        obtenerUbicacionLongitud(fusedLocationClient)
        obtenerUbicacionLatitud(fusedLocationClient)

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



        //inicialización variables view
        valorDealerView = findViewById<TextView>(R.id.valorDealer)
        valorJugadorView = findViewById<TextView>(R.id.valorJugador)
        valorMonedasView = findViewById<TextView>(R.id.textMonedasValor)
        valorApuestaView = findViewById<TextView>(R.id.textApuestaValor)
        cartasJugadorLayout = findViewById<LinearLayout>(R.id.cartas_jugador)
        cartasDealerLayout = findViewById<LinearLayout>(R.id.cartas_dealer)
        resultadosLayout = findViewById<LinearLayout>(R.id.resultados)
        resultadoView = findViewById<ImageView>(R.id.resultadoView)

        botonHit = findViewById<Button>(R.id.buttonHit)
        botonStand = findViewById<Button>(R.id.buttonStand)

        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        val musicEnabled = sharedPreferences.getBoolean("musicEnabled", true)
        val musicUriString = sharedPreferences.getString("selectedMusicUri", null)
        val uri = musicUriString?.let { Uri.parse(it) }

        if (musicEnabled) {
            GestorMusica.iniciaMusica(this, uri)
        }
        email = intent.getStringExtra("email").toString()
        obtenerMonedasUsuario()

        valorApuestaView.text = apuestaMano.toString()

        mostrarDialogoOpciones()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inicial)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun mostrarDialogoOpciones() {
        val dialogoOpciones = PartidaOpcionesDialogo()
        dialogoOpciones.crearPartidaOpcionesListener(this)
        dialogoOpciones.show(supportFragmentManager, "PartidaOpcionesDialogo")
    }

    //Funciones para los botones del cuadro DialogoOpciones
    override fun onJugarPartida() {
        reiniciarPartida()
        mano()
    }

    override fun onCambiarApuesta() {
        val apuestaDialogo = PartidaOpcionesApuesta()
        apuestaDialogo.setSaldoJugador(monedas)
        apuestaDialogo.crearApuestaFinalListener(object :
            PartidaOpcionesApuesta.OpcionesApuestaListener {
            override fun apuestaFinal(cantidad: Int) {
                apuestaMano = cantidad
                valorApuestaView.text = apuestaMano.toString()
                mostrarDialogoOpciones()
            }
        })
        apuestaDialogo.show(supportFragmentManager, "PartidaOpcionesApuesta")
    }

    override fun onMenuPrincipal() {

        finish() //Cerrar la actividad actual y volvemos al menú principal
    }

    override fun onCapturarPantalla() {
        GestorCapturas.capturarPantalla(window.decorView.rootView)

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "captura_partida_${System.currentTimeMillis()}.png")
        }
        capturaPantallaLauncher.launch(intent)
    }

    //Funciones para la lógica del juego
    //Función Principal
    private fun mano() {
        manoUnoJugador.addAll(baraja.repartirManoInicial())
        manoDealer.addAll(baraja.repartirManoInicial())

        val valorManoJugador: Int = calcularValorMano(manoUnoJugador)
        val valorManoDealer: Int = calcularValorMano(manoDealer)

        //Se reparten las cartas y se muestran al jugador y al dealer(exceptuando la segunda)
        agregarCartaAmano(manoUnoJugador[iJugador], cartasJugadorLayout)
        iJugador++
        agregarCartaAmano(manoUnoJugador[iJugador], cartasJugadorLayout)
        iJugador++
        actualizarValor(manoUnoJugador, valorJugadorView)

        agregarCartaAmano(manoDealer[iDealer], cartasDealerLayout)
        iDealer++
        //actualizarValor(manoDealer,valorDealerView)
        agregarCartaAmano(manoDealer[iDealer], cartasDealerLayout)
        iDealer++
        //vistaManoJugador(manoUnoJugador,cartaJugador1View,cartaJugador2View,valorJugadorView)
        //vistaManoDealer(manoDealer,cartaDealer1View,cartaDealer2View,valorDealerView)

        //Determinar BlackJack Dealer o Jugador

        if (valorManoDealer == 21 || valorManoJugador == 21) {
            val resultadosLayout = findViewById<LinearLayout>(R.id.resultados)
            val resultadoView = findViewById<ImageView>(R.id.resultadoView)
            determinarGanador(
                longitudcontext,
                latitudcontext,
                manoDealer,
                manoUnoJugador,
                resultadosLayout,
                resultadoView,
                email,
                apuestaMano,
                monedas,
                db,
                compositeDisposable,
                soundPool,
                ganarSoundId,
                perderSoundId,
                this
            )
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                valorMonedasView.text = monedas.toString()
                mostrarDialogoOpciones()
            }

            return
        }
        turnoJugador()
    }

    private fun turnoJugador() {

        var valorActualJugador = calcularValorMano((manoUnoJugador))

        // Listener para "Hit" (pedir carta)
        botonHit.setOnClickListener {
            if (valorActualJugador < 21) {
                manoUnoJugador.add(baraja.repartirCarta())
                agregarCartaAmano(manoUnoJugador[iJugador], findViewById(R.id.cartas_jugador))
                iJugador++
                valorActualJugador = calcularValorMano(manoUnoJugador)
                actualizarValor(manoUnoJugador, valorJugadorView)

                if (valorActualJugador > 21) {
                    jugadorSePasa = true
                    destaparCartaDealer()
                    val resultadosLayout = findViewById<LinearLayout>(R.id.resultados)
                    val resultadoView = findViewById<ImageView>(R.id.resultadoView)
                    determinarGanador(longitudcontext,
                        latitudcontext,
                        manoDealer,
                        manoUnoJugador,
                        resultadosLayout,
                        resultadoView,
                        email,
                        apuestaMano,
                        monedas,
                        db,
                        compositeDisposable,
                        soundPool,
                        ganarSoundId,
                        perderSoundId,
                        this
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)

                        botonStand.isEnabled = false
                        botonHit.isEnabled = false
                        valorMonedasView.text = monedas.toString()
                        mostrarDialogoOpciones()
                    }
                }
            }
        }

        // Listener para "Stand" (plantarse)
        botonStand.setOnClickListener {
            jugadorSePlanta = true
            botonHit.isEnabled = false
            botonStand.isEnabled = false
            turnoDealer()
        }
    }

    private fun turnoDealer() {

        var valorManoDealer = calcularValorMano(manoDealer)


        //vistaManoDealer(manoDealer, cartaDealer1View, cartaDealer2View, valorDealerView)
        //manoDealer[1].posicion


        if (jugadorSePlanta) {
            destaparCartaDealer()
        }

        // Turno dealer: sigue pidiendo cartas hasta que tenga al menos 17 puntos
        while (valorManoDealer < 17 && !jugadorSePasa) {
            manoDealer.add(baraja.repartirCarta())
            agregarCartaAmano(manoDealer[iDealer], findViewById(R.id.cartas_dealer))
            valorManoDealer = calcularValorMano(manoDealer)
            actualizarValor(manoDealer, valorDealerView)
            iDealer++
        }
        determinarGanador(longitudcontext,
            latitudcontext,
            manoDealer,
            manoUnoJugador,
            resultadosLayout,
            resultadoView,
            email,
            apuestaMano,
            monedas,
            db,
            compositeDisposable,
            soundPool,
            ganarSoundId,
            perderSoundId,
            this
        )
        valorMonedasView.text = monedas.toString()
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            mostrarDialogoOpciones()
        }

    }

    private fun agregarCartaAmano(carta: Carta, layout: LinearLayout) {
        val nuevaCartaView = ImageView(this)
        val nombreCarta = "c${carta.posicion}"
        val recursoID = resources.getIdentifier(nombreCarta, "drawable", packageName)

        if ((iDealer == 0 && iJugador == 2) || (iJugador == 0)) {
            nuevaCartaView.layoutParams = LinearLayout.LayoutParams(96.dp, 128.dp)
        } else {
            nuevaCartaView.layoutParams = LinearLayout.LayoutParams(96.dp, 128.dp).apply {
                marginStart = (-48).dp //movemos la carta para que se solape con la anterior
            }
        }


        if (iDealer == 1) {
            nuevaCartaView.setImageResource(R.drawable.dorso_blanco_logo)
            laSegundaDealer.add(nuevaCartaView)
        } else {
            nuevaCartaView.setImageResource(recursoID)
        }
        layout.addView(nuevaCartaView)
    }

    private fun actualizarValor(mano: MutableList<Carta>, puntos: TextView) {
        puntos.text = (calcularValorMano(mano).toString())

    }

    private fun destaparCartaDealer() {
        val cartaNumero: Int = manoDealer[1].posicion
        val nombreCartas = "c$cartaNumero"
        val recursoID = resources.getIdentifier(nombreCartas, "drawable", packageName)
        laSegundaDealer[0].setImageResource(recursoID)

        valorDealerView.text = calcularValorMano(manoDealer).toString()
    }

    private fun reiniciarPartida() {



        // obtenemos las monedas del jugador despues de la partida
        obtenerMonedasUsuario()

        // Limpiar las manos de los jugadores y del dealer
        manoUnoJugador.clear()
        manoDealer.clear()

        // Limpiamos el imageView de la carta 2 del dealer
        laSegundaDealer.clear()

        // Limpiar las vistas de las cartas de la interfaz
        cartasJugadorLayout.removeAllViews()  // Elimina todas las cartas anteriores del jugador
        cartasDealerLayout.removeAllViews()   // Elimina todas las cartas anteriores del dealer

        // Regeneramos los contadores
        iDealer = 0
        iJugador = 0


        // Limpiar cualquier otro elemento visual que puedas tener, como los valores de las manos
        valorJugadorView.text = "0"
        valorDealerView.text = "0"

        // Reseteamos las variables de estado de la partida
        jugadorSePlanta = false
        jugadorSePasa = false

        // Crear una nueva baraja
        baraja.generarBaraja()
        // Barajar la baraja para la nueva partida
        baraja.barajar()

        // Habilitar los botones para la nueva ronda
        botonHit.isEnabled = true
        botonStand.isEnabled = true

        //hacemos desaparecer el resultado
        //resultadosLayout, resultadoView)
        resultadosLayout.visibility = View.GONE

    }

    private fun obtenerMonedasUsuario() {

        val disposable = db.obtenerMonedasRx(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ monedasObtenidas ->
                monedas = monedasObtenidas
                valorMonedasView.text = monedas.toString()
                Log.d("obtener monedas", "se muestran las monedas del jugador $monedas")
            }, { error ->
                Log.e("obtener monedas", "error al obtener monedas")
                error.printStackTrace()
            })
        compositeDisposable.add(disposable)
    }

    fun capturaYGuardarPantalla(context: Context, rootView: View) {
        try {
            val bitmap =
                Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            rootView.draw(canvas)

            if (bitmap.width <= 0 || bitmap.height <= 0) {
                Toast.makeText(context, "Error: el bitmap es inválido", Toast.LENGTH_SHORT).show()
                return
            }

            // mapea la imagen
            capturaBitmap = bitmap

            // Abrir el selector de ubicación
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/png"
                putExtra(Intent.EXTRA_TITLE, "captura_partida_${System.currentTimeMillis()}.png")
            }
            capturaPantallaLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al capturar la pantalla", Toast.LENGTH_SHORT).show()
        }

        /*CoroutineScope(Dispatchers.Main).launch {
            delay(3000)*/
            mostrarDialogoOpciones()
        //}
    }


    override fun onPause() {
        super.onPause()

        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        val musicEnabled = sharedPreferences.getBoolean("musicEnabled", true)


        if (!musicEnabled && GestorMusica.estaSonando()) {
            GestorMusica.detenMusica()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        compositeDisposable.clear()
        db.close()
    }

    fun obtenerUbicacionLatitud(fusedLocationClient: FusedLocationProviderClient): Double {

        try {
            val locationTask: Task<Location> = fusedLocationClient.lastLocation
            locationTask.addOnSuccessListener { location ->

                if (location != null) {

                    latitudcontext = location.latitude
                    Log.d("Pruebas", "Longitud desde el try: $latitudcontext")


                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.d("Pruebas", "Error toma datos ${e.printStackTrace()}")

        }

        return latitudcontext


    }

    fun obtenerUbicacionLongitud(fusedLocationClient: FusedLocationProviderClient): Double {




        try {
            val locationTask: Task<Location> = fusedLocationClient.lastLocation
            locationTask.addOnSuccessListener { location ->

                if (location != null) {

                    longitudcontext = location.longitude

                    Log.d("Pruebas", "Longitud desde el try: $longitudcontext")


                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.d("Pruebas", "Se fue todo a la puta ${e.printStackTrace()}")

        }

        return longitudcontext


    }
}
