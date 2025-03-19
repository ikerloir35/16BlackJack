package org.example


data class Carta(
    val valor: Int,
    val nombre: String,
    val palo: String
    val imagenCarta: String
)

class Apuesta(var cantidad: Int = 100) {

    //función para cambiar la apuesta

    fun cambiarApuesta(): Int {
        //reseteamos el valor de la apuesta a 0
        var scanner = Scanner(System.`in`)
        var cantidad = 0
        println("Apuesta actual: $cantidad") //cambiar pantalla apuesta

        while (true){

            //asignar objeto en la pantalla
            println("Suma tu moneda:")
            println("1. Agregar moneda de 5")
            println("2. Agregar moneda de 10")
            println("3. Agregar moneda de 25")
            println("4. Agregar moneda de 50")
            println("5. Agregar moneda de 100")
            println("6. Agregar moneda de 250")
            println("7. Borrar Apuesta")
            println("8. Jugar")

            //asignar función al objeto en la pantalla
            val opcion = scanner.nextInt()

            when (opcion) {
                1 -> cantidad +=5
                2 -> cantidad +=10
                3 -> cantidad +=25
                4 -> cantidad +=50
                5 -> cantidad +=100
                6 -> cantidad +=250
                7 -> cantidad = 0 //reseteamos la apuesta a 0
                8 -> break
                else -> println("Opcion Invalida")
            }
        }
        return cantidad
    }
}