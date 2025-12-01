package com.example.library

import android.content.Context
import org.json.JSONArray

object LivroRepository {

    val livros: MutableList<Book> = mutableListOf()

    fun carregarLivros(context: Context) {
        livros.clear()

        val prefs = context.getSharedPreferences("books_db", Context.MODE_PRIVATE)
        val json = prefs.getString("books_list", "[]") ?: "[]"
        val arr = JSONArray(json)

        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)

            val book = Book(
                title = obj.getString("title"),
                author = obj.getString("author"),
                language = obj.getString("language"),
                coverUri = obj.optString("coverUri", null)
            )

            livros.add(book)
        }
    }
}
