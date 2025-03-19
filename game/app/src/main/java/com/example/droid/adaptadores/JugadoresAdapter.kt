package com.example.droid.adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.droid.databinding.ItemJugadorBinding
import com.example.droid.model.Jugador

class JugadoresAdapter : ListAdapter<Jugador, JugadoresAdapter.JugadorViewHolder>(JugadorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewHolder {
        val binding = ItemJugadorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JugadorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JugadorViewHolder, position: Int) {
        val jugador = getItem(position)
        holder.bind(jugador)
    }

    class JugadorViewHolder(private val binding: ItemJugadorBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(jugador: Jugador) {
            binding.emailText.text = jugador.email
            binding.puntuacionText.text = "Puntuación: ${jugador.puntuacionTotal}"
        }
    }

    class JugadorDiffCallback : DiffUtil.ItemCallback<Jugador>() {
        override fun areItemsTheSame(oldItem: Jugador, newItem: Jugador): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: Jugador, newItem: Jugador): Boolean {
            return oldItem == newItem
        }
    }
}