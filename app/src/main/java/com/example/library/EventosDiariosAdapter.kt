package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class EventosDiariosAdapter(
    private var lista: List<Evento>,
    private val onItemClick: (Evento) -> Unit
) : RecyclerView.Adapter<EventosDiariosAdapter.ViewHolder>() {

    // --- CORRECTION: The init block is now INSIDE the ViewHolder ---
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // You don't need the full package path (android.widget.TextView)
        val ivEvento: ImageView = view.findViewById(R.id.ivEvento)
        val nome: TextView = view.findViewById(R.id.tvNomeEvento)
        val horario: TextView = view.findViewById(R.id.tvHorarioEvento)
        val tipo: TextView = view.findViewById(R.id.tvTipoEvento)
        val local: TextView = view.findViewById(R.id.tvLocalEvento)
        val layoutConvidados: LinearLayout = view.findViewById(R.id.layoutConvidados)
        val tvConvidados: TextView = view.findViewById(R.id.tvConvidadosEvento)

        // The init block belongs here
        init {
            itemView.setOnClickListener {
                // Garante que o clique só acontece se a posição for válida
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(lista[adapterPosition])
                }
            }
        }
    } // <-- This brace correctly closes the ViewHolder class

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_eventos_diarios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = lista[position]

        // --- POPULATE ALL VIEWS FROM THE VIEWHOLDER ---
        Glide.with(holder.itemView.context)
            .load(evento.imagemUrl)
            .placeholder(R.drawable.ic_livro_aberto)
            .error(R.drawable.ic_livro_aberto)
            .into(holder.ivEvento)

        holder.nome.text = evento.nome
        holder.tipo.text = evento.tipo ?: "Não informado"
        holder.local.text = evento.local ?: "Não informado"

        try {
            // --- CLEAN UP IMPORTS: Use java.util.Locale ---
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parsedDate = parser.parse(evento.dataHora)
            holder.horario.text = parsedDate?.let { formatter.format(it) } ?: "--:--"
        } catch (e: java.lang.Exception) {
            holder.horario.text = "Inválido"
        }

        // (Here you can add the logic to fetch and display the guests)
        holder.layoutConvidados.visibility = View.GONE
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(nova: List<Evento>) {
        lista = nova
        notifyDataSetChanged()
    }
}
