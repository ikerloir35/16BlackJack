package com.example.droid.model

import java.time.LocalDate

class Partida(var estado: Boolean = true,
              var apuesta: Int =0,
              var mano1: Int = 0,
              var mano2: Int =0,
              var dealer: Int=0,
              var cartas1: List<Int> = emptyList(),
              var cartas2: List<Int> = emptyList(),
              var cartasD: List<Int> = emptyList(),
              var fecha: LocalDate = LocalDate.now()

) {
    fun pedirCarta(){}

    fun doblar(){}

    fun split(){}

    fun plantarse(){}

    fun abandonar(){}

    fun resultado(): Int{
        var puntos = 0;
        return puntos;
    }
}