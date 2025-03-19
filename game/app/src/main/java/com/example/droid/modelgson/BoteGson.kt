package com.example.droid.modelgson

import com.google.gson.annotations.SerializedName

data class BoteGson(
    @SerializedName("Ganador") val Ganador: String,
    @SerializedName("mes") val mes: String,
    @SerializedName("premioAcumulado") val premioAcumulado: Int,
    @SerializedName("totalPartidasPerdidas") val totalPartidasPerdidas: Int

)