package com.samuel.financasapp.Util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samuel.financasapp.databinding.ItemServicoBinding
import java.text.NumberFormat
import java.util.Locale
import com.samuel.financasapp.R

class ServicoAdapter(
    private var lista: List<ServicoModel>,
    private val onDeleteClick: (ServicoModel) -> Unit,
    private val onItemClick: (ServicoModel) -> Unit // Mapeia o clique para abrir o BottomSheet
) : RecyclerView.Adapter<ServicoAdapter.ServicoViewHolder>() {

    class ServicoViewHolder(val binding: ItemServicoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicoViewHolder {
        val binding = ItemServicoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServicoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicoViewHolder, position: Int) {
        val servico = lista[position]
        holder.binding.apply {
            val valorFormatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(servico.valor)
            txtItemInfo.text = "${servico.descricao}  •  $valorFormatado"

            // --- LÓGICA DE VENCIMENTO DINÂMICA ---
            val hoje = java.time.LocalDate.now()
            val diaAtual = hoje.dayOfMonth
            val diaVencimento = servico.vencimento

            val dataVencimentoAlvo = if (diaVencimento >= diaAtual) {
                ajustarParaUltimoDiaDoMes(hoje.year, hoje.monthValue, diaVencimento)
            } else {
                val proximoMes = hoje.plusMonths(1)
                ajustarParaUltimoDiaDoMes(proximoMes.year, proximoMes.monthValue, diaVencimento)
            }

            val nomeMesFormatado = dataVencimentoAlvo.month
                .getDisplayName(java.time.format.TextStyle.SHORT, Locale("pt", "BR"))
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }

            val textoVencimentoCompleto = "Vence em ${dataVencimentoAlvo.dayOfMonth}/$nomeMesFormatado"
            txtItemVencimento.text = textoVencimentoCompleto
            // -------------------------------------

            when (servico.categoria) {
                "Streaming" -> imgItemIcone.setImageResource(R.drawable.ic_tv)
                "Saúde" -> imgItemIcone.setImageResource(R.drawable.ic_fit)
                "Aplicativos" -> imgItemIcone.setImageResource(R.drawable.ic_app)
                else -> imgItemIcone.setImageResource(R.drawable.ic_more)
            }

            btnItemDeletar.setOnClickListener { onDeleteClick(servico) }

            // Configura o clique no card inteiro repassando o texto do vencimento calculado
            root.setOnClickListener {
                onItemClick(servico)
            }
        }
    }

    private fun ajustarParaUltimoDiaDoMes(ano: Int, mes: Int, diaDesejado: Int): java.time.LocalDate {
        val primeiroDiaDoMes = java.time.LocalDate.of(ano, mes, 1)
        val ultimoDiaDoMes = primeiroDiaDoMes.lengthOfMonth()
        val diaFinal = if (diaDesejado > ultimoDiaDoMes) ultimoDiaDoMes else diaDesejado
        return java.time.LocalDate.of(ano, mes, diaFinal)
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<ServicoModel>) {
        this.lista = novaLista
        notifyDataSetChanged()
    }
}