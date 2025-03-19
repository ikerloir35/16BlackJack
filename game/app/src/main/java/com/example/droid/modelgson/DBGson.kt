package com.example.droid.modelgson

import com.google.gson.annotations.SerializedName

data class DBGson(
    @SerializedName("Bote") val bote: BoteGson,
    @SerializedName("Usuarios") val usuarios: Map<String, UsuarioGson>
)
