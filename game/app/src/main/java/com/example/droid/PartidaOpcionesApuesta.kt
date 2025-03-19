package com.example.droid

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.droid.gestores.GestorMusica
import android.media.SoundPool


class PartidaOpcionesApuesta : DialogFragment() {

    interface OpcionesApuestaListener {
        fun apuestaFinal(cantidad: Int)
    }

    private var listener: OpcionesApuestaListener? = null
    private var nuevaApuesta = 0
    private var saldoJugador: Int = 0
    private lateinit var casillaApuesta: TextView
    private lateinit var btnMonedas: List<ImageButton>
    private lateinit var btnHacerApuesta: Button
    private lateinit var btnBorrarApuesta: Button
    private lateinit var agrandarBoton: Animation
    private lateinit var soundPool: SoundPool
    private var coinClickSoundId: Int = 0
    private var betPlacedSoundId: Int = 0

    fun crearApuestaFinalListener (listener: OpcionesApuestaListener) {
        this.listener = listener
    }

    fun setSaldoJugador(saldo:Int) {
        this.saldoJugador = saldo
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.apuesta_opciones, container, false)

        // Iniciamos SoundPool
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .build()

        //Cargamos los sonidos
        coinClickSoundId = soundPool.load(requireContext(), R.raw.coin_click2, 1)
        betPlacedSoundId = soundPool.load(requireContext(), R.raw.place_bet1, 1)

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) { // 0 means success
                if (sampleId == betPlacedSoundId) {
                    println("Bet placed sound loaded successfully!")
                }
            } else {
                println("Error loading sound with ID $sampleId")
            }
        }

        //Cargamos la views
        casillaApuesta = view.findViewById(R.id.casillaApuesta)
        btnBorrarApuesta = view.findViewById(R.id.btnBorrarApuesta)
        btnHacerApuesta = view.findViewById(R.id.btnHacerApuesta)
        btnMonedas = listOf(
            view.findViewById(R.id.btnMoneda5),
            view.findViewById(R.id.btnMoneda10),
            view.findViewById(R.id.btnMoneda25),
            view.findViewById(R.id.btnMoneda50),
            view.findViewById(R.id.btnMoneda100),
            view.findViewById(R.id.btnMoneda250)
        )

        agrandarBoton = AnimationUtils.loadAnimation(requireContext(), R.anim.agrandar)

        setupButtons()
        return view
    }

    private fun setupButtons() {
        setupBtnMonedas()
        setupBtnBorrarApuesta()
        setupBtnHacerApuesta()
    }

    //accion para los botones de las monedas
    private fun setupBtnMonedas() {
        btnMonedas.forEach { boton ->
            boton.setOnClickListener {
                boton.startAnimation(agrandarBoton) //animación de los botones monedas
                soundPool.play(coinClickSoundId, 1f, 1f, 0, 0, 1f)//add sonido de incrementar apuesta
                val importe = boton.contentDescription.toString().toInt()
                addApuesta(importe)
            }
        }
    }

    //accion para el boton de borrar apuesta
    private fun setupBtnBorrarApuesta() {
        btnBorrarApuesta.setOnClickListener {
            borrarApuesta()
        }
    }

    //accion para el boton de hacer apuesta
    private fun setupBtnHacerApuesta() {
        btnHacerApuesta.setOnClickListener {
            if (saldoJugador < nuevaApuesta){
                context?.let {
                    Toast.makeText(requireContext(), "No tienes saldo suficiente", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            else if (nuevaApuesta >= 5) {
                soundPool.play(betPlacedSoundId, 1f, 1f, 0, 0, 1f) // add sonido hacer apuesta
                println("Sound should play now")
                listener?.apuestaFinal(nuevaApuesta)
                dismiss() //se cierra el dialogo
            } else {
                context?.let {
                    Toast.makeText(requireContext(), "Debes hacer una Apuesta", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun addApuesta(cantidad: Int){
        nuevaApuesta += cantidad
        actualizarCasillaApuesta()
    }

    private fun borrarApuesta() {
        nuevaApuesta = 0
        actualizarCasillaApuesta()
    }

    private fun actualizarCasillaApuesta(){
        casillaApuesta.text = nuevaApuesta.toString()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.BOTTOM)

        val sharedPreferences = requireContext().getSharedPreferences("MusicPreferences", Context.MODE_PRIVATE)
        val musicEnabled = sharedPreferences.getBoolean("musicEnabled", true)
        val musicUriString = sharedPreferences.getString("selectedMusicUri", null)
        val uri = musicUriString?.let { Uri.parse(it) }

        if (musicEnabled && !GestorMusica.estaSonando()) {
            GestorMusica.iniciaMusica(requireContext(), uri)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Fondo transparente
        return dialog
    }

    //metodo para liberar memoria borrando los sonidos cargados
    override fun onDestroyView() {
        super.onDestroyView()
        soundPool.release()
    }
}