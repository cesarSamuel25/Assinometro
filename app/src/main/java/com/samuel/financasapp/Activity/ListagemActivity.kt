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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.samuel.financasapp.databinding.ActivityListagemBinding
import com.samuel.financasapp.databinding.DialogDetalheServicoBinding
import com.samuel.financasapp.ViewModel.ListagemViewModel
import com.samuel.financasapp.Util.ServicoAdapter
import com.samuel.financasapp.R
import com.samuel.financasapp.Util.ServicoModel
import com.samuel.financasapp.Util.setAnimateOnClickListener
import java.text.NumberFormat
import java.util.Locale

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
        viewModel.carregarServicos()
    }

    private fun setupRecyclerView() {
        adapter = ServicoAdapter(
            lista = emptyList(),
            onDeleteClick = { servicoSelecionado -> exibirConfirmacaoDelecao(servicoSelecionado) },
            onItemClick = { servicoSelecionado -> exibirBottomSheetDetalhes(servicoSelecionado) }
        )

        // IMPORTANTE: Altere aqui se o id do seu RecyclerView no XML for diferente
        binding.recyclerViewServicos.apply {
            layoutManager = LinearLayoutManager(this@ListagemActivity)
            this.adapter = this@ListagemActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.apply {

            // --- NOVOS BOTÕES FLUTUANTES DA IMAGEM ---

            // Redireciona para a tela de Histórico de Cancelados
            btnListagemHistorico.setAnimateOnClickListener {
                val intent = Intent(this@ListagemActivity, HistoricoActivity::class.java)
                startActivity(intent)
            }

            // Redireciona para a tela de Cadastro de um novo item
            btnListagemAdd.setAnimateOnClickListener {
                val intent = Intent(this@ListagemActivity, AddActivity::class.java)
                startActivity(intent)
            }

            // ------------------------------------------

            icListList.setAnimateOnClickListener{ }

            icHomeList.setAnimateOnClickListener {
                val intent = Intent(this@ListagemActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

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

    private fun exibirBottomSheetDetalhes(servico: ServicoModel) {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = DialogDetalheServicoBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            txtNomeDetalhe.text = servico.descricao

            val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            txtValorDetalhe.text = formatador.format(servico.valor)

            val hoje = java.time.LocalDate.now()
            val dataAlvo = if (servico.vencimento >= hoje.dayOfMonth) {
                java.time.LocalDate.of(hoje.year, hoje.monthValue, 1).withDayOfMonth(
                    if (servico.vencimento > hoje.lengthOfMonth()) hoje.lengthOfMonth() else servico.vencimento
                )
            } else {
                val prox = hoje.plusMonths(1)
                java.time.LocalDate.of(prox.year, prox.monthValue, 1).withDayOfMonth(
                    if (servico.vencimento > prox.lengthOfMonth()) prox.lengthOfMonth() else servico.vencimento
                )
            }
            val nomeMes = dataAlvo.month.getDisplayName(java.time.format.TextStyle.SHORT, Locale("pt", "BR"))
                .replaceFirstChar { it.uppercase() }

            txtDataDetalhe.text = "${dataAlvo.dayOfMonth}/$nomeMes"

            btnFecharDialog.setAnimateOnClickListener { dialog.dismiss() }

            btnAlterarDialog.setAnimateOnClickListener {
                dialog.dismiss()
                val intent = Intent(this@ListagemActivity, AddActivity::class.java).apply {
                    putExtra("SERVICO_ID", servico.id)
                    putExtra("MODO_EDICAO", true)
                }
                startActivity(intent)
            }
        }
        dialog.show()
    }

    private fun exibirConfirmacaoDelecao(servico: ServicoModel) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Excluir Serviço")
            .setMessage("Tem certeza que deseja ocultar o serviço \"${servico.descricao}\"?")
            .setCancelable(false)
            .setPositiveButton("Sim, Excluir") { dialog, _ ->
                viewModel.deletarServico(servico)
                Toast.makeText(this, "Serviço excluído", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun exibirDialogoFiltro() {
        val opcoes = arrayOf("Todos", "Streaming", "Saúde", "Aplicativos", "Outros")
        var itemSelecionado = 0

        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Filtrar por Categoria")
            .setSingleChoiceItems(opcoes, itemSelecionado) { _, which ->
                itemSelecionado = which
            }
            .setPositiveButton("Aplicar") { dialog, _ ->
                val categoriaEscolhida = opcoes[itemSelecionado]
                viewModel.filtrarPorCategoria(categoriaEscolhida)
                binding.btnFiltros.text = if (categoriaEscolhida == "Todos") "Filtros" else "Filtro: $categoriaEscolhida"
                dialog.dismiss()
            }
            .setNegativeButton("Fechar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}