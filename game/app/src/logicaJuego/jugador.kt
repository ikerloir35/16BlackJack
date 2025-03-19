package org.example

class jugador (
    var nombre: String,
    var saldo: Int,
    var mail: String,
    var apuesta: Int = 100
) {
    fun apostar(cantidad:Int): Int {
        val realizarApuesta = false

        while (cantidad >= saldo) {
            println("Saldo insuficinete para apostar $cantidad")
            return cantidad
        }
        else {

        }
    }
}