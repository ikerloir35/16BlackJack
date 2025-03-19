package com.example.droid.gestores

import android.annotation.SuppressLint
import android.util.Log
import com.example.droid.model.Bote
import com.example.droid.model.Jugador
import com.example.droid.modelgson.BoteGson
import com.example.droid.modelgson.UsuarioBoteGson
import com.example.droid.modelgson.UsuarioGson
import com.example.droid.retrofit.RetrofitClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

object GestorDBFirebase {

    var puntos = 0
    var ganador = ""
    fun registrarUsuarioEnDB(email: String) {
        val sanitizedEmail = email.replace(".", ",")

        // Obtener el usuario para verificar si ya existe
        val call = RetrofitClient.service.obtenerUsuario(sanitizedEmail)
        call.enqueue(object : Callback<UsuarioGson?> {
            override fun onResponse(call: Call<UsuarioGson?>, response: Response<UsuarioGson?>) {
                if (response.isSuccessful && response.body() != null) {

                    Log.d("Retrofit DB", "El usuario ya existe en la base de datos.")
                } else {

                    val nuevoUsuario = UsuarioGson(partidasGanadasMes = "0", puntuacionTotal = "0",monedas = 1000)

                    val crearUsuarioCall = RetrofitClient.service.actualizarUsuario(
                        sanitizedEmail, nuevoUsuario
                    )
                    crearUsuarioCall.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("Retrofit DB", "Usuario registrado con éxito en la db.")
                            } else {
                                Log.e(
                                    "Retrofit DB",
                                    "Error al registrar usuario: ${response.message()}"
                                )
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("Retrofit DB", "Fallo en la conexión: ${t.message}")
                        }
                    })
                }
            }
            override fun onFailure(call: Call<UsuarioGson?>, t: Throwable) {
                Log.e("Retrofit DB", "Fallo en la conexión: ${t.message}")
            }
        })
    }
    fun actualizarScoreUsuario(email: String, puntosGanados: Int,apuesta :Int) {
        val sanitizedEmail = email.replace(".", ",")

        // Obtener el usuario actual para verificar su puntuación total
        val call = RetrofitClient.service.obtenerUsuario(sanitizedEmail)
        call.enqueue(object : Callback<UsuarioGson?> {
            override fun onResponse(call: Call<UsuarioGson?>, response: Response<UsuarioGson?>) {
                if (response.isSuccessful) {
                    val usuarioGson = response.body()
                    val puntuacionTotalActual = usuarioGson?.puntuacionTotal?.toIntOrNull() ?: 0

                    // Sumar los puntos ganados a la puntuación actual
                    val nuevaPuntuacionTotal = puntuacionTotalActual + puntosGanados

                    // Actualizar el usuario con la nueva puntuación total
                    val usuarioActualizado = UsuarioGson(
                        monedas = apuesta,
                        partidasGanadasMes = usuarioGson?.partidasGanadasMes ?: "0",
                        puntuacionTotal = nuevaPuntuacionTotal.toString()

                    )

                    val actualizarCall =
                        RetrofitClient.service.actualizarUsuario(sanitizedEmail, usuarioActualizado)
                    actualizarCall.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d(
                                    "Retrofit DB",
                                    "Puntuación del usuario actualizada correctamente: $nuevaPuntuacionTotal"
                                )
                            } else {
                                Log.e(
                                    "Retrofit DB",
                                    "Error al actualizar la puntuación: ${response.message()}"
                                )
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("Retrofit DB", "Fallo en la conexión al actualizar: ${t.message}")
                        }
                    })
                } else {
                    Log.e("Retrofit DB", "Error al obtener el usuario: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UsuarioGson?>, t: Throwable) {
                Log.e("Retrofit DB", "Fallo en la conexión al obtener usuario: ${t.message}")
            }
        })
    }

    fun actualizarPartidasGanadasUsuario(email: String) {
        val sanitizedEmail = email.replace(".", ",")

        val call = RetrofitClient.service.obtenerUsuario(sanitizedEmail)
        //se obtiene el usuario
        call.enqueue(object : Callback<UsuarioGson?> {
            override fun onResponse(call: Call<UsuarioGson?>, response: Response<UsuarioGson?>) {
                if (response.isSuccessful) {
                    val usuarioGson = response.body()
                    val partidasGanadasMesActual = usuarioGson?.partidasGanadasMes?.toIntOrNull() ?: 0
                    val nuevasPartidasGanadasMes = partidasGanadasMesActual + 1
                    val monedasJugador = usuarioGson?.monedas ?:0
                    val usuarioActualizado = UsuarioGson(
                        monedas = monedasJugador,
                        partidasGanadasMes = nuevasPartidasGanadasMes.toString(),
                        puntuacionTotal = usuarioGson?.puntuacionTotal ?: "0"
                    )

                    val actualizarCall = RetrofitClient.service.actualizarUsuario(sanitizedEmail, usuarioActualizado)
                    actualizarCall.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("Retrofit DB", "Partidas ganadas actualizadas correctamente: $nuevasPartidasGanadasMes")
                            } else {
                                Log.e("Retrofit DB", "Error al actualizar partidas ganadas: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("Retrofit DB", "Fallo en la conexión al actualizar: ${t.message}")
                        }
                    })
                } else {
                    Log.e("Retrofit DB", "Error al obtener el usuario: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UsuarioGson?>, t: Throwable) {
                Log.e("Retrofit DB", "Fallo en la conexión al obtener usuario: ${t.message}")
            }
        })
    }
    fun actualizarBote() {
        val call = RetrofitClient.service.obtenerBote()

        call.enqueue(object : Callback<BoteGson> {
            override fun onResponse(call: Call<BoteGson>, response: Response<BoteGson>) {
                if (response.isSuccessful) {
                    val bote = response.body()

                    if (bote != null) {
                        // Incrementar los valores
                        val nuevoTotalPartidasPerdidas = bote.totalPartidasPerdidas + 1
                        val nuevoPremioAcumulado = bote.premioAcumulado + 5

                        // Crear un nuevo objeto BoteGson con los valores actualizados
                        val boteActualizado = BoteGson(
                            Ganador = bote.Ganador,
                            mes = bote.mes,
                            totalPartidasPerdidas = nuevoTotalPartidasPerdidas,
                            premioAcumulado = nuevoPremioAcumulado
                        )

                        // Enviar la actualización a Firebase
                        val actualizarCall = RetrofitClient.service.actualizarBote(boteActualizado)
                        actualizarCall.enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d("Retrofit DB", "Bote actualizado correctamente.")
                                } else {
                                    Log.e("Retrofit DB", "Error al actualizar el bote: ${response.message()}")
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.e("Retrofit DB", "Fallo en la conexión al actualizar el bote: ${t.message}")
                            }
                        })
                    } else {
                        Log.e("Retrofit DB", "Error: El bote no existe en la base de datos.")
                    }
                } else {
                    Log.e("Retrofit DB", "Error al obtener el bote: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BoteGson>, t: Throwable) {
                Log.e("Retrofit DB", "Fallo en la conexión al obtener el bote: ${t.message}")
            }
        })
    }

    fun botea0(){
        val call = RetrofitClient.service.obtenerBote()
        call.enqueue(object : Callback<BoteGson> {
            override fun onResponse(call: Call<BoteGson>, response: Response<BoteGson>) {
                if (response.isSuccessful) {
                    val bote = response.body()

                    if (bote != null) {
                        // Incrementar los valores


                        // Crear un nuevo objeto BoteGson con los valores actualizados
                        val boteActualizado = BoteGson(
                            Ganador = "Botereboot",
                            mes = LocalDate.now().toString(),
                            premioAcumulado = 0,
                            totalPartidasPerdidas = 0

                        )

                        // Enviar la actualización a Firebase
                        val actualizarCall = RetrofitClient.service.actualizarBote(boteActualizado)
                        actualizarCall.enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d("Retrofit DB", "Bote actualizado correctamente.")
                                } else {
                                    Log.e("Retrofit DB", "Error al actualizar el bote: ${response.message()}")
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.e("Retrofit DB", "Fallo en la conexión al actualizar el bote: ${t.message}")
                            }
                        })
                    } else {
                        Log.e("Retrofit DB", "Error: El bote no existe en la base de datos.")
                    }
                } else {
                    Log.e("Retrofit DB", "Error al obtener el bote: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BoteGson>, t: Throwable) {
                Log.e("Retrofit DB", "Fallo en la conexión al obtener el bote: ${t.message}")
            }
        })
    }


    fun traspasarbote() {
        val call = RetrofitClient.service.obtenerUsuarios()
            call.enqueue(object : Callback<Map<String, UsuarioBoteGson>> {
                override fun onResponse(call: Call<Map<String, UsuarioBoteGson>>, response: Response<Map<String, UsuarioBoteGson>>) {
                    if (response.isSuccessful) {
                        val usuariosMap = response.body()
                        var valormaximo = 0
                        usuariosMap?.forEach { (nombreNodo) ->
                            val call = RetrofitClient.service.obtenerUsuario(nombreNodo)
                            call.enqueue(object : Callback<UsuarioGson?> {
                                override fun onResponse(call: Call<UsuarioGson?>, response: Response<UsuarioGson?>) {
                                    if (response.isSuccessful) {
                                        val UsuarioBoteGson = response.body()
                                        val puntuacionTotalActual = UsuarioBoteGson?.puntuacionTotal?.toIntOrNull() ?: 0

                                        // Actualizar el usuario con la nueva puntuación total
                                        val usuarioActualizado = UsuarioBoteGson(
                                            mail = nombreNodo,
                                            puntuacionTotal = puntuacionTotalActual

                                        )

                                            if (usuarioActualizado.puntuacionTotal > valormaximo) {
                                                Log.e("Ganador", "$nombreNodo")
                                                valormaximo = usuarioActualizado.puntuacionTotal
                                                actualizarBoteconGanador(nombreNodo)
                                            }






                                    } else {
                                        Log.e("Retrofit DB", "Error al obtener el usuario: ${response.message()}")
                                    }
                                }

                                override fun onFailure(call: Call<UsuarioGson?>, t: Throwable) {
                                    Log.e("Retrofit DB", "Fallo en la conexión al obtener usuario: ${t.message}")
                                }
                            })

                        }

                    } else {
                        Log.e("puntajes", "Error en la respuesta: ${response.errorBody()}")
                    }

                }

                override fun onFailure(call: Call<Map<String, UsuarioBoteGson>>, t: Throwable) {
                    Log.e("puntajes", "Error en la solicitud: ${t.message}")
                }
            })


    }

    fun actualizarBoteconGanador(nombreNodo: String) {
        val call = RetrofitClient.service.obtenerBote()

        call.enqueue(object : Callback<BoteGson> {
            override fun onResponse(call: Call<BoteGson>, response: Response<BoteGson>) {
                if (response.isSuccessful) {
                    val bote = response.body()

                    if (bote != null) {
                        // Incrementar los valores
                        val fecha = LocalDate.now().toString()



                                // Crear un nuevo objeto BoteGson con los valores actualizados
                               val boteActualizado = BoteGson(
                                    Ganador = nombreNodo,
                                    mes = bote.mes,
                                    totalPartidasPerdidas = bote.totalPartidasPerdidas,
                                    premioAcumulado = bote.premioAcumulado
                                )

                        ganador = nombreNodo


                            // Enviar la actualización a Firebase
                            val actualizarCall =
                                RetrofitClient.service.actualizarBote(boteActualizado)
                            actualizarCall.enqueue(object : Callback<Void> {
                                override fun onResponse(
                                    call: Call<Void>,
                                    response: Response<Void>
                                ) {
                                    if (response.isSuccessful) {
                                        Log.d("Retrofit DB", "Bote actualizado correctamente.")

                                    } else {
                                        Log.e(
                                            "Retrofit DB",
                                            "Error al actualizar el bote: ${response.message()}"
                                        )
                                    }

                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.e(
                                        "Retrofit DB",
                                        "Fallo en la conexión al actualizar el bote: ${t.message}"
                                    )
                                }

                            })


                    } else {
                        Log.e("Retrofit DB", "Error: El bote no existe en la base de datos.")
                    }
                } else {
                    Log.e("Retrofit DB", "Error al obtener el bote: ${response.message()}")
                }

        }

            override fun onFailure(call: Call<BoteGson>, t: Throwable) {
                Log.e("Retrofit DB", "Fallo en la conexión al obtener el bote: ${t.message}")
            }
        })
    }

    fun moverBote(){
        val call = RetrofitClient.service.obtenerBote()

        call.enqueue(object : Callback<BoteGson> {
            override fun onResponse(call: Call<BoteGson>, response: Response<BoteGson>) {
                if (response.isSuccessful) {
                    val bote = response.body()

                    if (bote != null) {
                        // Incrementar los valores
                        val call = RetrofitClient.service.obtenerUsuario(bote.Ganador)
                        //se obtiene el usuario
                        call.enqueue(object : Callback<UsuarioGson?> {
                            override fun onResponse(call: Call<UsuarioGson?>, response: Response<UsuarioGson?>) {
                                if (response.isSuccessful) {

                                    val usuarioGson = response.body()
                                    val partidasGanadasMesActual = usuarioGson?.partidasGanadasMes?.toIntOrNull() ?: 0
                                    val puntucionTotalActual = usuarioGson?.puntuacionTotal?.toIntOrNull() ?: 0
                                    val monedasJugador = usuarioGson?.monedas?:0
                                    val puntosnuevos = monedasJugador + bote.premioAcumulado




                                            val usuarioActualizado = UsuarioGson(
                                                monedas = puntosnuevos,
                                                partidasGanadasMes = partidasGanadasMesActual.toString(),
                                                puntuacionTotal = puntucionTotalActual.toString()
                                            )

                                            val actualizarCall =
                                                RetrofitClient.service.actualizarUsuario(
                                                    bote.Ganador,
                                                    usuarioActualizado
                                                )
                                            actualizarCall.enqueue(object : Callback<Void> {
                                                override fun onResponse(
                                                    call: Call<Void>,
                                                    response: Response<Void>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        Log.d(
                                                            "Retrofit DB",
                                                            "Puntos actulizados correctamente: $puntosnuevos , ${bote.Ganador}, $puntucionTotalActual "

                                                        )




                                                    } else {
                                                        Log.e(
                                                            "Retrofit DB",
                                                            "Error al actualizar partidas ganadas: ${response.message()}"
                                                        )
                                                    }
                                                }


                                                override fun onFailure(
                                                    call: Call<Void>,
                                                    t: Throwable
                                                ) {
                                                    Log.e(
                                                        "Retrofit DB",
                                                        "Fallo en la conexión al actualizar: ${t.message}"
                                                    )
                                                }
                                            })




                                } else {
                                    Log.e("Retrofit DB", "Error al obtener el usuario: ${response.message()}")
                                }
                            }

                            override fun onFailure(call: Call<UsuarioGson?>, t: Throwable) {
                                Log.e("Retrofit DB", "Fallo en la conexión al obtener usuario: ${t.message}")
                            }
                        })

                    } else {
                        Log.e("Retrofit DB", "Error: El bote no existe en la base de datos.")
                    }
                } else {
                    Log.e("Retrofit DB", "Error al obtener el bote: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BoteGson>, t: Throwable) {
                Log.e("Retrofit DB", "Fallo en la conexión al obtener el bote: ${t.message}")
            }
        })
    }


    fun comprobarfecha(){

            val call = RetrofitClient.service.obtenerBote()


            call.enqueue(object : Callback<BoteGson> {
                override fun onResponse(call: Call<BoteGson>, response: Response<BoteGson>) {
                    if (response.isSuccessful) {
                        val bote = response.body()

                        if (bote != null) {
                            // Incrementar los valores

                            val fecha = LocalDate.now().toString()

                            if(fecha!= bote.mes){
                                    moverBote()
                                    botea0()
                            }


                        } else {
                            Log.e("Retrofit DB", "Error: El bote no existe en la base de datos.")
                        }
                    } else {
                        Log.e("Retrofit DB", "Error al obtener el bote: ${response.message()}")
                    }


                }

                override fun onFailure(call: Call<BoteGson>, t: Throwable) {
                    Log.e("Retrofit DB", "Fallo en la conexión al obtener el bote: ${t.message}")
                }
            })
    }
}