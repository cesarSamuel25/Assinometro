package com.samuel.financasapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.databinding.ActivityAddBinding
import java.text.NumberFormat
import java.util.Locale
import com.samuel.financasapp.ViewModel.AddViewModel
import com.samuel.financasapp.R
import com.samuel.financasapp.Util.setAnimateOnClickListener

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private val viewModel: AddViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarDropdownCategorias()
        configurarMascaraMoeda(binding.editValorServico)
        setupListeners()
        setupObservers()

        // --- INTERCEPTAÇÃO DA INTENT PARA IDENTIFICAR PROCESSO DE ALTERAÇÃO ---
        val idServico = intent.getIntExtra("SERVICO_ID", -1)
        val ehEdicao = intent.getBooleanExtra("MODO_EDICAO", false)

        viewModel.carregarModo(idServico, ehEdicao)

        // Altera cirurgicamente os textos estáticos da interface se for uma alteração
        if (ehEdicao) {
            binding.textViewTitle.text = "Alterar Serviço"
            binding.btnSalvarServico.text = "Atualizar Dados"
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnSalvarServico.setOnClickListener {
                if (validarCampos()) {
                    viewModel.processarEAlvar(
                        editNomeServico.text.toString(),
                        editValorServico.text.toString(),
                        autoCompleteCategoria.text.toString(),
                        editVencimento.text.toString()
                    )
                } else {
                    Toast.makeText(this@AddActivity, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
                }
            }

            icHomeAdd.setAnimateOnClickListener {
                val intent = Intent(this@AddActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            icStatisticsAdd.setAnimateOnClickListener {
                val intent = Intent(this@AddActivity, ProjecaoActivity::class.java)
                startActivity(intent)
                finish()
            }

            icListAdd.setAnimateOnClickListener {
                val intent = Intent(this@AddActivity, ListagemActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupObservers() {
        viewModel.cadastroSucesso.observe(this) { sucesso ->
            if (sucesso) {
                Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.erroMensagem.observe(this) { mensagem ->
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        }

        // Observa os dados recuperados do banco e popula as Views automaticamente
        viewModel.servicoCarregado.observe(this) { servico ->
            binding.apply {
                editNomeServico.setText(servico.descricao)
                editVencimento.setText(servico.vencimento.toString())
                autoCompleteCategoria.setText(servico.categoria, false)

                // Converte o double bruto vindo do SQLite e força o preenchimento formatado na máscara
                val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                editValorServico.setText(formatador.format(servico.valor))
            }
        }
    }

    private fun validarCampos(): Boolean {
        var todosValidos = true
        binding.apply {
            if (editNomeServico.text.toString().isEmpty()) {
                inputLayoutNome.error = "Digite o nome do serviço"
                todosValidos = false
            } else { inputLayoutNome.error = null }

            if (editValorServico.text.toString().isEmpty()) {
                inputLayoutValor.error = "Digite o valor"
                todosValidos = false
            } else { inputLayoutValor.error = null }

            if (autoCompleteCategoria.text.toString().isEmpty()) {
                inputLayoutCategoria.error = "Selecione uma categoria"
                todosValidos = false
            } else { inputLayoutCategoria.error = null }

            val vencimentoTexto = editVencimento.text.toString()
            val vencimentoNumero = vencimentoTexto.toIntOrNull()

            if (vencimentoTexto.isEmpty()) {
                inputLayoutVencimento.error = "Digite o dia"
                todosValidos = false
            } else if (vencimentoNumero == null || vencimentoNumero < 1 || vencimentoNumero > 31) {
                inputLayoutVencimento.error = "O dia deve ser entre 1 e 31"
                todosValidos = false
            } else {
                inputLayoutVencimento.error = null
            }
        }
        return todosValidos
    }

    private fun configurarDropdownCategorias() {
        val categories = arrayOf("Streaming", "Saúde", "Aplicativos", "Outros")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.autoCompleteCategoria.setAdapter(adapter)
    }

    private fun configurarMascaraMoeda(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var atual = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != atual) {
                    editText.removeTextChangedListener(this)
                    val limpo = s.toString().replace(Regex("[^\\d]"), "")
                    if (limpo.isNotEmpty()) {
                        val parsed = limpo.toDouble() / 100.0
                        val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed)
                        atual = formatador
                        editText.setText(formatador)
                        editText.setSelection(formatador.length)
                    } else {
                        atual = ""
                        editText.setText("")
                    }
                    editText.addTextChangedListener(this)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}