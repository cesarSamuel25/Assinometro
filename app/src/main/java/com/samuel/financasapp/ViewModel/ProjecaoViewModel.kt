package com.samuel.financasapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samuel.financasapp.Repository.FinancasRepository

class ProjecaoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinancasRepository(application)

    private var custoMensalTotal = 0.0

    private val _mesesSelecionados = MutableLiveData<Int>()
    val mesesSelecionados: LiveData<Int> = _mesesSelecionados

    private val _valorCalculado = MutableLiveData<Double>()
    val valorCalculado: LiveData<Double> = _valorCalculado

    fun carregarDadosIniciais() {
        val resumo = repository.buscarResumo()
        custoMensalTotal = resumo["TOTAL"] ?: 0.0

        // Inicializa o estado com o valor padrão do meio (12 meses)
        atualizarProjecao(12)
    }

    fun atualizarProjecao(meses: Int) {
        _mesesSelecionados.value = meses
        _valorCalculado.value = custoMensalTotal * meses
    }
}