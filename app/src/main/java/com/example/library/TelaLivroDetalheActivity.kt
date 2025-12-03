package com.example.library

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import java.util.Calendar

class TelaLivroDetalheActivity : AppCompatActivity() {

    private var tituloLivro: String = "Título do livro"
    private var capaResId: Int = R.drawable.livro1

    private lateinit var layoutAvaliacoes: LinearLayout
    private lateinit var tvResumoAvaliacoes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_livro_detalhe)

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }

        layoutAvaliacoes = findViewById(R.id.layoutAvaliacoes)
        tvResumoAvaliacoes = findViewById(R.id.tvResumoAvaliacoes)

        intent.getStringExtra("tituloLivro")?.let {
            if (it.isNotBlank()) tituloLivro = it
        }

        val capaExtra = intent.getIntExtra("capaResId", 0)
        if (capaExtra != 0) capaResId = capaExtra

        val idTitulo = resources.getIdentifier("tvTituloLivro", "id", packageName)
        if (idTitulo != 0) {
            val tvTitulo = findViewById<TextView>(idTitulo)
            tvTitulo.text = tituloLivro
        }

        val idCapa = resources.getIdentifier("imgCapaLivro", "id", packageName)
        if (idCapa != 0) {
            val imgCapa = findViewById<ImageView>(idCapa)
            imgCapa.setImageResource(capaResId)
        }
    }

    override fun onResume() {
        super.onResume()
        carregarAvaliacoes()
    }

    private fun carregarAvaliacoes() {
        layoutAvaliacoes.removeAllViews()

        val lista = AvaliacaoStorage.carregarAvaliacoes(this, tituloLivro)

        if (lista.isEmpty()) {
            tvResumoAvaliacoes.text = "0.0 ★ (0 avaliações)"
            val vazio = TextView(this)
            vazio.text = "Nenhuma avaliação ainda."
            layoutAvaliacoes.addView(vazio)
            return
        }

        var soma = 0.0

        for (av in lista) {
            soma += av.estrelas

            val item = layoutInflater.inflate(R.layout.item_avaliacao, layoutAvaliacoes, false)

            val tvNome = item.findViewById<TextView>(R.id.tvNomeUser)
            val tvEstrelas = item.findViewById<TextView>(R.id.tvEstrelasUser)
            val tvComentario = item.findViewById<TextView>(R.id.tvComentarioUser)

            tvNome.text = av.usuario
            tvEstrelas.text = "★".repeat(av.estrelas) + "☆".repeat(5 - av.estrelas)
            tvComentario.text = av.comentario

            layoutAvaliacoes.addView(item)
        }

        val media = soma / lista.size
        tvResumoAvaliacoes.text = String.format("%.1f ★ (%d avaliações)", media, lista.size)
    }

    private fun abrirPopupAlugar() {
        val view = layoutInflater.inflate(R.layout.popup_alugar_livro, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgPopupCapa = view.findViewById<ImageView>(R.id.imgPopupCapa)
        val tvPopupTitulo = view.findViewById<TextView>(R.id.tvPopupTitulo)
        val tvPopupUsuario = view.findViewById<TextView>(R.id.tvPopupUsuario)

        val dateInicio = view.findViewById<DatePicker>(R.id.dateInicio)
        val dateFim = view.findViewById<DatePicker>(R.id.dateFim)

        val btnConfirmar = view.findViewById<Button>(R.id.btnPopupConfirmar)
        val btnFechar = view.findViewById<TextView>(R.id.btnPopupFechar)

        imgPopupCapa.setImageResource(capaResId)
        tvPopupTitulo.text = tituloLivro

        tvPopupUsuario.text = SessionManager.getUserName(this)

        val calendar = Calendar.getInstance()
        val hoje = calendar.timeInMillis

        dateInicio.minDate = hoje
        dateFim.minDate = hoje

        val limiteMaxGeral = hoje + (30L * 24 * 60 * 60 * 1000)
        dateFim.maxDate = limiteMaxGeral

        btnConfirmar.setOnClickListener {
            Toast.makeText(this, "Alugado!", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        btnFechar.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}
