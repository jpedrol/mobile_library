package com.example.library

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MenuInicialActivity : AppCompatActivity() {

    // trava o swipe lateral, deixando o menu abrir só pelo botão
    private fun DrawerLayout.disableSwipeGesture() {
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_inicial)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.disableSwipeGesture()

        val btnMenu        = findViewById<ImageButton>(R.id.btnMenu)
        val btnSearch      = findViewById<ImageButton>(R.id.btnSearch)
        val btnRefreshPill = findViewById<ImageButton>(R.id.btnRefreshPill)
        val btnChatbot     = findViewById<FloatingActionButton>(R.id.btnChatbot)
        val tvCalendario   = findViewById<TextView>(R.id.tvCalendario)

        val itemAluguelCabines = findViewById<LinearLayout>(R.id.itemAluguelCabines)
        val itemAcessibilidade = findViewById<LinearLayout>(R.id.itemAcessibilidade)
        val itemMenuInicial    = findViewById<LinearLayout>(R.id.itemMenuInicial)
        val itemSair           = findViewById<LinearLayout>(R.id.itemSair)

        // abre o menu lateral
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        // fluxo original de pesquisa
        btnSearch.setOnClickListener {
            startActivity(Intent(this, PesquisaActivity::class.java))
        }

        btnRefreshPill.setOnClickListener {
            Toast.makeText(this, "Atualizando recomendados…", Toast.LENGTH_SHORT).show()
        }

        btnChatbot.setOnClickListener {
            startActivity(Intent(this, ChatBotActivity::class.java))
        }

        tvCalendario.setOnClickListener {
            Toast.makeText(this, "Ir para calendário de eventos", Toast.LENGTH_SHORT).show()
        }

        fun closeDrawer() = drawerLayout.closeDrawer(Gravity.START)

        itemAluguelCabines.setOnClickListener {
            Toast.makeText(this, "Aluguel de cabines (em breve)", Toast.LENGTH_SHORT).show()
            closeDrawer()
        }

        itemAcessibilidade.setOnClickListener {
            Toast.makeText(this, "Acessibilidade (em breve)", Toast.LENGTH_SHORT).show()
            closeDrawer()
        }

        itemMenuInicial.setOnClickListener {
            // já está na tela inicial
            closeDrawer()
        }

        itemSair.setOnClickListener {
            // fecha o app
            finishAffinity()
        }
    }
}
