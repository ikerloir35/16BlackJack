package com.example.droid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.droid.gestores.GestorDBFirebase
import com.example.droid.gestores.GestorIdioma
import com.example.droid.gestores.GestorMusica
import com.example.droid.logica.determinarganador
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class MainActivity : AppCompatActivity() {
    private lateinit var authentication: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        val savedLanguage = GestorIdioma.getLenguajeSalvado(this)
        GestorIdioma.cambiarIdioma(this,savedLanguage)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        //val emailEditText = findViewById<EditText>(R.id.email)
        //var botonLogin = findViewById<Button>(R.id.loginbutton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inicial)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        /*botonLogin.setOnClickListener{
            Log.d("Main Activity","Boton de login clickado")
            val email = emailEditText.text.toString()
            Log.d("email entrado", email)
            var intent = Intent(this,MenuPrincipal::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }*/
        //autenticación en firebase con google id
        var botonLoginGoogle = findViewById<com.google.android.gms.common.SignInButton>(R.id.btnGoogleSignIn)
        authentication = FirebaseAuth.getInstance()

        val googleOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,googleOptions)

        botonLoginGoogle.setOnClickListener(){
            val login = googleSignInClient.signInIntent
            determinarganador()
            startActivityForResult(login,RC_SIGN_IN)
            
        }



    }

    override fun onStop() {
        super.onStop()

        // Solo detenemos la música si el usuario desactiva la música
        val sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE)
        val musicEnabled = sharedPreferences.getBoolean("musicEnabled", true)

        if (!musicEnabled) {
            GestorMusica.detenMusica()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        authentication.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Login FB","login correcto en la autenticacion firebase")
                    val user = authentication.currentUser
                    var email = user?.email ?: return@addOnCompleteListener

                    //Registro del usuario ne la db RealTime Firebase
                    GestorDBFirebase.registrarUsuarioEnDB(email)

                    //Navegación al menú principal
                    val intent = Intent(this, MenuPrincipal::class.java)
                    intent.putExtra("email", user?.email)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d("Login FB","error en la autenticacion firebase")
                }
            }
    }
}