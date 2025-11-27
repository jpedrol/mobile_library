package com.example.library

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MenuInicialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_inicial)

        val btnMenu   = findViewById<ImageButton>(R.id.btnMenu)

        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)

        val btnRefreshPill = findViewById<ImageButton>(R.id.btnRefreshPill)

        val btnChatbot = findViewById<FloatingActionButton>(R.id.btnChatbot)

        val tvCalendario = findViewById<TextView>(R.id.tvCalendario)

        btnMenu.setOnClickListener {
            Toast.makeText(this, "Abrir menu", Toast.LENGTH_SHORT).show()
        }

        btnSearch.setOnClickListener {
            val intent = Intent(this, PesquisaActivity::class.java)
            startActivity(intent)
        }

        btnRefreshPill.setOnClickListener {
            Toast.makeText(this, "Atualizando recomendados…", Toast.LENGTH_SHORT).show()
        }

        btnChatbot.setOnClickListener {
            val i = Intent(this, ChatBotActivity::class.java)
            startActivity(i)
            Toast.makeText(this, "Abrir chatbot", Toast.LENGTH_SHORT).show()
        }

        tvCalendario.setOnClickListener {
            Toast.makeText(this, "Ir para calendário de eventos", Toast.LENGTH_SHORT).show()
        }
    }
}
