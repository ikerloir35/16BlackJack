package com.example.droid.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.droid.R
import java.text.SimpleDateFormat
import java.util.Locale

class ListaScores (private val scoresList: List<Scores>) :
    RecyclerView.Adapter<ListaScores.ScoreViewHolder>() {

        //Configuramos el formato fecha
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        inner class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textFecha: TextView = view.findViewById(R.id.textoFecha)
            val textEstado: TextView = view.findViewById(R.id.textoEstado)
            val textScoringInicial: TextView = view.findViewById(R.id.textoScoringInicial)
            val textScoringFinal: TextView = view.findViewById(R.id.textoScoringFinal)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.linea_scoring, parent, false)
            return ScoreViewHolder(view)
        }

        override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
            val score = scoresList[position]

            holder.textFecha.text = dateFormat.format(score.fecha)
            holder.textEstado.text = when (score.estado){
                EstadoPartida.GANADA -> "WIN"
                EstadoPartida.EMPATE -> "DRAW"
                EstadoPartida.PERDIDA -> "LOSE"
            }
            holder.textScoringInicial.text = score.scorignInicial.toString()
            holder.textScoringFinal.text = score.scorignFinal.toString()
        }

        override fun getItemCount() = scoresList.size

}