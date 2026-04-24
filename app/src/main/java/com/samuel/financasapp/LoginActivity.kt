package com.samuel.financasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences(Constantes.SharedPreferencesConst.PREFS_NAME, MODE_PRIVATE)
        val nomeSalvo = sharedPref.getString(Constantes.SharedPreferencesConst.KEY_NOME_USUARIO, null)
        if (nomeSalvo != null) {
            irParaProximaTela()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            buttonSave.setOnClickListener { saveUserName() }
        }
    }

    private fun saveUserName(){
        val name = binding.editTextName.text.toString()

        if (name.isNotEmpty()) {
            salvarNoSharedPreferences(name)
            irParaProximaTela()
        } else {
            Toast.makeText(this, "Nome não pode estar vazio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun salvarNoSharedPreferences(nome: String) {

        val sharedPref = getSharedPreferences(Constantes.SharedPreferencesConst.PREFS_NAME, MODE_PRIVATE)

        with(sharedPref.edit()) {
            putString(Constantes.SharedPreferencesConst.KEY_NOME_USUARIO, nome)
            apply()
        }
    }

    private fun irParaProximaTela() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}