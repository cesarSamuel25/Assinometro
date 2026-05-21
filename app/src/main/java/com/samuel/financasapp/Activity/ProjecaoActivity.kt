package com.samuel.financasapp.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.databinding.ActivityProjecaoBinding
import java.text.NumberFormat
import java.util.Locale
import com.samuel.financasapp.ViewModel.ProjecaoViewModel
import com.samuel.financasapp.R
import com.samuel.financasapp.Util.setAnimateOnClickListener

class ProjecaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjecaoBinding
    private val viewModel: ProjecaoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjecaoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        setupObservers()

        viewModel.carregarDadosIniciais()
    }

    private fun setupListeners() {
        binding.apply {
            sliderMeses.addOnChangeListener { _, value, _ ->
                viewModel.atualizarProjecao(value.toInt())
            }

            // Ações dos botões e navegação
            icHomeProjecao.setAnimateOnClickListener { val intent = Intent(this@ProjecaoActivity, MainActivity::class.java)
                startActivity(intent)
                finish() }

            icListProjecao.setAnimateOnClickListener {
                startActivity(Intent(this@ProjecaoActivity, ListagemActivity::class.java))
                finish()
            }

            icStatisticsProjecao.setAnimateOnClickListener {}

            btnProjecaoAdd.setAnimateOnClickListener {
                startActivity(Intent(this@ProjecaoActivity, AddActivity::class.java))
            }
        }
    }

    private fun setupObservers() {
        viewModel.mesesSelecionados.observe(this) { meses ->
            binding.txtTextoMeses.text = "Em $meses Meses\nvocê irá gastar:"
            binding.txtNumeroMesAtual.text = meses.toString()

            binding.apply {
                // --- LÓGICA DE OCULTAR NAS PONTAS E TRANSMUTAR COR ---
                when (meses) {
                    1 -> {
                        txtNumeroMesAtual.visibility = android.view.View.INVISIBLE
                        txtNum1.setTextColor(getColor(R.color.orange))
                        txtNum24.setTextColor(getColor(R.color.blue))
                    }
                    24 -> {
                        txtNumeroMesAtual.visibility = android.view.View.INVISIBLE
                        txtNum1.setTextColor(getColor(R.color.blue))
                        txtNum24.setTextColor(getColor(R.color.orange))
                    }
                    else -> {
                        txtNumeroMesAtual.visibility = android.view.View.VISIBLE
                        txtNum1.setTextColor(getColor(R.color.blue))
                        txtNum24.setTextColor(getColor(R.color.blue))
                    }
                }

                // --- MATEMÁTICA CORRIGIDA DO SEGUIDOR DA BOLINHA ---
                sliderMeses.post {
                    val slider = sliderMeses
                    val larguraUtil = slider.width - slider.trackSidePadding * 2
                    val totalPassos = slider.valueTo - slider.valueFrom
                    val progresso = (meses - slider.valueFrom) / totalPassos
                    val xBolinha = slider.trackSidePadding + (larguraUtil * progresso)

                    txtNumeroMesAtual.x = slider.x + xBolinha - (txtNumeroMesAtual.width / 2f)
                }
            }
        }

        // Formatação monetária reativa
        viewModel.valorCalculado.observe(this) { valorTotal ->
            val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            binding.txtValorProjetado.text = formatador.format(valorTotal).replace("R$", "R$: ")
        }
    }
}