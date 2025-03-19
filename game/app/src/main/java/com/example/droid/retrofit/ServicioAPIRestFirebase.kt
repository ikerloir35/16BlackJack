package com.example.droid.retrofit

import com.example.droid.model.Bote
import com.example.droid.model.Jugador
import com.example.droid.modelgson.BoteGson
import com.example.droid.modelgson.DBGson
import com.example.droid.modelgson.UsuarioBoteGson
import com.example.droid.modelgson.UsuarioGson
import retrofit2.Call
import retrofit2.http.*

interface ServicioAPIRestFirebase {

    // Obtener toda la db
    @GET(".json")
    fun obtenerBaseDeDatos(): Call<DBGson>

    @GET("Bote.json")
    fun obtenerBote(): Call<BoteGson>

    // Obtener un usuario específico filtrado por email
    @GET("Usuarios/{email}.json")
    fun obtenerUsuario(@Path("email") email: String): Call<UsuarioGson>

    // Actualizar el nodo Bote
    @PATCH("Bote.json")
    fun actualizarBote(@Body bote: BoteGson): Call<Void>

    // Actualizar un usuario específico
    @PATCH("Usuarios/{email}.json")
    fun actualizarUsuario(
        @Path("email") email: String,
        @Body usuario: UsuarioGson
    ): Call<Void>

    @GET("Usuarios.json")
    fun obtenerUsuarios(): Call<Map<String, UsuarioBoteGson>>

    @GET("Usuarios/{email}.json")
    fun obtenerUsuarioBote(@Path("email") email: String): Call<UsuarioBoteGson>

    @PATCH("Bote.json")
    fun boteganado(@Body bote: BoteGson): Call<Void>

}