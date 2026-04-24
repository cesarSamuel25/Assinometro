package com.samuel.financasapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        carregarNomeUsuario()

        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            buttonAdd.setOnClickListener { irParaTelaAdicionar() }
        }
    }


    private fun carregarNomeUsuario() {
        val sharedPref = getSharedPreferences(Constantes.SharedPreferencesConst.PREFS_NAME, MODE_PRIVATE)
        val nomeSalvo = sharedPref.getString(Constantes.SharedPreferencesConst.KEY_NOME_USUARIO, "Usuário")
        binding.textViewHello.text = "Olá, $nomeSalvo!"
    }

    private fun irParaTelaAdicionar() {
        val intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
    }
}