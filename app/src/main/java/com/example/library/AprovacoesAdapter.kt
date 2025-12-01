package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView


class AprovacoesAdapter(
    private var listaAprovacoes: List<AprovacaoEmprestimo>,
    private val onItemClick: (AprovacaoEmprestimo) -> Unit
) : RecyclerView.Adapter<AprovacoesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescricao: TextView = view.findViewById(R.id.tvDescricaoReserva)
        val btnAprovar: AppCompatButton = view.findViewById(R.id.btnAprovarReserva)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aprovacao, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val aprovacao = listaAprovacoes[position]
        val periodo = "${aprovacao.data_inicio} a ${aprovacao.data_fim}"

        holder.tvDescricao.text =
            "${aprovacao.usuario.nome_completo} está solicitando a reserva do livro “${aprovacao.livro.title}” para o período de ${periodo}."

        holder.btnAprovar.setOnClickListener {
            onItemClick(aprovacao)
        }
        holder.itemView.setOnClickListener {
            onItemClick(aprovacao)
        }
    }

    override fun getItemCount(): Int = listaAprovacoes.size

    fun atualizarLista(novaLista: List<AprovacaoEmprestimo>) {
        listaAprovacoes = novaLista
        notifyDataSetChanged()
    }
}
