package com.example.droid.model

class Baraja (var baraja : MutableList<Carta> = mutableListOf()){
    fun generarBaraja(): MutableList<Carta> {

        val palos = listOf("Corazones", "Diamantes", "Picas","Treboles")
        val nombresCartas = listOf("As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val valoresCartas = listOf(11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10)
        val posicionCarta = listOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52)
        var posi:Int = 0
        //val baraja = mutableListOf<Carta>()
        for (palo in palos) {
            for (i in nombresCartas.indices) {

                val carta = Carta(valoresCartas[i], nombresCartas[i], palo,posicionCarta[posi])
                posi +=1
                baraja.add(carta)
            }
        }
        return baraja
    }
    fun barajar() {
        baraja.shuffle()
    }

    //Funcion para repartirCarta y quitarla de la baraja

    fun repartirCarta(): Carta {
        return baraja.removeAt(0) //Quitamos la primera carta de la baraja.
    }

    fun repartirManoInicial(): List<Carta> {
        return listOf(repartirCarta(), repartirCarta())
    }
}
