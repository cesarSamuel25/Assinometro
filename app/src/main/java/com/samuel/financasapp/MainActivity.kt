package com.samuel.financasapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.Locale

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

        atualizarDashboard()
    }

    override fun onResume() {
        super.onResume()

        atualizarDashboard()
    }

    private fun atualizarDashboard() {
        val db = Database(this)
        val dados = db.obterResumoFinanceiro()

        binding.apply {
            val totalGeral = dados["TOTAL"] ?: 0.0
            val valorFormatado = formatarMoeda(totalGeral)

            textViewTotal.text = getString(R.string.total_formatado, valorFormatado)

            valueStreaming.text = formatarMoeda(dados["Streaming"] ?: 0.0)
            valueFit.text = formatarMoeda(dados["Saúde"] ?: 0.0)
            valueApps.text = formatarMoeda(dados["Aplicativos"] ?: 0.0)
            valueMore.text = formatarMoeda(dados["Outros"] ?: 0.0)
        }
    }

    private fun formatarMoeda(valor: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)
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