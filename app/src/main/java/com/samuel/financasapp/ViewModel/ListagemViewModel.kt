package com.samuel.financasapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samuel.financasapp.Repository.FinancasRepository
import com.samuel.financasapp.Util.ServicoModel

class ListagemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinancasRepository(application)

    private var listaOriginal = listOf<ServicoModel>()

    private val _listaServicos = MutableLiveData<List<ServicoModel>>()
    val listaServicos: LiveData<List<ServicoModel>> = _listaServicos

    fun carregarServicos() {
        // Busca do banco e já ordena em ordem alfabética real (A-Z)
        listaOriginal = repository.buscarServicosAtivos().sortedBy { it.descricao.lowercase() }
        _listaServicos.value = listaOriginal
    }

    fun filtrarPorCategoria(categoria: String) {
        if (categoria == "Todos") {
            _listaServicos.value = listaOriginal
        } else {
            // Mantém a ordenação alfabética da lista original ao filtrar
            val listaFiltrada = listaOriginal.filter { it.categoria == categoria }
            _listaServicos.value = listaFiltrada
        }
    }

    fun deletarServico(servico: ServicoModel) {
        val sucesso = repository.ocultarServico(servico.id)
        if (sucesso) {
            carregarServicos()
        }
    }
}