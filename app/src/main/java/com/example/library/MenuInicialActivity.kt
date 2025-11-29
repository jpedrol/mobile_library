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

class MenuInicialActivity : AppCompatActivity() {

    // trava o swipe lateral, deixando o menu abrir só pelo botão
    private fun DrawerLayout.disableSwipeGesture() {
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_inicial)

        // carrega livros do SharedPreferences para o repositório em memória
        LivroRepository.carregarLivros(this)

        // ---------- RECOMENDADOS (HOME) ----------
        val rvRecomendados = findViewById<RecyclerView>(R.id.rvRecomendados)
        val livros = LivroRepository.livros

        rvRecomendados.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvRecomendados.adapter =
            RecomendadosHomeAdapter(livros) { position ->
                val livro = livros[position]
                val intent = Intent(this, TelaLivroDetalheActivity::class.java)
                // por enquanto mandamos só o título (usado nas avaliações)
                intent.putExtra("tituloLivro", livro.title)
                startActivity(intent)
            }

        // ---------- MENU LATERAL E BOTÕES ----------
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.disableSwipeGesture()

        val btnMenu        = findViewById<ImageButton>(R.id.btnMenu)
        val btnSearch      = findViewById<ImageButton>(R.id.btnSearch)
        val btnChatbot     = findViewById<FloatingActionButton>(R.id.btnChatbot)
        val tvCalendario   = findViewById<TextView>(R.id.tvCalendario)

        val itemAcessibilidade = findViewById<LinearLayout>(R.id.itemAcessibilidade)
        val itemMenuInicial    = findViewById<LinearLayout>(R.id.itemMenuInicial)
        val itemSair           = findViewById<LinearLayout>(R.id.itemSair)

        // abre o menu lateral
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // busca
        btnSearch.setOnClickListener {
            startActivity(Intent(this, PesquisaActivity::class.java))
        }

        btnChatbot.setOnClickListener {
            startActivity(Intent(this, ChatBotActivity::class.java))
        }

        tvCalendario.setOnClickListener {
            // ir para tela de eventos, se quiser no futuro
        }

        fun closeDrawer() = drawerLayout.closeDrawer(GravityCompat.START)

        itemAcessibilidade.setOnClickListener {
            closeDrawer()
            startActivity(Intent(this, AcessibilidadeActivity::class.java))
        }

        itemMenuInicial.setOnClickListener {
            closeDrawer()
        }

        // *** BOTÃO SAIR — DESLOGAR ***
        itemSair.setOnClickListener {
            closeDrawer()

            val intent = Intent(this, LoguinActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
