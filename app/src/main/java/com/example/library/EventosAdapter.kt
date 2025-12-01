import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.library.Evento
import com.example.library.R

class EventosAdapter(
    private var lista: List<Evento>
) : RecyclerView.Adapter<EventosAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tvTituloEvento)
        val descricao: TextView = view.findViewById(R.id.tvDescricaoEvento)
        val imagem: ImageView = view.findViewById(R.id.ivImagemEvento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_eventos_diarios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = lista[position]

        holder.titulo.text = evento.titulo
        holder.descricao.text = evento.descricao

        Glide.with(holder.itemView.context)
            .load(evento.imagem)
            .into(holder.imagem)
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(nova: List<Evento>) {
        lista = nova
        notifyDataSetChanged()
    }
}
