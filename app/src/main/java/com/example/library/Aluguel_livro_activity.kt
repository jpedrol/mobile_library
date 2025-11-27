/*
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class Alugel_Livro_Activity : AppCompatActivity() {

    private lateinit var tvMesAno: TextView
    private lateinit var layoutDatasReserva: LinearLayout
    private lateinit var btnVoltarSemana: ImageButton
    private lateinit var btnAvancarSemana: ImageButton
    private lateinit var btnConfirmarReserva: Button
    private var currentDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alugar_livro)  // Corrigido para o nome correto do XML

        // Referências da UI
        tvMesAno = findViewById(R.id.tvMesAno)
        layoutDatasReserva = findViewById(R.id.layoutDatasReserva)
        btnVoltarSemana = findViewById(R.id.btnVoltarSema   na)
        btnAvancarSemana = findViewById(R.id.btnAvancarSemana)
        btnConfirmarReserva = findViewById(R.id.btnConfirmarReserva)

        // Configura a data inicial
        updateCalendar()

        // Configura a navegação de semana
        btnVoltarSemana.setOnClickListener {
            currentDate.add(Calendar.WEEK_OF_YEAR, -1)
            updateCalendar()
        }

        btnAvancarSemana.setOnClickListener {
            currentDate.add(Calendar.WEEK_OF_YEAR, 1)
            updateCalendar()
        }

        // Configura o botão de confirmação de reserva
        btnConfirmarReserva.setOnClickListener {
            val selectedDate = getSelectedDate()
            if (selectedDate != null) {
                Toast.makeText(this, "Reserva confirmada para: $selectedDate", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, selecione uma data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Atualiza o calendário com base na semana atual
    private fun updateCalendar() {
        // Atualiza o texto com o mês e ano
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMesAno.text = monthYearFormat.format(currentDate.time)

        // Limpa os dias anteriores
        layoutDatasReserva.removeAllViews()

        // Adiciona os dias da semana ao layout
        val daysOfWeek = Calendar.getInstance().apply {
            time = currentDate.time
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }

        for (i in 0 until 7) {
            val dayView = layoutInflater.inflate(R.layout.item_day, null) as LinearLayout
            val dayText = dayView.findViewById<TextView>(R.id.tvDay)
            val dayName = dayView.findViewById<TextView>(R.id.tvDayName)

            val dayOfWeek = daysOfWeek.get(Calendar.DAY_OF_WEEK)
            val dayOfMonth = daysOfWeek.get(Calendar.DAY_OF_MONTH)

            dayName.text = daysOfWeek.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
            dayText.text = dayOfMonth.toString()

            // Define o clique para selecionar o dia
            dayView.setOnClickListener {
                selectDay(dayText, dayOfMonth)
            }

            // Adiciona ao layout
            layoutDatasReserva.addView(dayView)

            // Avança para o próximo dia
            daysOfWeek.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    // Marca o dia selecionado
    private fun selectDay(dayText: TextView, dayOfMonth: Int) {
        // Limpar seleção anterior
        for (i in 0 until layoutDatasReserva.childCount) {
            val dayView = layoutDatasReserva.getChildAt(i) as LinearLayout
            val dayTextView = dayView.findViewById<TextView>(R.id.tvDay)
            dayTextView.setBackgroundResource(0)  // Remove a seleção
        }

        // Marca o novo dia
        dayText.setBackgroundResource(R.drawable.selected_day_background)  // Defina o drawable de seleção
    }

    // Obtém a data selecionada
    private fun getSelectedDate(): String? {
        val selectedDays = mutableListOf<String>()
        for (i in 0 until layoutDatasReserva.childCount) {
            val dayView = layoutDatasReserva.getChildAt(i) as LinearLayout
            val dayText = dayView.findViewById<TextView>(R.id.tvDay)
            if (dayText.background != null) {  // Verifica se o dia está selecionado
                selectedDays.add(dayText.text.toString())
            }
        }
        return if (selectedDays.isNotEmpty()) {
            "Período selecionado: ${selectedDays.joinToString(", ")}"
        } else {
            null
        }
    }

}
*/
