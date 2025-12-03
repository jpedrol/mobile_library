package com.example.library

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections.emptyList

class AprovacoesActivity : AppCompatActivity() {

    private lateinit var recyclerAprovacoes: RecyclerView
    private lateinit var adapter: AprovacoesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprovacoes_aluguel)

        recyclerAprovacoes = findViewById(R.id.recyclerAprovacoes)
        recyclerAprovacoes.layoutManager = LinearLayoutManager(this)

        adapter = AprovacoesAdapter(emptyList()) { aprovacao ->
            // Enviar apenas os campos necessários via Intent
            val intent = Intent(this, TelaLivroDetalheActivity::class.java)
            intent.putExtra("idReserva", aprovacao.id) // exemplo: id do empréstimo
            intent.putExtra("usuario_id", aprovacao.usuario_id)
            intent.putExtra("livro_id", aprovacao.livro_id)

            startActivity(intent)
        }

        recyclerAprovacoes.adapter = adapter

        carregarAprovacoesDoSupabase()
    }

    private fun carregarAprovacoesDoSupabase() {
        val call: Call<List<AprovacaoEmprestimo>> = SupabaseClient.api.listarAprovacoes(
            apiKey = SupabaseConfig.SUPABASE_KEY,
            bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
        )

        call.enqueue(object : Callback<List<AprovacaoEmprestimo>> {
            override fun onResponse(
                call: Call<List<AprovacaoEmprestimo>>,
                response: Response<List<AprovacaoEmprestimo>>
            ) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    adapter.atualizarLista(lista)
                } else {
                    Toast.makeText(
                        this@AprovacoesActivity,
                        "Erro ao carregar aprovações: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<AprovacaoEmprestimo>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(
                    this@AprovacoesActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
