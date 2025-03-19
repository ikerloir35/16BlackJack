package com.example.droid.logica

//convertidor a densidad de pixeles
import android.content.res.Resources

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()