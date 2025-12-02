package com.example.library

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import kotlin.math.abs

class AcessibilidadeActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var sbFontScale: SeekBar
    private lateinit var tvFontScaleLabel: TextView
    private lateinit var tvFontDescription: TextView
    private lateinit var btnMenu: ImageButton

    private lateinit var itemMenuInicial: LinearLayout
    private lateinit var itemAcessibilidade: LinearLayout
    private lateinit var itemSair: LinearLayout
    private lateinit var btnSave: Button

    private val allowedValues = listOf(5, 10, 15, 20)

    override fun onCreate(savedInstanceState: Bundle?) {

        prefs = getSharedPreferences("acessibilidade", MODE_PRIVATE)

        applyFontScale()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acessibilidade)

        drawerLayout = findViewById(R.id.drawerLayout)
        btnMenu = findViewById(R.id.btnMenu)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        itemMenuInicial = findViewById(R.id.itemMenuInicial)
        itemAcessibilidade = findViewById(R.id.itemAcessibilidade)
        itemSair = findViewById(R.id.itemSair)

        itemMenuInicial.setOnClickListener {
            startActivity(Intent(this, MenuInicialActivity::class.java))
            finish()
        }

        itemAcessibilidade.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        itemSair.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)

            val intent = Intent(this, LoguinActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        sbFontScale = findViewById(R.id.seekBarFontScale)
        tvFontScaleLabel = findViewById(R.id.tvFontScale)
        tvFontDescription = findViewById(R.id.tvFontDescription)
        btnSave = findViewById(R.id.btnSave)

        sbFontScale.max = 20

        sbFontScale.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                if (fromUser) {
                    val nearest = allowedValues.minByOrNull { abs(it - value) } ?: value
                    sbFontScale.progress = nearest
                    updateFontLabel(nearest)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        loadPrefs()

        btnSave.setOnClickListener {
            prefs.edit()
                .putInt("font_progress", sbFontScale.progress)
                .apply()

            Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show()

            recreate()
        }
    }

    private fun updateFontLabel(progress: Int) {
        val scale = progress / 10f
        tvFontScaleLabel.text = "Tamanho da fonte: %.1fx".format(scale)
    }

    private fun loadPrefs() {
        val saved = prefs.getInt("font_progress", 10)
        val nearest = allowedValues.minByOrNull { abs(it - saved) } ?: 10

        sbFontScale.progress = nearest
        updateFontLabel(nearest)
    }

    private fun applyFontScale() {
        val fontScale = prefs.getInt("font_progress", 10) / 10f
        val config = resources.configuration
        config.fontScale = fontScale
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
