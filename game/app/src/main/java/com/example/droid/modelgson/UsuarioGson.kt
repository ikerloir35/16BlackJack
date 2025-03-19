package com.example.droid.modelgson

import com.google.gson.annotations.SerializedName

data class UsuarioGson(

    @SerializedName("Monedas") val monedas : Int,
    @SerializedName("PartidasGanadasMes") val partidasGanadasMes: String,
    @SerializedName("PuntuacionTotal") val puntuacionTotal: String

)
