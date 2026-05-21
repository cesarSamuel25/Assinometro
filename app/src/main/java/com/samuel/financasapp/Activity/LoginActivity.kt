package com.samuel.financasapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.databinding.ActivityLoginBinding
import com.samuel.financasapp.ViewModel.LoginViewModel
import com.samuel.financasapp.R

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pergunta ao ViewModel se o usuário já existe
        viewModel.verificarUsuarioLogado()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            val nomeDigitado = binding.editTextName.text.toString()
            viewModel.tentarSalvarUsuario(nomeDigitado)
        }
    }

    private fun setupObservers() {
        viewModel.estadoLogin.observe(this) { estado ->
            when (estado) {
                is LoginViewModel.LoginState.Sucesso -> irParaProximaTela()
                is LoginViewModel.LoginState.Erro -> Toast.makeText(this, estado.mensagem, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irParaProximaTela() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}