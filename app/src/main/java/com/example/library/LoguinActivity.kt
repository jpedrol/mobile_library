package com.example.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoguinActivity : AppCompatActivity() {

    companion object {
        private const val ADMIN_MATRICULA = "0"
        private const val ADMIN_SENHA = "admin123"

        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_ROLE = "role"
        private const val KEY_MATRICULA = "matricula"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loguin)

        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val linkRegistrar = findViewById<TextView>(R.id.linkRegistrar)

        val inputMatricula = findViewById<EditText>(R.id.inputMatricula)
        val inputSenha = findViewById<EditText>(R.id.inputSenha)

        btnEntrar.setOnClickListener {
            try {
                val matricula = inputMatricula.text.toString().trim()
                val senha = inputSenha.text.toString().trim()

                if (matricula.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 🍀 LÓGICA: ADMIN OU USER?
                val role = if (matricula == ADMIN_MATRICULA && senha == ADMIN_SENHA) {
                    "admin"
                } else {
                    "user"
                }

                // 💾 salvar localmente o papel do usuário
                val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                prefs.edit()
                    .putString(KEY_ROLE, role)
                    .putString(KEY_MATRICULA, matricula)
                    .apply()

                // 👉 Agora todos vão para a mesma tela Home
                startActivity(Intent(this, MenuInicialActivity::class.java))

                finish()

            } catch (e: Exception) {
                Log.e("LoguinActivity", "Erro ao abrir a próxima tela", e)
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        linkRegistrar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
