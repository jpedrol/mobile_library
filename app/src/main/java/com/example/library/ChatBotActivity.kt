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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatBotActivity : AppCompatActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var adapter: MessagesAdapter

    lateinit var generativeContent: GenerativeModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

//        geminiClient = GeminiClient(BuildConfig.GEMINI_API_KEY)
        generativeContent = GenerativeModel("gemini-2.5-flash",
            BuildConfig.GEMINI_API_KEY)
        val btnVoltar   = findViewById<ImageButton>(R.id.btnVoltar)
        val btnExcluir  = findViewById<ImageButton>(R.id.btnExcluir)
        val btnPesquisa = findViewById<ImageButton>(R.id.btnSearch)
        val txtPergunta = findViewById<EditText>(R.id.txtPergunta)
        rvMessages      = findViewById(R.id.rvMessages)

        adapter = MessagesAdapter(mutableListOf())
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = adapter

        btnVoltar.setOnClickListener {
            val intent = Intent(this, MenuInicialActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnExcluir.setOnClickListener {
            txtPergunta.setText("")
            Toast.makeText(this, "Texto apagado", Toast.LENGTH_SHORT).show()
        }

        btnPesquisa.setOnClickListener {
            val pergunta = txtPergunta.text.toString().trim()

            if (pergunta.isEmpty()) {
                Toast.makeText(this, "Digite uma pergunta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            adapter.addMessage(ChatMessage(pergunta, true))
            rvMessages.scrollToPosition(adapter.itemCount - 1)

            txtPergunta.setText("")

            enviarPerguntaGemini(pergunta)
        }
    }

    private fun enviarPerguntaGemini(pergunta: String) {
        lifecycleScope.launch {
            val resposta = generativeContent.generateContent(pergunta)


            adapter.addMessage(ChatMessage(resposta.text.toString(), false))
            rvMessages.scrollToPosition(adapter.itemCount - 1)
        }
        }
    }

