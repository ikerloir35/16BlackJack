package com.example.droid

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.droid.gestores.GestorMusica


class PantallaSettings : AppCompatActivity() {
    private lateinit var botonMusicaOnOff: Button
    private lateinit var botonSeleccionMusica: Button
    private lateinit var botonMenuPrincipal: Button
    private lateinit var botonIdiomas: Button


    private var musicaEncendida: Boolean = false
    private val PICK_AUDIO_REQUEST = 1 // Código para identificar la selección del audio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        GestorMusica.configLlamada(this)

        verificarPermisoAlmacenamiento() //comprobamos que puede acceder a la biblioteca del dispositivo

        var email =intent.getStringExtra("email")
        botonMusicaOnOff = findViewById(R.id.button_music_on_off)
        botonSeleccionMusica = findViewById(R.id.button_select_music)
        botonMenuPrincipal = findViewById(R.id.botonMenuPrincipal)
        botonIdiomas = findViewById(R.id.button_select_languages)

        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        musicaEncendida = sharedPreferences.getBoolean("musicEnabled", false)

        if (musicaEncendida) {
            botonMusicaOnOff.text = getString(R.string.musicoff)
            val musicUriString = sharedPreferences.getString("selectedMusicUri", null)
            if (musicUriString != null) {
                val uri = Uri.parse(musicUriString)
                GestorMusica.iniciaMusica(this, uri)}
        } else {
            botonMusicaOnOff.text = getString(R.string.musicon)
        }
        //activar o desactivar música
        botonMusicaOnOff.setOnClickListener {
            musicaOnOff()
        }

        // selecciona la música de la libreria del dispositivio
        botonSeleccionMusica.setOnClickListener {
            seleccionarDeBiblioteca()
        }
        botonMenuPrincipal.setOnClickListener(){
            var intent = Intent(this,MenuPrincipal::class.java)
            intent.putExtra("email",email)
            startActivity(intent)
        }
        botonIdiomas.setOnClickListener(){
            var intent = Intent(this,PantallaIdiomas::class.java)
            intent.putExtra("email",email)
            startActivity(intent)
        }
    }


    fun musicaOnOff() {
        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (musicaEncendida) {
            GestorMusica.detenMusica()
            botonMusicaOnOff.text = getString(R.string.musicon)
            editor.putBoolean("musicEnabled", false)
        } else {
            val musicUriString = sharedPreferences.getString("selectedMusicUri", null)
            val uri = musicUriString?.let { Uri.parse(it) }
            GestorMusica.iniciaMusica(this, uri)
            botonMusicaOnOff.text = getString(R.string.musicoff)
            editor.putBoolean("musicEnabled", true)
        }
        editor.apply()
        musicaEncendida = !musicaEncendida
    }

    //Función para abrir la biblioteca y seleccionar la musica
    private fun seleccionarDeBiblioteca() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_AUDIO_REQUEST)
    }

    // Manejar el resultado de la selección de música
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedAudioUri: Uri? = data.data
            selectedAudioUri?.let {
                reproducirMusica(it)
            }
        }
    }

    // Reproducir la música seleccionada
    private fun reproducirMusica(uri: Uri) {
        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        sharedPreferences.edit().putString("selectedMusicUri", uri.toString()).apply()

        GestorMusica.detenMusica()
        GestorMusica.iniciaMusica(this, uri)

        musicaEncendida = true
        botonMusicaOnOff.text = getString(R.string.musicoff)
    }

    private fun verificarPermisoAlmacenamiento() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //botonSeleccionMusica.isEnabled = true
        } else {
            //botonSeleccionMusica.isEnabled = false
        }
    }
}
