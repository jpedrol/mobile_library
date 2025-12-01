package com.example.library

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val lista: List<Book>,
    private val isAdmin: Boolean,
    private val onEdit: (Book, Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val imgCapa: ImageView = item.findViewById(R.id.imgCapa)
        val txtTitulo: TextView = item.findViewById(R.id.txtTitulo)
        val txtAutor: TextView = item.findViewById(R.id.txtAutor)
        val btnEditar: Button = item.findViewById(R.id.btnEditar)
        val btnExcluir: Button = item.findViewById(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val livro = lista[position]

        holder.txtTitulo.text = livro.title
        holder.txtAutor.text = livro.author

        // Exibe a capa
        if (!livro.coverUri.isNullOrEmpty()) {
            holder.imgCapa.setImageURI(Uri.parse(livro.coverUri))
        } else {
            holder.imgCapa.setImageResource(R.drawable.ic_book_placeholder)
        }

        // 🔥 SE NÃO FOR ADMIN → ESCONDE OS BOTÕES COMPLETAMENTE
        if (isAdmin) {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnExcluir.visibility = View.VISIBLE
        } else {
            holder.btnEditar.visibility = View.GONE
            holder.btnExcluir.visibility = View.GONE
        }

        // Funções do admin
        holder.btnEditar.setOnClickListener {
            onEdit(livro, position)
        }

        holder.btnExcluir.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount(): Int = lista.size
}
