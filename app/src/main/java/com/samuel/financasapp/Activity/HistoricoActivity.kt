package com.samuel.financasapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.samuel.financasapp.R
import com.samuel.financasapp.Util.CanceladoAdapter
import com.samuel.financasapp.Util.setAnimateOnClickListener
import com.samuel.financasapp.ViewModel.HistoricoViewModel
import com.samuel.financasapp.databinding.ActivityHistoricoBinding

class HistoricoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoBinding
    private val viewModel: HistoricoViewModel by viewModels()
    private lateinit var historicoAdapter: CanceladoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarCancelados()
    }

    private fun setupRecyclerView() {
        historicoAdapter = CanceladoAdapter(emptyList())
        binding.recyclerViewCancelados.apply {
            layoutManager = LinearLayoutManager(this@HistoricoActivity)
            adapter = historicoAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            // Ação do botão de Limpeza no Topo
            btnLimparHistorico.setOnClickListener {
                if (historicoAdapter.itemCount > 0) {
                    exibirConfirmacaoLimpeza()
                } else {
                    Toast.makeText(this@HistoricoActivity, "O histórico já está vazio", Toast.LENGTH_SHORT).show()
                }
            }

            // Barra inferior
            icHomeHistorico.setAnimateOnClickListener {
                startActivity(Intent(this@HistoricoActivity, MainActivity::class.java))
                finish()
            }

            icListHistorico.setAnimateOnClickListener {
                startActivity(Intent(this@HistoricoActivity, ListagemActivity::class.java))
                finish()
            }

            icStatisticsHistorico.setAnimateOnClickListener {
                startActivity(Intent(this@HistoricoActivity, ProjecaoActivity::class.java))
                finish()
            }
        }
    }

    private fun setupObservers() {
        viewModel.listaCancelados.observe(this) { lista ->
            historicoAdapter.atualizarLista(lista)
        }
    }

    private fun exibirConfirmacaoLimpeza() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Limpar Histórico")
            .setMessage("Tem certeza que deseja apagar permanentemente todos os serviços cancelados? Essa ação não pode ser desfeita.")
            .setCancelable(false)
            .setPositiveButton("Apagar Tudo") { dialog, _ ->
                viewModel.limparTudo()
                Toast.makeText(this, "Histórico limpo com sucesso!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}