package com.samuel.financasapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samuel.financasapp.Repository.FinancasRepository
import com.samuel.financasapp.Util.ServicoModel

class HistoricoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinancasRepository(application)

    private val _listaCancelados = MutableLiveData<List<ServicoModel>>()
    val listaCancelados: LiveData<List<ServicoModel>> = _listaCancelados

    fun carregarCancelados() {
        _listaCancelados.value = repository.buscarServicosCancelados().sortedBy { it.descricao.lowercase() }
    }

    fun limparTudo() {
        val sucesso = repository.excluirDefinitivoCancelados()
        if (sucesso) {
            carregarCancelados() // Zera a lista no visor imediatamente
        }
    }
}