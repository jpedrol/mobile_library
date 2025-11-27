package com.example.library

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatBotActivity : AppCompatActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var adapter: MessagesAdapter
    private lateinit var generativeContent: GenerativeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // Inicializa o modelo Gemini
        generativeContent = GenerativeModel(
            "gemini-2.5-flash",
            BuildConfig.GEMINI_API_KEY
        )

        // Referências da interface
        val btnVoltar   = findViewById<ImageButton>(R.id.btnVoltar)
        val btnEnviar   = findViewById<ImageButton>(R.id.btnEnviar)
        val txtPergunta = findViewById<EditText>(R.id.txtPergunta)
        rvMessages      = findViewById(R.id.rvMessages)

        // RecyclerView
        adapter = MessagesAdapter(mutableListOf())
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = adapter

        // Voltar para o Menu Inicial
        btnVoltar.setOnClickListener {
            val intent = Intent(this, MenuInicialActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Enviar mensagem
        btnEnviar.setOnClickListener {
            val pergunta = txtPergunta.text.toString().trim()

            if (pergunta.isEmpty()) {
                Toast.makeText(this, "Digite uma pergunta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Adiciona a mensagem do usuário
            adapter.addMessage(ChatMessage(pergunta, true))
            rvMessages.scrollToPosition(adapter.itemCount - 1)

            txtPergunta.setText("")

            // Envia ao modelo Gemini
            enviarPerguntaGemini(pergunta)
        }
    }

    private fun enviarPerguntaGemini(pergunta: String) {
        lifecycleScope.launch {
            try {
                val resposta = generativeContent.generateContent(pergunta)

                adapter.addMessage(
                    ChatMessage(
                        resposta.text.toString(),
                        false
                    )
                )
                rvMessages.scrollToPosition(adapter.itemCount - 1)
            } catch (e: Exception) {
                adapter.addMessage(
                    ChatMessage(
                        "Erro ao gerar resposta. Tente novamente.",
                        false
                    )
                )
            }
        }
    }
}
