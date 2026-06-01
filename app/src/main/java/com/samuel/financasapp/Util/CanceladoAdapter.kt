package com.samuel.financasapp.Util

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samuel.financasapp.R
import com.samuel.financasapp.databinding.ItemCanceladoBinding
import java.text.NumberFormat
import java.util.Locale

class CanceladoAdapter(
    private var lista: List<ServicoModel>
) : RecyclerView.Adapter<CanceladoAdapter.CanceladoViewHolder>() {

    class CanceladoViewHolder(val binding: ItemCanceladoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanceladoViewHolder {
        val binding = ItemCanceladoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CanceladoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CanceladoViewHolder, position: Int) {
        val servico = lista[position]
        holder.binding.apply {
            val valorFormatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(servico.valor)
            txtCanceladoInfo.text = "${servico.descricao}  •  $valorFormatado"

            // --- LEITURA DIRETA DA DATA PERSISTIDA NO BANCO ---
            val dataTextoStr = servico.dataCancelamento ?: "N/A"
            val textoCompleto = "Cancelado em $dataTextoStr"

            val spannable = SpannableString(textoCompleto)
            val indiceInicioData = textoCompleto.indexOf(dataTextoStr)

            if (indiceInicioData != -1) {
                spannable.setSpan(
                    ForegroundColorSpan(holder.itemView.context.getColor(R.color.orange)),
                    indiceInicioData,
                    indiceInicioData + dataTextoStr.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            txtCanceladoData.text = spannable

            when (servico.categoria) {
                "Streaming" -> imgCanceladoIcone.setImageResource(R.drawable.ic_tv)
                "Saúde" -> imgCanceladoIcone.setImageResource(R.drawable.ic_fit)
                "Aplicativos" -> imgCanceladoIcone.setImageResource(R.drawable.ic_app)
                else -> imgCanceladoIcone.setImageResource(R.drawable.ic_more)
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<ServicoModel>) {
        this.lista = novaLista
        notifyDataSetChanged()
    }
}