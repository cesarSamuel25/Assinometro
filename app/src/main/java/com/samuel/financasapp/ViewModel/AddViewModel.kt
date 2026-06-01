package com.samuel.financasapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samuel.financasapp.Repository.FinancasRepository
import com.samuel.financasapp.Util.ServicoModel

class AddViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinancasRepository(application)

    private val _cadastroSucesso = MutableLiveData<Boolean>()
    val cadastroSucesso: LiveData<Boolean> = _cadastroSucesso

    private val _erroMensagem = MutableLiveData<String>()
    val erroMensagem: LiveData<String> = _erroMensagem

    // LiveData que notificará a Activity para preencher os inputs textuais na tela
    private val _servicoCarregado = MutableLiveData<ServicoModel>()
    val servicoCarregado: LiveData<ServicoModel> = _servicoCarregado

    private var modoEdicao = false
    private var idServicoEdicao = -1

    // Configura o estado inicial do ViewModel dependendo da Intenção de clique
    fun carregarModo(id: Int, ehEdicao: Boolean) {
        this.modoEdicao = ehEdicao
        this.idServicoEdicao = id

        if (ehEdicao && id != -1) {
            val servico = repository.buscarServicoPorId(id)
            if (servico != null) {
                _servicoCarregado.value = servico
            } else {
                _erroMensagem.value = "Não foi possível recuperar os dados do serviço."
            }
        }
    }

    fun processarEAlvar(nome: String, valorMascarado: String, categoria: String, vencimentoStr: String) {
        val valorLimpo = valorMascarado.replace(Regex("[^\\d]"), "")
        val valorParsed = if (valorLimpo.isNotEmpty()) valorLimpo.toDouble() / 100.0 else 0.0
        val vencimentoParsed = vencimentoStr.toIntOrNull() ?: 0

        // Decide dinamicamente se cria uma nova linha ou altera a linha do ID ativo
        val salvou: Boolean = if (modoEdicao) {
            repository.atualizarServico(idServicoEdicao, nome, valorParsed, categoria, vencimentoParsed)
        } else {
            repository.salvarServico(nome, valorParsed, categoria, vencimentoParsed)
        }

        if (salvou) {
            _cadastroSucesso.value = true
        } else {
            _erroMensagem.value = "Erro interno ao processar banco de dados"
        }
    }
}