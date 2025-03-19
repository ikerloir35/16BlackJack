package org.example

import com.example.droid.model.Carta


//Funcion para sumar los valores de las cartas en juego. El As puede valer 11 o 1

fun calcularValorMano(mano: List<Carta>): Int {
    var valorTotal = 0
    var numeroAses = 0

    for (carta in mano) {
        valorTotal += carta.valor
        if (carta.nombre == "As") {
            numeroAses++
        }
    }

    //Ajustar el valor del As si nos hemos pasado de 21
    while (valorTotal > 21 && numeroAses > 0) {
        valorTotal -= 10
        numeroAses--
    }

    return valorTotal
}
