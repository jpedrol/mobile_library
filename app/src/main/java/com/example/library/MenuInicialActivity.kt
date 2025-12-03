package com.example.library

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.lifecycle.lifecycleScope
import android.view.View
import android.widget.Toast
import com.example.library.SupabaseClient
import com.example.library.SupabaseConfig
import kotlinx.coroutines.launch
import com.example.library.PesquisaLivroActivity

class MenuInicialActivity : AppCompatActivity() {

    private fun DrawerLayout.disableSwipeGesture() {
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private val supabaseApi = SupabaseClient.api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_inicial)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.disableSwipeGesture()

        val btnMenu        = findViewById<ImageButton>(R.id.btnMenu)
        val btnSearch      = findViewById<ImageButton>(R.id.btnSearch)
        val btnChatbot     = findViewById<FloatingActionButton>(R.id.btnChatbot)
        val tvCalendario   = findViewById<TextView>(R.id.tvCalendario)

        val itemAcessibilidade = findViewById<LinearLayout>(R.id.itemAcessibilidade)
        val itemMenuInicial    = findViewById<LinearLayout>(R.id.itemMenuInicial)
        val itemSair           = findViewById<LinearLayout>(R.id.itemSair)
        val itemEventos        = findViewById<LinearLayout>(R.id.itemEventos)
        val itemAluguelCabines = findViewById<LinearLayout>(R.id.itemAluguelCabines)
        val itemAdmin          = findViewById<LinearLayout>(R.id.itemAdmin)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        btnSearch.setOnClickListener {
            startActivity(Intent(this, PesquisaLivroActivity::class.java))
        }

        btnChatbot.setOnClickListener {
            startActivity(Intent(this, ChatBotActivity::class.java))
        }

        tvCalendario.setOnClickListener {
            startActivity(Intent(this, EventosMensaisActivity::class.java))
        }

        fun closeDrawer() = drawerLayout.closeDrawer(GravityCompat.START)

        itemAcessibilidade.setOnClickListener {
            closeDrawer()
            startActivity(Intent(this, AcessibilidadeActivity::class.java))
        }

        itemMenuInicial.setOnClickListener {
            closeDrawer()
        }

        itemEventos.setOnClickListener {
            closeDrawer()
            startActivity(Intent(this, EventosActivity::class.java))
        }

        itemAluguelCabines.setOnClickListener {
            closeDrawer()
            startActivity(Intent(this, CabinesActivity::class.java))
        }

        // Lógica para exibir o item de Admin apenas para administradores
        if (SessionManager.isAdmin(this)) {
            itemAdmin.visibility = View.VISIBLE
            itemAdmin.setOnClickListener {
                closeDrawer()
                startActivity(Intent(this, AdminActivity::class.java))
            }
        } else {
            itemAdmin.visibility = View.GONE
        }

        itemSair.setOnClickListener {
            closeDrawer()

            val intent = Intent(this, LoguinActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        carregarLivrosRecomendados()
    }

    private fun carregarLivrosRecomendados() {
        val rvRecomendados = findViewById<RecyclerView>(R.id.rvRecomendados)
        rvRecomendados.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            try {
                val response = supabaseApi.getLivros(
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful && response.body() != null) {
                    val livros = response.body()!!
                    rvRecomendados.adapter = RecomendadosHomeAdapter(livros) { livro ->
                        val intent = Intent(this@MenuInicialActivity, TelaLivroDetalheActivity::class.java)
                        intent.putExtra("LIVRO_ID", livro.id)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this@MenuInicialActivity, "Erro ao carregar livros recomendados.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MenuInicialActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
