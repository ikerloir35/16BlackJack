package com.example.droid.model

data class Apuestas(
    var valor: Int = 50
){
    fun cambiarApuesta(nuevoValor: Int){
        valor = nuevoValor
    }

    override fun toString(): String {
        return "Apuestas(valor=$valor)"
    }
}
