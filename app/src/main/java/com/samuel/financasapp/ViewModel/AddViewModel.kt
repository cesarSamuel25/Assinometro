package com.samuel.financasapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samuel.financasapp.Repository.FinancasRepository

class AddViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinancasRepository(application)

    private val _cadastroSucesso = MutableLiveData<Boolean>()
    val cadastroSucesso: LiveData<Boolean> = _cadastroSucesso

    private val _erroMensagem = MutableLiveData<String>()
    val erroMensagem: LiveData<String> = _erroMensagem

    fun processarEAlvar(nome: String, valorMascarado: String, categoria: String, vencimentoStr: String) {
        // Lógica de negócio: Converter a string limpa da máscara para Double
        val valorLimpo = valorMascarado.replace(Regex("[^\\d]"), "")
        val valorParsed = if (valorLimpo.isNotEmpty()) valorLimpo.toDouble() / 100.0 else 0.0
        val vencimentoParsed = vencimentoStr.toIntOrNull() ?: 0

        val salvou = repository.salvarServico(nome, valorParsed, categoria, vencimentoParsed)
        if (salvou) {
            _cadastroSucesso.value = true
        } else {
            _erroMensagem.value = "Erro interno ao salvar no banco de dados"
        }
    }
}