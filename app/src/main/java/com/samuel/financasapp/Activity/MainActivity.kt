package com.samuel.financasapp.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.Util.Constantes
import com.samuel.financasapp.ViewModel.MainViewModel
import com.samuel.financasapp.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.Locale
import com.samuel.financasapp.R
import com.samuel.financasapp.Util.setAnimateOnClickListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

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
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarDadosDashboard()
    }

    private fun setupObservers() {
        viewModel.resumoFinanceiro.observe(this) { dados ->
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
    }

    private fun formatarMoeda(valor: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)
    }

    private fun setupListeners() {
        binding.apply {
            buttonAdd.setAnimateOnClickListener { irParaTelaAdicionar() }

            icListAdd.setAnimateOnClickListener { irParaTelaListagem() }

            icStatisticsAdd.setAnimateOnClickListener {
                val intent = Intent(this@MainActivity, ProjecaoActivity::class.java)
                startActivity(intent)
            }

            icHomeAdd.setAnimateOnClickListener { }
        }
    }

    private fun irParaTelaListagem() {
        val intent = Intent(this, ListagemActivity::class.java)
        startActivity(intent)
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