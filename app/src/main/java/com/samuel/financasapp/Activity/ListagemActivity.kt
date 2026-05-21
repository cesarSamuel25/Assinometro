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
import com.samuel.financasapp.databinding.ActivityListagemBinding
import com.samuel.financasapp.ViewModel.ListagemViewModel
import com.samuel.financasapp.Util.ServicoAdapter
import com.samuel.financasapp.R
import com.samuel.financasapp.Util.ServicoModel
import com.samuel.financasapp.Util.setAnimateOnClickListener

class ListagemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListagemBinding
    private val viewModel: ListagemViewModel by viewModels()
    private lateinit var adapter: ServicoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListagemBinding.inflate(layoutInflater)
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
        viewModel.carregarServicos() // Garante dados atualizados ao entrar na tela
    }

    private fun setupRecyclerView() {
        // Passamos o clique de deletar para o ViewModel
        adapter = ServicoAdapter(emptyList()) { servicoSelecionado ->
            exibirConfirmacaoDelecao(servicoSelecionado)
        }

        binding.recyclerViewServicos.apply {
            layoutManager = LinearLayoutManager(this@ListagemActivity)
            this.adapter = this@ListagemActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.apply {

            icListList.setAnimateOnClickListener{ }

            icHomeList.setAnimateOnClickListener { val intent = Intent(this@ListagemActivity, MainActivity::class.java)
                startActivity(intent)
                finish() }

            btnFiltros.setAnimateOnClickListener { exibirDialogoFiltro() }

            icStatisticsList.setAnimateOnClickListener {
                val intent = Intent(this@ListagemActivity, ProjecaoActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupObservers() {
        viewModel.listaServicos.observe(this) { lista ->
            adapter.atualizarLista(lista)
        }
    }

    private fun exibirConfirmacaoDelecao(servico: ServicoModel) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Excluir Serviço")
            .setMessage("Tem certeza que deseja ocultar o serviço \"${servico.descricao}\"?")
            .setCancelable(false) // Impede o usuário de fechar clicando fora da caixa
            .setPositiveButton("Sim, Excluir") { dialog, _ ->
                viewModel.deletarServico(servico) // Só deleta se clicar em Sim
                Toast.makeText(this, "Serviço excluído", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Apenas fecha o alerta sem fazer nada
            }
            .show()
    }

    private fun exibirDialogoFiltro() {
        val opcoes = arrayOf("Todos", "Streaming", "Saúde", "Aplicativos", "Outros")

        // Armazena temporariamente qual índice está selecionado se quiser, vamos iniciar no 0 (Todos)
        var itemSelecionado = 0

        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Filtrar por Categoria")
            .setSingleChoiceItems(opcoes, itemSelecionado) { _, which ->
                itemSelecionado = which // Atualiza o índice do que o usuário clicou
            }
            .setPositiveButton("Aplicar") { dialog, _ ->
                val categoriaEscolhida = opcoes[itemSelecionado]

                // Avisa o ViewModel para aplicar o filtro correspondente
                viewModel.filtrarPorCategoria(categoriaEscolhida)

                // Atualiza o texto do botão para o usuário saber qual filtro está ativo
                binding.btnFiltros.text = if (categoriaEscolhida == "Todos") "Filtros" else "Filtro: $categoriaEscolhida"

                dialog.dismiss()
            }
            .setNegativeButton("Fechar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}