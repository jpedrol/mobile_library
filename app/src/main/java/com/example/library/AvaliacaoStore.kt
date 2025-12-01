package com.example.library

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AvaliacaoStorage {

    private const val PREFS_NAME = "avaliacoes_prefs"

    fun salvarAvaliacao(context: Context, tituloLivro: String, avaliacao: Avaliacao) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val jsonAntigo = prefs.getString(tituloLivro, "[]")
        val tipo = object : TypeToken<MutableList<Avaliacao>>() {}.type

        val lista: MutableList<Avaliacao> = Gson().fromJson(jsonAntigo, tipo)
        lista.add(avaliacao)

        val jsonNovo = Gson().toJson(lista)

        prefs.edit().putString(tituloLivro, jsonNovo).apply()
    }

    fun carregarAvaliacoes(context: Context, tituloLivro: String): List<Avaliacao> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val json = prefs.getString(tituloLivro, "[]")
        val tipo = object : TypeToken<List<Avaliacao>>() {}.type

        return Gson().fromJson(json, tipo)
    }
}
