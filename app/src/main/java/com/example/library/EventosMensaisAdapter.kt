package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class EventosMensaisAdapter(
    private var lista: List<Evento>,
    // ✅ ADICIONA UM LISTENER DE CLIQUE
    private val onItemClick: (Evento) -> Unit
) : RecyclerView.Adapter<EventosMensaisAdapter.ViewHolder>() {

    private val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val formatadorDia = SimpleDateFormat("dd", Locale.getDefault())
    private val formatadorHorario = SimpleDateFormat("HH:mm", Locale.getDefault())

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDia: TextView = view.findViewById(R.id.tvDiaEvento)
        val tvNome: TextView = view.findViewById(R.id.tvNomeEvento)
        val tvTipo: TextView = view.findViewById(R.id.tvTipoEvento)
        val tvHorario: TextView = view.findViewById(R.id.tvHorarioEvento)
        val tvLocal: TextView = view.findViewById(R.id.tvLocalEvento)

        // ✅ CONFIGURA O CLIQUE NO ITEM
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(lista[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_eventos_mes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = lista[position]

        holder.tvNome.text = evento.nome
        holder.tvTipo.text = evento.tipo ?: "Não informado"
        holder.tvLocal.text = evento.local ?: "Não informado"

        try {
            val dataParseada = parser.parse(evento.dataHora)
            if (dataParseada != null) {
                holder.tvDia.text = formatadorDia.format(dataParseada)
                holder.tvHorario.text = "às ${formatadorHorario.format(dataParseada)}"
            } else {
                holder.tvDia.text = "?"
                holder.tvHorario.text = ""
            }
        } catch (e: Exception) {
            holder.tvDia.text = "!"
            holder.tvHorario.text = "Inválido"
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(nova: List<Evento>) {
        lista = nova
        notifyDataSetChanged()
    }
}
