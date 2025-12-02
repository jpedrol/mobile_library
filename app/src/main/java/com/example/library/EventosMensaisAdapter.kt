package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

// Adapter específico para a visão da lista na tela mensal
class EventosMensaisAdapter(
    private var lista: List<Evento>
) : RecyclerView.Adapter<EventosMensaisAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // IDs que existem no seu layout item_eventos_mes.xml
        val nome: TextView = view.findViewById(R.id.tvNomeEvento)
        val horario: TextView = view.findViewById(R.id.tvHorarioEvento)
        val tipo: TextView = view.findViewById(R.id.tvTipoEvento)
        val local: TextView = view.findViewById(R.id.tvLocalEvento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_eventos_mes, parent, false) // Usa o layout correto
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = lista[position]

        holder.nome.text = evento.nome
        holder.tipo.text = evento.tipo ?: "Não informado"
        holder.local.text = evento.local ?: "Não informado"

        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parsedDate = parser.parse(evento.dataHora)
            holder.horario.text = parsedDate?.let { formatter.format(it) } ?: "--:--"
        } catch (e: Exception) {
            holder.horario.text = "Inválido"
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(nova: List<Evento>) {
        lista = nova
        notifyDataSetChanged() // Recarrega a lista
    }
}
