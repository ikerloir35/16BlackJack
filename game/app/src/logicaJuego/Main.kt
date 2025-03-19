package org.example

fun main() {
    val baraja: MutableList<Carta> = generarBaraja()
    //imprimirCartas(baraja) y numero de cartas en el mazo
    //baraja.forEach { println(it) }
    //println(baraja.size)

    barajar(baraja)
    //imprimirCartas(barajaRandom) y numero de cartas en el mazo
    //baraja.forEach { println(it) }
    //println(baraja.size)

    //variables de manos jugador y dealer
    val manoUnoJugador = mutableListOf<Carta>()
    val manoDosJugador = mutableListOf<Carta>()
    val manoDealer = mutableListOf<Carta>()

    //reparto de cartas
    manoUnoJugador.add(repartirCarta(baraja))
    manoUnoJugador.add(repartirCarta(baraja))
    manoDealer.add(repartirCarta(baraja))
    manoDealer.add(repartirCarta(baraja))


    //print de la mano del jugador
    println("\nMano jugador:")
    manoUnoJugador.forEach { println(it) }
    println("Valor de la mano del jugador: ${calcularValorMano(manoUnoJugador)}")
    //print de la mano del dealer. Solo imprimimos la primera carta, la segunda permanece oculta.
    println("\nMano Dealer")
    println(manoDealer[0])
    println("Valor de la mano del Dealer: ${manoDealer[0].valor}")
    //print de las cartas restantes
    println("\nCartas restantes en el mazo:")
    //baraja.forEach {println(it)}
    println(baraja.size)

    //Turno jugador. Solo opciones BlackJack o Planta o Nueva Carta

    var jugadorTieneBlackJack = false
    var jugadorSePlanta = false

    if (calcularValorMano(manoUnoJugador) == 21) {
        jugadorTieneBlackJack = true
        println("Vaya suerte que tienes")
    }
    else {
        while (!jugadorSePlanta && (calcularValorMano(manoUnoJugador) < 21)) {
            println("Quieres pedir otra carta? Si o No: ")
            val respuesta = readLine()

            if (respuesta == "Si") {
                manoUnoJugador.add(repartirCarta(baraja))
                println("\nMano jugador:")
                manoUnoJugador.forEach { println(it) }
                println("Valor de la mano del jugador: ${calcularValorMano(manoUnoJugador)}")
            }
            else {
                jugadorSePlanta = true
            }
        }
    }
    //Turno Dealer
    while (calcularValorMano(manoDealer) <= 17) {
        manoDealer.add(repartirCarta(baraja))
    }

    //Resultado final mano

    var contadorFinalManoDealer: Int = calcularValorMano(manoDealer)
    var contadorFinalManoUnoJugador: Int = calcularValorMano(manoUnoJugador)
    println("Mano del Dealer: $manoDealer")
    println("Score del Dealer: $contadorFinalManoDealer")
    println("Mano del Jugador: $manoUnoJugador")
    println("Score del Jugador: $contadorFinalManoUnoJugador")

    if (contadorFinalManoUnoJugador > 21) {
        println("Has perdido!!!")
    }
    else if (contadorFinalManoDealer > 21 ) {
        println("Has ganado!!!")
    }
    else if (contadorFinalManoDealer == contadorFinalManoUnoJugador) {
        println("Empate")
    }
    else if (contadorFinalManoDealer < contadorFinalManoUnoJugador) {
        println("Has ganado!!!")
    }
    else {
        println("Has pedido!!!")
    }
}