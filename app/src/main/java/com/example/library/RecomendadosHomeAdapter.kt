package com.example.library

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class RecomendadosHomeAdapter(
    private val lista: List<Book>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<RecomendadosHomeAdapter.RecomendadoViewHolder>() {

    inner class RecomendadoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val capa: ImageView = view.findViewById(R.id.imgCapaHome)

        init {
            view.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick(pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecomendadoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recomendado_home, parent, false)
        return RecomendadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecomendadoViewHolder, position: Int) {
        val livro = lista[position]

        if (!livro.coverUri.isNullOrEmpty()) {
            holder.capa.setImageURI(Uri.parse(livro.coverUri))
        } else {
            holder.capa.setImageResource(R.drawable.ic_book_placeholder)
        }
    }

    override fun getItemCount(): Int = lista.size
}
