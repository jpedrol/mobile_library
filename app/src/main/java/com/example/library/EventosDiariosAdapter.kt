package com.example.library

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EventosDiariosAdapter(
    private var lista: List<Evento>,
    private val onItemClick: (Evento) -> Unit
) : RecyclerView.Adapter<EventosDiariosAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivEvento: ImageView = view.findViewById(R.id.ivEvento)
        val nome: TextView = view.findViewById(R.id.tvNomeEvento)
        val horario: TextView = view.findViewById(R.id.tvHorarioEvento)
        val tipo: TextView = view.findViewById(R.id.tvTipoEvento)
        val local: TextView = view.findViewById(R.id.tvLocalEvento)
        val layoutConvidados: LinearLayout = view.findViewById(R.id.layoutConvidados)
        val tvConvidados: TextView = view.findViewById(R.id.tvConvidadosEvento)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(lista[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_eventos_diarios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = lista[position]

        // --- Preenchimento dos dados do evento (sem alteração) ---
        Glide.with(holder.itemView.context)
            .load(evento.imagemUrl)
            .placeholder(R.drawable.ic_livro_aberto)
            .error(R.drawable.ic_livro_aberto)
            .into(holder.ivEvento)

        holder.nome.text = evento.nome
        holder.tipo.text = evento.tipo ?: "Não informado"
        holder.local.text = evento.local ?: "Não informado"

        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parsedDate = parser.parse(evento.dataHora)
            holder.horario.text = parsedDate?.let { formatter.format(it) } ?: "--:--"
        } catch (e: java.lang.Exception) {
            holder.horario.text = "Inválido"
        }

        // ✅ --- LÓGICA PARA BUSCAR E EXIBIR CONVIDADOS ---
        // Oculta a seção de convidados por padrão
        holder.layoutConvidados.visibility = View.GONE

        // Usa o ciclo de vida da view para lançar a coroutine de forma segura
        holder.itemView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            try {
                // Busca os convidados para o ID do evento atual
                val response = SupabaseClient.api.buscarConvidadosPorEvento(
                    idEventoFiltro = "eq.${evento.id}",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    val convidados = response.body() ?: emptyList()
                    if (convidados.isNotEmpty()) {
                        // Monta o texto: "Nome do primeiro +X" se houver mais de um
                        val textoConvidados = if (convidados.size > 1) {
                            "${convidados.first().nome} +${convidados.size - 1}"
                        } else {
                            convidados.first().nome
                        }
                        holder.tvConvidados.text = textoConvidados
                        // Torna a seção de convidados visível
                        holder.layoutConvidados.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                // Em caso de erro, apenas loga e mantém a seção oculta
                Log.e("EventosDiariosAdapter", "Erro ao buscar convidados para o evento ${evento.id}", e)
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(nova: List<Evento>) {
        lista = nova
        notifyDataSetChanged()
    }
}
