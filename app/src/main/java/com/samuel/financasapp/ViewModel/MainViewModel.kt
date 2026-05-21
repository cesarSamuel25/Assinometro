package com.samuel.financasapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samuel.financasapp.Repository.FinancasRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FinancasRepository(application)

    private val _resumoFinanceiro = MutableLiveData<Map<String, Double>>()
    val resumoFinanceiro: LiveData<Map<String, Double>> = _resumoFinanceiro

    fun carregarDadosDashboard() {
        val dados = repository.buscarResumo()
        _resumoFinanceiro.value = dados
    }
}