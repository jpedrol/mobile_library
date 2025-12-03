package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExemplarAdapter(
    private var exemplares: MutableList<Exemplar>,
    private val onEditClick: (Exemplar) -> Unit,
    private val onDeleteClick: (Exemplar) -> Unit
) : RecyclerView.Adapter<ExemplarAdapter.ExemplarViewHolder>() {

    inner class ExemplarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val registroTextView: TextView = itemView.findViewById(R.id.registroTextView)
        val situacaoTextView: TextView = itemView.findViewById(R.id.situacaoTextView)
        val localizacaoTextView: TextView = itemView.findViewById(R.id.localizacaoTextView)
        val editButton: ImageView = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExemplarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exemplar, parent, false)
        return ExemplarViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExemplarViewHolder, position: Int) {
        val exemplar = exemplares[position]

        holder.registroTextView.text = exemplar.registro
        holder.situacaoTextView.text = exemplar.situacao
        holder.localizacaoTextView.text = exemplar.localizacao

        holder.editButton.setOnClickListener {
            onEditClick(exemplar)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(exemplar)
        }
    }

    override fun getItemCount(): Int = exemplares.size

    fun updateList(novaLista: List<Exemplar>) {
        exemplares.clear()
        exemplares.addAll(novaLista)
        notifyDataSetChanged()
    }

    fun removeItem(exemplar: Exemplar) {
        val index = exemplares.indexOfFirst { it.id == exemplar.id }
        if (index != -1) {
            exemplares.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
