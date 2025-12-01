package com.example.library

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import com.example.library.data.supabase.UsuarioInsert
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMatricula: EditText
    private lateinit var etSenha: EditText
    private lateinit var etConfirmarSenha: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        etNome = findViewById(R.id.etNomeCompleto)
        etEmail = findViewById(R.id.etEmail)
        etMatricula = findViewById(R.id.etMatricula)
        etSenha = findViewById(R.id.etSenha)
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        progressBar = findViewById(R.id.progressBar)

        btnRegistrar.setOnClickListener {

            val nome = etNome.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val matricula = etMatricula.text.toString().trim()
            val senha = etSenha.text.toString().trim()
            val confirmar = etConfirmarSenha.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || matricula.isEmpty() || senha.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmar) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarUsuario(nome, email, matricula, senha)
        }
    }

    private fun registrarUsuario(
        nome: String,
        email: String,
        matricula: String,
        senha: String
    ) {
        val senhaHash = senha

        val novoUsuario = UsuarioInsert(
            nome_completo = nome,
            email = email,
            matricula = matricula,
            senha_hash = senhaHash
        )

        progressBar.visibility = View.VISIBLE
        btnRegistrar.isEnabled = false

        lifecycleScope.launch {
            try {
                val api = SupabaseClient.api

                val response = api.registrar(
                    novoUsuario = novoUsuario,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                progressBar.visibility = View.GONE
                btnRegistrar.isEnabled = true

                if (response.isSuccessful) {
                    val usuarios = response.body()

                    if (!usuarios.isNullOrEmpty()) {
                        val usuario = usuarios[0]
                        Toast.makeText(
                            this@RegisterActivity,
                            "Usuário cadastrado: ${usuario.nome_completo}",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish() // volta para login
                    } else {
                        Toast.makeText(this@RegisterActivity, "Erro: sem retorno", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Erro ao cadastrar: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                btnRegistrar.isEnabled = true
                Toast.makeText(
                    this@RegisterActivity,
                    "Falha na conexão: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
