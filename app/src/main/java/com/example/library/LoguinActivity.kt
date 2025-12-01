package com.example.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class LoguinActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_ROLE = "role"
        private const val KEY_MATRICULA = "matricula"
    }

    private lateinit var btnEntrar: Button
    private lateinit var linkRegistrar: TextView
    private lateinit var inputMatricula: EditText
    private lateinit var inputSenha: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loguin)

        btnEntrar = findViewById(R.id.btnEntrar)
        linkRegistrar = findViewById(R.id.linkRegistrar)
        inputMatricula = findViewById(R.id.inputMatricula)
        inputSenha = findViewById(R.id.inputSenha)

        btnEntrar.setOnClickListener {
            val matricula = inputMatricula.text.toString().trim()
            val senha = inputSenha.text.toString().trim()

            if (matricula.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha matrícula e senha", Toast.LENGTH_SHORT).show()
            } else {
                fazerLogin(matricula, senha)
            }
        }

        linkRegistrar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun fazerLogin(matricula: String, senha: String) {
        val senhaHash = senha // depois vocês podem aplicar hash

        btnEntrar.isEnabled = false

        lifecycleScope.launch {
            try {
                val api = SupabaseClient.api

                val response = api.login(
                    matriculaFilter = "eq.$matricula",
                    senhaHashFilter = "eq.$senhaHash",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                btnEntrar.isEnabled = true

                if (response.isSuccessful) {
                    val usuarios = response.body()

                    if (!usuarios.isNullOrEmpty()) {
                        val usuario = usuarios[0]

                        // se quiser, salva os dados no SharedPreferences
                        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        prefs.edit()
                            .putString(KEY_MATRICULA, matricula)
                            .putString(KEY_ROLE, usuario.tipo_usuario ?: "user")
                            .apply()

                        Toast.makeText(
                            this@LoguinActivity,
                            "Bem-vindo, ${usuario.nome_completo}",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@LoguinActivity, MenuInicialActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(
                            this@LoguinActivity,
                            "Matrícula ou senha inválidos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoguinActivity,
                        "Erro no login: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                btnEntrar.isEnabled = true
                Log.e("LoguinActivity", "Erro no login", e)
                Toast.makeText(
                    this@LoguinActivity,
                    "Falha na conexão: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
