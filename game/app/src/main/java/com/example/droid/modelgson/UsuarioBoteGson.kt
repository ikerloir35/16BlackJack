package com.example.droid.modelgson

import com.google.gson.annotations.SerializedName

data class UsuarioBoteGson(
    @SerializedName("mail") val mail: String,
    @SerializedName("puntuacionTotal") val puntuacionTotal: Int
)

