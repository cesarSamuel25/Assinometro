package com.samuel.financasapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samuel.financasapp.Repository.FinancasRepository
import com.samuel.financasapp.Util.ServicoModel
import java.time.LocalDate

class ListagemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinancasRepository(application)

    private val _listaServicos = MutableLiveData<List<ServicoModel>>()
    val listaServicos: LiveData<List<ServicoModel>> = _listaServicos

    private var listaOriginal = emptyList<ServicoModel>()

    fun carregarServicos() {
        // Busca do banco e ordena colocando a data mais próxima primeiro
        listaOriginal = repository.buscarServicosAtivos()
        _listaServicos.value = ordenarPorDataMaisProxima(listaOriginal)
    }

    fun deletarServico(servico: ServicoModel) {
        val sucesso = repository.ocultarServico(servico.id)
        if (sucesso) {
            carregarServicos()
        }
    }

    fun filtrarPorCategoria(categoria: String) {
        val listaFiltrada = if (categoria == "Todos") {
            listaOriginal
        } else {
            listaOriginal.filter { it.categoria == categoria }
        }
        _listaServicos.value = ordenarPorDataMaisProxima(listaFiltrada)
    }

    // --- LÓGICA DE ORDENAÇÃO POR PROXIMIDADE CRONOLÓGICA ---
    private fun ordenarPorDataMaisProxima(lista: List<ServicoModel>): List<ServicoModel> {
        val hoje = LocalDate.now()

        return lista.sortedBy { servico ->
            // Se o dia de vencimento já passou neste mês, o vencimento real é no mês que vem
            if (servico.vencimento >= hoje.dayOfMonth) {
                val limiteMes = hoje.lengthOfMonth()
                val diaAjustado = if (servico.vencimento > limiteMes) limiteMes else servico.vencimento
                LocalDate.of(hoje.year, hoje.monthValue, diaAjustado)
            } else {
                val proximoMes = hoje.plusMonths(1)
                val limiteMesProx = proximoMes.lengthOfMonth()
                val diaAjustadoProx = if (servico.vencimento > limiteMesProx) limiteMesProx else servico.vencimento
                LocalDate.of(proximoMes.year, proximoMes.monthValue, diaAjustadoProx)
            }
        }
    }
}