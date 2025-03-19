package com.example.droid

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.droid.databinding.PartidaOpcionesBinding
import com.example.droid.gestores.GestorMusica

class PartidaOpcionesDialogo : DialogFragment(){

    interface PartidaOpcionesListener {


        fun onJugarPartida()
        fun onCambiarApuesta()
        fun onMenuPrincipal()
        fun onCapturarPantalla()
    }

    private var listener: PartidaOpcionesListener? = null
    private var _binding: PartidaOpcionesBinding? = null
    private val binding get() = _binding!!

    fun crearPartidaOpcionesListener(listener: PartidaOpcionesListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PartidaOpcionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnJugar.setOnClickListener {
            listener?.onJugarPartida()
            dismiss()
        }

        binding.btnApuesta.setOnClickListener {
            listener?.onCambiarApuesta()
            dismiss()
        }

        binding.btnMenu.setOnClickListener {
            listener?.onMenuPrincipal()
            dismiss()
        }
        binding.btnCaptura.setOnClickListener {
            (activity as? PantallaPartida)?.capturaYGuardarPantalla(requireContext(), requireActivity().window.decorView.rootView)
            dismiss()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}