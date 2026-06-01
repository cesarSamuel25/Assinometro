package com.samuel.financasapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samuel.financasapp.Repository.FinancasRepository
import com.samuel.financasapp.Util.ServicoModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoricoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinancasRepository(application)

    private val _listaCancelados = MutableLiveData<List<ServicoModel>>()
    val listaCancelados: LiveData<List<ServicoModel>> = _listaCancelados

    fun carregarCancelados() {
        val listaBanco = repository.buscarServicosCancelados()

        // Ordena colocando a data de cancelamento mais recente primeiro (decrescente)
        _listaCancelados.value = listaBanco.sortedByDescending { servico ->
            converterStringParaData(servico.dataCancelamento)
        }
    }

    fun limparTudo() {
        val sucesso = repository.excluirDefinitivoCancelados()
        if (sucesso) {
            carregarCancelados()
        }
    }

    // Função auxiliar para transformar "16/Jun" de volta em um objeto LocalDate comparável
    private fun converterStringParaData(dataStr: String?): LocalDate {
        if (dataStr.isNullOrEmpty()) return LocalDate.MIN
        return try {
            // Adiciona o ano atual dinamicamente para conseguir converter a String do banco
            val anoAtual = LocalDate.now().year
            val textoCompleto = "$dataStr/$anoAtual"
            val formatador = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale("pt", "BR"))
            LocalDate.parse(textoCompleto, formatador)
        } catch (e: Exception) {
            LocalDate.MIN // Caso dê erro em dados antigos/vazios, joga para o fim da lista
        }
    }
}