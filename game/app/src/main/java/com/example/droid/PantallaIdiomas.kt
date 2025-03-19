package com.example.droid


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.example.droid.gestores.GestorIdioma

class PantallaIdiomas : AppCompatActivity(){

    private lateinit var botonIngles : RadioButton
    private lateinit var botonEspanol : RadioButton
    private lateinit var botonMenuprincipal : Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_idiomas)
        var email =intent.getStringExtra("email")

        botonIngles = findViewById(R.id.radioEnglish)
        botonEspanol = findViewById(R.id.radioSpanish)
        botonMenuprincipal = findViewById(R.id.botonMenuPrincipal4)
        botonIngles.setOnClickListener(){
        GestorIdioma.cambiarIdioma(this,"en")
        GestorIdioma.guardarPreferenciasLenguaje(this, "en")
        recreate()
        }

        botonEspanol.setOnClickListener(){
        GestorIdioma.cambiarIdioma(this,"es")
        GestorIdioma.guardarPreferenciasLenguaje(this, "es")
        recreate()
        }
        botonMenuprincipal.setOnClickListener(){
            var intent = Intent(this,MenuPrincipal::class.java)
            intent.putExtra("email",email)
            startActivity(intent)
        }

    }

}