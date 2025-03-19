package com.example.droid.gestores

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast


object GestorCapturas {

    private var capturaBitmap: Bitmap? = null


    fun guardarCaptura(context: Context, uri: Uri, bitmap: Bitmap) {
        if (!esUriCorrecto(context,uri)){
            Log.e("Guardar captura","El Uri no es corecto")
            Toast.makeText(context,"Uri no valido",Toast.LENGTH_LONG).show()
            return
        }
        if (bitmap == null){
            Log.e("Bitmap","El problema esta en el bitmap")
            Toast.makeText(context,"El problema esta en el bitmap",Toast.LENGTH_LONG).show()
            return
        }
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            Log.e("Guardar captura", "El Bitmap tiene dimensiones inválidas.")
            Toast.makeText(context, "El Bitmap tiene dimensiones inválidas.", Toast.LENGTH_LONG).show()
            return
        }
        try {
            val resolver = context.contentResolver
            resolver.openOutputStream(uri)?.use { outputStream ->
                val success = capturaBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                if (success){
                    Toast.makeText(context, "Captura guardada correctamente.", Toast.LENGTH_SHORT).show()
                    Log.d("Guardar captura", "Bitmap guardado exitosamente en URI: $uri")

                }
                if (!success){
                    Log.e("Guardar captura","No ha comprimido")
                    Toast.makeText(context, "Error al comprimir la imagen.", Toast.LENGTH_SHORT).show()
                }
            }?:run{
                Log.e("Guardar captura", "No se pudo obtener OutputStream del URI.")
                Toast.makeText(context, "Error: No se pudo obtener OutputStream del URI.", Toast.LENGTH_SHORT).show()}
            //Toast.makeText(context, "Captura guardada.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al guardar la captura.", Toast.LENGTH_SHORT).show()
        }
    }


    fun capturarPantalla(rootView: View):Bitmap? {
        return try {
            if (rootView.width > 0 && rootView.height > 0) {
                val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bitmap)
                rootView.draw(canvas)
                capturaBitmap = bitmap
                bitmap
            } else {
                throw Exception("Dimension incorrecta")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun esUriCorrecto(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
