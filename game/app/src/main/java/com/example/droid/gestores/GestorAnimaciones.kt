package com.example.droid.gestores

import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

object GestorAnimaciones {

    fun mostrarFin(imagen: Int, resultadosLayout: LinearLayout, resultadoView: ImageView) {

        resultadoView.setImageResource(imagen)

        resultadosLayout.visibility = View.VISIBLE

        // Animación de Fade
        val fadeIn = ObjectAnimator.ofFloat(resultadoView, "alpha", 0f, 1f)
        fadeIn.duration = 1000
        fadeIn.start()

        // Animación de escalado
        val scaleX = ObjectAnimator.ofFloat(resultadoView, "scaleX", 0.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(resultadoView, "scaleY", 0.5f, 1f)

        scaleX.duration = 1000
        scaleY.duration = 1000

        scaleX.start()
        scaleY.start()

        // interpolador para mejorar la fluided de la animación.
        fadeIn.interpolator = AccelerateDecelerateInterpolator()
        scaleX.interpolator = AccelerateDecelerateInterpolator()
        scaleY.interpolator = AccelerateDecelerateInterpolator()
    }
}