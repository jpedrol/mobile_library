package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class EventosAdapter(private var eventos: List<Evento>, private val onEventoClick: (Evento) -> Unit) :
    RecyclerView.Adapter<EventosAdapter.EventoViewHolder>() {

    class EventoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tv_evento_titulo)
        val tvTipo: TextView = view.findViewById(R.id.tv_evento_tipo)
        val tvDataHora: TextView = view.findViewById(R.id.tv_evento_data_hora)
        val tvLocal: TextView = view.findViewById(R.id.tv_evento_local)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.tvTitulo.text = evento.nome
        holder.tvTipo.text = "Tipo: ${evento.tipo}"
        holder.tvLocal.text = "Local: ${evento.local}"

        // Formatar data e hora
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
            val date = inputFormat.parse(evento.dataHora.substringBefore(".")) // Remove milissegundos se houver
            holder.tvDataHora.text = "Data/Hora: ${outputFormat.format(date)}"
        } catch (e: Exception) {
            holder.tvDataHora.text = "Data/Hora: ${evento.dataHora}"
        }

        holder.itemView.setOnClickListener {
            onEventoClick(evento)
        }
    }

    override fun getItemCount() = eventos.size

    fun updateList(novaLista: List<Evento>) {
        eventos = novaLista
        notifyDataSetChanged()
    }
}
