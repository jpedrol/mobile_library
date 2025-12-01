package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EventosAdapter(
    private var lista: List<Evento>
) : RecyclerView.Adapter<EventosAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // As views estão corretas
        val titulo: TextView = view.findViewById(R.id.tvTituloEvento)
        val imagem: ImageView = view.findViewById(R.id.ivImagemEvento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_eventos_diarios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = lista[position]

        holder.titulo.text = evento.nome

        Glide.with(holder.itemView.context)
            .load(evento.imagemUrl)
            .into(holder.imagem)
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(nova: List<Evento>) {
        lista = nova
        notifyDataSetChanged()
    }
}
