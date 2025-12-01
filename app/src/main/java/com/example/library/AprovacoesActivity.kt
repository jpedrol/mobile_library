package com.example.library

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AprovacoesActivity : AppCompatActivity() {

    private lateinit var recyclerAprovacoes: RecyclerView
    private lateinit var adapter: AprovacoesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprovacoes_aluguel)

        recyclerAprovacoes = findViewById(R.id.recyclerAprovacoes)
        recyclerAprovacoes.layoutManager = LinearLayoutManager(this)

        adapter = AprovacoesAdapter(emptyList()) { aprovacao ->
            val intent = Intent(this, TelaLivroDetalheActivity::class.java)
            intent.putExtra("reserva", aprovacao)
            startActivity(intent)
        }

        recyclerAprovacoes.adapter = adapter

        carregarAprovacoesDoSupabase()
    }

//    private fun carregarAprovacoesDoSupabase() {
//        lifecycleScope.launch {
//            try {
//                val lista = supabase.from("aprovacao_emprestimos")
//                    .select()
//                    .decodeList<AprovacaoEmprestimo>()
//
//                adapter.atualizarLista(lista)
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(this@AprovacoesActivity, "Erro ao carregar aprovações", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}

