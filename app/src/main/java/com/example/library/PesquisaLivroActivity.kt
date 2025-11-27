package com.example.library

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PesquisaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_livro)

        val btnVoltar  = findViewById<ImageButton>(R.id.btnVoltar)
        val btnSearch  = findViewById<ImageButton>(R.id.btnSearch)
        val txtPesquisa = findViewById<EditText>(R.id.txtPesquisa)

        btnVoltar.setOnClickListener {
            val intent = Intent(this, MenuInicialActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSearch.setOnClickListener {
            val texto = txtPesquisa.text.toString().trim()

            if (texto.isEmpty()) {
                Toast.makeText(this, "Digite algo para buscar 📚", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ResultadoPesquisaActivity::class.java)
                intent.putExtra("TERMO_PESQUISA", texto)
                startActivity(intent)
            }
        }

    }
}
