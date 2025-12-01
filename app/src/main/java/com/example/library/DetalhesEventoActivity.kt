package com.example.library

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetalhesEventoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_evento)

        val botaoVoltar = findViewById<ImageButton>(R.id.botao_voltar)
        val btnEditar = findViewById<Button>(R.id.btnEditarEvento)
        val btnExcluir = findViewById<Button>(R.id.btnExcluir)

        val titulo = findViewById<TextView>(R.id.tvTituloEvento)
        val nomeCard = findViewById<TextView>(R.id.tvNomeEventoCard)
        val tipo = findViewById<TextView>(R.id.tvTipoEvento)
        val dataHora = findViewById<TextView>(R.id.tvDataHora)
        val local = findViewById<TextView>(R.id.tvLocalEvento)
        val descricaoConvidado = findViewById<TextView>(R.id.tvDescricaoConvidado)
        val layoutConvidados = findViewById<LinearLayout>(R.id.layoutConvidados)
        val imagem = findViewById<ImageView>(R.id.ivImagemEvento)

        val isAdmin = intent.getBooleanExtra("isAdmin", false)

        val nomeEvento = intent.getStringExtra("nomeEvento") ?: ""
        val tipoEvento = intent.getStringExtra("tipoEvento") ?: ""
        val dataEvento = intent.getStringExtra("dataEvento") ?: ""
        val localEvento = intent.getStringExtra("localEvento") ?: ""
        val convidadoNome = intent.getStringExtra("convidadoNome") ?: ""
        val convidadoDescricao = intent.getStringExtra("convidadoDescricao") ?: ""

        titulo.text = nomeEvento
        nomeCard.text = nomeEvento
        tipo.text = tipoEvento
        dataHora.text = dataEvento
        local.text = localEvento
        descricaoConvidado.text = convidadoDescricao

        // Aplica nome do convidado ao layout
        val cardConvidado = layoutConvidados.getChildAt(0) as LinearLayout
        val tvNomeConvidado = cardConvidado.getChildAt(1) as TextView
        tvNomeConvidado.text = convidadoNome

        // Apenas admins enxergam botões Editar/Excluir
        if (!isAdmin) {
            btnEditar.visibility = View.GONE
            btnExcluir.visibility = View.GONE
        }

        botaoVoltar.setOnClickListener {
            finish()
        }

//        btnEditar.setOnClickListener {
//            val intent = Intent(this, FormularioEventoActivity::class.java)
//            intent.putExtra("modo", "editar")
//            intent.putExtra("nomeEvento", nomeEvento)
//            startActivity(intent)
//        }

        btnExcluir.setOnClickListener {
            mostrarPopupExclusao()
        }
    }

    private fun mostrarPopupExclusao() {
        AlertDialog.Builder(this)
            .setTitle("Excluir evento")
            .setMessage("Tem certeza que deseja excluir este evento?")
            .setPositiveButton("Excluir") { _, _ ->
                Toast.makeText(this, "Evento excluído", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
