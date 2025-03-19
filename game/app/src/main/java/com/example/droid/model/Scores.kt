package com.example.droid.model

import java.util.Date


enum class EstadoPartida{GANADA,EMPATE,PERDIDA}
data class Scores(
    val fecha: Date,
    val estado: EstadoPartida,
    val scorignInicial: Int,
    val scorignFinal: Int,
    val jugador: String
)
