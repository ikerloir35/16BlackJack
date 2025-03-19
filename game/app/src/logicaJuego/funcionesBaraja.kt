package org.example

// Function para crear una nueva baraja en cada mano 

fun generarBaraja(): MutableList<Carta> {

    val palos = listOf("Corazones", "Diamantes", "Treboles", "Picas")
    val nombresCartas = listOf("As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    val valoresCartas = listOf(11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10)
    val imagenCarta = (1..52).map {"c$it"} //creamos la lista de Strings con los mismos nombres
    //y mismo orden de las imagenes de las cartas

    val baraja = mutableListOf<Carta>()
    var index = 0

    for (palo in palos) {
        for (i in nombresCartas.indices) {
            val carta = Carta(valoresCartas[i], nombresCartas[i], palo, imagenCarta[index])
            baraja.add(carta)
            index++
        }
    }
    return baraja
}

//Funcion para barajar las cartas de la nueva baraja

fun barajar(baraja: MutableList<Carta>) {
    baraja.shuffle()
}

//Funcion para repartirCarta y quitarla de la baraja

fun repartirCarta(baraja: MutableList<Carta>): Carta {
    return baraja.removeAt(0) //Quitamos la primera carta de la baraja.
}