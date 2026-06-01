package com.samuel.financasapp.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.R
import com.samuel.financasapp.Util.setAnimateOnClickListener
import com.samuel.financasapp.ViewModel.ProjecaoViewModel
import com.samuel.financasapp.databinding.ActivityProjecaoBinding
import java.text.NumberFormat
import java.util.Locale

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
            // Escuta as mudanças do SeekBar nativo de 0 a 23
            seekBarMeses.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    viewModel.atualizarProjecao(progress + 1)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // Cliques inferiores com efeito de animação elástica
            icHomeProjecao.setAnimateOnClickListener { finish() }

            icListProjecao.setAnimateOnClickListener {
                startActivity(Intent(this@ProjecaoActivity, ListagemActivity::class.java))
                finish()
            }

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
                // Sincroniza o indicador visual caso mude pelo ViewModel
                if (seekBarMeses.progress != (meses - 1)) {
                    seekBarMeses.progress = meses - 1
                }

                // Controle de visibilidade dinâmica e destaque das pontas
                when (meses) {
                    1 -> {
                        txtNumeroMesAtual.visibility = View.INVISIBLE
                        txtNum1.setTextColor(getColor(R.color.orange))
                        txtNum24.setTextColor(getColor(R.color.blue))
                    }
                    24 -> {
                        txtNumeroMesAtual.visibility = View.INVISIBLE
                        txtNum1.setTextColor(getColor(R.color.blue))
                        txtNum24.setTextColor(getColor(R.color.orange))
                    }
                    else -> {
                        txtNumeroMesAtual.visibility = View.VISIBLE
                        txtNum1.setTextColor(getColor(R.color.blue))
                        txtNum24.setTextColor(getColor(R.color.blue))
                    }
                }

                // --- MATEMÁTICA DO SEGUIDOR ADAPTADA PARA SEEKBAR ---
                seekBarMeses.post {
                    val larguraUtil = seekBarMeses.width - seekBarMeses.paddingLeft - seekBarMeses.paddingRight
                    val progressoMapeado = (meses - 1) / 23f
                    val xBolinha = seekBarMeses.paddingLeft + (larguraUtil * progressoMapeado)

                    txtNumeroMesAtual.x = seekBarMeses.x + xBolinha - (txtNumeroMesAtual.width / 2f)
                }
            }
        }

        // Formatação monetária em tempo real (R$)
        viewModel.valorCalculado.observe(this) { valorTotal ->
            val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            binding.txtValorProjetado.text = formatador.format(valorTotal).replace("R$", "R$: ")
        }
    }
}