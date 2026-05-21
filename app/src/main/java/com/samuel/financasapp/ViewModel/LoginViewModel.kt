package com.samuel.financasapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samuel.financasapp.Repository.FinancasRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinancasRepository(application)

    private val _estadoLogin = MutableLiveData<LoginState>()
    val estadoLogin: LiveData<LoginState> = _estadoLogin

    fun verificarUsuarioLogado() {
        val nome = repository.buscarNomeUsuario()
        if (nome != null) {
            _estadoLogin.value = LoginState.Sucesso
        }
    }

    fun tentarSalvarUsuario(nome: String) {
        if (nome.trim().isNotEmpty()) {
            repository.salvarNomeUsuario(nome.trim())
            _estadoLogin.value = LoginState.Sucesso
        } else {
            _estadoLogin.value = LoginState.Erro("O nome não pode estar vazio")
        }
    }

    sealed class LoginState {
        object Sucesso : LoginState()
        data class Erro(val mensagem: String) : LoginState()
    }
}